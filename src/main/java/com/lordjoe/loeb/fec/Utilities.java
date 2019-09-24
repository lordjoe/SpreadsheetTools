package com.lordjoe.loeb.fec;

import info.debatty.java.stringsimilarity.JaroWinkler;

import java.util.Comparator;

/**
 * com.lordjoe.loeb.fec.Utilities
 * User: Steve
 * Date: 7/20/19
 */
public class Utilities {

    public final  static Comparator<IContributor>  ByContributions = new ByTotalContrbutions();


    private static  class ByTotalContrbutions implements Comparator<IContributor>    {
        @Override
        public int compare(IContributor t1 , IContributor t2) {
            return Double.compare(t2.getTotalContributions(),t1.getTotalContributions());
        }
    }


    public static final int ONE_MILLION = 1000000;
    public static String formatMoney(double d)  {
        int test = (int)d;
        String numberAsString = String.format("%,d", test);
        if(test < 10 *  ONE_MILLION)
            return "$" + numberAsString;
        test = test /  ONE_MILLION;
        numberAsString = String.format("%,d", test);
        return "$" + numberAsString + " Million";
    }

    public static final JaroWinkler jw = new JaroWinkler();

    public static  double similarityMeasure(String s1,String s2)   {
         s1 = s1.toUpperCase();
        s2 = s2.toUpperCase();
        if(s1.startsWith("SARA"))
            s1.toUpperCase();
       double d1 = jw.similarity(s1, s2);
        int lx = Math.min(s1.length(),s2.length()) ;
        double d2 = jw.similarity(s1.substring(0,lx), s2.substring(0,lx));

        return Math.max(d1,d2);
    }

}
