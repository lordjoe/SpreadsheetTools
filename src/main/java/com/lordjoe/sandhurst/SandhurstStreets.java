package com.lordjoe.sandhurst;

import com.lordjoe.votebuilder.Address;

/**
 * com.lordjoe.sandhurst.Streets
 * User: Steve
 * Date: 8/12/2018
 */
public enum SandhurstStreets {
    NE_44th_St,
    NE_42nd_Pl,
    NE_107th_Pl,
    _106th_Pl_NE,
    _107th_Pl_NE,
    _105th_Ave_NE,
    _106th_Ave_NE,
    _107th_Ave_NE;
    public static final SandhurstStreets[] EMPTY_ARRAY = {};

    @Override
    public String toString() {
        return super.toString().replace("_", " ").trim() ;
    }

    public static SandhurstStreets findStreet(Address adr)   {
        return  findStreet(adr.address);
    }

    public static SandhurstStreets findStreet(String address) {
        for(SandhurstStreets s : SandhurstStreets.values()) {
            if(address.endsWith(s.toString()))
                return s;
        }
        return null;
    }

    public static String findStreetNumber(String address) {
        SandhurstStreets s = findStreet(  address );
        if(s != null)
            return address.replace(s.toString(),"");
        return address;
    }

}
