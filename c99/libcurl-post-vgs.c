/*
 * 2018 gjyoung1974@gmail.com
 *
 * VGS C libcurl  example
 *
 * This example demonstrates sharing sensitive data with a 3rd party processor API
 * 1. Collect the tokenized form of a Primary Account Number (PAN) from our application's backend database
 * 2. Securely share it with an upstream 3rd party processor's API
 * 3. Observe that we have not had to handle any sensitive data within our environment
 *
 *      The handling of sensitive data is covered by the VGS proxy on behalf by simply setting up a forward proxy
 *      VGS Proxy details are in libcurl by:
 *
 *      CURLOPT_PROXY <your vault's proxy URL>
 *      CURLOPT_PROXYUSERNAME <your vault's proxy username>
 *      CURLOPT_PROXYPASSWORD <your vault's proxy password>
 */

#include "libcurl-post-vgs.h"
#include <stdio.h>
#include <stdlib.h>
#include <curl/curl.h>

void post_with_libcurl(void)
{
    CURL *curl;
    CURLcode res;
    struct curl_slist *headers = NULL;

    // Get our system wide env variables for proxy settings
    char* username = (char*) getenv("FORWARD_HTTP_PROXY_USERNAME");
    char* password = (char*) getenv("FORWARD_HTTP_PROXY_PASSWORD");
    char* forward_proxy = (char*) getenv("FORWARD_HTTP_PROXY_HOST");
    //const char* reverse_proxy = getenv("REVERSE_HTTP_PROXY_HOST");

    //Set the properties for our Vault's foward proxy details:
    //char *vgs_foward_proxy_url = "http://tntlvnzzqsz.SANDBOX.verygoodproxy.com:8080";
    //char *vgs_foward_proxy_username = "US2dihmmMZD8BGsQj2yKgjZk";
    //char *vgs_forward_proxy_password = "6e478e95-52ed-4c3b-9493-3aefa7f9137a";

    //what we want to post:
    char *json_body = "{\"CCN\": \"4012882363931881\"}"; //This is the FPE encrypted test Visa PAN from our backend database
    //ClearText vaulue sent to processor will be: 4012888888881881
    // FPE or Format preserving encryption: https://en.wikipedia.org/wiki/Format-preserving_encryption

    //specify any reuired headers for our message
    headers = curl_slist_append(headers, "Shoesize: 11"); // we can specify custom headers as needed
    headers = curl_slist_append(headers, "Content-type: application/json"); //set the mime=type as JSON

    curl_global_init(CURL_GLOBAL_ALL);

    /* get a curl handle */
    curl = curl_easy_init();
    if(curl) {
        /* First set the URL that is about to receive our POST. This URL can
           just as well be a https:// URL if that is what should receive the
           data. */
	curl_easy_setopt(curl, CURLOPT_VERBOSE, 1L);	
        curl_easy_setopt(curl, CURLOPT_URL, "https://httpbin.verygoodsecurity.io/post"); //Third Party API to share sensitive data with

        curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0); //TODO: fix this never blindly accept server certificates
        curl_easy_setopt(curl, CURLOPT_PROXY, forward_proxy);
        curl_easy_setopt(curl, CURLOPT_PROXYUSERNAME, username);
        curl_easy_setopt(curl, CURLOPT_PROXYPASSWORD, password);

        /* Now specify the POST data */
        curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);
        curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, 27);
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, json_body); // our Json data structure

        /* Perform the request, res will get the return code */
        res = curl_easy_perform(curl);
        /* Check for errors */
        if(res != CURLE_OK)
            fprintf(stderr, "curl_easy_perform() failed: %s\n",
                    curl_easy_strerror(res));

        /* always cleanup */
        curl_easy_cleanup(curl);
    }
    curl_global_cleanup();

}
