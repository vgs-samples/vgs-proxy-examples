/*
 * 2018 gjyoung1974@gmail.com
 *
 * VGS C libcurl  example
 *
 * This example demonstrates protecting sensitive data before persisting into our applications backend via API call
 * This demo leverages OpenSSL, to demonsrate a lower-level integration
 *
 * We could use any library that supports higher level HTTP1.1 VERBS
 * Here we are just writing a buffer to a socket over TLS 1.2
 */

#include "openssl-post-vgs.h"
#include <stdio.h>
#include <openssl/ssl.h>
#include <openssl/err.h>
#include <openssl/bio.h>
#include <string.h>
#include <stdlib.h>

#define HOST "tntlvnzzqsz.SANDBOX.verygoodproxy.com"//get this from the VGS Dashboard
#define PORT "443"

void post_with_openssl(void) {

        // Get our system wide env variables for proxy settings
    char* username = (char*) getenv("FORWARD_HTTP_PROXY_USERNAME");
    char* password = (char*) getenv("FORWARD_HTTP_PROXY_PASSWORD");
    char* proxy_host = (char*) getenv("FORWARD_HTTP_PROXY_HOST");

    //  Use a bio, ssl and ssl context:
    BIO* bio;
    SSL* ssl;
    SSL_CTX* ctx;
    const SSL_METHOD* method = TLSv1_2_method();
    //   Initialize OpenSSL
    SSL_library_init();

    //  Create a new SSL_CTX object as a framework to establish TLS/SSL transport
    ctx = SSL_CTX_new(method);

    //  -> Error checks
    if (ctx == NULL)
    {
        printf("Ctx is null\n");
    }

    //   Creates a new BIO chain consisting of an SSL BIO
    bio = BIO_new_ssl_connect(ctx);

    //  set the VGS Proxy URL to perform a rewrite of our POST method:
    BIO_set_conn_hostname(bio, HOST ":" PORT);

    //   Attempts to connect the supplied BIO
    if(BIO_do_connect(bio) <= 0)
    {
        printf("Failed connection\n");
        //return 1;
    }
    else
    {
        printf("Connected\n");
    }

    //
    //  The bare minimum to make a HTTP request.
    //
    char* write_buf = "POST /post HTTP/1.1\r\n" //set the HTTP 1.1 method and API endpoint
                      "Host: " HOST "\r\n" // set the hostname

                      //set some headers
                      "Content-type: application/json\r\n"
                      "User-Agent: awesome-client/1.2/client\r\n"
                      "Content-Length: 27\r\n"
                      "\r\n" // seperate headers from body with a blank line

                      //send a JSON Structure
                      "{\"CCN\": \"4012888888881881\"}\r\n"

                      //Close the connection
                      "Connection: close\r\n"
                      "\r\n"; //send a new line to force an update

    //   Attempts to write len bytes from buf to BIO
    if(BIO_write(bio, write_buf, strlen(write_buf)) <= 0)
    {
        //  Handle failed writes here
        if(!BIO_should_retry(bio))
        {
            // should we retry?
        }

        //  -> Lets us know about the failed writes
        printf("Failed write\n");
    }

    //  Collect the response from the server
    int size;
    char buf[1024];

    //  Do something with the repsonse
    for(;;)
    {
        //  Get chunks of the response 1023 at the time.
        size = BIO_read(bio, buf, 1023);

        //  If no more data, then we are done..
        if(size <= 0)
        {
            break;
        }

        //  Terminate the string with a 0, to let know C when the string ends.
        buf[size] = 0;

        //  ->  Print out the response
        printf("%s", buf); //TODO: do something usefull like parse the JSON
    }

    //  Clean up!
    BIO_free_all(bio);
    SSL_CTX_free(ctx);

}
