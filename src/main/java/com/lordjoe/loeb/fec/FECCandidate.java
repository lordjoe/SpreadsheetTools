package com.lordjoe.loeb.fec;

import com.lordjoe.loeb.State;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * com.lordjoe.loeb.fec.FECCandidate
 * User: Steve
 * Date: 7/19/19
 */
public class FECCandidate {
    public static final Map<String,FECCandidate> byID = new HashMap<>();

    public static  FECCandidate getById(String id)   {
        return byID.get(id) ;
    }

    public final String id;
    public final String name;
    public final PoliticalParty party;
    public final String office;
    public final State office_state;

    public FECCandidate(String id, String name, PoliticalParty party, String office, State office_state) {
        this.id = id;
        this.name = name;
        this.party = party;
        this.office = office;
        this.office_state = office_state;

        byID.put(id,this);
    }

    
    @Override
    public String toString() {
        return   name ;
    }

    public static  void addCandidateFromFile(File input)  {
        LineNumberReader rdr = null;
        try  {
            rdr = new LineNumberReader(new FileReader(input));
            String line = rdr.readLine();
            while(line != null)  {
                addCandidateFromLine( line  ) ;
                line = rdr.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
        finally {
            if(rdr != null)   {
                try {
                    rdr.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);

                }
            }
        }
    }

    static int index = 0;
    public static final int ID_POSITION = index++;
    public static final int NAME_POSITION = index++;
    public static final int PARTY_POSITION = index++;
    public static final int JUNK_POSITION = index++;
     public static final int STATE_POSITION = index++;
    public static final int OFFICE_POSITION = index++;


    public static FECCandidate addCandidateFromLine(String line  )  {

        String strRegex = "\\Q|\\E";   //  https://www.baeldung.com/java-regexp-escape-char
        String[] items = line.split(strRegex) ;

        String id = items[ID_POSITION];
        FECCandidate ret = getById(id);
        if(ret != null)
            return ret;
        String name = items[NAME_POSITION];
        String partyStr =  items[PARTY_POSITION];
        String stateStr =  items[STATE_POSITION];
        String officeStr =  items[OFFICE_POSITION];

        PoliticalParty party = PoliticalParty.toParty(partyStr) ;
        State state = State.fromString(stateStr);

        ret = new FECCandidate(id,name,party,officeStr,state);
        if(false && byID.size() % 1000 == 0)
            System.out.println(name + " " + byID.size());
        return ret;
    }

    public static void readCandidatesFromFEC(File f)   {
        if(f.isDirectory())  {
            File[] items = f.listFiles();
            if(items != null)  {
                for (int i = 0; i < items.length; i++) {
                    File item = items[i];
                    addCandidateFromFile(item);
                }
            }
        }
        else {
            addCandidateFromFile(f);

        }

    }


    public static void writeCandidate (File f)   {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(f));
            for (String s : byID.keySet()) {
                FECCandidate comm = getById(s);
                pw.println(comm.id + "\t" + comm.name + "\t" + comm.party  );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    public static void main(String[] args) {
        File f = new File(args[0]);
        readCandidatesFromFEC(  f);
        File out = new File(args[1]);
        writeCandidate(out);

        for (PoliticalParty politicalParty : PoliticalParty.candidateCount.keySet()) {
            System.out.println(politicalParty + " " + PoliticalParty.candidateCount.get(politicalParty));

        }

//        for (String politicalParty : PoliticalParty.partyCount.keySet()) {
//            System.out.println(politicalParty + " " + PoliticalParty.partyCount.get(politicalParty));
//
//        }

    }

}
