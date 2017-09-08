using System;
using System.Net.Http;
using System.Threading.Tasks;
using Newtonsoft.Json.Linq;
using System.Net;

namespace example
{
    public static class Example
    {
        private static readonly string Username = Environment.GetEnvironmentVariable("FORWARD_HTTP_PROXY_USERNAME");
        private static readonly string Password = Environment.GetEnvironmentVariable("FORWARD_HTTP_PROXY_PASSWORD");
        private static readonly string ForwardProxy = Environment.GetEnvironmentVariable("FORWARD_HTTP_PROXY_HOST");
        private static readonly string ReverseProxy = Environment.GetEnvironmentVariable("REVERSE_HTTP_PROXY_HOST");

        public static async Task<string> RedactViaReverseProxy(String originalData)
        {
            var client = new HttpClient();
            var response = await client.PostAsync(String.Format("https://{0}/post", ReverseProxy), new StringContent(originalData));
            var responseBody = await response.Content.ReadAsStringAsync();
            return JObject.Parse(responseBody)["data"].ToObject<String>();
        }

        public static async Task<string> RevealViaForwardProxy(string redactData)
        {
            var credentials = new NetworkCredential(Username, Password);
            var proxy = new WebProxy(string.Format("http://{0}", ForwardProxy), false)
            {
                UseDefaultCredentials = false,
                Credentials = credentials,
            };

            var handler = new HttpClientHandler()
            {
                Proxy = proxy,
                PreAuthenticate = true,
                UseDefaultCredentials = false,
            };

            var client = new HttpClient(handler);
            var response = await client.PostAsync("https://httpbin.verygoodsecurity.io/post", new StringContent(redactData));
            var responseBody = await response.Content.ReadAsStringAsync();
            return JObject.Parse(responseBody)["data"].ToObject<string>();
        }
    }
}
