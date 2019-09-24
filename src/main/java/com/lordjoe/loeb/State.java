package com.lordjoe.loeb;

/**
 * com.lordjoe.loeb.State
 * User: Steve
 * Date: 4/4/2018
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum State implements Comparable<State> {

    ALABAMA("Alabama", "AL"),
    ALASKA("Alaska", "AK"),
    AMERICAN_SAMOA("American Samoa", "AS",false),
    ARIZONA("Arizona", "AZ"),
    ARKANSAS("Arkansas", "AR"),
    CALIFORNIA("California", "CA"),
    COLORADO("Colorado", "CO"),
    CONNECTICUT("Connecticut", "CT"),
    DELAWARE("Delaware", "DE"),
    DISTRICT_OF_COLUMBIA("District of Columbia", "DC",false),
    FEDERATED_STATES_OF_MICRONESIA("Federated States of Micronesia", "FM",false),
    FLORIDA("Florida", "FL"),
    GEORGIA("Georgia", "GA"),
    GUAM("Guam", "GU",false),
    HAWAII("Hawaii", "HI"),
    IDAHO("Idaho", "ID"),
    ILLINOIS("Illinois", "IL"),
    INDIANA("Indiana", "IN"),
    IOWA("Iowa", "IA"),
    KANSAS("Kansas", "KS"),
    KENTUCKY("Kentucky", "KY"),
    LOUISIANA("Louisiana", "LA"),
    MAINE("Maine", "ME"),
    MARYLAND("Maryland", "MD"),
    MARSHALL_ISLANDS("Marshall Islands", "MH",false),
    MASSACHUSETTS("Massachusetts", "MA"),
    MICHIGAN("Michigan", "MI"),
    MINNESOTA("Minnesota", "MN"),
    MISSISSIPPI("Mississippi", "MS"),
    MISSOURI("Missouri", "MO"),
    MONTANA("Montana", "MT"),
    NEBRASKA("Nebraska", "NE"),
    NEVADA("Nevada", "NV"),
    NEW_HAMPSHIRE("New Hampshire", "NH"),
    NEW_JERSEY("New Jersey", "NJ"),
    NEW_MEXICO("New Mexico", "NM"),
    NEW_YORK("New York", "NY"),
    NORTH_CAROLINA("North Carolina", "NC"),
    NORTH_DAKOTA("North Dakota", "ND"),
    NORTHERN_MARIANA_ISLANDS("Northern Mariana Islands", "MP",false),
    OHIO("Ohio", "OH"),
    OKLAHOMA("Oklahoma", "OK"),
    OREGON("Oregon", "OR"),
    PALAU("Palau", "PW",false),
    PENNSYLVANIA("Pennsylvania", "PA"),
    PUERTO_RICO("Puerto Rico", "PR",false),
    RHODE_ISLAND("Rhode Island", "RI"),
    SOUTH_CAROLINA("South Carolina", "SC"),
    SOUTH_DAKOTA("South Dakota", "SD"),
    TENNESSEE("Tennessee", "TN"),
    TEXAS("Texas", "TX"),
    UTAH("Utah", "UT"),
    VERMONT("Vermont", "VT"),
    VIRGIN_ISLANDS("Virgin Islands", "VI",false),
    VIRGINIA("Virginia", "VA"),
    WASHINGTON("Washington", "WA"),
    WEST_VIRGINIA("West Virginia", "WV"),
    WISCONSIN("Wisconsin", "WI"),
    WYOMING("Wyoming", "WY"),
    ALBERTA("Alberta","AB",false),
    MAMATOBA("Manitoba","MB",false),
    ONTARIO("Ontaria","ON",false),
    BRITISH_COLUMBIA("British Columbia","BC",false),
    SASKATCHEWAN("Saskatchewan","SK",false),
    QUEBEC("Quebec","QC",false),
    PRINCE_EDWARD_ISLAND("Prince Edward Island","PE",false),
    NEWFOUNDLAND("Newfoundland","NL",false),
    NEW_BRUNSWICK("New Brunswick","NB",false),
    NOVA_SCOTIA("Nova Scotia","NS",false),
    UNKNOWN_PROVENCE("Unknown Provence", "",false),
    OVERSEAS("Overseas", "",false),
    UNITED_STATES("United States", "US",false),
    UNKNOWN("Unknown", "",false);

    /**
     * The set of states addressed by abbreviations.
     */
    private static final Map<String, State> STATES_BY_ABBR = new HashMap<String, State>();
    /**
     * The state's name.
     */
    private final String name;

    /**
     * The state's abbreviation.
     */
    private final String abbreviation;

    private final boolean USState;

    /**
     * Constructs a new state.
     *
     * @param name the state's name.
     * @param abbreviation the state's abbreviation.
     */
    State(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
        USState = true;
    }
    /**
     * Constructs a new state.
     *
     * @param name the state's name.
     * @param abbreviation the state's abbreviation.
     */
    State(String name, String abbreviation,boolean isState) {
        this.name = name;
        this.abbreviation = abbreviation;
        USState = isState;
    }

    public boolean isUSState() {
        return USState;
    }

    public static State[] getUSStates()
    {
        List<State> realStates = new ArrayList<>() ;
        State[] values = State.values();
        for (int i = 0; i < values.length; i++) {
            State s = values[i];
            if(s.isUSState())
                realStates.add(s);

        }
        State[] ret = new State[realStates.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = realStates.get(i);
         }
         if(ret.length != 50)
             throw new IllegalStateException("problem"); // ToDo change
         return ret;
    }

    public static State fromString(String datum) {
        State ret = valueOfAbbreviation(datum.toUpperCase().trim());
        if(ret != UNKNOWN)
            return ret;
        try {
            String name = datum.replace(" ", "_").toUpperCase();
            ret = valueOf(name);
            return ret;
        }
        catch(Exception ex) {
            if(datum.length() <= 2)
                return UNKNOWN;
            else
                return fromString(datum.substring(0,2)) ; // CT - Connecticut
        }
    }



    /**
     * Returns the state's abbreviation.
     *
     * @return the state's abbreviation.
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Gets the enum constant with the specified abbreviation.
     *
     * @param abbr the state's abbreviation.
     * @return the enum constant with the specified abbreviation.
     * @throws SunlightException if the abbreviation is invalid.
     */
    public static State valueOfAbbreviation(final String abbr) {
        synchronized (STATES_BY_ABBR) {
            if (STATES_BY_ABBR.isEmpty()) {
                for (State state : values()) {
                    STATES_BY_ABBR.put(state.getAbbreviation(), state);
                }
            }
        }
        final State state = STATES_BY_ABBR.get(abbr);
        if (state != null) {
            return state;
        } else {
            return UNKNOWN;
        }
    }

    public static State valueOfName(final String name) {
        final String enumName = name.toUpperCase().replaceAll(" ", "_");
        try {
            return valueOf(enumName);
        } catch (final IllegalArgumentException e) {
            return State.UNKNOWN;
        }
    }

    @Override
    public String toString() {
        return name;
    }



}