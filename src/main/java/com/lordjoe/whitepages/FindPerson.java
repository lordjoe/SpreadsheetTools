package com.lordjoe.whitepages;

import com.lordjoe.loeb.State;
import com.lordjoe.loeb.contributer.Contributer;
import com.lordjoe.loeb.contributer.ContributorAddress;
import com.lordjoe.loeb.contributer.PhoneNumber;
import com.lordjoe.loeb.contributer.ZipCode;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.internetresources.util.ApacheHttpClientWithSSLConnectionSocketFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static com.lordjoe.loeb.contributer.Main.contrubuterIsValid;
import static com.lordjoe.utilities.StringUtilities.getPropertyString;
import static com.lordjoe.utilities.StringUtilities.writeTextToFile;
/**
 * com.lordjoe.whitepages.FindPerson
 * User: Steve
 * Date: 4/16/2018
 */
public class FindPerson {

    public static final String BAD_FIND_PERSON_KEY = "foobar";
    public static final String STEVE_FIND_PERSON_KEY = "c05ee4dcce524045869365cecbff4ce7";
    public static final String PAUL_FIND_PERSON_KEY  = "4cb8972d67dc48a19f119857599544bb";
    public static final String KATE_FIND_PERSON_KEY  = "160d9a4be71746bbacc9a77f719e4949";
    public static final String REBECCA_FIND_PERSON_KEY  = "6e48b40140674b868ca2a59c49928305";
    public static final String[] KEYS = {
//            BAD_FIND_PERSON_KEY,
//            STEVE_FIND_PERSON_KEY,
            PAUL_FIND_PERSON_KEY,
            KATE_FIND_PERSON_KEY,
            REBECCA_FIND_PERSON_KEY,
    };
    public static final String WHITE_PAGES_FIND_PERSON_KEY = STEVE_FIND_PERSON_KEY;

    public static String find(Contributer source,String key) throws URISyntaxException, IOException, NoSuchAlgorithmException, KeyManagementException {
        StringBuilder sb = new StringBuilder();


        // Build the URI.
        URIBuilder uriB = new URIBuilder()
                .setScheme("https")
                .setHost("proapi.whitepages.com")
                .setPath("/3.0/person.json")
                .setParameter("api_key", key);


        String s1 = makeSearchableName(source);
        String name = s1;

        uriB.setParameter("name", name);


        ContributorAddress address = source.getAddress();
        if (address != null) {
            String street = address.getStreet();
            if (street != null && street.length() > 0)
                uriB.setParameter("address.street_line_1", street);

            street = address.getCity();
            if (street != null && street.length() > 0)
                uriB.setParameter("address.city", street);

            State st = address.getState();
            if (st != null)
                uriB.setParameter("address.state_code", st.getAbbreviation());

            ZipCode zip = address.getZip();
            if (zip != null)
                uriB.setParameter("address.postal_code", zip.toString());
            uriB.setParameter("address.country_code", "US");
        }


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

        if(!WPUtilities.isResponseValid(response))
            return null;

        String contentText = WPUtilities.getContentText(response);
        return contentText;


    }

    public static String makeSearchableName(Contributer source) {
        return makeSearchableName(source.getFirstName()) + "+" + makeSearchableName(source.getLastName());
    }

    /**
     * convert thinge like John & Lilly to John
     *
     * @param s
     * @return
     */
    public static String makeSearchableName(String s) {
        StringBuilder sb = new StringBuilder();
        s = s.trim();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLetter(c)) {
                sb.append(c);
            } else {
                if (sb.length() > 0)
                    return sb.toString();
            }

        }
        return sb.toString();
    }


    private static void setURIBuilderAddress(URIBuilder uriB, ContributorAddress address) {
        String street = address.getStreet();
        if (street != null && street.length() > 0)
            uriB.setParameter("primary.address.street_line_1", street);

        street = address.getCity();
        if (street != null && street.length() > 0)
            uriB.setParameter("primary.address.city", street);

        State st = address.getState();
        if (st != null)
            uriB.setParameter("primary.address.state_code", st.getAbbreviation());

        ZipCode zip = address.getZip();
        if (zip != null)
            uriB.setParameter("primary.address.postal_code", zip.toString());
        uriB.setParameter("primary.address.country_code", "US");

    }

    public static void ssaveContributerPeople(Contributer c) {
        File saveDir = new File("SaveDir");
        File peopleDir = new File("People");
        saveDir.mkdirs();
        peopleDir.mkdirs();
        File responseFile = new File(saveDir, c.getId() + ".json");
        if (!responseFile.exists())
            return;
        if (!contrubuterIsValid(c))
            return;

        JSONObject people = WPUtilities.readJSonFile(responseFile);


    }

    public static int getPersonCount(JSONObject json) {
        Object count_personObj = json.get("count_person");

        if (count_personObj instanceof Integer) {
            return (Integer) count_personObj;
        }
        return 0;
    }

    public static List<JSONObject> getPeople(JSONObject json) {
        List<JSONObject> ret = new ArrayList<>();

        int count_person = getPersonCount(json);
        if (count_person > 0) {
            JSONArray array = (JSONArray) json.get("person");
            for (int i = 0; i < array.length(); i++) {
                JSONObject person = array.getJSONObject(i);
                ret.add(person);
            }
        }

        return ret;
    }


    public static void handleFoundCoutributors(String[] args) throws Exception {
        int index = 0;
        File eduRequestFile = new File(args[index++]);
        File out = new File(args[index++]);


        List<Contributer> requests = WPUtilities.readContributerFile(eduRequestFile);
        int numberToValidate = 10;
        PrintWriter outWriter = new PrintWriter(new FileWriter(out));

        File saveDir = new File("SaveDir");
        File peopleDir = new File("People");
        saveDir.mkdirs();
        peopleDir.mkdirs();

        File savePeople = new File(saveDir, "savedPeople.txt");
        PrintWriter saveWriter = new PrintWriter(new FileWriter(savePeople));
        writeHeaders(saveWriter);
        for (Contributer request : requests) {
            File responseFile = new File(saveDir, request.getId() + ".json");
            if (!responseFile.exists())
                continue;
            if (!contrubuterIsValid(request))
                continue;

            String response = WPUtilities.readFile(responseFile);
            JSONObject json = new JSONObject(response); // Convert text to object

            List<JSONObject> people = getPeople(json);

            handleContributorPeople(request, people, saveDir, saveWriter);


        }

        saveWriter.close();

    }

    public static void writeHeaders(PrintWriter saveWriter)    {
        saveWriter.println("Id\tName\tLastName\tFirstName\tMiddleName\tStreet\tCity\tState\tZip\tPhone1\tPhone 1 Type\t,Phone2\tPhone 2 Type\tAge\tAmount\tNotes");
    }

    private static void writePerson(Contributer c,JSONObject person,PrintWriter saveWriter)    {
      //  saveWriter.println("Id\tName\tLastName\tFirstName\tMiddleName\tStreet\tCity,State,Zip,Phone1\tPhone 1 Type\t,Phone2\tPhone 2 Type\tAge\tAmount\tNotes");
      StringBuilder sb = new StringBuilder();
        String prop = getPropertyString(person, "loeb_id");
        sb.append(prop);
        sb.append("\t");

        prop = getPropertyString(person, "name");
        sb.append(prop);
        sb.append("\t");

        prop = getPropertyString(person, "lastname");
        sb.append(prop);
        sb.append("\t");

        prop = getPropertyString(person, "firstname");
        sb.append(prop);
        sb.append("\t");

        prop = getPropertyString(person, "middlename");
        sb.append(prop);
        sb.append("\t");


        if(person.has("current_addresses")) {
            JSONArray current_address = person.getJSONArray("current_addresses");
            if (current_address == null || current_address.length() == 0) {
                sb.append("\t\t\t\t");
            } else {
                JSONObject address = current_address.getJSONObject(0);
                prop = getPropertyString(address, "street_line_1");
                sb.append(prop);
                sb.append("\t");

                prop = getPropertyString(address, "city");
                sb.append(prop);
                sb.append("\t");

                prop = getPropertyString(address, "state_code");
                sb.append(prop);
                sb.append("\t");

                prop = getPropertyString(address, "postal_code");
                sb.append(prop);
                sb.append("\t");

            }
        }
        else {
            sb.append("\t\t\t\t");

        }

        if(person.has("phones")) {
            JSONArray phones = person.getJSONArray("phones");
            if (phones == null || phones.length() == 0) {
                sb.append("\t\t\t\t");
            } else {
                int i = 0;
                int numberPhones = Math.min(phones.length(), 2);
                for (; i < numberPhones; i++) {
                    JSONObject phone = phones.getJSONObject(i);
                    prop = getPropertyString(phone, "phone_number");
                    prop = formatPhone(prop);
                    sb.append(prop);
                    sb.append("\t");

                    prop = getPropertyString(phone, "line_type");
                    sb.append(prop);
                    sb.append("\t");


                }
                for (int j = numberPhones; j < 2; j++) {
                    sb.append("\t\t");
                 }

            }
        }
        else {
            sb.append("\t\t\t\t");
        }

        prop = getPropertyString(person, "age_range");
        sb.append(prop);
        sb.append("\t");

        sb.append(c.getContributionString());
        sb.append("\t");

        prop = c.getNotes();
        if(prop != null)
            sb.append(prop);


        String x = sb.toString();
        saveWriter.println(x);

    }

    public  static String formatPhone(String prop) {
        if(prop.startsWith("+1"))
            prop = prop.substring(2);
        return new PhoneNumber(prop).toString();
    }

    public static void handleContributorPeople(Contributer c, List<JSONObject> people, File peopleDir, PrintWriter saveWriter) {
        for (JSONObject person : people) {
            writePerson(c, peopleDir, saveWriter, person);
        }
    }

    public static void writePerson(Contributer c, File peopleDir, PrintWriter saveWriter, JSONObject person) {
        String replace = c.getId().replace("RawPerson", "Person");
        person.put("loeb_id", replace);
        person.put("contribution", c.getContributionString());
        String employer = c.getEmployer();
        if(employer != null)
            person.put("employer", employer);
        File outFile = makePersonFile(person, peopleDir);
        String response = person.toString(4);
        writeTextToFile(outFile, response);
        writePerson(  c,  person,  saveWriter);
    }


    public static File makePersonFile(JSONObject person, File peopleDir) {
        String personName = (String) person.get("name");
        File personFile = new File(peopleDir, personName + ".json");
        return personFile;
    }

//    public static void main(String[] args) throws Exception {
//       main.downloadPeople(args);
//       // handleFoundCoutributors(args);
//    }

}
