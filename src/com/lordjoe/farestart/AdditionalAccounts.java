package com.lordjoe.farestart;

import java.io.File;
import java.text.CollationElementIterator;
import java.util.*;

import static com.lordjoe.farestart.Main.readTSVFile;

/**
 * com.lordjoe.farestart.AdditionalAccounts
 * User: Steve
 * Date: 4/18/2017
 */
public class AdditionalAccounts {
    public static final String ADDITIONAL_ACCOUNTS_FILE = "FareStartCategories.txt";
    public static final String ADDITIONAL_ACCOUNTS_HEADER = "Additional Accounts";
    public static final String ADDITIONAL_ACCOUNTS_TYPE = "Account_Business_Type";

    private final Map<String,AdditionalAccount> additionalAccounts;
     private final Set<AdditionalAccount> additionalAccountsUsed;

    public AdditionalAccounts() {
        File f = new File(ADDITIONAL_ACCOUNTS_FILE);
        Map<String, List<String>> stringListMap = readTSVFile(new File(ADDITIONAL_ACCOUNTS_FILE));
        List<String> additional =  stringListMap.get(ADDITIONAL_ACCOUNTS_HEADER);
        List<String> additional_type =  stringListMap.get(ADDITIONAL_ACCOUNTS_TYPE);
        additionalAccountsUsed = new HashSet<AdditionalAccount>();
        additionalAccounts = new HashMap<String, AdditionalAccount>() ;
        for (int i = 0; i < additional.size(); i++) {
             String name =  additional.get(i) ;
            Business_Type type = Business_Type.fromString(additional_type.get(i));
            additionalAccounts.put(name.toLowerCase(),new AdditionalAccount(name,type));
        }

    }

    public void clearUse() {
        additionalAccountsUsed.clear();
    }

    public List<AdditionalAccount> allAccountsUsed()
    {
        List<AdditionalAccount> ret = new ArrayList<AdditionalAccount>();
        if(additionalAccountsUsed.size() == additionalAccounts.size())
            return ret;
        for (String acct : additionalAccounts.keySet()) {
            AdditionalAccount realAcct =  additionalAccounts.get(acct);
                if(!additionalAccountsUsed.contains(realAcct))
                    ret.add(realAcct);
        }
        Collections.sort(ret);
        return ret;
    }

    public AdditionalAccount getAdditionalAccount(String s) {
        String key = s.toLowerCase();

        AdditionalAccount s1 = additionalAccounts.get(key);
        if(s1 != null)
            additionalAccountsUsed.add(s1);
        return s1;
    }
}
