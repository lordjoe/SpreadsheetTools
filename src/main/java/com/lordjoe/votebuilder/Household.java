package com.lordjoe.votebuilder;

/**
 * com.lordjoe.com.lordjoe.votebuilder.Voter
 * User: Steve
 * Date: 7/30/2018
 */

import com.lordjoe.sandhurst.Person;
import com.lordjoe.sandhurst.Sandhurst;
import com.lordjoe.sandhurst.SandhurstStreets;
import com.lordjoe.utilities.StringUtilities;

import java.io.File;
import java.util.*;

/**
 * com.lordjoe.votebuilder.Voter
 * User: Steve
 * Date: 4/4/2018
 */
public class Household implements Comparable<Household> {

    private static Map<Address, Household> byAddress = new HashMap<>();

    public static Household getByAddress(Address adr) {
        Household ret = byAddress.get(adr);
        if (ret == null) {
            ret = new Household(adr);
            byAddress.put(adr, ret);
        }
        return ret;
    }

    public static Household mergeWithVoter(Household h, Person v) {
        h.addPerson(v);
        return h;
    }

    public static List<Household> mergeVoters(List<Household> SandHurst, List<Person> democrate) {
        Set<Household> ret = new HashSet<Household>();

        Map<Address, Household> byAddress = new HashMap<>();
        for (Household voter : SandHurst) {
            Household h = Household.getByAddress(voter.address);
            byAddress.put(voter.address, voter);
        }
        for (Person voter : democrate) {
            Household h = byAddress.get(voter.address);
            if (h == null)
                continue;
            Household e = mergeWithVoter(h, voter);
            ret.add(e);
        }


        ArrayList<Household> households = new ArrayList<>(ret);
        Collections.sort(households);
        return households;
    }


    public static List<Household> fromSandhurst(List<String> lines) {
        List<Household> ret = new ArrayList<Household>();
        for (String line : lines) {
            Household v = buildFromLine(line);
            if (v != null)
                ret.add(v);
        }


        return ret;
    }

    public static Household buildFromLine(String line) {
        String[] split = line.split("\t");
        if (split[0].equals("Owner"))
            return null; // skip title
        int index = 0;
        String name = split[index++];
        Address address = new Address(split[index++]);
        String email = split[index++];
        String phone = split[index++];
        Integer age = null;
        List<String> names = new ArrayList<String>();
        if (name != null && name.length() > 0)
            names.add(name);
        Party party = null;
        Household v = Household.getByAddress(address);
        if (name.length() > 0) {
            String[] split1 = name.split(":");
            for (int i = 0; i < split1.length; i++) {
                String realName = split1[i];
                Person o = Person.getPerson(realName, v.address);
                o.setEmail(email);
                o.setPhone(phone);
                o.setAge(age);

                v.addPerson(o);

            }
        }
        return v;
    }

    public static final Map<String, Household> byEmail = new HashMap<>();

    public static Household getByEMail(String email) {
        return byEmail.get(email);
    }

    public final Set<Person> names = new HashSet<>();
    public final Address address;

    private Household(Address address) {
        this.address = address;
    }

    public void addPerson(Person p) {
        names.add(p);
    }

    public List<Person> getPeople() {
        List<Person> ret = new ArrayList<>(names);
        Collections.sort(ret);
        return ret;
    }

    public Person getPerson(String name) {
        for (Person p : names) {
            if (p.name.equals(name)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return address.toString();
    }

    public String toTabbedLine() {

        StringBuilder sb = new StringBuilder();

        sb.append(toPersonString());
        sb.append("\t");
        sb.append(address.address);
        sb.append("\t");
     //   String phone = getPhone();
     //   if (phone != null)
     //       sb.append(getPhone());
        sb.append("\t");
    //    if (getEmail() != null)
    //        sb.append(getEmail());
        sb.append("\t");
    //    if (_party != null)
     //       sb.append(getParty());

        return sb.toString();
    }

    private String toPersonString() {
        StringBuilder sb = new StringBuilder();

        List<Person> people = getPeople();
        int size = people.size();
        for (int i = 0; i < size; i++) {
            Person person = people.get(i);
            sb.append(person.name);
            if(i < size - 1)
                sb.append(":");
        }

        return sb.toString();
    }


    public static List<String> readLines(File f1) {
        String s = StringUtilities.readFile(f1);
        String[] split = s.split("\n");
        return Arrays.asList(split);
    }

    @Override
    public int compareTo(Household o) {
        int ret = address.compareTo(o.address);
        if (ret != 0)
            return ret;

        return toString().compareTo(o.toString());
    }

    public static Sandhurst getSandhurst( File f1,File f2 ) {
        Sandhurst snd = new Sandhurst();
        List<Person> voters = Voter.fromVotebuilder(readLines(f1));
        List<String> lines = readLines(f2);
        List<Household> sandHurst = fromSandhurst(lines);

        List<Person> sandhurstPeople = new ArrayList<>();
        Set<Address> sandhurstAddress = new HashSet<>();
        for (Household democrat : sandHurst) {
            snd.addHousehold(democrat);
            sandhurstAddress.add(democrat.address);
            sandhurstPeople.addAll(democrat.getPeople());
        }

        List<Person> democratPeople = new ArrayList<>();
        for (Person democrat : voters) {
            if (sandhurstAddress.contains(democrat.address))
                democratPeople.add(democrat);
        }

        List<Household> democrats = mergeVoters(sandHurst, voters);

        for (Household democrat : democrats) {
            snd.addHousehold(democrat);
        }
        return snd;
    }


    public static void main(String[] args) {
        File f1 = new File(args[0]);
        File f2 = new File(args[1]);

        Sandhurst snd = getSandhurst( f1,f2);

        int NPeople = 0;
        snd.save(System.out);
         System.out.println("People " + NPeople);
    }


}
