package com.lordjoe.loeb.fec;

import com.lordjoe.loeb.State;
import org.apache.http.HttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * com.lordjoe.loeb.fec.ZipCode
 * User: Steve
 * Date: 8/12/19
 */
public class ZipCode implements Comparable<ZipCode> {

    public static final Class THIS_CLASS = ZipCode.class;
    private static List<ZipCode> AllZips = null;
    private static Map<String, ZipCode> ByName = null;
    private static Map<ZipCode, Set<CongressionalDistrict>> zipToDistrict = new HashMap<>();
    private static Map<CongressionalDistrict, Set<ZipCode>> districtToZips = new HashMap<>();

    public static List<ZipCode> getAllZips() {
        synchronized (ZipCode.class) {
            if (AllZips == null) {
                buildZips();
            }
            return AllZips;
        }
    }


    public static List<ZipCode> getByState(State s) {
        List<ZipCode> ret = new ArrayList<>();
        List<ZipCode> allZips = getAllZips() ;
        for (ZipCode allZip : allZips) {
             if(allZip.state == s)
                 ret.add(allZip);
        }
       return ret;
    }

    public static ZipCode getById(String id) {
        synchronized (ZipCode.class) {
            if (AllZips == null) {
                buildZips();
            }
            return ByName.get(id);
        }
    }

    private static void buildZips() {
        synchronized (ZipCode.class) {
            if (AllZips == null) {
                readZipsFromResource();
            }
        }
    }


    private static void buildDistrictToZips() {
        List<CongressionalDistrict>  districts = CongressionalDistrict.getAllDistricts();
        for (CongressionalDistrict d : districts) {
            districtToZips.put(d,new HashSet<ZipCode>()) ;
        }
        List<ZipCode> zips = getAllZips();
        for (ZipCode zip : zips) {
            Set<CongressionalDistrict> cds = zipToDistrict.get(zip);
            if(cds != null)  {
                for (CongressionalDistrict cd : cds) {
                    districtToZips.get(cd).add(zip);
                }
            }
        }

    }


    private static void readZipsFromResource() {
        try {
            AllZips = new ArrayList<ZipCode>();
            ByName = new HashMap<>();
            InputStream zipStr = THIS_CLASS.getResourceAsStream("/free-zipcode-database-Primary.csv");
            LineNumberReader rdr = new LineNumberReader(new InputStreamReader(zipStr));
            String line = rdr.readLine(); // headers
            line = rdr.readLine();
            while (line != null) {
                handleZipLine(line);
                line = rdr.readLine();
            }
            rdr.close();
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    static int index = 0;
    public static final int ID_ITEM = index++;
    public static final int TYPE_ITEM = index++;
    public static final int CITY_ITEM = index++;
    public static final int STATE_ITEM = index++;
    public static final int LOCATION_TYPE_ITEM = index++;
    public static final int LAT_ITEM = index++;
    public static final int LON_ITEM = index++;
    public static final int LOC_ITEM = index++;
    public static final int DECOMMISSIONED_ITEM = index++;
    public static final int RETURN_COUNT_ITEM = index++;
    public static final int POPULATION_ITEM = index++;
    public static final int WAGES_ITEM = index++;

    private static void handleZipLine(String line) {
        String[] items = line.split(",");
        String type = items[TYPE_ITEM];
        if (!items[TYPE_ITEM].equals("\"STANDARD\""))
            return;
        if (!items[DECOMMISSIONED_ITEM].equals("\"false\""))
            return;

        String name = items[ID_ITEM].replace("\"", "");
        String city = items[CITY_ITEM].replace("\"", "");
        String item = items[STATE_ITEM].replace("\"", "");
        State state = State.fromString(item);
        double lat = Double.parseDouble(items[LAT_ITEM].replace("\"", ""));
        double longitude = Double.parseDouble(items[LON_ITEM].replace("\"", ""));

        int returns = 0;
        if (items.length > RETURN_COUNT_ITEM && items[RETURN_COUNT_ITEM].length() > 0)
            returns = Integer.parseInt(items[RETURN_COUNT_ITEM]);
        int population = 0;
        if (items.length > POPULATION_ITEM && items[POPULATION_ITEM].length() > 0)
            population = Integer.parseInt(items[POPULATION_ITEM]);
        double wages = 0;
        if (items.length > WAGES_ITEM && items[WAGES_ITEM].length() > 0)
            wages = Double.parseDouble(items[WAGES_ITEM]);

        ZipCode z = new ZipCode(name, city, state, population, returns, wages);

    }

    public final String id;
    public final String city;
    public final State state;
    public final int population;
    public final int taxReturns;
    public final double income;

    private ZipCode(String id, String city, State state, int population, int taxReturns, double income) {
        this.id = id;
        this.state = state;
        this.city = city;
        this.population = population;
        this.taxReturns = taxReturns;
        this.income = income;

        AllZips.add(this);
        ByName.put(id, this);

    }

    public double getAverageWages() {
        if (taxReturns == 0)
            return 0;
        return income / taxReturns;
    }

    @Override
    public String toString() {
        return id;
    }

    public static void main2(String[] args) {
        List<ZipCode> zips = getAllZips();
        System.out.println(zips.size());
    }

    public static void main(String[] args) throws URISyntaxException, IOException {

        List<ZipCode> zips = getAllZips();
        getKnownZipToDistrict();
        for (ZipCode zip : zips) {
            if(zipToDistrict.containsKey(zip))
                continue;
            if(zip.state.isUSState())  // drop puerto rico ...
                     getDistricts(zip);
        }
    }

    public static void getKnownZipToDistrict() {
        try {
            InputStream zipStr = THIS_CLASS.getResourceAsStream("/ZipcodeToLegislativeDistrict.tsv");
            LineNumberReader rdr = new LineNumberReader(new InputStreamReader(zipStr));
            List<String> lines = new ArrayList<>();
            String   line = rdr.readLine();
            while (line != null) {
                lines.add(line);
                handleZipcodeToLegislativeDistrictLine(line);
                line = rdr.readLine();
            }
            rdr.close();

            System.out.println(("========================"));
            Collections.sort(lines);
            for (String s : lines) {
                System.out.println(s);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);

        }

    }

    private static void handleZipcodeToLegislativeDistrictLine(String line) {
        int index = line.indexOf("\t") ;
        if(index == -1)
            return;
        String id = line.substring(0,index);
        ZipCode zip = getById(  id);
        CongressionalDistrict cd = CongressionalDistrict.getDistrictFromLine(line.substring(index + 1));
        Set<CongressionalDistrict> cds = zipToDistrict.get(zip) ;
        if(cds == null) {
            cds = new HashSet<>() ;
            zipToDistrict.put(zip,cds) ;
        }
        cds.add(cd);
        cd.addZipCode(zip);
    }

    public List<CongressionalDistrict>  getCongressionalDistricts(ZipCode z) {
        Set<CongressionalDistrict> cds = zipToDistrict.get(z) ;
        if(cds != null)    {
            List<CongressionalDistrict>  ret = new ArrayList<>(cds) ;
            Collections.sort(ret);

        }
        return  new ArrayList<CongressionalDistrict>() ;
    }

    private static void getDistricts(ZipCode zip) {

        State s = zip.state;

        HttpResponse response = null;
        try {
            // Build the URI.
            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost("ziplook.house.gov")
                    .setPath("/htbin/findrep_house")
                    .setParameter("ZIP", zip.id)
                    .build();


            // Init GET request and client.
     //       HttpGet getRequest = new HttpGet(uri);
    //        HttpClient httpClient = HttpClientBuilder.create().build();

            // Get the response.
     //       response = httpClient.execute(getRequest);

            String s1 = uri.toString();
            Document doc = Jsoup.connect(s1).get();

            Elements newsHeadlines = doc.getElementsByClass("Repinfo");
            if(newsHeadlines.size() > 0)
                for (Element headline : newsHeadlines) {
                    handleRepInfoElement(zip, s, headline);
                }
            else {
                Element elx = doc.getElementById("RepInfo");
                if(elx != null)
                     handleRepInfoElement(zip, s, elx);
            }

            /*
             InputStream content = response.getEntity().getContent();
               String s = FileUtilities.readInFile(content);

            // Read response content to a Map.
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {
            };
            Map<String, Object> jsonMap = mapper.readValue(response.getEntity().getContent(), typeRef);

            System.out.println(jsonMap);
            */

        } catch (Exception e) {
            throw new RuntimeException(e);

        }

    
    }

    private static void handleRepInfoElement(ZipCode zip, State s, Element headline) {
        String text = headline.text();
        String test = s.toString().toUpperCase() + " DISTRICT ";
        int index = text.toUpperCase().indexOf(test);
        if (index > 0) {
            String numberStr = text.substring(index + test.length()).trim();
            int number = Integer.parseInt(numberStr);
            CongressionalDistrict d =  CongressionalDistrict.getDistrict(s,number) ;
            d.addZipCode(zip) ;
            System.out.println(zip + "\t" + d);
        }
        else {
            Element grandparent = headline.parent().parent();
            String text1 = grandparent.text();
            String is_located_in_the_ = "is located in the ";
            index = text1.toLowerCase().indexOf(is_located_in_the_);
            if (index > 0) {
                String numberStr = text1.substring(index + is_located_in_the_.length()).trim();
                  int number = pealDigits(numberStr);
                CongressionalDistrict d =  CongressionalDistrict.getDistrict(s,number) ;
                d.addZipCode(zip) ;
                System.out.println(zip + "\t" + d);
            }
        }
    }

    public static int pealDigits(String s)  {
        int index = 0;
        int ret = 0;
        while(index < s.length())    {
            char c = s.charAt(index++);
            if(!Character.isDigit(c))
                return ret;
            ret = 10 * ret + (c - '0');
        }
        return ret;
    }

    @Override
    public int compareTo(ZipCode zipCode) {
        return id.compareTo(zipCode.id);
    }
}
