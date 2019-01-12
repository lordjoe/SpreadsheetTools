package com.lordjoe.loeb.contributer;

import com.lordjoe.loeb.State;
import com.lordjoe.utilities.StringUtilities;

import java.io.*;
import java.util.*;

/**
 * com.lordjoe.loeb.contributer.IdentifiedPersion
 * User: Steve
 * Date: 9/29/2018
 */
public class IdentifiedPersion {
    public static final Map<EMail, IdentifiedPersion> allPeople = new HashMap<>();

    public static IdentifiedPersion getIdentifiedPerson(EMail email) {
        IdentifiedPersion ret = allPeople.get(email);
        if (ret == null) {
            ret = new IdentifiedPersion(email);
            allPeople.put(email, ret);
        }
        return ret;
    }

    public static List<IdentifiedPersion> getAllPeople() {
        List<IdentifiedPersion> ret = new ArrayList<>(allPeople.values());
        Collections.sort(ret, new Comparator<IdentifiedPersion>() {
            @Override
            public int compare(IdentifiedPersion o1, IdentifiedPersion o2) {
                return o1.email.compareTo(o2.email);
            }
        });
        return ret;
    }

    public final EMail email;
    private String name;

    public IdentifiedPersion(EMail email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private static void readPeople(File f) {
        LineNumberReader rdr = null;
        try {
            rdr = new LineNumberReader(new FileReader(f));
            String line = rdr.readLine();
            while (line != null) {
                final String[] data = StringUtilities.lineToValues(line);
                EMail eml = null;
                for (int i = 0; i < data.length; i++) {
                    String datum = data[i];
                    if(datum.contains("@"))  {
                        eml = new EMail(datum);
                        break;
                    }

                }
                if(eml != null) {
                    IdentifiedPersion person = getIdentifiedPerson(eml);
                    switch (data.length) {
                        case 1:
                            break;
                        case 2:
                            person.setName(data[1]);
                            break;
                        case 7:
                            person.setName(data[5]);
                            break;
                        default:
                            person.setName(data[1] + " " + data[2]);
                            break;
                    }
                }
                  line = rdr.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        } finally {
            try {
                if (rdr != null)
                    rdr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);

            }
        }

    }

    private static void readPeopleInDomains(File f,Set<String> domains,State st) {
        LineNumberReader rdr = null;
        int dropped = 0;
        try {
            rdr = new LineNumberReader(new FileReader(f));
            String line = rdr.readLine();
            while (line != null) {
                final String[] data = StringUtilities.lineToValues(line);
                EMail eml = new EMail(data[0]);
                if(eml.getTopDomain().equals("edu")) {
                    if (domains.contains(eml.getDomain())) {
                        IdentifiedPersion person = getIdentifiedPerson(eml);
                        switch (data.length) {
                            case 1:
                                break;
                            case 2:
                                person.setName(data[1]);
                                break;
                            case 7:
                                person.setName(data[5]);
                                break;
                            default:
                                person.setName(data[1] + " " + data[2]);
                                break;
                        }
                    }
                    else {
                        dropped++;
                    }
                }
                else {
                    for (int i = 1; i < data.length; i++) {
                        State testState = State.fromString(data[1]);
                        if(testState == st) {
                            IdentifiedPersion person = getIdentifiedPerson(eml);
                        }

                    }
                }
                line = rdr.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        } finally {
            try {
                if (rdr != null)
                    rdr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);

            }
        }

    }

    private static Set<String> readPeopleWithDomains(File f) {
        readPeople(f);
        return buildDomains();
    }

    private static Set<String> buildDomains() {
        Set<String> ret = new HashSet<>();
        for (EMail eMail : allPeople.keySet()) {
            if(eMail.getTopDomain().equals("edu")) {
                String domain = eMail.getDomain();
                while(!domain.equals("edu"))    {
                    ret.add(domain);
                    domain = domain.substring(domain.indexOf('.') + 1) ;

                }
            }

        }
        return ret;
    }

    private static void writePerson(IdentifiedPersion allPerson, PrintWriter out) {
        out.println(allPerson.email + "\t" + allPerson.getName());
    }


    public static void main(String[] args) throws Exception {
        File outFile = new File(args[args.length - 2]);
        File outNonEduFile = new File(args[args.length - 1]);
        State st = State.TEXAS;

        Set<String> domains = readPeopleWithDomains(new File(args[0]));

        for (int i = 1; i < args.length - 1; i++) {
            String arg = args[i];
            File f = new File(arg);
            readPeopleInDomains(f,domains,st);

        }
        PrintWriter out = new PrintWriter((new FileWriter(outFile)));

        for (IdentifiedPersion allPerson : getAllPeople()) {
            if(allPerson.email.getTopDomain().equals("edu"))
                writePerson(allPerson, out);
        }
        out.close();
         out = new PrintWriter((new FileWriter(outNonEduFile)));

        for (IdentifiedPersion allPerson : getAllPeople()) {
            if(!allPerson.email.getTopDomain().equals("edu"))
                writePerson(allPerson, out);
        }
        out.close();
    }

 }
