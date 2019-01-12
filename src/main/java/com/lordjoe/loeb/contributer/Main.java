package com.lordjoe.loeb.contributer;


import java.io.*;
import java.util.*;

import com.lordjoe.utilities.StringUtilities;
import com.lordjoe.whitepages.*;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * com.lordjoe.loeb.contributer.Main
 * User: Steve
 * Date: 4/16/2018
 */
public class Main {




    public static void validateContributors(String[] args) throws Exception {
        int index = 0;
        File eduRequestFile = new File(args[index++]);
        File goodRequestFile = new File(args[index++]);
        File badRequestFile = new File(args[index++]);

        List<String> badLines = new ArrayList<String>();
        List<Contributer> ret = new ArrayList<Contributer>();
        LineNumberReader rdr = null;
        try {
            rdr = new LineNumberReader(new FileReader(eduRequestFile));
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
                    if (e.isValidName())
                        ret.add(e);
                    //             System.out.println(line);
                } catch (Exception e1) {
                    badLines.add(line);
                }

                line = rdr.readLine();
            }

            System.out.println("Attempted " + attempted + " Succeeded " + ret.size());
        } catch (IOException e) {
            throw new RuntimeException(e);

        } finally {
            try {
                if (rdr != null)
                    rdr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);

            }
            System.out.println("Requests " + ret.size());
        }

        PrintWriter good = new PrintWriter(new FileWriter(goodRequestFile));
        for (Contributer c : ret) {
            good.println(c.toTabbedLine());
        }
        good.close();

        PrintWriter bad = new PrintWriter(new FileWriter(badRequestFile));
        for (String badLine : badLines) {
            bad.println(badLine);
        }
        bad.close();


    }


    public static void downloadPeople(String[] args) throws Exception {
        int index = 0;
        File eduRequestFile = new File(args[index++]);
        File out = new File(args[index++]);

        int keyIndex = 0;
        String current_key = FindPerson.KEYS[keyIndex++];


        List<Contributer> requests = WPUtilities.readContributerFile(eduRequestFile);
        int numberToValidate = 1000;
        PrintWriter outWriter = new PrintWriter(new FileWriter(out));

        File saveDir = new File("SaveDir");
        File peopleDir = new File("People");
        saveDir.mkdirs();
        peopleDir.mkdirs();
        int validated = 2;
        for (Contributer request : requests) {
            File outFile = new File(saveDir, request.getId() + ".json");
            if (outFile.exists())
                continue;
            if (!contrubuterIsValid(request))
                continue;
            String response = FindPerson.find(request,current_key);
            if(response == null)   {
                if(keyIndex >= FindPerson.KEYS.length )
                    break; // no more Keys

                 current_key = FindPerson.KEYS[keyIndex++];
                response = FindPerson.find(request,current_key);
            }


            JSONObject json = new JSONObject(response); // Convert text to object
            Object count_personObj = json.get("count_person");

            if (count_personObj instanceof Integer) {
                int count_person = (Integer) count_personObj;
                if (count_person > 0) {
                    JSONArray array = (JSONArray) json.get("person");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject person = array.getJSONObject(i);
                        String personStr = person.toString(4);
                        //     System.out.println(personStr);

                        String personName = (String) person.get("name");
                        System.out.println(personName);
                        File personFile = new File(peopleDir, personName + ".json");
                        StringUtilities.writeTextToFile(personFile, personStr);

                    }
                }
            }
            else {
                System.out.println(response);
            }

            StringUtilities.writeTextToFile(outFile, json.toString(4));

            outWriter.println(response);
            if (validated++ >= numberToValidate)
                break;
            System.out.println("read " + validated);
        }
        outWriter.close();
    }

    public static boolean contrubuterIsValid(Contributer c) {
        String firstName = c.getFirstName();
        if (isEmpty(firstName))
            return false;
        if (!nameValid(firstName))
            return false;
        if (isEmpty(c.getLastName()))
            return false;
        ContributorAddress address = c.getAddress();
        if (address == null)
            return false;
        return true;
     //  return isValidAddress(address);
    }

    private static boolean nameValid(String firstName) {
        if (isEmpty(firstName))
            return false;
        for (int i = 0; i < firstName.length(); i++) {
            char c = firstName.charAt(i);
            if (Character.isWhitespace(c))
                return false;
            if (!Character.isLetter(c))
                return false;

        }
        return true;
    }

    private static boolean isValidAddress(ContributorAddress address) {
        if (isEmpty(address.getStreet()))
            return false;
        if (isEmpty(address.getCity()))
            return false;

        if (address.getState() == null)
            return false;
        return true;

    }

    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static void main(String[] args) throws Exception {
        //downloadPeople(args);
        // validateContributors(args);

    }

}
