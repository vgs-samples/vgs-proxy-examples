import os
import requests
import json
from faker import Factory

username = os.environ.get('FORWARD_HTTP_PROXY_USERNAME')
password = os.environ.get('FORWARD_HTTP_PROXY_PASSWORD')
forward_proxy = os.environ.get('FORWARD_HTTP_PROXY_HOST')
reverse_proxy = os.environ.get('REVERSE_HTTP_PROXY_HOST')
print(username, password, forward_proxy, reverse_proxy)

fake = Factory.create()


def random_json():
    data = {
        "secret": fake.text()
    }
    # data = { # this data failed https://getverygood.slack.com/archives/C6KHUMMEX/p1503202918000040
    #     "secret": {
    #         "name": fake.name(),
    #         "address": fake.address(),
    #         "text": fake.text()
    #     }
    # }
    # data = fake.text()
    return json.dumps(data, separators=(',', ':'))


def tokenize_via_reverse_proxy(original_data):
    r = requests.post(
        'https://{}/post'.format(reverse_proxy),
        data=original_data,
        headers={"Content-type": "application/json", "VGS-Log-Request": "all"}
    )
    assert r.status_code == 200
    return r.json()['data']


def reveal_via_forward_proxy(tokenized_data):
    r = requests.post(
        'https://echo.apps.verygood.systems/post',
        data=tokenized_data,
        headers={"Content-type": "application/json", "VGS-Log-Request": "all"},
        proxies={
            "https": "https://{}:{}@{}".format(username, password, forward_proxy),
            "http": "https://{}:{}@{}".format(username, password, forward_proxy)
        }
    )
    assert r.status_code == 200
    return r.json()['data']


def main():
    original_value = random_json()
    print(original_value)

    tokenized_value = tokenize_via_reverse_proxy(original_value)
    print(tokenized_value)
    assert original_value != tokenized_value

    revealed_value = reveal_via_forward_proxy(tokenized_value)
    print(revealed_value)

    assert original_value == revealed_value
    print("Test passed")


if __name__ == '__main__':
    main()
