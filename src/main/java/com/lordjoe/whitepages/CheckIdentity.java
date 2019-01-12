package com.lordjoe.whitepages;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lordjoe.loeb.State;
import com.lordjoe.loeb.contributer.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.internetresources.util.ApacheHttpClientWithSSLConnectionSocketFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static com.lordjoe.loeb.contributer.Main.downloadPeople;

/**
 * com.lordjoe.whitepages.FindPerson
 * User: Steve
 * Date: 4/16/2018
 */
public class CheckIdentity {

    public static final String WHITE_PAGES_IDENTITY_CHECK_KEY  = "a3fef713bb33474eb63687e21ee76a68";



    public static String makeSearchableName(Contributer source ) {
        return makeSearchableName(source.getFirstName()) + "+"  + makeSearchableName(source.getLastName());
    }

    /**
     * convert thinge like John & Lilly to John
     * @param s
     * @return
     */
    public static String makeSearchableName( String s) {
        StringBuilder sb = new StringBuilder();
        s = s.trim();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i) ;
            if(Character.isLetter(c))  {
                sb.append(c) ;
            }
            else {
                if(sb.length() > 0)
                    return sb.toString();
            }

        }
        return sb.toString();
    }


    public static String check_identity(Contributer source) throws URISyntaxException, IOException, NoSuchAlgorithmException, KeyManagementException {
        StringBuilder sb = new StringBuilder();




        // Build the URI.
        URIBuilder uriB = new URIBuilder()
                .setScheme("https")
                .setHost("proapi.whitepages.com")
                .setPath("/3.2/identity_check.json")
                .setParameter("api_key", WHITE_PAGES_IDENTITY_CHECK_KEY);


        String name = makeSearchableName(source);

        uriB.setParameter("primary.name", name);

        PhoneNumber phoneNumber = source.getPhoneNumber();
        if(phoneNumber  != null)
            uriB.setParameter("primary.phone", phoneNumber.number) ;

        ContributorAddress address = source.getAddress();
        if(address != null) {
            setURIBuilderAddress(uriB,address);
        }

        EMail eMail = source.getEMail();
        if(eMail != null)
            uriB.setParameter("email_address", eMail.toString());

        URI uri = uriB.build();
        String s = uri.toString();
        // Init GET request and client.
        HttpGet getRequest = new HttpGet(uri);


        CloseableHttpClient httpClient = HttpClients
                .custom()
                .setSSLSocketFactory(
                        ApacheHttpClientWithSSLConnectionSocketFactory.SSLUtil.getInsecureSSLConnectionSocketFactory())
                .build();

        //      HttpClient httpClient = HttpClientBuilder.create().build();

        // Get the response.
        HttpResponse response = httpClient.execute(getRequest);


        // Read response content to a Map.
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
        Map<String, Object> jsonMap = mapper.readValue(response.getEntity().getContent(), typeRef);

        String[] availableChecks = new String[]{"primary_phone_checks", "primary_address_checks",
                "secondary_phone_checks", "secondary_address_checks",
                "email_address_checks", "ip_address_checks"};

        // Display check results.
        for(String check : availableChecks){
            System.out.println(jsonMap.get(check).toString());
        }
        return sb.toString();

    }

    private static void setURIBuilderAddress(URIBuilder uriB, ContributorAddress address) {
        String street = address.getStreet();
        if(street != null && street.length() > 0)
            uriB.setParameter("primary.address.street_line_1", street);

         street = address.getCity();
        if(street != null && street.length() > 0)
            uriB.setParameter("primary.address.city", street);

        State st = address.getState();
        if(st != null )
            uriB.setParameter("primary.address.state_code", st.getAbbreviation());

        ZipCode zip = address.getZip();
        if(zip != null )
            uriB.setParameter("primary.address.postal_code", zip.toString());
        uriB.setParameter("primary.address.country_code", "US");

    }
    public static void main(String[] args) throws Exception {
          downloadPeople(args);
    }

}
