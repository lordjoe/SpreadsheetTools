package com.lordjoe.farestart;

/**
 * com.lordjoe.farestart.EventCategory
 * User: Steve
 * Date: 4/17/2017
 */
public enum Category {
        Flagship_OnSite,Flagship_Delivery,Flagship_Pickup,Pacific_Tower_OnSite, Flagship_Offsite,Meeting,Unmapped;

    @Override
    public String toString() {
        return super.toString().replace("_"," ");
    }

    public static Category remap(String value, CateringObject obj) {
        String acct = obj.getRawColumnData(RemapType.Account_Category.toString());
        String event = obj.getRawColumnData(RemapType.Category.toString());
        String party = obj.getRawColumnData(RemapType.Party_Name.toString());
        String themeStr = obj.getRawColumnData(RemapType.Theme.toString());
        String client = obj.getRawColumnData("Client/Organization" );

        if(event != null)    {
            String testValue = event.toLowerCase();
            if (testValue.contains("breakfast"))
                return Flagship_OnSite;
            if (testValue.contains("tasting"))
                return Flagship_OnSite;
            if (testValue.contains("casey family"))
                return Flagship_OnSite;

        }
        if(themeStr != null)    {
            String testValue = themeStr.toLowerCase();
            if (testValue.contains("breakfast"))
                return Flagship_OnSite;
            if (testValue.contains("tasting"))
                return Flagship_OnSite;
            if (testValue.contains("casey family"))
                return Flagship_OnSite;

        }

        if(value != null) {

            String testValue = value.toLowerCase();
            if (testValue.contains("pactower"))
                return Pacific_Tower_OnSite;
            if (testValue.contains("onsite"))
                return Flagship_OnSite;
            if (testValue.contains("on-site"))
                return Flagship_OnSite;
            if (testValue.contains("offsite"))
                return Flagship_Offsite;
            if (testValue.contains("pick up"))
                return Flagship_Pickup;
            if (testValue.contains("pickup"))
                return Flagship_Pickup;
            if (testValue.contains("delivery"))
                return Flagship_Delivery;
            if (testValue.contains("all day meeting"))
                return Meeting;
        }

        if(party != null) {
            String test = party.toLowerCase();
            if (test.contains("pick up"))
                return Flagship_Pickup;
            if (test.contains("pickup"))
                return Flagship_Pickup;
            if (test.contains("all day meeting"))
                return Meeting;
            if (test.contains("tasting"))
                return Flagship_OnSite;
        }
        if(acct != null) {
            String test = acct.toLowerCase();
            if (test.contains("tasting"))
                return Flagship_OnSite;
            if (test.contains("casey family"))
                return Flagship_OnSite;
           }

        return  Unmapped;
    }

}
