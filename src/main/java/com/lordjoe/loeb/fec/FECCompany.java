package com.lordjoe.loeb.fec;

import java.util.*;

/**
 * com.lordjoe.loeb.fec.FECCompany
 * User: Steve
 * Date: 7/21/19
 */
public class FECCompany implements Comparable<FECCompany> {

    public static String cleanUpCompanyName(String s)     {
        if(s == null)
            return "";
        s = s.toUpperCase();
        s = s.replace("  "," ");
        s = s.replace(",","");
        s = s.replace(".","");
        s = s.replace("\'","");
        s = s.replace("[","");
        s = s.replace("]","");
        s = s.replace("\\","");
        s = s.replace("{","");
        s = s.replace("}","");
        s = s.replace("¿","");
        s = s.replace("`","");
        if(s.endsWith(" INC") || s.endsWith(" LLC") || s.endsWith(" LLP") || s.endsWith(" LTD"))
            s = s.substring(0,s.length() - 4);
        s = s.trim();
        return s;
    }

    private static final Map<String,FECCompany> allCompanies = new HashMap<>();

    public static List<FECCompany>  getAllCompanies() {
        ArrayList<FECCompany> ret = new ArrayList< >(allCompanies.values() );
        Collections.sort(ret);
        return ret;
    }

    public static FECCompany getCompany(String s)   {
          s = s.toUpperCase();
        FECCompany ret = allCompanies.get(s) ;
        if(ret == null) {
            ret = new FECCompany(s);
       //     System.out.println("Creating Company " + s);
        }
        return ret;
    }
    public static FECCompany maybeGetCompany(String s)   {
        if(filterNonCompany(s))
            return null;
         return getCompany(s);
    }

    private static boolean filterNonCompany(String s) {
        if(s == null)
            return true;
        if("".equalsIgnoreCase(s))
            return true;
        if("NONE".equalsIgnoreCase(s))
            return true;
        if("BLANK".equalsIgnoreCase(s))
            return true;

        if("RETIRED".equalsIgnoreCase(s))
            return true;

        if("EMPLOYER".equalsIgnoreCase(s))
            return true;

        if("SELF".equalsIgnoreCase(s))
            return true;

        if("SELF EMPLOYED".equalsIgnoreCase(s))
            return true;

        return false;
    }

    public final String name;
    private Set<FECContributor>  employees = new HashSet<>();

    private FECCompany(String name) {
        this.name = name.toUpperCase();
        allCompanies.put( this.name,this);
    }

    public void addEmployee(FECContributor c)  {
        employees.add(c);
    }

    public List<FECContributor> getEmployees()  {
        return new ArrayList<>(employees);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(FECCompany fecCompany) {
        return name.compareTo(fecCompany.name);
    }
}
