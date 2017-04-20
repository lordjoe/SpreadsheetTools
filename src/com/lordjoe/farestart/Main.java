package com.lordjoe.farestart;

import java.io.*;
import java.util.*;

import static com.lordjoe.farestart.CateringObject.getRemappedValues;
import static com.lordjoe.farestart.CateringObject.remapData;

public class Main {

    public static final String SPLIT_STRING = "\t";

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


    public static String[] lineToValues(String line)  {
        String[] columnHeaders = line.split(SPLIT_STRING);
        for (int i = 0; i < columnHeaders.length; i++) {
            columnHeaders[i] = columnHeaders[i].trim();
            columnHeaders[i] = columnHeaders[i].replace("\"","");
        }
        return columnHeaders;
    }

    public static String[]  readHeaders(File f)
    {
        List<CateringObject> ret = new ArrayList<CateringObject>();
        LineNumberReader rdr = null;
        try {
            rdr = new LineNumberReader(new FileReader(f));
            String line = rdr.readLine();
            String[] columnHeaders = lineToValues(  line) ;
              return columnHeaders;
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

    public static List<CateringObject> readCSVFile(File f)
    {
        List<CateringObject> ret = new ArrayList<CateringObject>();
        LineNumberReader rdr = null;
        try {
            rdr = new LineNumberReader(new FileReader(f));
            String line = rdr.readLine();
            String[] columnHeaders = lineToValues(  line) ;
            line = rdr.readLine();
             while(line != null) {
                ret.add(new CateringObject(columnHeaders,lineToValues(line)));
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

    public static Map<String,List<String>> readTSVFile(File f)
    {
        Map<String,List<String>> ret = new HashMap<String, List<String>>() ;
        LineNumberReader rdr = null;
        try {
            rdr = new LineNumberReader(new FileReader(f));
            String line = rdr.readLine();
            String[] columnHeaders = lineToValues(  line) ;
            for (int i = 0; i < columnHeaders.length; i++) {
                String columnHeader = columnHeaders[i];
                ret.put(columnHeader,new ArrayList<String>()) ;
            }
            line = rdr.readLine();
            while(line != null) {
                String[] strings = lineToValues(line);
                for (int i = 0; i < strings.length; i++) {
                    String string = strings[i];
                    if(string != null && string.length() > 0)   {
                        ret.get(columnHeaders[i]).add(string);
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

    private static  Map<String,Set<String>> processCateringObjects(List<CateringObject> items) {
        Set<String > colNames = new HashSet(Arrays.asList(items.get(0).getColumns()));
        Map<String,Set<String>>  colValues = new HashMap<String, Set<String>>();
        for (int i = 0; i < Interesting_Columns.length; i++) {
            String interesting_column = Interesting_Columns[i];
            if(!colNames.contains(interesting_column))
                throw new IllegalStateException("problem"); // ToDo change
            handleInterestingColumn(items, colValues, interesting_column);
        }
        return colValues;
    }

    private static void handleInterestingColumn(List<CateringObject> items, Map<String, Set<String>> colValues, String interesting_column) {
        Set<String> strings = colValues.get(interesting_column);
        if(strings == null)   {
            strings = new HashSet<String>();
            colValues.put(interesting_column,strings);
        }
        for (CateringObject item : items) {
            String rawColumnData = item.getRawColumnData(interesting_column);
            if(rawColumnData != null)
                strings.add(rawColumnData);
        }
    }
    private static void writeRemappingCode(Map<String, Set<String>> colValues, File mappingCodeFile) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(mappingCodeFile));
            out.println(HEADER);
            for (String s : colValues.keySet()) {
                String replace = s.replace(" ", "_");
                out.println("public static Hashmap<String,Object> " + replace + " = new HashMap<String,Object>();");
            }
            out.println("static {");
            for (String s : colValues.keySet()) {
                String replace = s.replace(" ", "_");
                for (String value : colValues.get(s)) {
                    out.println(replace + ".put(\"" + value + "\",\"" + value +  "\");");
                }
            }

            out.println("}");
            out.println("}");

        } catch (IOException e) {
            throw new RuntimeException(e);

        } finally {
            if(out != null)
                try {
                    out.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);

                }
        }
    }

    public static void writeColumnHeaders(String[] oldColumns, String[] newColumns)   {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter("ColumnHeaders.java"));
            out.println(HEADER);
            out.println("public static String[] oldColumns = { ");
            for (int i = 0; i < oldColumns.length; i++) {
                String oldColumn = oldColumns[i];
                out.println("\"" + oldColumn + "\",");
            }
            out.println("};");
            out.println("public static String[] newColumns = { ");
            for (int i = 0; i < newColumns.length; i++) {
                String oldColumn = newColumns[i];
                out.println("\"" + oldColumn + "\",");
            }
            out.println("};");

            out.println("}");

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


    public static void writeNewFile(File outFile,List<CateringObject>  remapped)   {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(outFile));
            String[] headers = CateringObject.newColumns;
            CateringObject.writeColumnHeader(out,headers);
              for (CateringObject cateringObject : remapped) {
                cateringObject.writeColumnData(out,headers);
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

    public static void main(String[] args) throws Exception {
        File tabDelimitedFile = new File(args[0]);
        File mappingCodeFile = new File(args[1]);

        AdditionalAccounts addedAccounts = new AdditionalAccounts( );

        // code to write column headers
//        String[] oldColumns = readHeaders(tabDelimitedFile);
//        String[] newColumns = readHeaders(mappingCodeFile);
//        writeColumnHeaders( oldColumns, newColumns);



        List<CateringObject>  items = readCSVFile(tabDelimitedFile);
        if(items.isEmpty())
            return;

        addedAccounts.clearUse();
        for (CateringObject item : items) {
            remapData(item,addedAccounts);
        }

        List<AdditionalAccount>  allUsed = addedAccounts.allAccountsUsed();
        for (AdditionalAccount s : allUsed) {
            System.out.println(s.name);
        }
        
        writeNewFile(mappingCodeFile,items);
         if(true)
             return;


        Map<String, Set<String>> colValues = processCateringObjects(items);

        writeRemappingCode(colValues,mappingCodeFile);
        //       Map<String, Map<String,Object>> remappedValues = getRemappedValues(colValues);

        // remapData(remappedValues,items);

    }


}
