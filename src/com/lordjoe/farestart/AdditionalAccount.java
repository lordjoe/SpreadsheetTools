package com.lordjoe.farestart;

/**
 * com.lordjoe.farestart.AdditionalAccount
 * User: Steve
 * Date: 4/19/2017
 */
public class AdditionalAccount implements Comparable<AdditionalAccount> {
    public final String name;
    public final String lower_case_name;
    public final Business_Type type;

    public AdditionalAccount(String name, Business_Type type) {
        this.name = name;
        lower_case_name = name.toLowerCase();
        this.type = type;
    }

    @Override
    public int compareTo(AdditionalAccount o) {
        return name.compareTo(o.name);
    }
}
