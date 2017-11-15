import faker._
import scalaj.http._
import spray.json._
import DefaultJsonProtocol._
import java.nio.charset.StandardCharsets.UTF_8

object Proxies extends App {

  val username = sys.env("FORWARD_HTTP_PROXY_USERNAME")
  val password = sys.env("FORWARD_HTTP_PROXY_PASSWORD")
  val reverseProxy = sys.env("REVERSE_HTTP_PROXY_HOST")
  val forwardProxy = sys.env("FORWARD_HTTP_PROXY_HOST")


  def base64(bytes: Array[Byte]): String = new String(Base64.encode(bytes))
  def base64(in: String): String = base64(in.getBytes(UTF_8))

  val proxyAuth = base64(username + ":" + password)

  //Random Name for data payload
  val fake = Name.name

  //Reverse Proxy
  val reverseUrl: String = (s"https://$reverseProxy/post")
  val data = s"""{"secret" : "$fake"}"""
  val request = Http(reverseUrl).postData(data).header("Content-type", "application/json")
  val response: HttpResponse[String] = request.asString
  val json = (response.body).parseJson
  val redacted = json.convertTo[Map[String, JsValue]]
  val redactedSecret = redacted.get("json").get.toString
  println(redactedSecret)

  //Forward forwardProxy
  val url = "https://httpbin.verygoodsecurity.io/post"

  val proxyHost= s"https://$forwardProxy"
  val requestForward = Http(url).postData(redactedSecret)
    .option(HttpOptions.allowUnsafeSSL)
    .header("Content-Type", "application/json")
    .param("Proxy-Authorization", s"Basic $proxyAuth")
    .proxy(proxyHost, 8080).asString
  val responseForward: HttpResponse[String] = requestForward
  val forwardJson = (responseForward.body).parseJson
  println(forwardJson)
  val revealed = json.convertTo[Map[String, JsValue]]
  val revealedSecret = revealed.get("json").get

}
