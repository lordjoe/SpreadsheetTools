package org.internetresources.util;

/**
 * org.internetresources.util.ApacheHttpClientWithSSLConnectionSocketFactory
 * User: Steve
 * Date: 4/17/2018
 */
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class ApacheHttpClientWithSSLConnectionSocketFactory {

    public void examplePost() throws NoSuchAlgorithmException,
            KeyManagementException {
        final String API_URI = "https://currencytrade-spray.herokuapp.com/v1/trade";

        /*
         * There are times during development that security certificates are not
         * available or you can not install the certificates in a particular
         * environment.
         *
         * In this situations you may face the error shown below when attempting
         * to make an SSL connection:
         *
         * javax.net.ssl.SSLHandshakeException:
         * sun.security.validator.ValidatorException: PKIX path building failed:
         * sun.security.provider.certpath.SunCertPathBuilderException: unable to
         * find valid certification path to requested target
         *
         * The HttpClient created below uses a "Trust All"
         * SSLConnectionSocketFactory which blindly trusts all certificates.
         * This is very insecure and leaves you vulnerable to MitM attacks.
         *
         * This approach can be useful during development if security
         * certificates are not available
         */
        final CloseableHttpClient client = HttpClients
                .custom()
                .setSSLSocketFactory(
                        SSLUtil.getInsecureSSLConnectionSocketFactory())
                .build();

        final HttpPost httpPost = new HttpPost(API_URI);

        /*
         * The JSON representation to be sent to the API
         * {
         * "userId": "134256",
         * "currencyFrom": "EUR",
         * "currencyTo": "GBP",
         * "amountSell": 1000,
         * "amountBuy": 747.10,
         * "rate": 0.7471,
         * "timePlaced" :"24-JAN-15 10:27:44",
         * "originatingCountry" : "FR"
         * }
         */
        final JSONObject jsonToSend = new JSONObject();
        jsonToSend.put("userId", "134256");
        jsonToSend.put("currencyFrom", "EUR");
        jsonToSend.put("currencyTo", "GBP");
        jsonToSend.put("amountSell", 1000);
        jsonToSend.put("amountBuy", 747.10);
        jsonToSend.put("rate", 0.7471);
        jsonToSend.put("timePlaced", "24-JAN-15 10:27:44");
        jsonToSend.put("originatingCountry", "FR");

        // set the content-type to application/json
        httpPost.setEntity(new StringEntity(jsonToSend.toString(), ContentType
                .create("application/json")));

        try {
            final CloseableHttpResponse response = client.execute(httpPost);
            final HttpEntity httpEntity = response.getEntity();
            final String result = EntityUtils.toString(httpEntity);
            System.out.println("INFO >>> Response from API was: " + result);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static class SSLUtil {
        public static SSLConnectionSocketFactory getInsecureSSLConnectionSocketFactory()
                throws KeyManagementException, NoSuchAlgorithmException {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(
                                final java.security.cert.X509Certificate[] arg0, final String arg1)
                                throws CertificateException {
                            // do nothing and blindly accept the certificate
                        }

                        public void checkServerTrusted(
                                final java.security.cert.X509Certificate[] arg0, final String arg1)
                                throws CertificateException {
                            // do nothing and blindly accept the server
                        }

                    }
            };

            final SSLContext sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(null, trustAllCerts,
                    new java.security.SecureRandom());

            final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext, new String[]{"TLSv1"}, null,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            return sslsf;
        }
    }
}