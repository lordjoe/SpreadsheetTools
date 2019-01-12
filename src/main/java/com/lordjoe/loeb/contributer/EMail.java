package com.lordjoe.loeb.contributer;

import java.util.Objects;

/**
 * com.lordjoe.loeb.contributer.PhoneNumber
 * User: Steve
 * Date: 4/13/2018
 */
public class EMail extends BaseVerifiedEntity implements Comparable<EMail> {

    public static EMail fromString(String inp)  {
        if (inp == null || inp.length() == 0)
            return null;
        return new EMail(inp);
    }


    public static String conditionEmail(String inp)
    {
        inp = inp.trim();
        String[] parts = inp.split("@") ;
        if(parts.length != 2)
            throw new IllegalArgumentException("problem"); // ToDo change

        StringBuilder sb = new StringBuilder();
        sb.append(parts[0]) ;
        sb.append("@") ;
        sb.append(parts[1]) ;


        return sb.toString();
    }

    public final String email;

    public EMail(String number) {
        this.email = conditionEmail(number);
    }

    @Override
    public String toString() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EMail eMail = (EMail) o;
        return Objects.equals(email, eMail.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public int compareTo(EMail o) {
        return email.compareTo(o.email);
    }

    public String getDomain() {
        String ret = email.substring(email.lastIndexOf('@') + 1) ;
        return ret;
    }

    public String getTopDomain() {
        String ret = email.substring(email.lastIndexOf('.') + 1) ;
        return ret;
    }
}
