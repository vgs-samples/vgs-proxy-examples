import faker._
import dispatch._, Defaults._
import spray.json._
import DefaultJsonProtocol._


object Proxies extends App {

  def username = sys.env("FORWARD_HTTP_PROXY_USERNAME")
  def password = sys.env("FORWARD_HTTP_PROXY_PASSWORD")
  def reverseProxy = sys.env("FORWARD_HTTP_PROXY_HOST")
  def forwardProxy = sys.env("REVERSE_HTTP_PROXY_HOST")

  val fake = Name.name

  def executeReverseProxy(reverseProxy: String) : String ={
    val request = url(s"https://$reverseProxy")
    val myRequestAsPost = request.POST

    val builtRequest = myRequestAsPost.addQueryParameter("secret", s"$fake")
      .addQueryParameter("Content-type", "application/json")

    val content = Http(builtRequest)
    content onSuccess {
      case x if x.getStatusCode() == 200 =>
        return (x.getResponseBody).parseJson.convertTo[String]
      case y =>
        println("Failed with status code" + y.getStatusCode())
    }

    content onFailure {
      case x =>
       println("Failed but"); println(x.getMessage)
    }
    readLine()
   }
}
