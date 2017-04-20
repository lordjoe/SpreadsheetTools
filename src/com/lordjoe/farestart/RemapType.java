package com.lordjoe.farestart;

/**
 * com.lordjoe.farestart.RemapType
 * User: Steve
 * Date: 4/17/2017
 */
public enum RemapType {
    Account_Category("Account" ),
    Party_Name("Business Type" ),
    Theme("Event Type" ),
    Category("Event Category" );


    public static RemapType fromString(String s)  {
        return RemapType.valueOf(s.replace(" ","_"));
    }

    private final String remapTo;

    RemapType(String remapTo) {
        this.remapTo = remapTo;
    }

    @Override
    public String toString() {
        return super.toString().replace("_"," ");
    }


    public String getRemapTo() {
        return remapTo;
    }
}
