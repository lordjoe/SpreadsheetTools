package com.lordjoe.loeb.fec;

/**
 * com.lordjoe.loeb.fec.AccountContributions
 * User: Steve
 * Date: 7/13/19
 */
public class AccountContributions  implements IContributor {

    public AccountContributions(FECCommittee accountName) {
        AccountName = accountName;
    }

    public  final FECCommittee AccountName;

    private int numberContributions = 0;
    private double totalContributions = 0;

    public int getNumberContributions() {
        return numberContributions;
    }

    public double getTotalContributions() {
        return totalContributions;
    }

    public void addContribution(double amt)  {
        if(amt <= 0)
            return;
        if(numberContributions == 0) {
            numberContributions++;
        }
        else {
            numberContributions++;

        }
        totalContributions += amt;

        AccountName.addContribution(amt);
    }


    @Override
    public String toString() {
        return   AccountName.toString() + " "  + totalContributions;
    }

}
