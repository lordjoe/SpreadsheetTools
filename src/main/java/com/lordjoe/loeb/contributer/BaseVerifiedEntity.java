package com.lordjoe.loeb.contributer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * com.lordjoe.loeb.contributer.BaseVerifiedEntity
 * User: Steve
 * Date: 4/13/2018
 */
public class BaseVerifiedEntity  implements VerifiedEntity {

    public static void appendNotNull(Object append,StringBuilder sb)  {
        if(append != null)
            sb.append(append.toString());
        else
            sb.append("");
        sb.append("\t");

    }

    public  static <T> T bestMember(List<T> choices)    {
        if(choices.isEmpty())
            return null;
        if(choices.size() == 1)
            return choices.get(0);
        return choices.get(0);
        // throw new UnsupportedOperationException("Fix This"); // ToDo
    }
    private final Set<Evidence> evidence = new HashSet<>();

    public   boolean isVerified() {
        return !getEvidence().isEmpty();
    }


    @Override
    public List<Evidence> getEvidence() {
        return new ArrayList<>(evidence);
    }

    @Override
    public void addEvidence(Evidence added) {
        evidence.add(added);
    }
}
