package com.lordjoe.loeb;

import com.lordjoe.utilities.FileUtilities;
import com.lordjoe.utilities.StringUtilities;

import java.io.*;
import java.util.*;

import static com.lordjoe.loeb.FixEDUState.getDomain;

/**
 * com.lordjoe.loeb.Main
 * User: Steve
 * Date: 4/5/2018
 */
public class Main {
    public static final Main[] EMPTY_ARRAY = {};


    public static class StateComparator  implements Comparator<EDUList>  {

        @Override
        public int compare(EDUList o1, EDUList o2) {
            State s1 = o1.getState();
            State s2 = o2.state;
            if(s1 == s2)
                return o1.getEmail().compareTo(o2.getEmail());
            if(s1 == null)
                return 1;
            if(s2 == null)
                return -1;

            return s1.compareTo(s2);
        }
    }



    public static final String HEADER = "package com.lordjoe.farestart;\n" +
            "\n" +
            "import java.util.*;\n" +
            "public class Remapper {";

    public static final String[] Interesting_Columns = {
            "Account Category",
            "Party Name" ,
            "Theme",
            "Category",
    } ;




    public static List<EduRequest> readEduRequestFile(File f)
    {
        List<EduRequest> ret = new ArrayList<EduRequest>();
        LineNumberReader rdr = null;
        try {
            rdr = new LineNumberReader(new FileReader(f));
            String line = rdr.readLine();
             String[] columnHeaders = StringUtilities.lineToValues(  line) ;
            line = rdr.readLine();
            while(line != null) {
                if(!line.startsWith("\tX")){
                    while (line.length() > 0 && line.charAt(0) == '\t') {
                        line = line.substring(1, line.length());
                    }
                }

                final String[] data = StringUtilities.lineToValues(line);
                if(data.length  > 15) {
                    final EduRequest e = new EduRequest(data);
                    if(e.state != State.UNKNOWN)
                        ret.add(e);
                    else {
                        System.err.println(line);
                    }
                }
                line = rdr.readLine();
            }
            return ret;
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
        finally {
            try {
                if(rdr != null)
                    rdr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);

            }
        }
    }



    public static List<EDUList> readEduListRequestFile(File f)
    {
        Set<Institution>  unknown = new HashSet<>() ;

        List<EDUList> ret = new ArrayList<EDUList>();
        LineNumberReader rdr = null;
        try {
            rdr = new LineNumberReader(new FileReader(f));
            String line = rdr.readLine();
            String[] columnHeaders = StringUtilities.lineToValues(  line) ;
            line = rdr.readLine();
            while(line != null) {
                EDUList e = new EDUList(StringUtilities.lineToValues(line));
                ret.add(e);
                if(e.state == null && e.email.endsWith(".edu"))   {
                    Institution institution = Institution.getInstitution(getDomain(e.email));
                    e.state = institution.getBestState();
                    if(e.state == State.UNKNOWN)
                         unknown.add(institution) ;
                }
                 line = rdr.readLine();
            }

            for (Institution institution : unknown) {
                System.out.println("rememberEDUState(\"" + institution.email + "\",State.Unknown);  ");
            }
            return ret;
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
        finally {
            try {
                if(rdr != null)
                    rdr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);

            }
        }
    }





    




 

    public static void writeListFile(File outFile,List<EDUList>  remapped) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(outFile));
     //       String[] headers = EduRequest.newColumns;
     ///       EduRequest.writeColumnHeader(out, headers);
            for (EDUList rq : remapped) {
                out.println(rq.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        } finally {
            try {
                out.close();
            } catch (Exception e) {
                throw new RuntimeException(e);

            }
        }
    }


 

    private static void handleLists(String[] args) {
        int index = 0;
        File eduRequestFile = new File(args[index++]);
        File eduListFile = new File(args[index++]);
        File eduListFileOut = new File(args[index++]);


        List<EduRequest> requests = readEduRequestFile(eduRequestFile);
        System.out.println("Requests " +   requests.size() );

        List<EDUList> lists = readEduListRequestFile(eduListFile);

        normalizeLists(lists);
        Collections.sort(lists,new StateComparator());

        Institution.showUnknownEdu();

        writeListFile(eduListFileOut,lists);



        System.out.println("Handled " +  (lists.size() - EDUList.notInRequest.size()));
        System.out.println("Not Handled " +  EDUList.notInRequest.size());


    }

    private static void normalizeLists(List<EDUList> lists) {

        for (EDUList list : lists) {
              normalizeList(list);
        }
    }

    public static  State fixState(String email,State state)    {
        State ret = State.UNKNOWN;
        if(email != null && email.startsWith("waysmp@miamioh.edu") )
              ret = State.UNKNOWN; // break here
        
        Institution institution = Institution.getInstitution(email);
        if(state != null &&  state != State.UNKNOWN)  {
            if(institution != null)   {
                institution.setState(state);
            }
            return state;
        }
        else {
            if(institution != null)   {
                ret =  Institution.getBestState(email);
            }
            return ret;
         }
    }

    private static void normalizeList(EDUList list) {
        String email = list.email;
          EduRequest byEMail = EduRequest.getByEMail(email);
        if(byEMail != null) {
             list.Full_Name = byEMail.Full_Name;
            State s = fixState(byEMail.Email,byEMail.state);
            list.state = s;
        }
        else {
            State state = list.state;
            if(state != null && list.state != State.UNKNOWN)  {
                Institution institution = Institution.getInstitution(email);
                if(institution != null)   {
                    institution.setState(state);
                }
            }
            else {
                State s = Institution.getBestState(email);
                list.state = s;
            }
           if(list.state == State.UNKNOWN)
             EDUList.notInRequest.add(list);
        }

    }

    public static String findEDULinks(String[] args) throws Exception {
        String linkFile =  FileUtilities.readInFile(args[0]);
        String[] lines = linkFile.split("\n");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if(line.contains(".edu"))   {
                sb.append(line.substring(0,line.indexOf("\t")) + "\n");
            }
        }
        return sb.toString();
    }
    public static void main(String[] args) throws Exception {
        String eduLinks = findEDULinks(args);
        String[] lines = eduLinks.split("\n");
        Arrays.sort(lines);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            System.out.println(line);

        }
         if(true)
            return;
        State s = FixEDUState.getEmailState("mcw.edu") ;
          handleLists(args);


    }



}
