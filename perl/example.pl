use LWP::UserAgent;
use IO::Socket::SSL;

$ENV{'PERL_LWP_SSL_VERIFY_HOSTNAME'} = 0;

$ENV{HTTPS_DEBUG} = 1;

IO::Socket::SSL::set_ctx_defaults(
     SSL_verifycn_scheme => 'www',
     SSL_verify_mode => 0,
);

my $ua = LWP::UserAgent->new;

my $server_endpoint = "https://httpbin.verygoodsecurity.io/post";

# set custom HTTP request header fields
my $req = HTTP::Request->new(POST => $server_endpoint);
$req->header('content-type' => 'application/json');
$req->header('super-cool-token' => 'some-token');

# add POST data to HTTP request body
my $post_data = '{ "CCN": "" }';
$req->content($post_data);

my $resp = $ua->request($req);
if ($resp->is_success) {
    my $message = $resp->decoded_content;
    print "Received reply: $message\n";
}
else {
    print "HTTP POST error code: ", $resp->code, "\n";
    print "HTTP POST error message: ", $resp->message, "\n";
}

