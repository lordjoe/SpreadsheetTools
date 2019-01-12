package com.lordjoe.whitepages;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lordjoe.loeb.contributer.Contributer;
import com.lordjoe.utilities.StringUtilities;
import org.apache.http.HttpResponse;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * com.lordjoe.whitepages.WPUtilities
 * User: Steve
 * Date: 4/18/2018
 */
public class WPUtilities {

    public static final boolean isResponseValid(HttpResponse resp) {
        return resp.getStatusLine().getStatusCode() == 200;
    }

    public static Map<String, Object> streamToJSonMap(InputStream is) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {
            };
            Map<String, Object> jsonMap = mapper.readValue(is, typeRef);
            return jsonMap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static List<Contributer> readContributerFile(File f) {

        List<String> badLines = new ArrayList<String>();
        List<Contributer> ret = new ArrayList<Contributer>();
        LineNumberReader rdr = null;
        try {
            rdr = new LineNumberReader(new FileReader(f));
            String line = rdr.readLine();
            line = rdr.readLine();
            String[] columnHeaders = StringUtilities.lineToValues(line);
            line = rdr.readLine();
            int attempted = 0;

            while (line != null) {
                line = line.trim();
                attempted++;
                String[] strings = StringUtilities.lineToValues(line);
                try {
                    Contributer e = new Contributer(strings);
                    ret.add(e);
                    //        System.out.println(line);
                } catch (Exception e1) {
                    System.err.println(line);
                }

                line = rdr.readLine();
            }

            System.out.println("Attempted " + attempted + " Succeeded " + ret.size());
            return ret;
        } catch (IOException e) {
            throw new RuntimeException(e);

        } finally {
            try {
                if (rdr != null)
                    rdr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);

            }
        }
    }
    public static Map<String, Object> stringToJSonMap(String s) {
        return streamToJSonMap(stringAsStream(s));
    }

    public static String jsonMapString(Map<String, Object> s) {
        String ret = s.toString();
        ret = ret.replace(",", ",\n");
        ret = ret.replace("{", ",{\n");
        return ret;
    }


     public static InputStream stringAsStream(String s) {
        return new ByteArrayInputStream(s.getBytes());
    }

    public static final String getContentText(HttpResponse resp) {
        try {
            InputStream is = resp.getEntity().getContent();
            String ret = readStream(is);
            return ret;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String readStream(InputStream is) {
        try {
            try {
                java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                return s.hasNext() ? s.next() : "";
            } finally {
                is.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }
    public static final String readFile(File f) {
        try {
             InputStream is = new FileInputStream(f);
             return readStream(is);
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }


    public static final JSONObject readJSonFile(File f) {
        return new JSONObject(readFile(f));
    }

}
