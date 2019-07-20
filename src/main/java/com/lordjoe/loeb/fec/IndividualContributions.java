package com.lordjoe.loeb.fec;

import com.lordjoe.loeb.State;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashSet;
import java.util.Set;

import static com.lordjoe.loeb.fec.CandidateCommitteeLink.readLinkssFromFEC;
import static com.lordjoe.loeb.fec.FECCandidate.readCandidatesFromFEC;
import static com.lordjoe.loeb.fec.FECCommittee.determineCommitteeParty;
import static com.lordjoe.loeb.fec.FECCommittee.readCommitteesFromFEC;
import static com.lordjoe.loeb.fec.SpecificContributor.mightBeSpecificContributor;

/**
 * com.lordjoe.loeb.fec.IndividualContributions
 * User: Steve
 * Date: 7/15/19
 */
public class IndividualContributions {
    public static final IndividualContributions INSTANCE = new IndividualContributions();


    private long nunberContributions = 0;
    private Set<FECContributor> contributors = new HashSet<>();

    private IndividualContributions() {
    }

    public void addContributorsFromFile(File input) {
        try {
            LineNumberReader rdr = new LineNumberReader(new FileReader(input));
            String line = rdr.readLine();
            while (line != null) {
                addContributionFromLine(line);
                line = rdr.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    public void registerContributor(FECContributor them) {
        contributors.add(them);
    }

    public static final int COMMITTEE_POSITION = 0;
    static int index = 7;
    public static final int NAME_POSITION = index++;
    public static final int CITY_POSITION = index++;
    public static final int STATE_POSITION = index++;
    public static final int ZIPCODE_POSITION = index++;
    public static final int EMPLOYER_POSITION = index++;
    public static final int OCCUPATION_POSITION = index++;
    public static final int DATE_POSITION = index++;
    public static final int AMOUNT_POSITION = index++;


    private boolean filterFullName(String fullName) {
        //    if(!mightBeSpecificContributor(fullName))
        //        return false;;
        if (!fullName.contains(","))
            return false; // like         ARKAN SOMO & ASSOCIATES
        char first = fullName.charAt(0);
        // cut number of people so we can handle
        switch (first) {
            case 'A':
            case 'B':
            case 'C':
                return true;
            default:
                return true;
        }


    }

    public void addContributionFromLine(String line) {

        String strRegex = "\\Q|\\E";   //  https://www.baeldung.com/java-regexp-escape-char
        String[] items = line.split(strRegex);

        String fullName = items[NAME_POSITION];
        if (!filterFullName(fullName))
            return;
        String[] firstLast = null;
        try {
            firstLast = parseFullName(fullName);
        } catch (ArrayIndexOutOfBoundsException ex) {
            //        System.out.println("Cant handle " + fullName);
            return;
        }
        String last = firstLast[0];
        String first = firstLast[1];
        String city = items[CITY_POSITION];
        String stateName = items[STATE_POSITION];
        String zip = items[ZIPCODE_POSITION];
        String employer = items[EMPLOYER_POSITION];
        String occupation = items[OCCUPATION_POSITION];
        String dateStr = items[DATE_POSITION];
        String amountStr = items[AMOUNT_POSITION];

        double amount = Double.parseDouble(amountStr);
        String committeeId = items[COMMITTEE_POSITION];

        FECCommittee committee = FECCommittee.getById(committeeId);
        State state = State.fromString(stateName);

        FECContributor them = FECContributor.getByData(first, last, city, state, zip, employer, occupation);
        registerContributor(them);

        them.addAccountContributions(committee, amount);

        if (nunberContributions++ % 1000000 == 0)
            System.out.println(fullName + " " + (nunberContributions - 1));
    }

    public static String[] parseFullName(String full) throws ArrayIndexOutOfBoundsException {
        String[] ret = new String[2];
        String[] items = full.split(",");
        String last = items[0];
        ret[0] = last;
        String[] others = items[1].trim().split(" ");
        String first = others[0];
        ret[1] = first;
        return ret;

    }


    private static FECContributor findPossibleContributor(SpecificContributor allContributor) {

        showAmbiguousContributor(allContributor);
        return null;

    }

    private static void showAmbiguousContributor(SpecificContributor allContributor) {
        System.out.println("Not Found " + allContributor);
    }

    private static FECContributor findContributor(SpecificContributor allContributor) {
        Set<FECContributor> test = INSTANCE.contributors;
        for (FECContributor fecContributor : test) {
            if (mightBeSpecificContributor(fecContributor.name)) {
                if (fecContributor.lastName.equalsIgnoreCase(allContributor.lastName)) {
                    if (fecContributor.firstName.equalsIgnoreCase(allContributor.firstName)) {
                        showFECContributor(fecContributor);
                        return fecContributor;
                    }
                }
            }
        }
        return null;
    }

    private static void showFECContributor(FECContributor fecContributor) {
        System.out.println(fecContributor.name + " " + fecContributor.getTotalContributions());
    }


    private static void showSpecificContributors() {
        Set<SpecificContributor> allContributors = new HashSet<>(SpecificContributor.allContributors);
        Set<SpecificContributor> foundContributors = new HashSet<>();

        for (SpecificContributor allContributor : allContributors) {
            FECContributor found = findContributor(allContributor);
            if (found != null) {
                foundContributors.add(allContributor);
            }

        }

        allContributors.removeAll(foundContributors);

        for (SpecificContributor allContributor : allContributors) {
            findPossibleContributor(allContributor);

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

          f = new File(args[index++]);
        if (f.isDirectory()) {
            File[] items = f.listFiles();
            if (items != null) {
                System.out.println("Handling " + items.length + " files");
                for (int i = 0; i < items.length; i++) {
                    File item = items[i];
                    INSTANCE.addContributorsFromFile(item);
                    System.out.println("================= Handled " + item.getName());
                }
            }
        } else {
            INSTANCE.addContributorsFromFile(f);
        }

        determineCommitteeParty();

   //     showSpecificContributors();


    }



}
