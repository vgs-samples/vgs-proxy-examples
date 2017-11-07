Imports System
Imports System.Net.Http
Imports System.Threading.Tasks
Imports Newtonsoft.Json.Linq
Imports System.Net



Namespace example

    Public Class Example

        Public Shared username As String = Environment.GetEnvironmentVariable("FORWARD_HTTP_PROXY_USERNAME")

        Public Shared password As String = Environment.GetEnvironmentVariable("FORWARD_HTTP_PROXY_PASSWORD")

        Public Shared forwardProxy As String = Environment.GetEnvironmentVariable("FORWARD_HTTP_PROXY_HOST")

        Public Shared reverseProxy As String = Environment.GetEnvironmentVariable("REVERSE_HTTP_PROXY_HOST")

        Public Shared Async Function RedactViaReverseProxy(ByVal originalData As String) As Task(Of String)
            Dim client = New HttpClient
            client.BaseAddress = New Uri($"https://{reverseProxy}")
            Dim response = Await client.PostAsync("/post", New StringContent(originalData))
            Dim responseBody = Await response.Content.ReadAsStringAsync
            Return JObject.Parse(responseBody)("data").ToObject(Of String)
        End Function

        Public Shared Async Function RevealViaForwardProxy(ByVal redactData As String) As Task(Of String)
            Dim proxy = New WebProxy($"http://{forwardProxy}")
            Dim credentials = New NetworkCredential(username, password)
            proxy.Credentials = credentials
            Dim clienthandler = New HttpClientHandler()
            clienthandler.Proxy = proxy
            Dim client As New HttpClient(clienthandler)
            client.BaseAddress = New Uri("https://httpbin.verygoodsecurity.io")
            Dim response = Await client.PostAsync("/post", New StringContent(redactData))
            Dim responseBody = Await response.Content.ReadAsStringAsync
            Return JObject.Parse(responseBody)("data").ToObject(Of String)
        End Function
    End Class
End Namespace
