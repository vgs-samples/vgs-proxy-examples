<?php

require_once 'vendor/autoload.php';


//$headers = array(
//    'Accept' => 'application/json'
//);
//$query = array('foo' => 'hello', 'bar' => 'world');
//
//$response = Unirest\Request::post('http://mockbin.com/request', $headers, $query);
//
//Print($response->code);        // HTTP Status code

$faker = Faker\Factory::create();

$username = "US6JXwGY219cUNWJ8jotyAMw";
$password = "81d369b2-8c35-4ba4-9748-24315b15abaa";
$forward_proxy = "tntbeiahp7q.SANDBOX.verygoodproxy.com:8080";
$reverse_proxy = "tntbeiahp7q.SANDBOX.verygoodproxy.com";
echo $username, $password, $forward_proxy, $reverse_proxy . PHP_EOL;

$client = new GuzzleHttp\Client();

function random_json()
{
    global $faker;

    $text = $faker->text;
    return '{"secret":"' . $text . '"}';
}


function tokenize_via_reverse_proxy($original_data)
{
    global $reverse_proxy, $client;

    $response = $client->post("https://$reverse_proxy/post", [
            'headers' => [
                "Content-type" => "application/json",
                "VGS-Log-Request" => "all"
            ],
            'body' => $original_data
        ]
    );

    return json_decode($response->getBody(), true)['data'];
}


function reveal_via_forward_proxy($tokenized_data)
{
    global $forward_proxy, $username, $password, $client;
    $proxies = explode(":", $forward_proxy);

    $response = $client->post("https://httpbin.verygoodsecurity.io/post", [
            'headers' => [
                "Content-type" => "application/json",
                "VGS-Log-Request" => "all"
            ],
//            'auth' => [$username, $password],
            'curl' => [
                CURLOPT_PROXY => $proxies[0],
                CURLOPT_PROXYPORT => (int)$proxies[1],
                CURLOPT_PROXYUSERPWD => "$username:$password",
            ],
            'ssl_key' => "/Users/phuonghqh/Documents/working/vault-examples/php/cert.pem",
            'body' => $tokenized_data
        ]
    );
    return json_decode($response->getBody(), true)['data'];
//    curl_setopt($ch, CURLOPT_SSLCERT, '/path/to/cert/client-cert.pem');

//    Unirest\Request::curlOpt(CURLOPT_SSLCERT, "/Users/phuonghqh/Documents/working/vault-examples/php/cert.pem");
//    Unirest\Request::proxy($proxies[0], $proxies[1]);
//    Unirest\Request::proxyAuth($username, $password, CURLAUTH_BASIC);
//
//    $response = Unirest\Request::post(
//        "https://httpbin.verygoodsecurity.io/post",
//        array(
//            "Content-type" => "application/json",
//            "VGS-Log-Request" => "all"
//        ),
//        $tokenized_data
//    );
//    return $response->body->data;
}

function main()
{
    $original_value = random_json();
    Print $original_value . PHP_EOL;

    $tokenized_value = tokenize_via_reverse_proxy($original_value);
    Print $tokenized_value . PHP_EOL;
    assert($original_value != $tokenized_value);


    $revealed_value = reveal_via_forward_proxy($tokenized_value);
    Print $revealed_value . PHP_EOL;

    assert($revealed_value == $original_value);
    Print "Test passed" . PHP_EOL;
}

main();