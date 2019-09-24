package com.lordjoe.loeb.fec;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * com.lordjoe.loeb.fec.CompanyContributors
 * User: Steve
 * Date: 7/22/19
 */
public class CompanyContributors {
    private Map<FECCompany, Set<FECContributor>> byCompany;

    public void addContributor(FECContributor c)  {
        FECCompany company = c.getCompany();
        if(company == null) return;

        Set<FECContributor> existingset = byCompany.get(company);
        if(existingset == null)   {
            existingset = new HashSet<>();
            byCompany.put(company,existingset)  ;
        }
        existingset.add(c) ;
    }

    public FECCompany getLargestCompany()
    {
        FECCompany ret = null;
        int maxContributors = 0;
        for (FECCompany fecCompany : byCompany.keySet()) {
            Set<FECContributor> fecContributors = byCompany.get(fecCompany);
            if(fecContributors.size() > maxContributors)  {
                maxContributors =  fecContributors.size();
                ret = fecCompany;
            }
        }
         return ret;
    }

    public   Set<FECContributor> getCompanyContributors(FECCompany company)
    {
        Set<FECContributor> existingset = byCompany.get(company);
        if(existingset == null)
            return new HashSet<>();
        return existingset;
    }

}
