<?php

require_once 'vendor/autoload.php';


$faker = Faker\Factory::create();

$username = getenv("FORWARD_HTTP_PROXY_USERNAME");
$password = getenv("FORWARD_HTTP_PROXY_PASSWORD");
$forward_proxy = getenv("FORWARD_HTTP_PROXY_HOST");
$reverse_proxy = getenv("REVERSE_HTTP_PROXY_HOST");
echo $username, $password, $forward_proxy, $reverse_proxy . PHP_EOL;

$client = new GuzzleHttp\Client();

function random_json()
{
    global $faker;

    $text = $faker->text;
    return '{"secret":"' . $text . '"}';
}


function redact_via_reverse_proxy($original_data)
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


function reveal_via_forward_proxy($redact_data)
{
    global $forward_proxy, $username, $password, $client;
    $proxies = explode(":", $forward_proxy);

    $response = $client->post("https://httpbin.verygoodsecurity.io/post", [
            'headers' => [
                "Content-type" => "application/json",
                "VGS-Log-Request" => "all"
            ],
            'auth' => [$username, $password],
            'curl' => [
                CURLOPT_PROXY => $proxies[0],
                CURLOPT_PROXYPORT => (int)$proxies[1],
                CURLOPT_PROXYUSERPWD => "$username:$password",
            ],
            'cert' => './cert.pem',
            'body' => $redact_data,
            'debug' => true
        ]
    );
    return json_decode($response->getBody(), true)['data'];
}

function main()
{
    $original_value = random_json();
    Print $original_value . PHP_EOL;

    $redact_value = redact_via_reverse_proxy($original_value);
    Print $redact_value . PHP_EOL;
    assert($original_value != $redact_value);

    $revealed_value = reveal_via_forward_proxy($redact_value);
    Print $revealed_value . PHP_EOL;

    assert($revealed_value == $original_value);
    Print "Test passed" . PHP_EOL;
}

main();