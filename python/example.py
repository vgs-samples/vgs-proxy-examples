#!/usr/bin/env python3

import requests
import os


def enrichment():
    post_url = 'https://httpbin.verygoodsecurity.io/post'
    username = os.environ.get('FORWARD_HTTP_PROXY_USERNAME')
    password = os.environ.get('FORWARD_HTTP_PROXY_PASSWORD')
    forward_proxy = os.environ.get('FORWARD_HTTP_PROXY_HOST')
    data = os.environ.get('DATA')
    proxies = {
        "https": f"https://{username}:{password}@{forward_proxy}"
    }

    print(username, password, forward_proxy, post_url, data)

    r = requests.post(
        post_url,
        data=data,
        headers={"Content-type": "application/json", "VGS-Log-Request": "all"},
        proxies=proxies,
        verify=False
    )
    print(r.status_code)
    print(r.text)


def main():
    enrichment()


if __name__ == '__main__':
    main()
