/***************************************************************************
 * VGS Proxy C99 style integration - with OpenSSL and libcurl
 * 2018 gjyoung1974
 */

#include <stdio.h>
#include "openssl-post-vgs.h" //declare our OpenSSL demo client
#include "libcurl-post-vgs.h" //declare our libcurl demo client

int main() {
    //post_with_openssl(); //demo VGS integration via OpenSSL
    printf("\nPosting to our api endpoint via VGS proxyt\n\n"); 
    post_with_libcurl(); //demo VGS integration via libcurl
    return 0;
}
