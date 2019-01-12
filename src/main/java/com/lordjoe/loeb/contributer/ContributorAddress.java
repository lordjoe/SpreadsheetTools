package com.lordjoe.loeb.contributer;

import com.lordjoe.loeb.State;

/**
 * com.lordjoe.loeb.contributer.ContributorAddress
 * User: Steve
 * Date: 4/13/2018
 */
public class ContributorAddress extends BaseVerifiedEntity {

    private String street;
    private String city;
    private State state;
    private ZipCode zip;


    public ContributorAddress() {
    }

    public ContributorAddress(String street, String city, String state, String zip) {
        this(street,city,State.fromString(state),ZipCode.fromString(zip));
    }

    public ContributorAddress(String street, String city, State state, ZipCode zip) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    public ZipCode getZip() {
        return zip;
    }

    public void setZip(ZipCode zip) {
        this.zip = zip;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String toTabbedString() {
        StringBuilder sb = new StringBuilder();
        BaseVerifiedEntity.appendNotNull(street,sb);
        BaseVerifiedEntity.appendNotNull(city,sb);
        BaseVerifiedEntity.appendNotNull(state,sb);
        BaseVerifiedEntity.appendNotNull(zip,sb);

        return sb.toString();
    }
}
