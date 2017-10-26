Imports System
Imports System.Net.Http
Imports System.Threading.Tasks
Imports Newtonsoft.Json.Linq
Imports System.Net

Namespace example

    Public Class Example

        Private Shared Username As String = Environment.GetEnvironmentVariable("FORWARD_HTTP_PROXY_USERNAME")

        Private Shared Password As String = Environment.GetEnvironmentVariable("FORWARD_HTTP_PROXY_PASSWORD")

        Private Shared ForwardProxy As String = Environment.GetEnvironmentVariable("FORWARD_HTTP_PROXY_HOST")

        Private Shared ReverseProxy As String = Environment.GetEnvironmentVariable("REVERSE_HTTP_PROXY_HOST")

        Public Shared Async Function RedactViaReverseProxy(ByVal originalData As String) As Task(Of String)
            Dim client = New HttpClient
            Dim response = Await client.PostAsync(String.Format("https://{0}/post", ReverseProxy), New StringContent(originalData))
            Dim responseBody = Await response.Content.ReadAsStringAsync
            Return JObject.Parse(responseBody)("data").ToObject(Of String)
        End Function

        Public Shared Async Function RevealViaForwardProxy(ByVal redactData As String) As Task(Of String)
            Dim credentials = New NetworkCredential(Username, Password)
            Dim proxy = New WebProxy(String.Format("http://{0}", ForwardProxy), False)
            Dim handler = New HttpClientHandler
            Dim client = New HttpClient(handler)
            Dim response = Await client.PostAsync("https://httpbin.verygoodsecurity.io/post", New StringContent(redactData))
            Dim responseBody = Await response.Content.ReadAsStringAsync
            Return JObject.Parse(responseBody)("data").ToObject(Of String)
        End Function
    End Class
End Namespace
