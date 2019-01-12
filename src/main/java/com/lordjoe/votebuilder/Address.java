package com.lordjoe.votebuilder;

import com.lordjoe.loeb.State;

import java.util.Objects;

/**
 * com.lordjoe.votebuilder.Address
 * User: Steve
 * Date: 7/30/2018
 */
public class Address implements Comparable<Address> {
    public static final String DEFAULT_CITY = "Kirkland";
    public static final String DEFAULT_ZIP = "98033";
    public static final State DEFAULT_STATE = State.WASHINGTON;

    public final String address;
    public final String city;
    public final String zip;
    public final State state;


    public Address(String address) {
        this.address = address.trim();
        city = DEFAULT_CITY;
        zip = DEFAULT_ZIP;
        state = DEFAULT_STATE;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address1 = (Address) o;
        return Objects.equals(address, address1.address) &&
                Objects.equals(city, address1.city) &&
                Objects.equals(zip, address1.zip) &&
                state == address1.state;
    }

    @Override
    public int hashCode() {

        return Objects.hash(address, city, zip, state);
    }

    @Override
    public String toString() {
        return  address;
    }

    public String toGMapsURL() {
        return "http://maps.google.com/?q=" +
                address.replace(" ","%20%") +
                zip;
    }

    @Override
    public int compareTo(Address o) {
        return address.compareTo(o.address);
    }
}
