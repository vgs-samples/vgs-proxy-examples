Imports System
Imports System.Net.Http
Imports System.Threading.Tasks
Imports Newtonsoft.Json.Linq
Imports System.Net
Imports System.Security.Cryptography.X509Certificates
Imports System.Net.Security

Namespace example

    Public Class Example

        Public Shared username As String = Environment.GetEnvironmentVariable("FORWARD_HTTP_PROXY_USERNAME")

        Public Shared password As String = Environment.GetEnvironmentVariable("FORWARD_HTTP_PROXY_PASSWORD")

        Public Shared forwardProxy As String = Environment.GetEnvironmentVariable("FORWARD_HTTP_PROXY_HOST")

        Public Shared reverseProxy As String = Environment.GetEnvironmentVariable("REVERSE_HTTP_PROXY_HOST")

        Public Shared Async Function RedactViaReverseProxy(ByVal originalData As String) As Task(Of String)
            Dim client = New HttpClient
            Dim response As HttpResponseMessage = Await client.PostAsync("https://tntdkkudcxl.SANDBOX.verygoodproxy.com/post", New StringContent(originalData))
            Dim responseBody = Await response.Content.ReadAsStringAsync
            Return JObject.Parse(responseBody)("data").ToObject(Of String)
        End Function

        Public Shared Async Function RevealViaForwardProxy(ByVal redactData As String) As Task(Of String)
            Dim credentials = New NetworkCredential(username, password)
            Dim proxy = New WebProxy("https://tntdkkudcxl.SANDBOX.verygoodproxy.com:8080")
            Dim x509 = X509Certificate.CreateFromCertFile("c:/Users/User/vgs-proxy-examples/vb/cert.pem")
            proxy.Credentials = New NetworkCredential(username, password)

            Dim requestHandler = New WebRequestHandler()
            requestHandler.ClientCertificates.Add(x509)
            requestHandler.Proxy = proxy
            requestHandler.ServerCertificateValidationCallback =
              Function(se As Object,
              cert As System.Security.Cryptography.X509Certificates.X509Certificate,
              chain As System.Security.Cryptography.X509Certificates.X509Chain,
              sslerror As System.Net.Security.SslPolicyErrors) True
            Dim client = New HttpClient(requestHandler)
            client.BaseAddress = New Uri("https://httpbin.verygoodsecurity.io")
            Dim response = Await client.PostAsync("/post", New StringContent(redactData))




            'Dim handler = New HttpClientHandler
            'handler.Credentials = credentials
            'handler.Proxy = proxy
            'Dim client = New HttpWebRequest
            'client.ClientCertificates.Add(x509)
            '  Dim response = Await client.PostAsync("https://httpbin.verygoodsecurity.io/post", New StringContent(redactData))
            Dim responseBody = Await response.Content.ReadAsStringAsync
            Return JObject.Parse(responseBody)("data").ToObject(Of String)
        End Function
    End Class
End Namespace
