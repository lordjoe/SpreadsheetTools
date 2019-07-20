package com.lordjoe.loeb.fec;

import java.io.*;
import java.util.*;

import static com.lordjoe.loeb.fec.IndividualContributions.parseFullName;

/**
 * com.lordjoe.loeb.fec.SpecificContributor
 * User: Steve
 * Date: 7/18/19
 */
public class SpecificContributor implements Comparable<SpecificContributor> {

    public static  final Set<SpecificContributor>  allContributors = new HashSet<>();
    public static  final Set<String>  allContributorsLastNames = new HashSet<>();


    public static boolean mightBeSpecificContributor(String fullName)   {
        Set<String> test = getContributorsLastNames();
        try {
            String[] strings = parseFullName(fullName);
            String lastName = strings[0].toUpperCase().trim();
            if(test.contains(lastName)) {
                String first = strings[1];
                return true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;

        }
        return false;
    }

    private static Set<String> getContributorsLastNames() {
        if(allContributorsLastNames.size() == 0) {
            for (SpecificContributor allContributor : allContributors) {
                allContributorsLastNames.add(allContributor.lastName.toUpperCase());
            }
        }
        return allContributorsLastNames;
    }

    public final String name;
    public final String firstName;
    public final String lastName;
    public final String middleName;
    public final String geographic;


    public SpecificContributor(String firstName, String lastName, String middleName, String geographic) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.geographic = geographic;
        StringBuilder sb = new StringBuilder();
        sb.append(lastName);
        sb.append(", ");
        sb.append(firstName);
        if(middleName != null && middleName.length() > 0)
            sb.append(" " + middleName);
        name = sb.toString();

        allContributors.add(this) ;
    }

    public static void readSpecificContributorsFromFile(File f)  {
        LineNumberReader rdr = null;
        try  {
            rdr = new LineNumberReader(new FileReader(f));
            String line = rdr.readLine();
            line = rdr.readLine();
            while(line != null)  {
                addSpecificContributorFromLine( line  ) ;
                line = rdr.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
        finally {
            if(rdr != null)   {
                try {
                    rdr.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);

                }
            }
        }

    }
    public static SpecificContributor addSpecificContributorFromLine(String line  )  {
          String[] items = line.split("\t", -1);
        String first =  items[0];
        String last =  items[1];
        String middle =  items[2];
        String geographic =  items[3];

        return new  SpecificContributor(  first,last, middle,geographic);
    }



    public static void writeSpecificContributors (File f)   {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(f));
            List<SpecificContributor> byName = new ArrayList<>(allContributors);
            Collections.sort(byName);
            for (SpecificContributor specificContributor : byName) {
                System.out.println(specificContributor.name);
            };
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }


    @Override
    public int compareTo(SpecificContributor o) {
        return name.compareTo(o.name);
    }


    @Override
    public String toString() {
        return   name ;
    }



    public static void main(String[] args) {
        File f = new File(args[0]);
        readSpecificContributorsFromFile(  f);
        File out = new File(args[1]);
        writeSpecificContributors(out);
    }


}
