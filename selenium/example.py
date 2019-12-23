import os
import json

from faker import Factory
from selenium.webdriver.support.wait import WebDriverWait

from selenium import webdriver
from selenium.webdriver.chrome.options import Options
import zipfile

diver_executable_path = 'http://selenium-chrome:4444/wd/hub'

username = os.environ.get('FORWARD_HTTP_PROXY_USERNAME')
password = os.environ.get('FORWARD_HTTP_PROXY_PASSWORD')
forward_proxy = os.environ.get('FORWARD_HTTP_PROXY_HOST')
reverse_proxy = os.environ.get('REVERSE_HTTP_PROXY_HOST')
print(username, password, forward_proxy, reverse_proxy)

fake = Factory.create()
plugin_file = 'proxy_auth_plugin.zip'


def random_json():
    data = {"secret": fake.text()}
    return json.dumps(data, separators=(',', ':'))


def create_chrome_driver(host=None, port=None):
    print('create_chrome_driver')
    print(host)
    print(port)
    co = Options()
    if host is not None and port is not None:
        manifest_json = read_script('./proxy_ext/manifest.json')
        print('manifest_json begin')
        print(manifest_json)
        print('manifest_json end')
        background_js = read_script('./proxy_ext/background.js', host=host, port=port, user=username, password=password)
        print('background_js begin')
        print(background_js)
        print('background_js end')
        with zipfile.ZipFile(plugin_file, 'w') as zp:
            zp.writestr("manifest.json", manifest_json)
            zp.writestr("background.js", background_js)

        co.add_extension(plugin_file)
    driver = webdriver.Remote(
        command_executor=diver_executable_path,
        desired_capabilities=co.to_capabilities()
    )
    return driver


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
    data = chrome_post(proxy=reverse_proxy, script=script)
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
