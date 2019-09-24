package com.lordjoe.loeb.fec;

import com.lordjoe.loeb.State;

import java.io.*;
import java.util.*;

import static com.lordjoe.loeb.fec.CandidateCommitteeLink.readLinkssFromFEC;
import static com.lordjoe.loeb.fec.FECCandidate.readCandidatesFromFEC;
import static com.lordjoe.loeb.fec.FECCommittee.determineCommitteeParty;
import static com.lordjoe.loeb.fec.FECCommittee.readCommitteesFromFEC;
import static com.lordjoe.loeb.fec.SpecificContributor.mightBeSpecificContributor;
import static com.lordjoe.loeb.fec.SpecificContributor.readSpecificContributorsFromFile;
import static com.lordjoe.loeb.fec.ZipCode.getAllZips;
import static com.lordjoe.loeb.fec.ZipCode.getKnownZipToDistrict;

/**
 * com.lordjoe.loeb.fec.IndividualContributions
 * User: Steve
 * Date: 7/15/19
 */
public class IndividualContributions {

    public static final double MAXIMUM_CONTRIBUTION = 5000;
    public static final double HIGH_CONTRIBUTION = MAXIMUM_CONTRIBUTION * 0.95;

    public static final IndividualContributions INSTANCE = new IndividualContributions();


    private long nunberContributions = 0;
    private Set<FECContributor> contributors = new HashSet<>();
    // do not reeenter contributions already seen
    private Set<Long> seen_contributions = new HashSet<>();

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
    // see https://www.fec.gov/campaign-finance-data/contributions-individuals-file-description/
    public static final int UNIQUE_ID = 20;



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

        // handle duplicates
        Long id = Long.parseLong(items[UNIQUE_ID]);
        if(seen_contributions.contains(id))
            return;
        seen_contributions.add(id);

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


    // for partial matches
    private static class RankedContributor implements Comparable<RankedContributor> {
        public final double rank;
        public final FECContributor contributor;

        public RankedContributor(double rank, FECContributor contributor) {
            this.rank = rank;
            this.contributor = contributor;
        }

        @Override
        public int compareTo(RankedContributor rankedContributor) {
            int ret = Double.compare( rankedContributor.rank,rank);
            if(ret == 0)
                ret = contributor.name.compareTo(rankedContributor.contributor.name) ;
            return ret;
        }

        @Override
        public String toString() {
            return contributor + " " + rank;
        }
    }


    public static final double MINIMUM_DISTANCE = 0.9;
    public static final int MAX_SIMILAR_SIZE = 3;

    private static  List<FECContributor>  findPossibleContributor(SpecificContributor allContributor ) {
        List<RankedContributor> temp = new ArrayList<>();
        List<FECContributor> ret = new ArrayList<>();
        Set<FECContributor> test = INSTANCE.contributors;
          for (FECContributor fecContributor : test) {
              if (mightBeSpecificContributor(fecContributor.name)) {
                  if (fecContributor.lastName.equalsIgnoreCase(allContributor.lastName)) {
                      double dx = Utilities.similarityMeasure(allContributor.firstName, fecContributor.firstName);
                      temp.add(new RankedContributor(dx, fecContributor));
                  }
              }
          }
        Collections.sort(temp);
        for (RankedContributor t : temp) {
             if(t.rank <  MINIMUM_DISTANCE)
                 break;
             if(ret.size() > MAX_SIMILAR_SIZE)
                 break;
             ret.add(t.contributor);
        }
        return ret;
    }

    private static void showAmbiguousContributor(SpecificContributor allContributor) {
        System.out.println("Not Found " + allContributor);
    }

    private static  List<FECContributor> findContributor(SpecificContributor allContributor) {
        List<FECContributor> ret = new ArrayList<>();
        Set<FECContributor> test = INSTANCE.contributors;
        for (FECContributor fecContributor : test) {
            if (mightBeSpecificContributor(fecContributor.name)) {
                if (fecContributor.lastName.equalsIgnoreCase(allContributor.lastName)) {
                    if (fecContributor.firstName.equalsIgnoreCase(allContributor.firstName)) {
                        ret.add(fecContributor)  ;
                    }
                }
            }
        }
        return ret;
    }

    private static void showFECContributor(FECContributor fecContributor) {
        String s = fecContributor.toTabbedString();
        System.out.println(s);
        String s1 = fecContributor.toContributionString();
        System.out.println(s1);
     }


    private static List<FECContributor> showSpecificContributors() {
        List<FECContributor> ret = new ArrayList<>();
        Set<SpecificContributor> allContributors = new HashSet<>(SpecificContributor.allContributors);
        Set<SpecificContributor> foundContributors = new HashSet<>();

        for (SpecificContributor allContributor : allContributors) {
            List<FECContributor> found = findContributor(allContributor);
            if (found != null && found.size() > 0) {
                ret.addAll(found) ;
                foundContributors.add(allContributor);
                for (FECContributor fecContributor : found) {
                    showFECContributor(fecContributor);

                }
            }

        }

        allContributors.removeAll(foundContributors);

        for (SpecificContributor allContributor : allContributors) {
            List<FECContributor> best  = findPossibleContributor(allContributor);
            if(best != null && best.size() > 0) {

                System.out.println(allContributor + " "  + allContributor.geographic + " -> Possible Matches");
                for (FECContributor fecContributor : best) {
                    showFECContributor(fecContributor);

                }
                System.out.println("=======");
            }
        }
        return ret;
    }

    public static List<FECContributor> largestContributorsOfParty(PoliticalParty p, double miniumuContribution) {
        List<FECContributor> ret = new ArrayList<>();
        for (FECContributor contributor : INSTANCE.contributors) {
            PoliticalParty prefered = contributor.getPrimaryParty();
            if (p != prefered)
                continue;
            double amt = contributor.getTotalContributions();
            if (amt < miniumuContribution)
                continue;
            ret.add(contributor);
        }
        ret.sort(Utilities.ByContributions);
        return ret;
    }

    public static void writeLargestDoners(PrintWriter px, List<FECContributor> largest) {
        for (FECContributor fecContributor : largest) {
            writeDoner(px, fecContributor);
        }
        px.close();
    }

    private static void writeDoner(PrintWriter px, FECContributor fecContributor) {
        String s = fecContributor.toTabbedString();
        px.println(s);
        String s1 = fecContributor.toContributionString();
        px.println(s1);
    }

    private static void handleContributorCompanies() {
        for (FECContributor contributor : INSTANCE.contributors) {
            FECCompany company = contributor.getCompany();
            if (company != null) {
                company.addEmployee(contributor);
                for (AccountContributions contribution : contributor.getContributions()) {
                    //                      contribution.committee.
                }
            }
        }
        if (false) {
            for (FECCompany allCompany : FECCompany.getAllCompanies()) {
                System.out.println(allCompany);
            }
        }
    }


    private static void showSpecificContributors(File output) throws IOException {
        List<FECContributor> spseific = showSpecificContributors();

        PrintWriter px = new PrintWriter(new FileWriter(output));
        for (FECContributor fecContributor : spseific) {
            writeDoner(px, fecContributor);
        }
    }



    private static void showCommittees() {
        listTrumpCommittees();
        if(true)
             return;
        List<FECCommittee> byContributions = FECCommittee.getCommitteesByContributions(50000.0, 1000);
        for (FECCommittee c : byContributions) {
            double totalContributions = c.getTotalContributions();
            System.out.println(c.name + " " + Utilities.formatMoney(totalContributions) + " " + c.getNumberContributions());
        }
    }


    public static void listTrumpCommittees()
    {
        List<FECCommittee> byContributions = getTrumpCommittees();
        for (FECCommittee c : byContributions) {
            double totalContributions = c.getTotalContributions();
            System.out.println(c.id + "\t" + c.name + " " + Utilities.formatMoney(totalContributions) + " " + c.getNumberContributions());

        }
    }


    public static  List<FECCommittee> getTrumpCommittees()
    {
        List<FECCommittee> ret = new ArrayList<>() ;
        List<FECCommittee> byContributions = FECCommittee.getCommitteesByContributions();
        for (FECCommittee c : byContributions) {
            if(c.name.toUpperCase().contains("TRUMP")) {
                double totalContributions = c.getTotalContributions();
                if(totalContributions < 20000)
                    continue;
                ret.add(c);
            }

        }
        return ret;
    }

    public static  List<FECCommittee> getDemocraticCommittees()
    {
        List<FECCommittee> ret = new ArrayList<>() ;
        List<FECCommittee> byContributions = FECCommittee.getCommitteesByContributions();
        for (FECCommittee c : byContributions) {
             if(c.getPresumedParty() == PoliticalParty.DEMOCRAT)
                ret.add(c);
        }
        return ret;
    }

    public static void showTrumpHighRollers()
    {
        Set<FECContributor> highRollers = new HashSet<>() ;
        final List<FECCommittee> forTrump = getTrumpCommittees();
        for (FECCommittee c : forTrump) {
            List<FECContributor> contributors = c.getContributors();
            for (FECContributor contributor : contributors) {
                AccountContributions accountContributions = contributor.getAccountContributions(c);
                if(accountContributions.getTotalContributions() < HIGH_CONTRIBUTION)
                    continue;
                highRollers.add(contributor);
            }
         }
        List<FECContributor> highRollersByContribution = new ArrayList<>(highRollers) ;
        highRollersByContribution.sort(new Comparator<FECContributor>() {
            @Override
            public int compare(FECContributor fecContributor, FECContributor t1) {
                double c1 = fecContributor.getAccountContributions(forTrump);
                double c2 = t1.getAccountContributions(forTrump);
                int ret = Double.compare(c2, c1);
                if(ret == 0)
                    return fecContributor.name.compareTo(t1.name) ;
                return ret;
            }
        });
        for (FECContributor c : highRollersByContribution) {
            double totalContributions = c.getAccountContributions(forTrump);
            System.out.println(  c.name + " " + Utilities.formatMoney(totalContributions) + " " + c.getNumberContributions(forTrump));

        }

    }

    public static class ByContribution implements Comparator<FECContributor> {
        final List<FECCommittee> forTrump;

        public ByContribution(List<FECCommittee> forTrump) {
            this.forTrump = forTrump;
        }

        @Override
        public int compare(FECContributor t2, FECContributor t1) {
            double c1 = t2.getAccountContributions(forTrump);
            double c2 = t1.getAccountContributions(forTrump);
            int ret = Double.compare(c2, c1);
            if(ret == 0)
                return t2.name.compareTo(t1.name) ;
            return ret;
        }
    }

    public static void showHighRollersByState(double minContribution, List<FECCommittee> forTrump)
    {
        Set<FECContributor> highRollers = new HashSet<>() ;
         for (FECCommittee c : forTrump) {
            List<FECContributor> contributors = c.getContributors();
            for (FECContributor contributor : contributors) {
                AccountContributions accountContributions = contributor.getAccountContributions(c);
                if(accountContributions.getTotalContributions() < minContribution)
                    continue;
                highRollers.add(contributor);
            }
        }

        Map<State,List<FECContributor>>  byState = new HashMap<>();
        State[] usStates = State.getUSStates();
        for (int i = 0; i < usStates.length; i++) {
            State usState = usStates[i];
            List<FECContributor> thisState = new ArrayList<>();
            byState.put(usState,thisState);
            for (FECContributor highRoller : highRollers) {
                 if(highRoller.state == usState)
                     thisState.add(highRoller);
            }
        }

        for (int i = 0; i < usStates.length; i++) {
            State usState = usStates[i];
            List<FECContributor> thisState = byState.get(usState);
            thisState.sort(new ByContribution(forTrump));
            System.out.println("====== " + usState + " ===============");
            for (FECContributor c : thisState) {
                double totalContributions = c.getAccountContributions(forTrump);
                System.out.println(  c.name + " " + Utilities.formatMoney(totalContributions) + " " + c.getNumberContributions(forTrump));

            }

        }
        if(true)
            return;
        List<FECContributor> highRollersByContribution = new ArrayList<>(highRollers) ;
        highRollersByContribution.sort(new ByContribution(forTrump));
        for (FECContributor c : highRollersByContribution) {
            double totalContributions = c.getAccountContributions(forTrump);
            System.out.println(  c.name + " " + Utilities.formatMoney(totalContributions) + " " + c.getNumberContributions(forTrump));

        }

    }


    public static void showHighRollersByDistrict(double minContribution, List<FECCommittee> forTrump)
    {
        Set<FECContributor> highRollers = new HashSet<>() ;
        for (FECCommittee c : forTrump) {
            List<FECContributor> contributors = c.getContributors();
            for (FECContributor contributor : contributors) {
                AccountContributions accountContributions = contributor.getAccountContributions(c);
                if(accountContributions.getTotalContributions() < minContribution)
                    continue;
                highRollers.add(contributor);
            }
        }

        Map<CongressionalDistrict,List<FECContributor>>  byDistrict = new HashMap<>();
        List<CongressionalDistrict> allDistricts = CongressionalDistrict.getAllDistricts();
        for (CongressionalDistrict allDistrict : allDistricts) {
            List<FECContributor> thisState = new ArrayList<>();
            for (FECContributor highRoller : highRollers) {
                String zipcode = highRoller.zipcode;
                ZipCode z = ZipCode.getById(zipcode);
                if(allDistrict.containsZipCode(z)   )
                    thisState.add(highRoller) ;
            }
            if(!thisState.isEmpty())
                byDistrict.put(allDistrict,thisState) ;

        }


        for (CongressionalDistrict allDistrict : allDistricts) {
            List<FECContributor> fecContributors = byDistrict.get(allDistrict);
            if(fecContributors == null)
                continue;
            System.out.println("====== " + allDistrict + " ===============");
            fecContributors.sort(new ByContribution(forTrump));
            for (FECContributor c : fecContributors) {
                double totalContributions = c.getAccountContributions(forTrump);
                System.out.println(  c.name + " " + Utilities.formatMoney(totalContributions) + " " + c.getNumberContributions(forTrump));

            }
        }


    }


    public static void main(String[] args) throws Exception {
        int index = 0;
        File output = new File(args[4]);

        File f = new File(args[5]);
        readSpecificContributorsFromFile(  f);

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

        handleContributorCompanies();

       // showSpecificContributors(output);
 
        showCommittees();

     //   final List<FECCommittee> forTrump = getTrumpCommittees();
    //    showHighRollersByState(4500,forTrump);

        List<ZipCode> zips = getAllZips();
        getKnownZipToDistrict();


        final List<FECCommittee> democratic = getDemocraticCommittees();
        showHighRollersByDistrict(4500,democratic);

        //  showTrumpHighRollers() ;

        //      List<FECContributor>  largest =  largestContributorsOfParty(PoliticalParty.DEMOCRAT,10000);
        //       writeLargestDoners(new PrintWriter(new FileWriter(output)),largest);

    }

}
