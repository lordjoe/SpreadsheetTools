package com.lordjoe.utilities;

import org.json.JSONObject;

import java.io.*;

/**
 * com.lordjoe.utilities.StringUtilities
 * User: Steve
 * Date: 4/18/2018
 */
public class StringUtilities {

    public static final String SPLIT_STRING = "\t";

    public static boolean isEmpty(String s)  {
        return s == null || s.trim().length() == 0;
    }

    public static String[] lineToValues(String line)  {
        String[] columnHeaders = line.split(SPLIT_STRING);
        for (int i = 0; i < columnHeaders.length; i++) {
            columnHeaders[i] = columnHeaders[i].trim();
            columnHeaders[i] = columnHeaders[i].replace("\"","");
        }
        return columnHeaders;
    }

    public static String  valuesToLine(String[] items)  {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        sb.append(items[i++]);

        for (; i < items.length; i++) {
            sb.append(SPLIT_STRING) ;
            sb.append(items[i]);

        }
        return sb.toString();
    }


    public static String prettyPrintJson(String s)    {
        JSONObject json = new JSONObject(s); // Convert text to object
       return json.toString(4);

    }

    public static final String readFile(File f) {
        try {
            InputStream is = new FileInputStream(f);
            return readStream(is);
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


    public static String getPropertyString(JSONObject obl,String property)     {
        StringBuilder sb = new StringBuilder();
        if(!obl.has(property))
            return sb.toString();
        Object o = obl.get(property);
        if(o != null)
            sb.append(o.toString()) ;
        return sb.toString();
    }



    public static void writeTextToFile(File outFile, String response) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outFile));
            out.println(response) ;
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }
}
