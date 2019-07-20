package com.lordjoe.loeb.fec;

import java.io.*;
import java.util.*;

/**
 * com.lordjoe.loeb.fec.FECCommittee
 * User: Steve
 * Date: 7/15/19
 */
public class FECCommittee   implements IContributor {
    public static final Map<String, FECCommittee> byID = new HashMap<>();

    public static FECCommittee getById(String id) {
        return byID.get(id);
    }

    public static Set<FECCommittee> knownDemocraticCommittees = new HashSet<>();
    public static Set<FECCommittee> knownRepublicanCommittees = new HashSet<>();
    public static Set<FECCommittee> knownOtherCommittees = new HashSet<>();

    public static Set<FECCommittee> presumedDemocraticCommittees = new HashSet<>();
    public static Set<FECCommittee> presumedRepublicanCommittees = new HashSet<>();
    public static Map<PoliticalParty, Set<FECCommittee>> partyToCommittee = new HashMap<>();

    static {
        partyToCommittee.put(PoliticalParty.DEMOCRAT, presumedDemocraticCommittees);
        partyToCommittee.put(PoliticalParty.REPUBLICAN, presumedRepublicanCommittees);
    }

    public static PoliticalParty[] PARTIES = {PoliticalParty.DEMOCRAT, PoliticalParty.REPUBLICAN};


    public static void determineCommitteeParty() {
        addCommitteesFromFile(knownDemocraticCommittees, "KnownDemocraticCommittees.tsv");
        addCommitteesFromFile(knownRepublicanCommittees, "KnownRepublicanCommittees.tsv");
        addCommitteesFromFile(knownOtherCommittees, "KnownOtherCommittees.tsv");

        addCommitteesFromName(presumedDemocraticCommittees, knownDemocraticCommittees, "DEMOCRAT");
        addCommitteesFromName(presumedRepublicanCommittees, knownRepublicanCommittees, "REPUBLICAN");


        for (int i = 0; i < PARTIES.length; i++) {
            PoliticalParty party = PARTIES[i];
            Set<FECCommittee> fecCommittees = partyToCommittee.get(party);
            addCommitteesFromCandidates(fecCommittees, party);
        }

        List<FECContributor> contributors = FECContributor.getAllContributors();

        for (FECCommittee presumedDemocraticCommittee : presumedDemocraticCommittees) {
            if(presumedDemocraticCommittee == null)
                continue;
            presumedDemocraticCommittee.setPresumedParty(PoliticalParty.DEMOCRAT);
        }

        for (FECCommittee presumedDemocraticCommittee : presumedRepublicanCommittees) {
            if(presumedDemocraticCommittee == null)
                continue;
            presumedDemocraticCommittee.setPresumedParty(PoliticalParty.REPUBLICAN);
        }

        assignByAssociation(contributors, partyToCommittee);
     }

    private static void assignByAssociation(List<FECContributor> contributors, Map<PoliticalParty, Set<FECCommittee>> partyToCommittee) {

        for (Set<FECCommittee> value : partyToCommittee.values()) {

        }

        for (String s : byID.keySet()) {
            FECCommittee test = byID.get(s);
        }

      }

    public static boolean isKnown(FECCommittee test) {
        if (knownDemocraticCommittees.contains(test))
            return true;
        if (knownRepublicanCommittees.contains(test))
            return true;
        if (knownOtherCommittees.contains(test))
            return true;
        return false;
    }

    private static void addCommitteesFromCandidates(Set<FECCommittee> presumedCommittees, PoliticalParty p) {
        for (String s : byID.keySet()) {
            FECCommittee test = byID.get(s);
            if (isKnown(test))
                continue;
            if (presumedCommittees.contains(test))
                continue;

            List<FECCandidate> associatedCandidates = new ArrayList(test.associatedCandidates);
            if (associatedCandidates == null)
                continue;
            switch (associatedCandidates.size()) {
                case 0:
                    continue;
                case 1:
                    FECCandidate fecCandidate = associatedCandidates.get(0);

                    if (fecCandidate == null)
                        continue;
                    if (fecCandidate.party == p)
                        presumedCommittees.add(test);
                    continue;
                default:
                    verifyPartyFromCandidates(test, presumedCommittees, associatedCandidates, p);
            }

        }
    }

    private static void verifyPartyFromCandidates(FECCommittee test, Set<FECCommittee> presumedCommittees, List<FECCandidate> associatedCandidates, PoliticalParty p) {
        int totalCandidates = associatedCandidates.size();
        int totalOfParty = 0;
        for (FECCandidate associatedCandidate : associatedCandidates) {
            if (associatedCandidate == null)
                continue;
            if (associatedCandidate.party == p)
                totalOfParty++;
        }

        if (totalOfParty > totalCandidates / 2)
            presumedCommittees.add(test);

    }

    private static void addCommitteesFromName(Set<FECCommittee> presumedList, Set<FECCommittee> knownList, String testFragment) {
        presumedList.addAll(knownList);
        for (String s : byID.keySet()) {
            FECCommittee test = byID.get(s);
            if (isKnown(test))
                continue;
            String testName = test.name.toUpperCase();
            if (testName.contains(testFragment))
                presumedList.add(test);

        }
    }

    private static void addCommitteesFromFile(Set<FECCommittee> set, String input) {
        LineNumberReader rdr = null;
        try {
            rdr = new LineNumberReader(new FileReader(input));
            String line = rdr.readLine();
            while (line != null) {
                addKnownCommittee(set, line);
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

    public static void addKnownCommittee(Set<FECCommittee> set, String line) {

        String strRegex = "\\Q|\\E";   //  https://www.baeldung.com/java-regexp-escape-char
        String[] items = line.split(strRegex);

        String id = items[0];
        FECCommittee ret = FECCommittee.getById(id);

        set.add(ret);
    }


    public final String id;
    public final String name;
    public final String zip;
    private Set<FECCandidate> associatedCandidates = new HashSet<>();
    private PoliticalParty presumedParty;

    public FECCommittee(String id, String name, String zip) {
        this.id = id;
        this.name = name;
        this.zip = zip;

        byID.put(id, this);
    }

    private int numberContributions = 0;
    private double totalContributions = 0;

    public int getNumberContributions() {
        return numberContributions;
    }

    public double getTotalContributions() {
        return totalContributions;
    }

    public void addContribution(double amt)  {
        if(amt <= 0)
            return;
        if(numberContributions == 0) {
            numberContributions++;
        }
        else {
            numberContributions++;

        }
        totalContributions += amt;
    }



    public PoliticalParty getPresumedParty() {
        return presumedParty;
    }

    public void setPresumedParty(PoliticalParty presumedParty) {

        this.presumedParty = presumedParty;
    }

    @Override
    public String toString() {
        return name;
    }

    public void addCandidate(FECCandidate c) {
        associatedCandidates.add(c);
    }


    public FECCandidate[] getAssociatedCandidates() {
        return associatedCandidates.toArray(new FECCandidate[0]);
    }


    public static void addCommitteeFromFile(File input) {
        LineNumberReader rdr = null;
        try {
            rdr = new LineNumberReader(new FileReader(input));
            String line = rdr.readLine();
            while (line != null) {
                addCommitteeFromLine(line);
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
    public static final int ID_POSITION = index++;
    public static final int NAME_POSITION = index++;
    public static final int ZIPCODE_POSITION = 7;


    public static FECCommittee addCommitteeFromLine(String line) {

        String strRegex = "\\Q|\\E";   //  https://www.baeldung.com/java-regexp-escape-char
        String[] items = line.split(strRegex);

        String id = items[ID_POSITION];
        FECCommittee ret = getById(id);
        if (ret != null)
            return ret;
        String name = items[NAME_POSITION];
        String zip = "";
        if (items[ZIPCODE_POSITION].length() >= 5)
            zip = items[ZIPCODE_POSITION].substring(0, 5);

        while (zip.length() < 5)
            zip = "0" + zip;

        ret = new FECCommittee(id, name, zip);
        if (false && byID.size() % 1000 == 0)
            System.out.println(name + " " + byID.size());
        return ret;
    }



    public static void readCommitteesFromFEC(File f) {
        if (f.isDirectory()) {
            File[] items = f.listFiles();
            if (items != null) {
                for (int i = 0; i < items.length; i++) {
                    File item = items[i];
                    addCommitteeFromFile(item);
                }
            }
        } else {
            addCommitteeFromFile(f);

        }

    }


    public static void writeCommittee(File f) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(f));
            for (String s : byID.keySet()) {
                FECCommittee comm = getById(s);
                pw.println(comm.id + "\t" + comm.name + "\t" + comm.zip);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    public static void main(String[] args) {
        File f = new File(args[0]);
        readCommitteesFromFEC(f);
        File out = new File(args[1]);
        writeCommittee(out);
    }

}
