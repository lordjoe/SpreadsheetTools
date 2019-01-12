package com.lordjoe.loeb.contributer;

/**
 * com.lordjoe.loeb.contributer.PhoneNumber
 * User: Steve
 * Date: 4/13/2018
 */
public class PhoneNumber extends BaseVerifiedEntity {


    public static PhoneNumber fromString(String inp)  {
        if (inp == null || inp.length() == 0)
            return null;
        return new PhoneNumber(inp);
    }
    public static String conditionNumber(String inp)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < inp.length(); i++)    {
            char c = inp.charAt(i);
            if(Character.isDigit(c))
                sb.append(c);

        }
        String ret = sb.toString();
        if(ret.length() > 10 && ret.startsWith("1"))
            ret = ret.substring(1,ret.length());
        if(ret.length() != 10)                       {
            if(ret.length() > 10)
                ret = ret.substring(ret.length() - 10,ret.length()) ;
            else
                throw new IllegalArgumentException("Phone needs 10 digits not  " + inp);
        }

        return ret;
    }

    public final String number;

    public PhoneNumber(String number) {
        this.number = conditionNumber(number);
    }

    public String getAreaCode()
    {
        return number.substring(0,3);
    }
    public String getDefiner()
    {
       return number.substring(3,6)    + "-"   + number.substring(6,10) ;
    }

    @Override
    public String toString() {
        return getAreaCode() + "-"   + getDefiner();
    }
}
