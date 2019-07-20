package com.lordjoe.loeb.fec;

import com.lordjoe.loeb.State;

import java.util.*;

/**
 * com.lordjoe.loeb.fec.FECContributor
 * User: Steve
 * Date: 7/13/19
 */
public class FECContributor {

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


    private FECContributor(String firstName, String lastName, State state, String city, String zipcode, String employer, String occupation) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.state = state;
        this.city = city;
        this.zipcode = zipcode;
        this.employer = employer;
        this.occupation = occupation;
        this.name = lastName + " ," + firstName;
        byName.put(name, this);
        IdentifyingInformation id = new IdentifyingInformation(firstName, lastName, state, city, zipcode);
        byData.put(id, this);
    }

    public AccountContributions getAccountContributions(FECCommittee comm) {
        AccountContributions v = contributions.get(comm);
        if (v == null) {
            v = new AccountContributions(comm);
        }
        contributions.put(comm, v);
        return v;
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

    public double getTotalContributions() {
        double ret = 0;
        for (FECCommittee s : contributions.keySet()) {
            ret += contributions.get(s).getTotalContributions();
        }
        return ret;
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
            if(zipcode.length() > 5)
                zipcode = zipcode.substring(0,5) ;
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


