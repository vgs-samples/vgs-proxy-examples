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

  val fake = Name.name
  val reverse_url: String = (s"https://$reverseProxy")
  val data = s"""{"secret" : "$fake"}"""
  val request = Http(reverse_url).header("Content-type", "application/json").postData(data)
  val response: HttpResponse[String] = request.asString

  println(response)
}
