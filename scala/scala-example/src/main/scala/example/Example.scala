package example

import faker._
import java.net.{InetSocketAddress, Proxy}
import com.softwaremill.sttp.okhttp.{OkHttpSyncBackend}
import okhttp3.{Authenticator, OkHttpClient}

import okhttp3.Credentials
import okhttp3.Route
import okhttp3.Response
import okhttp3.Request
import java.io.IOException
import com.softwaremill.sttp._

import org.json4s._
import org.json4s.native.JsonMethods._

object Example extends App {

    //Get Env variables
    val username = sys.env("FORWARD_HTTP_PROXY_USERNAME")
    val password = sys.env("FORWARD_HTTP_PROXY_PASSWORD")
    val reverseProxy = sys.env("REVERSE_HTTP_PROXY_HOST")
    val forwardProxy = sys.env("FORWARD_HTTP_PROXY_HOST")

    def redactViaReverseProxy(originalData: String): String = {
        implicit val backend: SttpBackend[Id, _] = OkHttpSyncBackend()

        val request = sttp
            .body(originalData)
            .post(uri"https://$reverseProxy/post")

        val json = parse(request.send.unsafeBody)

        implicit val formats: DefaultFormats = DefaultFormats
        (json \ "data").extract[String]
    }

    def revealViaForwardProxy(redactedSecret: String): String = {
        System.setProperty("javax.net.ssl.trustStore", this.getClass.getClassLoader.getResource("cacerts").getFile)
        System.setProperty("javax.net.ssl.trustStorePassword", "password")
        System.setProperty("javax.net.ssl.trustStoreType", "JKS")

        val proxyAuthenticator = new Authenticator() {
            @throws[IOException]
            def authenticate(route: Route, response: Response): Request = {
                val credential = Credentials.basic(username, password)
                response.request.newBuilder.header("Proxy-Authorization", credential).build
            }
        }

        val hostport: Array[String] = forwardProxy.split(":")
        val client = new OkHttpClient.Builder()
            .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostport(0), hostport(1).toInt)))
            .proxyAuthenticator(proxyAuthenticator)
            .build()


        implicit val backend: SttpBackend[Id, _] = OkHttpSyncBackend.usingClient(client)

        val request = sttp
            .body(redactedSecret)
            .post(uri"https://httpbin.verygoodsecurity.io/post")

        val json = parse(request.send.unsafeBody)

        implicit val formats: DefaultFormats = DefaultFormats
        (json \ "data").extract[String]
    }

    //Random Name to JSON for data payload
    val fake = Name.name

    def originalValue = s"""{"secret":"$fake"}"""
    println(originalValue)

    val redactedSecret = Example.redactViaReverseProxy(originalValue)
    println(redactedSecret)

    val revealedValue = Example.revealViaForwardProxy(redactedSecret)
    println(revealedValue)

    //Tests
    assert(s"$originalValue" != s"$redactedSecret")
    assert(s"$redactedSecret" != s"$revealedValue")
    assert(s"$revealedValue" == s"$revealedValue")
}
