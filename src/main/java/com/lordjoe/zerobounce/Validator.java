package com.lordjoe.zerobounce;

/**
 * com.lordjoe.zerobounce.Validator
 * User: Steve
 * Date: 4/19/2018
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import com.lordjoe.loeb.contributer.ContributorAddress;
import com.lordjoe.loeb.contributer.EMail;
import com.lordjoe.loeb.contributer.PhoneNumber;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.internetresources.util.ApacheHttpClientWithSSLConnectionSocketFactory;
import com.lordjoe.whitepages.*;

public class Validator {

    public static final String SECRET_KEY = "0d0b0923325a491b86b4acb00683ceff";

    public static void main(String[] args) {

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            EmailValidity response = doValidate(arg);
            System.out.println(arg + ":" + response);

        }
    }

    /*
    Connected to the target VM, address: '127.0.0.1:62272', transport: 'socket'
vharms47@gmail.com:{"address":"vharms47@gmail.com","status":"Valid","sub_status":"","account":"vharms47","domain":"gmail.com","disposable":false,"toxic":false,"firstname":null,"lastname":null,"gender":null,"location":null,"creationdate":null,"processedat":"2018-04-19 16:53:17.175"}
vharms48@gmail.com:{"address":"vharms48@gmail.com","status":"Invalid","sub_status":"mailbox_not_found","account":"vharms48","domain":"gmail.com","disposable":false,"toxic":false,"firstname":null,"lastname":null,"gender":null,"location":null,"creationdate":null,"processedat":"2018-04-19 16:53:18.468"}
Disconnected from the target VM, address: '127.0.0.1:62272', transport: 'socket'

     */


    public static EmailValidity doValidate(String email) {
        String targetURL = "https://api.zerobounce.net/v1/validate?apikey=" + SECRET_KEY + "&email=" + email;
        try {
            // Build the URI.
            URIBuilder uriB = new URIBuilder()
                    .setScheme("https")
                    .setHost("api.zerobounce.net")
                    .setPath("/v1/validate")
                    .setParameter("apikey", SECRET_KEY)
                    .setParameter("email", email);


            URI uri = uriB.build();
            // Init GET request and client.
            HttpGet getRequest = new HttpGet(uri);


            CloseableHttpClient httpClient = HttpClients
                    .custom()
                    .setSSLSocketFactory(
                            ApacheHttpClientWithSSLConnectionSocketFactory.SSLUtil.getInsecureSSLConnectionSocketFactory())
                    .build();

            //      HttpClient httpClient = HttpClientBuilder.create().build();

            // Get the response.
            HttpResponse httpresponse = httpClient.execute(getRequest);

            String response = WPUtilities.getContentText(httpresponse) ;

            int index = response.indexOf("status\":\"");
            String sr = response.substring(index + "status\":\"".length());

            int index2 = sr.indexOf("\"");
            String statusStr = sr.substring(0, index2);

            try {
                if ("Catch-All".equalsIgnoreCase(statusStr))
                    return EmailValidity.CatchAll;
                if ("CatchAll".equalsIgnoreCase(statusStr))
                    return EmailValidity.CatchAll;


                return EmailValidity.valueOf(statusStr);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);

            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);

        } catch (KeyManagementException e) {
            throw new RuntimeException(e);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);

        } catch (IOException e) {
            throw new RuntimeException(e);

        }

    }

   
}

