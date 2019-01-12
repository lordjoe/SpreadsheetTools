package com.lordjoe.whitepages;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.internetresources.util.ApacheHttpClientWithSSLConnectionSocketFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class PhoneVerify {

    public static final String PHONE_VERIFY_API_KEY = "";

    public static void main(String[] args) throws Exception {
        // Build the URI.
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("proapi.whitepages.com")
                .setPath("/3.0/phone")
                .setParameter("api_key",  PHONE_VERIFY_API_KEY)
                .setParameter("phone", "6464806649")
                .build();

        // Init GET request and client.
        HttpGet getRequest = new HttpGet(uri);
        CloseableHttpClient httpClient = HttpClients
                .custom()
                .setSSLSocketFactory(
                        ApacheHttpClientWithSSLConnectionSocketFactory.SSLUtil.getInsecureSSLConnectionSocketFactory())
                .build();

        // Get the response.
        HttpResponse response = httpClient.execute(getRequest);

        // Read response content to a Map.
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {
        };
        Map<String, Object> jsonMap = mapper.readValue(response.getEntity().getContent(), typeRef);

        System.out.println(jsonMap);
    }
}
