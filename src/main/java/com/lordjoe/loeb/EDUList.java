package com.lordjoe.loeb;

import java.util.*;

/**
 * com.lordjoe.loeb.EDUList
 * User: Steve
 * Date: 4/5/2018
 */
public class EDUList {

    public static final Set<EDUList> notInRequest = new HashSet<>();




    public final String email;
    public   String Full_Name;
    public   State state;

    public EDUList(String email, String full_Name, State state) {
        this.email = email.toLowerCase();;
        Full_Name = full_Name;
        this.state = state;
     }

    public String getEmail() {
        return email;
    }

    public String getFull_Name() {
        return Full_Name;
    }

    public State getState() {
        return state;
    }

    public EDUList(String[] data) {
        int index = 0;
        email = data[index++].toLowerCase();;
        if(data.length > 1)
            Full_Name = data[index++];
        else
            Full_Name = null;

        if("acamino@ndc.edu".equals(email))
            index = 2;  // break here


        while(index < data.length)   {
            String test =  data[index++];
            if(test.length() > 1) {
                this.state = State.fromString(test);
            }
            if(state != null)
                break;
        }

        if(state == null && email.endsWith(".edu")) {
//            System.out.println("rememberEDUState(\"" + email + "\",State.UNKNOWN);");
        }


    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(email);
        sb.append("\t");
        if(Full_Name != null)
            sb.append(Full_Name);
        sb.append("\t");
        if(state != null && state != State.UNKNOWN)
            sb.append(state.getAbbreviation());

        return sb.toString();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EDUList eduList = (EDUList) o;
        return Objects.equals(email, eduList.email);
    }

    @Override
    public int hashCode() {

        return Objects.hash(email);
    }
}
