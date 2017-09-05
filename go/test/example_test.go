package test

import (
  "fmt"
  "testing"
  "gopkg.in/stretchr/testify.v1/assert"
  "github.com/icrowley/fake"
  "gopkg.in/resty.v0"
  "os"
)

var username = os.Getenv("FORWARD_HTTP_PROXY_USERNAME")
var password = os.Getenv("FORWARD_HTTP_PROXY_PASSWORD")
var forward_proxy = os.Getenv("FORWARD_HTTP_PROXY_HOST")
var reverse_proxy = os.Getenv("REVERSE_HTTP_PROXY_HOST")

func random_json() string {
  return fmt.Sprintf(`{"secret":"%s"}`, fake.FullName())
}

func tokenize_via_reverse_proxy(original_data string) string {
  resp, _ := resty.R().
    SetHeader("Content-type", "application/json").
    SetHeader("VGS-Log-Request", "all").
    SetBody(original_data).
    SetResult(map[string]string{}).
    Post(fmt.Sprintf("https://%s/post", reverse_proxy))

  result := *resp.Result().(*map[string]string)
  return fmt.Sprint(result["data"])
}

func reveal_via_forward_proxy(tokenized_data string) string {
  url := fmt.Sprintf("https://%s:%s@%s", username, password, forward_proxy)
  resp, _ := resty.
  SetProxy(url).
    SetRootCertificate("./cert.pem").
    R().
    SetHeader("Content-type", "application/json").
    SetHeader("VGS-Log-Request", "all").
    SetBody(tokenized_data).
  //SetResult(&Data{}).
    SetResult(map[string]string{}).
    Post("https://httpbin.verygoodsecurity.io/post")

  result := *resp.Result().(*map[string]string)
  return fmt.Sprint(result["data"])
}

func TestIt(t *testing.T) {
  original_value := random_json()
  t.Log(original_value)

  tokenized_value := tokenize_via_reverse_proxy(original_value)
  t.Log(tokenized_value)
  assert.NotEqual(t, original_value, tokenized_value)

  revealed_value := reveal_via_forward_proxy(tokenized_value)
  t.Log(revealed_value)

  assert.Equal(t, original_value, revealed_value)
  t.Log("Test passed")
}
