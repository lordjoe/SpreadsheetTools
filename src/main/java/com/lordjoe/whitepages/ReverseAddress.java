package com.lordjoe.whitepages;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.internetresources.util.ApacheHttpClientWithSSLConnectionSocketFactory;
import org.json.JSONObject;

import java.net.URI;
import java.util.Map;

import static com.lordjoe.utilities.StringUtilities.prettyPrintJson;

public class ReverseAddress {

    public static final String ZREVERSE_ADDRESS_KEY = "affc39847ea64f8d8f8c92704c677955";

    public static void main(String[] args) throws Exception {
        // Build the URI.
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("proapi.whitepages.com")
                .setPath("/3.0/location")
                .setParameter("api_key", ZREVERSE_ADDRESS_KEY)
                .setParameter("street_line_1", "4221 105th Ave NE")
                .setParameter("city", "Kirkland")
                .setParameter("state_code", "WA")
                .setParameter("postal_code", "98033")
                .setParameter("state_code", "US")
                .build();

        // Init GET request and client.
        HttpGet getRequest = new HttpGet(uri);
        // make a factory that ignores security
        CloseableHttpClient httpClient = HttpClients
                .custom()
                .setSSLSocketFactory(
                        ApacheHttpClientWithSSLConnectionSocketFactory.SSLUtil.getInsecureSSLConnectionSocketFactory())
                .build();


        // Get the response.
        HttpResponse response = httpClient.execute(getRequest);
        if(!WPUtilities.isResponseValid(response))
            return;

        String contentText = WPUtilities.getContentText(response);

        System.err.println(contentText);

           System.out.println(prettyPrintJson(contentText));

      }
}

