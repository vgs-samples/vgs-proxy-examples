#[macro_use]
extern crate hyper;
extern crate hyper_tls;
extern crate hyper_proxy;
extern crate futures;
extern crate tokio_core;
extern crate http;
extern crate envy;
extern crate reqwest;
#[macro_use]
extern crate serde_derive;
extern crate serde_json;
#[macro_use]
extern crate fake;
extern crate base64;

use std::io::{self, Read};
use std::fs::File;
use serde_json::Value;
use reqwest::header::{ContentType};
use hyper::{Chunk, Client, Request, Method, Uri};
use hyper::client::HttpConnector;
use hyper::header::Basic;
use futures::{Future, Stream};
use hyper_proxy::{Proxy, ProxyConnector, Intercept};
use tokio_core::reactor::Core;


fn redact_via_reverse_proxy(original_data: String, reverse_http_proxy_host: String) -> String{
    let client = reqwest::Client::new();
    let url = format!("https://{}/post", reverse_http_proxy_host);
    let mut res = client.post(&url)
        .header(ContentType::json())
        .body(original_data)
        .send().unwrap();
    let mut buf = String::new();
    res.read_to_string(&mut buf).expect("Failed to read response");
    let v: Value = serde_json::from_str(&buf).map_err(|e| {
        io::Error::new(
            io::ErrorKind::Other,
            e
        )
    }).unwrap();
    let redacted_data = format!("{}", &v["json"]);
    println!("{}", redacted_data);
    return format!("{}", redacted_data);

}

fn reveal_via_forward_proxy(redacted_data: String, username: String, password: String,
     forward_proxy: String) {

     let mut core = Core::new().unwrap();
     let handle = core.handle();

     let proxy = {
         let proxy_uri = forward_proxy.clone().parse().unwrap();
         let mut proxy = Proxy::new(Intercept::All, proxy_uri);
         proxy.set_authorization(Basic {
                                    username: username.clone().into(),
                                    password: Some(password.clone().into()),
                                });
         let connector = HttpConnector::new(4, &handle);
         let proxy_connector = ProxyConnector::from_proxy(connector, proxy).unwrap();
         proxy_connector
     };

     let uri: Uri = "http://httbin.org/post".parse().unwrap();
     let mut req = Request::new(Method::Post, uri.clone());
     if let Some(headers) = proxy.http_headers(&uri) {
       req.headers_mut().extend(headers.iter());
       req.set_proxy(true);
       }
     let client = Client::configure().connector(proxy).build(&handle);
     let fut_http = client.request(req)
        .and_then(|res| res.body().concat2())
        .map(move |body: Chunk| ::std::str::from_utf8(&body).unwrap().to_string());

     let uri = "http://httpbin.org/post".parse().unwrap();
     let fut_https = client
        .post(uri)
        .body(redacted_data)
        .and_then(|res| res.body().concat2())
        .map(move |body: Chunk| ::std::str::from_utf8(&body).unwrap().to_string());

     let futs = fut_http.join(fut_https);

     let (_http_res, _https_res) = core.run(futs).unwrap();
}














    // let s: &str = &*redacted_data;
    // let json_data: Value = serde_json::from_str(&s).unwrap();
    //
    // let auth: String = base64::encode(format!("{}:{}", username, password).as_bytes());
    //
    // header! { (ProxyAuth, "Proxy-Authorization") => [String] }
    //
    // let raw_proxy = format!("http://{}", forward_proxy);
    // let proxy = reqwest::Proxy::all(&raw_proxy).unwrap();
    //
    // let mut headers = hyper::header::Headers::new();
    // headers.set(ProxyAuth(format!("Basic {}", auth)));
    // headers.set(ContentType::json());
    //
    // let headers_info = format!("{:?}", headers);
    // println!("{}", headers_info);
    //
    // let mut buf = Vec::new();
    // File::open("../cert.der").unwrap().read_to_end(&mut buf).unwrap();
    // let cert = reqwest::Certificate::from_der(&buf).unwrap();
    // let client = reqwest::Client::builder()
    //     .add_root_certificate(cert)
    //     .default_headers(headers)
    //     .proxy(proxy)
    //     .build().unwrap();
    //
    // let url = format!("{}", "http://httpbin.org/post");
    //
    // let mut res = client.post(&url)
    //     .json(&json_data)
    //     .send().unwrap();
    //
    // let mut buf = String::new();
    // res.read_to_string(&mut buf).expect("Failed to read response");
    // let v: Value = serde_json::from_str(&buf).map_err(|e| {
    //      io::Error::new(
    //          io::ErrorKind::Other,
    //          e
    //      )
    // }).unwrap();
    // let revealed_data = format!("{}", &v["json"]);
    // println!("{}", revealed_data);
    // return format!("{}", revealed_data);


fn main(){
    //import environment variables to use for functions
    #[derive(Deserialize, Debug)]
    struct Environment {
        forward_http_proxy_username: String,
        forward_http_proxy_password: String,
        forward_http_proxy_host: String,
        reverse_http_proxy_host: String,
    }
    let e: Environment = envy::from_env()
        .expect("Couldn't parse environment");

    println!("{:#?}", e);
    let username = e.forward_http_proxy_username;
    let password = e.forward_http_proxy_password;
    let forward_proxy = e.forward_http_proxy_host;
    let reverse_proxy = e.reverse_http_proxy_host;

        //original_data
        let original_data = format!("{{\"secret\":\"{}\"}}", fake!(Company.name));
        println!("{}", original_data); //show original_data

        //redact_data
        let redacted_data = redact_via_reverse_proxy(original_data.clone(), reverse_proxy);

        //reveal_data
        let revealed_data = reveal_via_forward_proxy(redacted_data.clone(), username, password, forward_proxy);

        assert_eq!(original_data.clone(), revealed_data.clone());
        assert!(original_data.clone() != redacted_data.clone());




}
