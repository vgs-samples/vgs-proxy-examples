import requests

r = requests.post(
    'https://httpbin.verygoodsecurity.io/post',
    data={"secret": "foo2"},
    headers={
        "Content-type": "application/json",
        "VGS-Log-Request": "all"
    },
    proxies={
        "http": "http://USnrfoj6Jk29uqVnFNmeH2r4:e8a1e647-0c4a-4524-8305-da88f1c3ac59@tnta6xo74mx.SANDBOX.verygoodproxy.com:8080"
    },
    verify=False
)

print r.status_code
print r.text