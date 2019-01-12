package com.lordjoe.utilities;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * com.lordjoe.utilities.EquivalentNames
 * User: Steve
 * Date: 4/23/2018
 */
public class EquivalentNames {

    public static final String NICKNAMES_FILE = "/NickNames.txt";
    private static Map<String, Set<String>> equivalency = new HashMap<>();

    public static Map<String, Set<String>> getEquivaslencies() {
        if (equivalency.isEmpty()) {
            buildEquivalencies();
        }
        return equivalency;
    }

    private static void buildEquivalencies() {
        InputStream inputStream = EquivalentNames.class.getResourceAsStream(NICKNAMES_FILE);
        String s = StringUtilities.readStream(inputStream);
        String[] lines = s.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.startsWith("#"))
                continue;
            processNicknameLine(line.toUpperCase());
        }

    }

    private static Set<String> getEquivalency(String s) {
        Set<String> nn = equivalency.get(s);
        if (nn == null) {
            nn = new HashSet<>();
            equivalency.put(s, nn);
        }
        return nn;
    }


    private static void processNicknameLine(String line) {
        String[] items = line.split("\t");
        if (items.length < 2)
            return;
        String nickName = items[0].trim().toUpperCase();
        String longName = items[1].trim().toUpperCase();
        Set<String> nn = getEquivalency(nickName);
        nn.add(longName);
        Set<String> ln = getEquivalency(longName);
        ln.add(nickName);
    }

    public static boolean isEquivalent(String name1, String name2) {
        name1 = name1.trim().toUpperCase();
        name2 = name2.trim().toUpperCase();
        if(name1.equals(name2))
            return true;
        Map<String, Set<String>> e = getEquivaslencies();
        Set<String> nickNames = e.get(name1);
        if (nickNames != null && nickNames.contains(name2))
            return true;
        e = getEquivaslencies();
        nickNames = e.get(name2);
        if (nickNames != null && nickNames.contains(name1))
            return true;
        return false;
    }

    public static void main(String[] args) {
        if (!isEquivalent("bill", " william"))
            throw new UnsupportedOperationException("Fix This"); // ToDo;
        if (!isEquivalent("bob", " robert"))
            throw new UnsupportedOperationException("Fix This"); // ToDo;
        if (!isEquivalent("robert", " bob"))
            throw new UnsupportedOperationException("Fix This"); // ToDo;
        if (!isEquivalent("betty", " elizabeth"))
            throw new UnsupportedOperationException("Fix This"); // ToDo;
        if (isEquivalent("tom", " dick"))
            throw new UnsupportedOperationException("Fix This"); // ToDo;
        if (isEquivalent("ann", " sue"))
            throw new UnsupportedOperationException("Fix This"); // ToDo;
    }

}
