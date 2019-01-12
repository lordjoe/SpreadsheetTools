package com.lordjoe.whitepages;

import com.lordjoe.loeb.contributer.Contributer;
import com.lordjoe.utilities.StringUtilities;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static com.lordjoe.loeb.contributer.Main.contrubuterIsValid;

/**
 * com.lordjoe.whitepages.ChooseBestPerson
 * User: Steve
 * Date: 4/21/2018
 */
public class ChooseBestPerson {
    public static final ChooseBestPerson[] EMPTY_ARRAY = {};


    public static List<WhitePagesPerson> makePeople(Contributer c,List<JSONObject> people) {
        List<WhitePagesPerson> ret = new ArrayList<>();
        int bestscore = Integer.MIN_VALUE;
        for (JSONObject person : people) {
            WhitePagesPerson p = new WhitePagesPerson(person,c);
            ret.add(p);
          }
        return ret;
    }

    public static List<WhitePagesPerson> chooseBestPerson(Contributer c,List<WhitePagesPerson> people ) {
        List<WhitePagesPerson> ret = new ArrayList<>();
        int bestscore = Integer.MIN_VALUE;
        for (WhitePagesPerson p : people) {
            int score = p.score() ;
            if(score == bestscore)  {
                ret.add(p);
            }
            if(score > bestscore)   {
                bestscore = score;
                ret.clear();
                ret.add(p);
            }
        }
        return ret;
    }

    public static List<WhitePagesPerson> chooseAdequatePerson(Contributer c,List<WhitePagesPerson> people ) {
        List<WhitePagesPerson> ret = new ArrayList<>();
        int bestscore = Integer.MIN_VALUE;
        for (WhitePagesPerson p : people) {
            int score = p.adequateScore() ;
            if(score == bestscore)  {
                ret.add(p);
            }
            if(score > bestscore)   {
                bestscore = score;
                ret.clear();
                ret.add(p);
            }
        }
        return ret;
    }

    public static List<WhitePagesPerson> chooseSuitablePerson(Contributer c,List<WhitePagesPerson> people ) {
        List<WhitePagesPerson> ret = new ArrayList<>();
        int bestscore = Integer.MIN_VALUE;
        for (WhitePagesPerson p : people) {
            int score = p.suitabilityScore() ;
            if(score == bestscore)  {
                ret.add(p);
            }
            if(score > bestscore)   {
                bestscore = score;
                ret.clear();
                ret.add(p);
            }
        }
        return ret;
    }


    public static void writePerson(WhitePagesPerson p,PrintWriter pw)    {
        pw.println(p.toTabbedString());
    }

    public static void main(String[] args)  throws Exception {
        int index = 0;
        File eduRequestFile = new File(args[index++]);
        File out = new File(args[index++]);


        List<Contributer> requests = WPUtilities.readContributerFile(eduRequestFile);
        int numberToValidate = 10;
        PrintWriter outWriter = new PrintWriter(new FileWriter(out));

        File saveDir = new File("SaveDir");
        File peopleDir = new File("People");
        saveDir.mkdirs();
        peopleDir.mkdirs();

        int numberSuitablePeople  = 0;
        int numberAmbiguousPeople  = 0;


        File savePeople = new File(saveDir, "savedPeople.txt");
        File ambiguousPeople = new File(saveDir, "ambiguousPeople.txt");
        PrintWriter otherWriter = new PrintWriter(new FileWriter(savePeople));
        PrintWriter saveWriter = new PrintWriter(new FileWriter(savePeople));
        PrintWriter ambigouosWriter = new PrintWriter(new FileWriter(ambiguousPeople));
        FindPerson.writeHeaders(saveWriter);
        for (Contributer contributer : requests) {
            File responseFile = new File(saveDir, contributer.getId() + ".json");
            if (!responseFile.exists())
                continue;
            if (!contrubuterIsValid(contributer))
                continue;

            JSONObject jsonObject = WPUtilities.readJSonFile(responseFile);

            List<JSONObject> people = FindPerson.getPeople(jsonObject);
            List<WhitePagesPerson> whitePagesPeople = makePeople(contributer,people);

            List<WhitePagesPerson> suitablePerson = chooseSuitablePerson(contributer,whitePagesPeople);

            suitablePerson = chooseAdequatePerson(contributer,suitablePerson) ;

            if(suitablePerson.size() == 1)  {
                writePerson(suitablePerson.get(0),saveWriter);
                numberSuitablePeople++;
            }
            else {
                for (WhitePagesPerson person : suitablePerson) {
                    writePerson(person, ambigouosWriter);
                    numberAmbiguousPeople++;
                }
            }
            List<WhitePagesPerson> bestPerson = chooseBestPerson(contributer,suitablePerson);


            for ( WhitePagesPerson person : bestPerson) {
                
            }

          //    FindPerson.writePerson(request,peopleDir,saveWriter,bestPerson);

        }

        System.out.println("Number Clear " + numberSuitablePeople + " number Ambiguous " + numberAmbiguousPeople);
        saveWriter.close();
        ambigouosWriter.close();



    }

}
