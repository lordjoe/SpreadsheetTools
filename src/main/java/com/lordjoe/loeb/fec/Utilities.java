package com.lordjoe.loeb.fec;

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

}
