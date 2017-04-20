package com.lordjoe.farestart;

/**
 * com.lordjoe.farestart.Business_Type
 * User: Steve
 * Date: 4/17/2017
 */
public enum Business_Type {
    Educational,Corporate,Wedding,Social,Non_dash_Profit,Government,Internal,Donor,
    Military,Religious,Fraternal,Unmapped;
    @Override
    public String toString() {
        return super.toString().replace("_dash_","-").replace("_"," ");
    }


    public static Business_Type fromString(String name) {
        if(name.equalsIgnoreCase("Non-Profit"))
            return  Non_dash_Profit;
        return Business_Type.valueOf(name);
    }


    public static Business_Type remap(String party_name,CateringObject obj,AdditionalAccounts added) {
        String acct = obj.getRawColumnData(RemapType.Account_Category.toString());
        String event = obj.getRawColumnData(RemapType.Category.toString());
        String party = obj.getRawColumnData(RemapType.Party_Name.toString());
        String themeStr = obj.getRawColumnData(RemapType.Theme.toString());
        Object acct_type =  obj.getRealColumnData(RemapType.Account_Category.toString());

        String client = obj.getRawColumnData("Client/Organization" );

         // if known account use account
        if(client != null) {
            AdditionalAccount additional = added.getAdditionalAccount(client);
            if (additional != null)
                return additional.type;
        }

        if(acct != null)   {
            String testValue = acct.toLowerCase();
            if(testValue.contains("non profit"))
                return  Non_dash_Profit;
            if(testValue.contains("individual"))
                return  Social;
            if(testValue.contains("school"))
                return  Educational;
            if(testValue.contains("corporate"))
                return  Corporate;
        }

        String testValue = null;
        if(party_name != null) {
             testValue = party_name.toLowerCase();
            if (testValue.contains("wedding"))
                return Wedding;
            if (testValue.contains("fundrais"))
                return Non_dash_Profit;
            if (testValue.contains("donor"))
                return Donor;
            if (testValue.contains("birthday"))
                return Social;
            if (testValue.contains("mitzvah"))
                return Religious;
            if (testValue.contains("sales"))
                return Corporate;
            if (testValue.contains("company"))
                return Corporate;
            if (testValue.contains("corporation"))
                return Corporate;
            if (testValue.contains("bank"))
                return Corporate;
            if (testValue.contains("pickup"))
                return Corporate;
            if (testValue.contains("school"))
                return Educational;
            if (testValue.contains(" uw "))
                return Educational;
            if (testValue.contains(" fs "))
                return Internal;
            if (testValue.contains("fs "))
                return Internal;
            if (testValue.contains("private"))
                return Fraternal;
        }

        if(client != null)  {
            testValue = client.toLowerCase();
            if(testValue.contains(" fs "))
                return Internal;
            if(testValue.contains("fs "))
                return Internal;
            if(testValue.contains(" king county "))
                return Internal;
            if(testValue.contains("king county "))
                return Internal;

        }

        if(themeStr != null) {
            String these = themeStr.toLowerCase();
            if (these.contains("non profit"))
                return Non_dash_Profit;
            if (these.contains("business"))
                return Corporate;
        }

        return  Unmapped;
    }

}
