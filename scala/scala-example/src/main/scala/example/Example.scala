import faker._
import scalaj.http._
import spray.json._
import DefaultJsonProtocol._
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8


object Proxies extends App {

  val username = sys.env("FORWARD_HTTP_PROXY_USERNAME")
  val password = sys.env("FORWARD_HTTP_PROXY_PASSWORD")
  val reverseProxy = sys.env("REVERSE_HTTP_PROXY_HOST")
  val forwardProxy = sys.env("FORWARD_HTTP_PROXY_HOST")

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

  //Forward forwardProxy
  val url: String = "https://httpbin.verygoodsecurity.io/post"
  val proxyHost= s"https://$username:$password@$forwardProxy"
  val requestForward = Http(url).proxy(proxyHost, 8080).option(HttpOptions.allowUnsafeSSL).postData(redactedSecret).header("Content-type", "application/json")
  val responseForward: HttpResponse[String] = requestForward.asString
  val forward_json = (responseForward.body).parseJson
  val revealed = json.convertTo[Map[String, JsValue]]
  val revealedSecret = revealed.get("json").get

}
