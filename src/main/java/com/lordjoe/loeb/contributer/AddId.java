package com.lordjoe.loeb.contributer;

import com.lordjoe.utilities.IdRepository;
import com.lordjoe.utilities.StringUtilities;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * com.lordjoe.loeb.contributer.AddId
 * User: Steve
 * Date: 4/18/2018
 */
public class AddId {
    public static final AddId[] EMPTY_ARRAY = {};


    public static void main(String[] args) {
        int idStart = 1000;

        int index = 0;
        File eduRequestFile = new File(args[index++]);
        File goodRequestFile = new File(args[index++]);
        File badRequestFile = new File(args[index++]);
        File rawIdentifiedFile = new File("Identified.txt");

        List<String> rawLines = new ArrayList<String>();
        List<String> badLines = new ArrayList<String>();
        List<Contributer> ret = new ArrayList<Contributer>();
        LineNumberReader rdr = null;
        String headers;
        try

        {
            rdr = new LineNumberReader(new FileReader(eduRequestFile));
            headers = rdr.readLine();
            String line = rdr.readLine();
            line = rdr.readLine();
            int attempted = 0;

            while (line != null) {
                line = line.trim();
                attempted++;
                String[] strings = StringUtilities.lineToValues(line);
                strings[0] =  IdRepository.getNewId("RawPerson");
                String rebuildLine = StringUtilities.valuesToLine(strings);
                rawLines.add(rebuildLine);
                try {
                    Contributer e = new Contributer(strings);
                     if (e.isValidName())
                        ret.add(e);
                     else
                         badLines.add(rebuildLine);
                    //             System.out.println(line);
                } catch (Exception e1) {
                    badLines.add(rebuildLine);
                }

                line = rdr.readLine();
            }


            PrintWriter raw = new PrintWriter(new FileWriter(rawIdentifiedFile));
            for (String c : rawLines) {
                raw.println(c);
            }
            raw.close();

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

            System.out.println("Attempted " + attempted + " Succeeded " + ret.size());
        } catch (
                IOException e)

        {
            throw new RuntimeException(e);

        } finally

        {
            try {
                if (rdr != null)
                    rdr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);

            }
            System.out.println("Requests " + ret.size());

        }
    }
}

 
