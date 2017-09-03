const unirest = require('unirest');
const faker = require('faker');
const assert = require('assert');
var fs = require('fs');

const username = process.env.FORWARD_HTTP_PROXY_USERNAME;
const password = process.env.FORWARD_HTTP_PROXY_PASSWORD;
const forward_proxy = process.env.FORWARD_HTTP_PROXY_HOST;
const reverse_proxy = process.env.REVERSE_HTTP_PROXY_HOST;
console.log(username, password, forward_proxy, reverse_proxy);


function random_json() {
  return JSON.stringify({
    "secret": faker.random.words()
  });
}


function tokenize_via_reverse_proxy(original_value) {
  return new Promise((rs, rj) => {
    unirest.post(`https://${reverse_proxy}/post`)
      .headers({
        'Content-Type': 'application/json',
        "VGS-Log-Request": "all"
      })
      .send(original_value)
      .end(function (response) {
        rs(response.body.data);
      });
  });
}

function reveal_via_forward_proxy(tokenized_value) {
  return new Promise((rs, rj) => {
    const proxy = `http://${username}:${password}@${forward_proxy}`;
    let post = unirest.post('https://httpbin.verygoodsecurity.io/post')
      .headers({
        'Content-Type': 'application/json',
        "VGS-Log-Request": "all"
      })
      .proxy(proxy);

    post.options.ca = fs.readFileSync('cert.pem');

    post.send(tokenized_value)
      .end(function (response) {
        rs(response.body.data);
      });
  });
}


function main() {
  const original_value = random_json();
  console.log(original_value);

  let tokenized_value, revealed_value;
  tokenize_via_reverse_proxy(original_value)
    .then((v) => {
      tokenized_value = v;
      console.log(tokenized_value);

      assert(original_value !== tokenized_value);
      return reveal_via_forward_proxy(tokenized_value);
    })
    .then((v) => {
      revealed_value = v;
      console.log(revealed_value);
      assert(original_value === revealed_value);
    })
    .then(() => {
      console.log("Test passed")
    });
}

main();
