package com.lordjoe.farestart;

/**
 * com.lordjoe.farestart.Account
 * User: Steve
 * Date: 4/17/2017
 */
public enum Account {
    SMERF, Corporate, Weddings, Government, Internal, Non_dash_Profit,FS_DEVO, Unmapped;

    public String toString() {
        return super.toString().replace("_dash_", "-").replace("_", " ");
    }


    public static String remap(String party_name, CateringObject obj,AdditionalAccounts added) {
        String acct = obj.getRawColumnData(RemapType.Account_Category.toString());
        String event = obj.getRawColumnData(RemapType.Category.toString());
        String party = obj.getRawColumnData(RemapType.Party_Name.toString());
        String themeStr = obj.getRawColumnData(RemapType.Theme.toString());
        String client = obj.getRawColumnData("Client/Organization" );

        int year = obj.getYear();

        if(client != null) {
            AdditionalAccount additional = added.getAdditionalAccount(client);
            if (additional != null)
                return additional.name;
        }

        if (party_name != null) {

            String testValue = party_name.toLowerCase();


            if (testValue.contains("fs devo"))
                return FS_DEVO.toString() + " " + year;
            if (testValue.contains("wedding"))
                return Weddings.toString() + " " + year;
            if (testValue.contains("school"))
                return SMERF.toString() + " " + year;
            if (testValue.contains("donor"))
                return Non_dash_Profit.toString() + " " + year;
            ;
            if (testValue.contains("government"))
                return Government.toString() + " " + year;
            ;
            if (testValue.contains("non profit"))
                return Non_dash_Profit.toString() + " " + year;
            ;
            if (testValue.contains("corporate")) {
                return Corporate.toString() + " " + year;
            }
            if (testValue.contains("internal"))
                return Internal.toString() + " " + year;
            if (testValue.contains("individual"))
                return SMERF.toString() + " " + year;
        }

        if (themeStr != null) {
            String these = themeStr.toLowerCase();
            if (these.contains("non profit"))
                return Non_dash_Profit.toString() + " " + year;
            ;
            if (these.contains("business"))
                return Corporate.toString() + " " + year;
            if (these.contains("city"))
                return Government.toString() + " " + year;
            if (these.contains("state"))
                return Government.toString() + " " + year;
        }

        return Unmapped.toString();
    }
}
