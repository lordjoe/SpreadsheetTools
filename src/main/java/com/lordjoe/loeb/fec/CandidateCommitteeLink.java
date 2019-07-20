package com.lordjoe.loeb.fec;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import static com.lordjoe.loeb.fec.FECCandidate.readCandidatesFromFEC;
import static com.lordjoe.loeb.fec.FECCommittee.readCommitteesFromFEC;

/**
 * com.lordjoe.loeb.fec.CandidateCommitteeLink
 * User: Steve
 * Date: 7/19/19
 */
public class CandidateCommitteeLink {
    public static final CandidateCommitteeLink[] EMPTY_ARRAY = {};

    public static void addLinkFromFile(File input) {
        LineNumberReader rdr = null;
        try {
            rdr = new LineNumberReader(new FileReader(input));
            String line = rdr.readLine();
            while (line != null) {
                addLinkFromLine(line);
                line = rdr.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        } finally {
            if (rdr != null) {
                try {
                    rdr.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);

                }
            }
        }
    }

    static int index = 0;
    public static final int CANDIDATE_ID_POSITION = index++;
    public static final int COMMITTEE_ID_POSITION = 3;


    public static void addLinkFromLine(String line) {

        String strRegex = "\\Q|\\E";   //  https://www.baeldung.com/java-regexp-escape-char
        String[] items = line.split(strRegex);

        String id = items[CANDIDATE_ID_POSITION];
        FECCandidate candidate = FECCandidate.getById(id);
        String committeeIdStr = items[COMMITTEE_ID_POSITION];
        FECCommittee committee = FECCommittee.getById(committeeIdStr);
        committee.addCandidate(candidate);
    }

    public static void readLinkssFromFEC(File f) {
        if (f.isDirectory()) {
            File[] items = f.listFiles();
            if (items != null) {
                for (int i = 0; i < items.length; i++) {
                    File item = items[i];
    //                System.out.println("Adding link " + item.getName());
                    addLinkFromFile(item);
                }
            }
        } else {
            addLinkFromFile(f);

        }
    }


    public static void main(String[] args) {
        int index = 0;
        File f;
        f = new File(args[index++]);
        readCommitteesFromFEC(f);

        f = new File(args[index++]);
        readCandidatesFromFEC(f);


        f = new File(args[index++]);
        readLinkssFromFEC(f);

    }

}
