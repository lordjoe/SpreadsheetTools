package com.lordjoe.votebuilder;

/**
 * com.lordjoe.com.lordjoe.votebuilder.Voter
 * User: Steve
 * Date: 7/30/2018
 */

import com.lordjoe.loeb.State;
import com.lordjoe.sandhurst.Person;
import com.lordjoe.utilities.StringUtilities;


import java.io.File;
import java.util.*;

/**
 * com.lordjoe.votebuilder.Voter
 * User: Steve
 * Date: 4/4/2018
 */
public class Voter  {

    public static List<Person> fromVotebuilder(List<String> lines) {
        List<Person> ret = new ArrayList<Person>();
        for (String line : lines) {
            String[] split = line.split("\t");
            if (split[0].equals("Name"))
                continue; // skip title
            int index = 0;
            String name = split[index++];
            Address address = new Address(split[index++]);
            String city = split[index++];
            String phone = split[index++];
            Integer age = null;
            String email = null;

            String agestr = split[index++];
            if (agestr.length() > 0)
                age = new Integer(agestr.trim());
            Party party = Party.Democrat;
            Person o = Person.getPerson(name, address);
            o.setEmail(email);
            o.setPhone(phone);
            o.setAge(age);
            o.setParty(party);
            ret.add(o);
        }


        return ret;
    }

//    public static List<Voter>  mergeVoters(List<Voter> SandHurst,List<Voter> democrate)   {
//        List<Voter> ret = new ArrayList<Voter>();
//
//        Map<Address,Voter> byAddress = new HashMap<>();
//        for (Voter voter : SandHurst) {
//            byAddress.put(voter.address,voter) ;
//        }
//        for (Voter voter : democrate) {
//            Voter voter1 = byAddress.get(voter.address);
//            if(voter1 == null)
//                continue;
//            ret.add(merge(voter1,voter));
//        }
//
//        for (String line : lines) {
//            String[] split = line.split("\t");
//            if(  split[0].equals("Name"))
//                continue; // skip title
//            int index = 0;
//            String name = split[index++];
//            Address address = new Address(split[index++]);
//            String city = split[index++];
//            String phone = split[index++];
//            Integer age = null;
//            String email = null;
//
//            String agestr = split[index++];
//            if(agestr.length() > 0)
//                age = new Integer(agestr.trim());
//            Party party = Party.Democrat;
//            Voter v = new Voter(name,address,email,phone,age,party);
//            ret.add(v) ;
//        }
//
//
//        return ret;
//    }
//

    public static List<Person> fromSandhurst(List<String> lines) {
        List<Person> ret = new ArrayList<Person>();
        for (String line : lines) {
            String[] split = line.split("\t");
            if (split[0].equals("Owner"))
                continue; // skip title
            int index = 0;
            String name = split[index++];
            Address address = new Address(split[index++]);
            String email = split[index++];
            String phone = split[index++];
            Integer age = null;

             Person o = Person.getPerson(name, address);
            o.setEmail(email);
            o.setPhone(phone);
            o.setAge(age);

            ret.add(o);
        }


        return ret;
    }

    public static final Map<String, com.lordjoe.votebuilder.Voter> byEmail = new HashMap<>();

    public static com.lordjoe.votebuilder.Voter getByEMail(String email) {
        return byEmail.get(email);
    }

 
 

    public String toTabbedLine() {
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }


    public static List<String> readLines(File f1) {
        String s = StringUtilities.readFile(f1);
        String[] split = s.split("\n");
        return Arrays.asList(split);
    }

    public static void main(String[] args) {
        File f1 = new File(args[0]);
        List<Person> voters = fromVotebuilder(readLines(f1));
        for (Person voter : voters) {

        }
        File f2 = new File(args[1]);
        List<Person> svoters = fromSandhurst(readLines(f2));
        for (Person voter : svoters) {

        }


    }

}
