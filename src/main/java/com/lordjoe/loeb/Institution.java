package com.lordjoe.loeb;

import java.util.*;

/**
 * com.lordjoe.loeb.Instutution
 * User: Steve
 * Date: 4/6/2018
 */
public class Institution {

    public static final String[] IGNORED_INSTITUTIONS = {
            "gmail.com" , "aol.com" ,  "mac.com",  "hotmail.com",     "msn.com",   "yahoo.com",
            "earthlink.net",  "sbcglobal.net", "verizon.net",  "comcast.net", "att.net",
            "cox.net",  "frontier.con" 

    } ;



    public static final Set<String>  TO_IGNORE = new HashSet<>(Arrays.asList(IGNORED_INSTITUTIONS));

    public static final Map<String,Institution> byID = new HashMap<>();

    public static void showUnknownEdu()
    {
        int count = 0;
        for (String s : byID.keySet()) {
            if(s.endsWith(".edu") )     {
                Institution inst =  getInstitution(s);
                if(inst.stateCount.isEmpty()) {
                    System.out.println("operate{ \"" + s + "\",State.UNKNOWN },");
                    count++;
                }
            }
        }
        System.out.println("Count " + count);
    }

    public static void registerInstitution(EduRequest reg)  {
        Institution inst =  getInstitution(reg.Email);
        if(inst != null)
             inst.setState(reg.state);
    }

    public static State getBestState(String email)  {
        Institution inst =  getInstitution(email);
        if(inst == null) {
            if(email.endsWith(".edu"))
                return FixEDUState.getEmailState(email);
            return State.UNKNOWN;
        }
        return inst.getBestState();
    }

    public static Institution getInstitution(String email)    {

        email = email.toLowerCase();
        if(TO_IGNORE.contains(email))
            return null;
        int indexAt = email.indexOf("@");
        if(indexAt == -1)
            return getInstitutionByAddress(email);
        else
            return getInstitution(email.substring(indexAt + 1,email.length())) ;
    }

    private static Institution getInstitutionByAddress(String email) {
        Institution ret = byID.get(email) ;
        if(ret == null)  {
            ret = new Institution(email);
            byID.put(email,ret);
            if(email.endsWith(".edu"))    {
                State emailState = FixEDUState.getEmailState(email);
                if(emailState != State.UNKNOWN)
                    ret.setState(emailState);
            }
        }
        return ret;
    }

    public final String email;
    private final Map<State,Integer>  stateCount = new HashMap<>() ;

    public Institution(String email) {
        this.email = email;
    }

    public void setState(State st) {
          if(st == State.UNKNOWN)
              return;
          Integer old = stateCount.get(st);
          if(old == null)
              stateCount.put(st,1);
          else
              stateCount.put(st,old + 1);
    }

    public State getBestState()
    {
        if(stateCount.isEmpty())
            return State.UNKNOWN;
        State ret = State.UNKNOWN;
        int bestCount = 0;
        for (State s : stateCount.keySet()) {
             int count = stateCount.get(s);
             if(count > bestCount)  {
                 bestCount = count;
                 ret = s;
             }
        }
        return ret;
    }
}
