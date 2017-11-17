import faker._
import scalaj.http._
import spray.json._
import DefaultJsonProtocol._
import java.net.{Authenticator,PasswordAuthentication}
import java.nio.file.{Files, Path, Paths, FileSystems}
import javax.net.ssl._
import scala.collection.JavaConversions._

class Proxies  {
  import Proxies._
  val resourcesPath = getClass.getResource("cacerts")
  val fullPath = resourcesPath.getPath
  System.setProperty("javax.net.ssl.trustStore", Proxies.getClass.getClassLoader.getResource("cacerts").getFile())
  System.setProperty("javax.net.ssl.trustStorePassword", "password")
  System.setProperty("javax.net.ssl.trustStoreType", "JKS")
}

object Proxies extends App {

  //Get Env variables
  val username = sys.env("FORWARD_HTTP_PROXY_USERNAME")
  val password = sys.env("FORWARD_HTTP_PROXY_PASSWORD")
  val reverseProxy = sys.env("REVERSE_HTTP_PROXY_HOST")
  val forwardProxy = sys.env("FORWARD_HTTP_PROXY_HOST")


  //Random Name to JSON for data payload
  val fake = Name.name
  def data = s"""{"secret":"$fake"}"""
  println(data)

  //Reverse Proxy
  val reverseUrl: String = (s"https://$reverseProxy/post")
  def request = Http(reverseUrl).postData(data).header("Content-type", "application/json")
    val response: HttpResponse[String] = request.asString
    val json = (response.body).parseJson
    val redacted = json.convertTo[Map[String, JsValue]]
    val redactedSecret = redacted.get("json").get.toString
    println(redactedSecret)

  //Forward Proxy

  val url = "https://httpbin.verygoodsecurity.io/post"
  val proxyHost= s"https://$forwardProxy"
  def requestForward: HttpRequest = Http(url).postData(data)
    .header("Content-Type", "application/json")
    .proxy(proxyHost, 8080)
    .option(HttpOptions.allowUnsafeSSL)

  Authenticator.setDefault(new Authenticator() {
   override def getPasswordAuthentication(): PasswordAuthentication = {
    new PasswordAuthentication( s"$username", s"$password".toCharArray())
   }
  })

    val responseForward: HttpResponse[String] = requestForward.asString
    val forwardJson = (responseForward.body).parseJson
    val revealed = forwardJson.convertTo[Map[String, JsValue]]
    val revealedSecret = revealed.get("json").get
    println(revealedSecret)

  //Tests
   assert(s"$data" != s"$redactedSecret")
   assert(s"$redactedSecret" != s"$revealedSecret")
   assert(s"$data" == s"$revealedSecret")
  

}
