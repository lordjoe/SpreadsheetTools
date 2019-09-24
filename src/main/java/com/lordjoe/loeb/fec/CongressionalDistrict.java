package com.lordjoe.loeb.fec;

import com.lordjoe.loeb.State;

import java.util.*;

/**
 * com.lordjoe.loeb.fec.CongressionalDistrict
 * User: Steve
 * Date: 8/13/19
 */
public class CongressionalDistrict implements Comparable<CongressionalDistrict> {

    public static String toDistrictName(State s, int number)     {
        return s + " " + number;
    }
    private static Map<String,CongressionalDistrict>  byName = new HashMap<>();
    public static Set<State> statesWithOneCongressionalDistrict = new HashSet();
    static {
        statesWithOneCongressionalDistrict.add(State.WYOMING);
        statesWithOneCongressionalDistrict.add(State.NORTH_DAKOTA);
        statesWithOneCongressionalDistrict.add(State.SOUTH_DAKOTA);
        statesWithOneCongressionalDistrict.add(State.ALASKA);
        statesWithOneCongressionalDistrict.add(State.PUERTO_RICO);
        statesWithOneCongressionalDistrict.add(State.DISTRICT_OF_COLUMBIA);
        statesWithOneCongressionalDistrict.add(State.DELAWARE);
        statesWithOneCongressionalDistrict.add(State.MONTANA);
      }

    public static CongressionalDistrict getDistrict(State s, int number)  {
        synchronized (byName) {
            CongressionalDistrict ret = byName.get(toDistrictName(s, number));
            if (ret == null)
                ret = new CongressionalDistrict(s, number);
            return ret;
        }
    }

    public static List<CongressionalDistrict> getAllDistricts()
    {
        ArrayList<CongressionalDistrict> list = new ArrayList<>(byName.values());
        Collections.sort(list);
        return list;
    }

    public final State state;
    public final int number;
    private final Set<ZipCode> zips = new HashSet<>();

    public static CongressionalDistrict getDistrictFromLine(String line) {
        int index = line.lastIndexOf(" ");
        String datum = line.substring(0, index).trim().toUpperCase();
        State s = State.fromString(datum);
        if(statesWithOneCongressionalDistrict.contains(s))
            return getDistrict(s,0);
        String trim = line.substring(index).trim();
        int district = Integer.parseInt(trim);
        return getDistrict(s,district);
    }

    @Override
    public String toString() {
        return toDistrictName(state,number);
    }

    private CongressionalDistrict(State state, int number) {
        this.state = state;
        this.number = number;
        byName.put(this.toString(),this);
    }

    @Override
    public int compareTo(CongressionalDistrict congressionalDistrict) {
        int ret = state.compareTo(congressionalDistrict.state);
        if(ret != 0)
            return ret;
        return Integer.compare(number,congressionalDistrict.number);
    }

    public void addZipCode(ZipCode zip) {
        zips.add(zip) ;
    }

    public boolean containsZipCode(ZipCode z)    {
        return zips.contains(z);
    }
    public List<ZipCode>   getZipCodes(){
        List<ZipCode> ret = new ArrayList<>(zips);
        Collections.sort(ret);
        return ret;
    }
}
