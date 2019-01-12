package com.lordjoe.loeb.contributer;

import java.util.List;

/**
 * com.lordjoe.loeb.contributer.VerifiedEntity
 * User: Steve
 * Date: 4/13/2018
 */
public interface VerifiedEntity {



    public List<Evidence> getEvidence();

    public void addEvidence(Evidence added) ;

}
