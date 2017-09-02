import platform
import os
import json

from faker import Factory
from selenium import webdriver
from selenium.webdriver import DesiredCapabilities
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.common.proxy import Proxy, ProxyType

from selenium import webdriver
from selenium.webdriver.common.proxy import Proxy
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities

from selenium import webdriver
from selenium.webdriver.chrome.options import Options
import zipfile

# from selenium.webdriver import DesiredCapabilities
# from selenium.webdriver.common.by import By

# proxy_host = "tnthm5gnofn.SANDBOX.verygoodproxy.io"
# proxy_port = 8080
# proxy_username = "USoPJ3oKKffD17FjC7FTBN5m"
# proxy_password = "611898ed-1995-4d52-aa8b-bc439f616a7e"
# SELENIUM_HUB_URL = "http://selenium-firefox:4444/wd/hub"

# capabilities = webdriver.DesiredCapabilities.CHROME

diver_executable_path = ''
is_mac = platform.mac_ver()[0] is not ''
is_linux = platform.linux_distribution()[0] is not ''
if is_mac:
    diver_executable_path = './chromedriver_mac'
elif is_linux:
    diver_executable_path = './chromedriver_linux'

if not diver_executable_path: raise Exception('Unsupported OS')
print("Using driver %s" % diver_executable_path)

username = 'USgq5SCMRXVWZQXPViM6Dxyo'
password = 'e73395db-1060-403b-a284-0cd4c9d25933'
forward_proxy = 'tntg0ztxcnn.SANDBOX.verygoodproxy.com:8080'
reverse_proxy = 'tntg0ztxcnn.SANDBOX.verygoodproxy.com'
print(username, password, forward_proxy, reverse_proxy)

fake = Factory.create()
plugin_file = 'proxy_auth_plugin.zip'


def random_json():
    data = {"secret": fake.text()}
    return json.dumps(data, separators=(',', ':'))


def create_chrome_driver(host=None, port=None):
    co = Options()
    if host is not None and port is not None:
        manifest_json = read_script('./proxy_ext/manifest.json')
        background_js = read_script('./proxy_ext/background.js', host=host, port=port, user=username, password=password)
        with zipfile.ZipFile(plugin_file, 'w') as zp:
            zp.writestr("manifest.json", manifest_json)
            zp.writestr("background.js", background_js)

        co.add_extension(plugin_file)
    return webdriver.Chrome(executable_path=diver_executable_path, chrome_options=co)


def chrome_post(proxy=None, script=None):
    host = None
    port = None
    if proxy is not None:
        host = proxy.split(":")[0]
        port = proxy.split(":")[1]

    driver = create_chrome_driver(host=host, port=port)
    data = None
    if script is not None:
        driver.execute_script(script=script)
        wait = WebDriverWait(driver=driver, timeout=10)
        data = wait.until(method=lambda dr: dr.find_element_by_name('data')).text
    driver.quit()
    return data


def read_script(filename=None, **kwargs):
    script_file = open(filename, 'r')
    text = script_file.read() % kwargs
    script_file.close()
    return text


def tokenize_via_reverse_proxy(original_data):
    script = read_script('./post_token.js', url='https://{}/post'.format(reverse_proxy), data=original_data)
    data = chrome_post(script=script)
    return json.loads(data)


def reveal_via_forward_proxy(tokenized_data):
    script = read_script(
        './post_token.js',
        url='https://httpbin.verygoodsecurity.io/post',
        data=tokenized_data
    )
    data = chrome_post(proxy=forward_proxy, script=script)
    return json.loads(data)


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

    if os.path.exists(plugin_file):  # remove proxy_ext file created
        os.remove(plugin_file)


if __name__ == '__main__':
    main()
