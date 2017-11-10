package example

import faker._
import dispatch._, Defaults._


object Proxies extends App {

  def username = sys.env("FORWARD_HTTP_PROXY_USERNAME")
  def password = sys.env("FORWARD_HTTP_PROXY_PASSWORD")
  def reverseProxy = sys.env("FORWARD_HTTP_PROXY_HOST")
  def forwardProxy = sys.env("REVERSE_HTTP_PROXY_HOST")

  val fake = Faker::ChuckNorris.fact

  def executeReverseProxy(reverseProxy: String) : String ={
    val params = Map("secret" -> s"$fake")
    val mySecureHost = host(s"$reverseProxy").secure
    val headers = Map("Content-type" -> "application/json")
    def myPost = executeReverseProxy.POST

  }

}
