package com.lordjoe.farestart;

/**
 * com.lordjoe.farestart.EventType
 * User: Steve
 * Date: 4/17/2017
 */
public enum Event_Type {
    Conferernce, Wedding_Reception, Wedding_Shower, Wedding_Ceremony_and_Reception, Engagement_Celebration, Retreat,
    Baby_Shower, Birthday_Party, Fundraiser, Retirement,
    Holiday_Party, Anniversary, Dinner, Lunch, Breakfast, Happy_Hour, Press_Release, Meeting, Memorial, Mitzvah, Networking, Other, Unmapped;

    @Override
    public String toString() {
        return super.toString().replace("_and_", "_&_").replace("_", " ");
    }


    public static Event_Type remap(String theme, CateringObject obj) {
        String acct = obj.getRawColumnData(RemapType.Account_Category.toString());
        String event = obj.getRawColumnData(RemapType.Category.toString());
        String party = obj.getRawColumnData(RemapType.Party_Name.toString());
        String themeStr = obj.getRawColumnData(RemapType.Theme.toString());
        Object acct_type = obj.getRealColumnData(RemapType.Account_Category.toString());

        String client = obj.getRawColumnData("Client/Organization");

        String testValue = null;

        if (theme != null) {
             testValue = theme.toLowerCase();
            if (testValue.contains("networking"))
                return Networking;

            if (testValue.contains("wedding")) {
                if (testValue.contains("shower"))
                    return Wedding_Shower;
                if (testValue.contains("reception")) {
                    if (testValue.contains("ceremony"))
                        return Wedding_Ceremony_and_Reception;
                    return Wedding_Reception;
                }

            }

            if (testValue.contains("baby shower"))
                return Baby_Shower;
            if (testValue.contains("retirement"))
                return Retirement;
            if (testValue.contains("fundraiser"))
                return Fundraiser;
            if (testValue.contains("birthday"))
                return Birthday_Party;
            if (testValue.contains("anniversary"))
                return Anniversary;
            if (testValue.contains("mitzvah"))
                return Mitzvah;
            if (testValue.contains("holiday"))
                return Holiday_Party;
            if (testValue.contains("funeral"))
                return Memorial;
            if (testValue.contains("memorial"))
                return Memorial;
            if (testValue.contains("meeting"))
                return Meeting;
            if (testValue.contains("retreat"))
                return Retreat;
            if (testValue.contains("conference"))
                return Conferernce;
            if (testValue.contains("press"))
                return Press_Release;
            if (testValue.contains("engagement"))
                return Engagement_Celebration;
            if (testValue.contains("release"))
                return Press_Release;
            if (testValue.contains("happy hour"))
                return Happy_Hour;
            if (testValue.contains("breakfast"))
                return Breakfast;
            if (testValue.contains("lunch"))
                return Lunch;
            if (testValue.contains("dinner"))
                return Dinner;
            if (testValue.contains("happy"))
                return Happy_Hour;
        }

        if (party != null) {
            testValue = party.toLowerCase();
            if (testValue.contains("baby shower"))
                return Baby_Shower;
            if (testValue.contains("retirement"))
                return Retirement;
            if (testValue.contains("fundraiser"))
                return Fundraiser;
            if (testValue.contains("birthday"))
                return Birthday_Party;
            if (testValue.contains("anniversary"))
                return Anniversary;
            if (testValue.contains("mitzvah"))
                return Mitzvah;
            if (testValue.contains("holiday"))
                return Holiday_Party;
            if (testValue.contains("funeral"))
                return Memorial;
            if (testValue.contains("memorial"))
                return Memorial;
            if (testValue.contains("meeting"))
                return Meeting;
            if (testValue.contains("retreat"))
                return Retreat;
            if (testValue.contains("conference"))
                return Conferernce;
            if (testValue.contains("press"))
                return Press_Release;
            if (testValue.contains("engagement"))
                return Engagement_Celebration;
            if (testValue.contains("release"))
                return Press_Release;
            if (testValue.contains("happy hour"))
                return Happy_Hour;
            if (testValue.contains("breakfast"))
                return Breakfast;
            if (testValue.contains("lunch"))
                return Lunch;
            if (testValue.contains("dinner"))
                return Dinner;
            if (testValue.contains("happy"))
                return Happy_Hour;
            if (testValue.contains("reception"))
                return Other;
        }
        if (event != null) {
            testValue = event.toLowerCase();
            if (testValue.contains("baby shower"))
                return Baby_Shower;
            if (testValue.contains("retirement"))
                return Retirement;
            if (testValue.contains("fundraiser"))
                return Fundraiser;
            if (testValue.contains("birthday"))
                return Birthday_Party;
            if (testValue.contains("anniversary"))
                return Anniversary;
            if (testValue.contains("mitzvah"))
                return Mitzvah;
            if (testValue.contains("holiday"))
                return Holiday_Party;
            if (testValue.contains("funeral"))
                return Memorial;
            if (testValue.contains("memorial"))
                return Memorial;
            if (testValue.contains("meeting"))
                return Meeting;
            if (testValue.contains("retreat"))
                return Retreat;
            if (testValue.contains("conference"))
                return Conferernce;
            if (testValue.contains("press"))
                return Press_Release;
            if (testValue.contains("engagement"))
                return Engagement_Celebration;
            if (testValue.contains("release"))
                return Press_Release;
            if (testValue.contains("happy hour"))
                return Happy_Hour;
            if (testValue.contains("breakfast"))
                return Breakfast;
            if (testValue.contains("lunch"))
                return Lunch;
            if (testValue.contains("dinner"))
                return Dinner;
            if (testValue.contains("happy"))
                return Happy_Hour;
        }

        return Unmapped;
    }

}
