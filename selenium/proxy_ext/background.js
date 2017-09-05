var config = {
    mode: "fixed_servers",
    rules: {
        singleProxy: {
            scheme: "http",
            host: "%(host)s",
            port: parseInt('%(port)s')
        },
        bypassList: ["foobar.com"]
    }
};

chrome.proxy.settings.set({value: config, scope: "regular"}, function () {
});

function callbackFn(details) {
    return {
        authCredentials: {
            username: "%(user)s",
            password: "%(password)s"
        }
    };
}

chrome.webRequest.onAuthRequired.addListener(
    callbackFn,
    {urls: ["<all_urls>"]},
    ['blocking']
);