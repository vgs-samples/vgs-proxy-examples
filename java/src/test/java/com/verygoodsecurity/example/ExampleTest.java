package com.verygoodsecurity.example;

import com.github.javafaker.Faker;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;


public class ExampleTest {
    static Faker faker = new Faker();

    static String username = System.getenv("FORWARD_HTTP_PROXY_USERNAME");
    static String password = System.getenv("FORWARD_HTTP_PROXY_PASSWORD");
    static String forward_proxy = System.getenv("FORWARD_HTTP_PROXY_HOST");
    static String reverse_proxy = System.getenv("REVERSE_HTTP_PROXY_HOST");

    static {
        Unirest.setDefaultHeader("Content-type", "application/json");
        Unirest.setDefaultHeader("VGS-Log-Request", "all");
    }

    static String randomJson() {
        return new JSONObject()
                .put("secret", faker.name().nameWithMiddle())
                .toString();
    }

    static String tokenizeViaReverseProxy(String originalData) throws UnirestException {
        return Unirest.post(String.format("https://%s/post", reverse_proxy))
                .body(originalData)
                .asJson().getBody().getObject()
                .getString("data");
    }

    static String revealViaForwardProxy(String tokenizedData) throws UnirestException {
        System.setProperty("javax.net.ssl.trustStore", ExampleTest.class.getClassLoader().getResource("cacerts").getFile());
        System.setProperty("javax.net.ssl.trustStorePassword", "password");
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        clientBuilder.useSystemProperties();
        clientBuilder.setProxy(HttpHost.create(forward_proxy));
        clientBuilder.setDefaultCredentialsProvider(credsProvider);
        clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());

        Lookup<AuthSchemeProvider> authProviders = RegistryBuilder.<AuthSchemeProvider>create()
                .register(AuthSchemes.BASIC, new BasicSchemeFactory())
                .build();
        clientBuilder.setDefaultAuthSchemeRegistry(authProviders);

        Unirest.setHttpClient(clientBuilder.build());
        return Unirest.post("https://httpbin.verygoodsecurity.io/post")
                .body(tokenizedData)
                .asJson().getBody().getObject()
                .getString("data");
    }

    @Test
    public void testIt() throws UnirestException, IOException {
        String originalValue = randomJson();
        System.out.println(originalValue);

        String tokenizedValue = tokenizeViaReverseProxy(originalValue);
        System.out.println(tokenizedValue);
        Assert.assertFalse(originalValue.equals(tokenizedValue));

        String revealedValue = revealViaForwardProxy(tokenizedValue);
        System.out.println(revealedValue);

        Assert.assertTrue(originalValue.equals(revealedValue));

        Unirest.shutdown();
        System.out.println("Test passed");
    }
}
