package com.lordjoe.sandhurst;

import com.lordjoe.votebuilder.Address;
import com.lordjoe.votebuilder.Household;

import java.io.*;
import java.util.*;

/**
 * com.lordjoe.sandhurst.Sandhurst
 * User: Steve
 * Date: 8/12/2018
 */
public class Sandhurst {
    private final Map<SandhurstStreets, Set<Household>> byStreet = new HashMap<SandhurstStreets, Set<Household>>();
    private final Map<String, Person> byName = new HashMap<String, Person>();
    private final Map<Person, Household> byPerson = new HashMap<Person, Household>();


    public Sandhurst() {

    }


    public void mergePersons(List<Person> merging) {
        for (Person person : merging) {
            Household h = Household.getByAddress(person.address);
            mergePerson(person, h);
        }
    }


    public List<Person> getPeople() {
        List<Person> ret = new ArrayList<>(byName.values());
        Collections.sort(ret);
        return ret;
    }


    public void mergePerson(Person p, Household h) {
        addHousehold(h.address);

        byPerson.put(p, h);
        byName.put(p.name, p);
    }

    public List<Household> getHouseholds(SandhurstStreets s) {
        Set<Household> households = byStreet.get(s);
        if (households == null) {
            households = new HashSet<Household>();
            byStreet.put(s, households);
        }
        List<Household> ret = new ArrayList<Household>(households);
        Collections.sort(ret, new Comparator<Household>() {
            @Override
            public int compare(Household o1, Household o2) {
                return o1.address.compareTo(o2.address);
            }
        });
        return ret;
    }

    public void addHousehold(Household h) {
        addHousehold(h.address);
        for (Person p : h.names) {
            mergePerson(p, h);
        }
    }

    public void addHousehold(Address adr) {
        Household hse = Household.getByAddress(adr);
        String adrStr = adr.address;
        adrStr = adrStr.replace("  ", " ");
        adrStr = adrStr.toLowerCase();
        for (SandhurstStreets s : SandhurstStreets.values()) {
            String suffix = s.toString();
            suffix = suffix.toLowerCase();
            if (adrStr.endsWith(suffix)) {
                Set<Household> lst = byStreet.get(s);
                if (lst == null) {
                    lst = new HashSet<Household>();
                    byStreet.put(s, lst);
                }
                lst.add(hse);
                return;
            }
        }
        for (SandhurstStreets s : SandhurstStreets.values()) {
            String suffix = s.toString();
            suffix = suffix.toLowerCase();
            if (adrStr.endsWith(suffix)) {
                Set<Household> lst = byStreet.get(s);
                if (lst == null) {
                    lst = new HashSet<Household>();
                    byStreet.put(s, lst);
                }
                lst.add(hse);
                return;
            }
        }

        return;
    }

    public void save(File file) {
        try {
            moveToBackup(file);
            PrintWriter out = new PrintWriter(new FileWriter(file));
            save(out);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
         }


    }

    public static void moveToBackup(File file) {
        if (!file.exists())
            return;
        File newFile = new File(file.getAbsolutePath() + ".bk");
        newFile.delete();
        boolean success = file.renameTo(newFile);
        if (!success)
            throw new IllegalStateException("did not rename " + file.getName() + " to " + newFile.getName());

    }

    public void save(Appendable out) {
        try {
            out.append(Person.toTabbedHeadderLine());
            out.append("\n");
            for (SandhurstStreets s : SandhurstStreets.values()) {
                List<Household> households =  getHouseholds(s);
                for (Household household : households) {
                    List<Person> people = household.getPeople();
                    if(people.isEmpty())  {
                         out.append(household.toTabbedLine());
                        out.append("\n");

                    }
                    else {
                        for (Person person : people) {
                             out.append(person.toTabbedLine());
                            out.append("\n");
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        }

    }
}
