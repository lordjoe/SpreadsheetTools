package com.lordjoe.loeb.fec;

import com.lordjoe.loeb.State;

import java.util.*;

/**
 * com.lordjoe.loeb.fec.FECContributor
 * User: Steve
 * Date: 7/13/19
 */
public class FECContributor implements IContributor {

    public static final Map<IdentifyingInformation, FECContributor> byData = new HashMap<>();
    public static final Map<String, FECContributor> byName = new HashMap<>();

    public static FECContributor getByData(String firstName, String lastName, String city, State st, String zip, String employer, String occupation) {
        IdentifyingInformation id = new IdentifyingInformation(firstName, lastName, st, city, zip);
        FECContributor ret = byData.get(id);
        if (ret == null) {
            ret = new FECContributor(firstName, lastName, st, city, zip, employer, occupation);
        }
        return ret;
    }

    public static List<FECContributor> getAllContributors() {
        return new ArrayList<>(byData.values());
    }

    public final String name;
    public final String firstName;
    public final String lastName;
    public final State state;
    public final String city;
    public final String zipcode;
    public final String employer;
    public final String occupation;
    private final Map<FECCommittee, AccountContributions> contributions = new HashMap<>();
    private double totalContributions = 0;
    private int numberContributions = 0;

    private PoliticalParty primaryParty;


    private FECContributor(String firstName, String lastName, State state, String city, String zipcode, String employer, String occupation) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.state = state;
        this.city = city;
        if(zipcode != null && zipcode.length() > 5)
            zipcode = zipcode.substring(0,5);
        this.zipcode = zipcode;
        this.employer = FECCompany.cleanUpCompanyName(employer);
        this.occupation = occupation;
        this.name = lastName + " ," + firstName;
        byName.put(name, this);
        IdentifyingInformation id = new IdentifyingInformation(firstName, lastName, state, city, zipcode);
        byData.put(id, this);
        totalContributions = 0;
    }

    public PoliticalParty getPrimaryParty() {
        if (primaryParty == null)
            determinePrimaryParty();
        return primaryParty;
    }

    private FECCommittee getOneCommittee() {
        for (FECCommittee committee : contributions.keySet()) {
            return committee;

        }
        throw new UnsupportedOperationException("Never Get here");
     }

     public FECCompany getCompany()
     {
         return FECCompany.maybeGetCompany(employer);
     }

     public List<AccountContributions> getContributions()
     {
         return new ArrayList<>(contributions.values()) ;
     }

    public String toTabbedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lastName);
        sb.append(", ");
        sb.append(firstName);
        sb.append("\t");
        sb.append(city);
        sb.append("\t");
        sb.append(state);
        sb.append("\t");
        sb.append(zipcode);
        sb.append("\t");
        sb.append(employer);
        sb.append("\t");
        sb.append(occupation);
        sb.append("\t");
        sb.append(getPrimaryParty());
        sb.append("\t");
        sb.append(getTotalContributions());
        sb.append("\t");
        sb.append(getNumberContributions());
        sb.append("\t");

        return sb.toString();
    }

    public String toContributionString() {
        Collection<AccountContributions> values = contributions.values();
        List<AccountContributions> ctx = new ArrayList<>(values);
        ctx.sort(new Comparator<AccountContributions>() {
            @Override
            public int compare(AccountContributions accountContributions, AccountContributions t1) {
                return accountContributions.committee.name.compareTo(t1.committee.name) ;
            }
        });
        StringBuilder sb = new StringBuilder();
        for (AccountContributions accountContributions : ctx) {
            sb.append("\t\t\t\t\t");
            sb.append( accountContributions.committee.name);
            sb.append("\t");
            sb.append( accountContributions.getTotalContributions() );
            sb.append("\t");
            sb.append( accountContributions.getNumberContributions() );
            sb.append("\n");

        }

        return sb.toString();
    }

    private void determinePrimaryParty() {
        switch (contributions.size()) {
            case 0:
                primaryParty = PoliticalParty.OTHER;
                return;
            case 1:
                PoliticalParty presumedParty = getOneCommittee().getPresumedParty();
                primaryParty = presumedParty;
                return;
            default:
                primaryParty = getConsenusParty();

        }

    }

    private PoliticalParty getConsenusParty() {
        double democratContributopns = 0;
        double republicanContributopns = 0;
        for (FECCommittee committee : contributions.keySet()) {
            PoliticalParty presumedParty = committee.getPresumedParty();
            if (presumedParty == null)
                continue;
            if (!presumedParty.isMainstream())
                continue;
            switch (presumedParty) {
                case DEMOCRAT:
                    democratContributopns += contributions.get(committee).getTotalContributions();
                    break;
                case REPUBLICAN:
                    republicanContributopns += contributions.get(committee).getTotalContributions();
                    break;
            }
        }
        if (democratContributopns > 2 * republicanContributopns)
            return PoliticalParty.DEMOCRAT;
        if (democratContributopns < 0.5 * republicanContributopns)
            return PoliticalParty.REPUBLICAN;
        return PoliticalParty.OTHER;
    }

    public void setPrimaryParty(PoliticalParty primaryParty) {
        this.primaryParty = primaryParty;
    }

    public AccountContributions getAccountContributions(FECCommittee comm) {
        AccountContributions v = contributions.get(comm);
        if (v == null) {
            v = new AccountContributions(comm,this);
        }
        contributions.put(comm, v);
        return v;
    }


    public double getAccountContributions(List<FECCommittee> comm) {
         double d = 0;
        for (FECCommittee fecCommittee : comm) {
            AccountContributions v = contributions.get(fecCommittee);
            if(v != null)
                d += v.getTotalContributions();
        }

        return d;
    }



    public int getNumberContributions(List<FECCommittee> comm) {
        int number = 0;
        for (FECCommittee fecCommittee : comm) {
            AccountContributions v = contributions.get(fecCommittee);
            if(v != null)
                number += v.getNumberContributions();
       }
        return number;
     }

    public int getNumberContributions() {
        if (numberContributions == 0) {
            for (AccountContributions value : contributions.values()) {
                numberContributions += value.getNumberContributions();
            }
        }
        return numberContributions;
    }

    public double getTotalContributions() {
        if (totalContributions == 0) {
            for (AccountContributions value : contributions.values()) {
                totalContributions += value.getTotalContributions();
            }
        }
        return totalContributions;
    }


    @Override
    public String toString() {
        return name;
    }


    public double addAccountContributions(FECCommittee comm, double amount) {
        AccountContributions v = getAccountContributions(comm);
        v.addContribution(amount);
        return v.getTotalContributions();
    }


    public static class IdentifyingInformation {
        public final String firstName;
        public final String lastName;
        public final State state;
        public final String city;
        public final String zipcode;

        public IdentifyingInformation(String firstName, String lastName, State state, String city, String zipcode) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.state = state;
            this.city = city;
            if (zipcode.length() > 5)
                zipcode = zipcode.substring(0, 5);
            this.zipcode = zipcode;
        }

        @Override
        public String toString() {
            return "IdentifyingInformation{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", state=" + state +
                    ", city='" + city + '\'' +
                    ", zipcode='" + zipcode + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IdentifyingInformation that = (IdentifyingInformation) o;
            return Objects.equals(firstName, that.firstName) &&
                    Objects.equals(lastName, that.lastName) &&
                    state == that.state &&
                    Objects.equals(city, that.city) &&
                    Objects.equals(zipcode, that.zipcode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstName, lastName, state, city, zipcode);
        }
    }


}


