package com.lordjoe.loeb.fec;

import java.util.HashMap;
import java.util.Map;

/**
 * com.lordjoe.loeb.fec.PoliticalParty
 * User: Steve
 * Date: 7/19/19
 */
public enum PoliticalParty {
    DEMOCRAT, REPUBLICAN, LIBERTARIAN, INDEPENDENT,GREEN, SOCIALIST, NONE, OTHER;

    public static Map<PoliticalParty, Integer> candidateCount = new HashMap<>();
    public static Map<String, Integer> partyCount = new HashMap<>();

    public static PoliticalParty toParty(String name) {
        PoliticalParty ret = OTHER;
        if ("W".equalsIgnoreCase(name))
            ret = NONE;
        if ("N".equalsIgnoreCase(name))
            ret = NONE;
        if ("NLP".equalsIgnoreCase(name))
            ret = NONE;
        if ("NOP".equalsIgnoreCase(name))
            ret = NONE;
        if ("UN".equalsIgnoreCase(name))
            ret = NONE;
        if ("UNK".equalsIgnoreCase(name))
            ret = NONE;
        if ("NNE".equalsIgnoreCase(name))
            ret = NONE;
        if ("NPA".equalsIgnoreCase(name))
            ret = NONE;
        if ("".equalsIgnoreCase(name))
            ret = NONE;
        if ("GRE".equalsIgnoreCase(name))
            ret = GREEN;
        if ("LIB".equalsIgnoreCase(name))
            ret = LIBERTARIAN;
        if ("REP".equalsIgnoreCase(name))
            ret = REPUBLICAN;
        if ("SWP".equalsIgnoreCase(name))
            ret = SOCIALIST;
        if ("SUS".equalsIgnoreCase(name))
            ret = SOCIALIST;
        if ("SOC".equalsIgnoreCase(name))
            ret = SOCIALIST;
        if ("GOP".equalsIgnoreCase(name))
            ret = REPUBLICAN;
          if ("IND".equalsIgnoreCase(name))
            ret = INDEPENDENT;
        if ("DEM".equalsIgnoreCase(name))
            ret = DEMOCRAT;
        if ("DFL".equalsIgnoreCase(name))
            ret = DEMOCRAT;

        Integer val = candidateCount.get(ret) ;
        if(val != null)
            candidateCount.put(ret,val + 1) ;
        else
            candidateCount.put(ret,  1) ;

        if(OTHER == ret) {
            val = partyCount.get(name);
            if (val != null)
                partyCount.put(name, val + 1);
            else
                partyCount.put(name, 1);
        }

        return ret;
    }

}
