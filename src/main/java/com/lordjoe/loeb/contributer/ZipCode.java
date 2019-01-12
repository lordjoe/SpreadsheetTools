package com.lordjoe.loeb.contributer;

/**
 * com.lordjoe.loeb.contributer.PhoneNumber
 * User: Steve
 * Date: 4/13/2018
 */
public class ZipCode extends BaseVerifiedEntity {

    public static ZipCode fromString(String inp) {
        if (inp == null || inp.length() == 0)
            return null;
        return new ZipCode(inp);
    }

    public static String conditionNumber(String inp) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inp.length(); i++) {
            char c = inp.charAt(i);
            if (Character.isDigit(c))
                sb.append(c);
        }
        String ret = sb.toString();
        while (ret.length() < 5)
            ret = "0" + ret;
        if (ret.length() == 5)
            return ret;

        if (ret.length() != 9)
            throw new IllegalArgumentException("5 or 9 digits required ");

        return sb.toString();
    }

    public final String number;

    public ZipCode(String number) {
        this.number = conditionNumber(number);
    }

    public String getAreaCode() {
        return number.substring(0, 3);
    }

    public String getDefiner() {
        return number.substring(3, 6) + "-" + number.substring(6, 10);
    }

    @Override
    public String toString() {
        if(number.length() > 5)
            return number.substring(0,5)  + "-" + number.substring(5,number.length()) ;
        return number;
    }
}
