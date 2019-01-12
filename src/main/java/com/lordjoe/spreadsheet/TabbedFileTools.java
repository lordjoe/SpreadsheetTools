package com.lordjoe.spreadsheet;



import java.io.*;
import java.util.*;

/**
 * com.lordjoe.TabbedFileTools
 * User: Steve
 * Date: 8/3/2018
 *
 * Arguments InputFile, OutputFile columnOrder
 * TestSheet.txt TestSheetSorted.txt 0,1,2,3,8,9,10,11,4,5,6,7
 * or
 * InputFile, OutputFile columnOrder sortOrder
 *  TestSheet.txt TestSheetSorted.txt 0,1,2,3,8,9,10,11,4,5,6,7 0,2
 */
public class TabbedFileTools {
    public static final String SPLIT_STRING = "\t";

    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static String[] lineToValues(String line) {
        String[] columnHeaders = line.split(SPLIT_STRING);
        for (int i = 0; i < columnHeaders.length; i++) {
            columnHeaders[i] = columnHeaders[i].trim();
            columnHeaders[i] = columnHeaders[i].replace("\"", "");
        }
        return columnHeaders;
    }

    public static String valuesToLine(Object[] items) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        sb.append(items[i++]);

        for (; i < items.length; i++) {
            sb.append(SPLIT_STRING);
            sb.append(items[i]);

        }
        return sb.toString();
    }


    public static final List<String> readFile(File f) {
        try {
            InputStream is = new FileInputStream(f);
            return readStreamLines(is);
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    public static final List<String> readStreamLines(InputStream is) {
        try {
            List<String> ret = new ArrayList<>();
            LineNumberReader rdr = new LineNumberReader(new InputStreamReader(is));
            String line = rdr.readLine();
            while (line != null) {
                ret.add(line);
                rdr.readLine();
            }
            return ret;
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    /**
     * convera a String like 23,4,5,6 to an array of ints
     *
     * @param arg
     * @return
     */
    private static int[] parseInts(String arg) {
        String[] items = arg.split(",");
        int[] ret = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            ret[i] = Integer.parseInt(items[i]);

        }
        return ret;
    }

    private static List<String[]> readColumnsAndHeaders(File input, List<String> headers) {
        try {
            List<String[]> ret = new ArrayList<>();
            LineNumberReader rdr = new LineNumberReader(new InputStreamReader(new FileInputStream(input)));
            String line = rdr.readLine();
            String[] valued = lineToValues(line);
            // build headers
            for (int i = 0; i < valued.length; i++) {
                headers.add(valued[i]);
            }
            line = rdr.readLine();
            while (line != null) {
                String[] columns = lineToValues(line);
                ret.add(columns);
                line = rdr.readLine();
            }
            return ret;
        } catch (IOException e) {
            throw new RuntimeException(e);

        }

    }

 

    private static void reorderArray(String[] headers, int[] reorderMap) {
        String[] temp = new String[reorderMap.length];
        for (int i = 0; i < reorderMap.length; i++) {
            temp[i] = headers[reorderMap[i]];
        }
        for (int i = 0; i < temp.length; i++) {
            headers[i] = temp[i];
        }

    }

    private static void reorderList(List<String> headers, int[] reorderMap) {
        String[] temp = new String[reorderMap.length];
        for (int i = 0; i < reorderMap.length; i++)
            temp[i] = headers.get(i);
        reorderArray(temp, reorderMap);
        headers.clear();
        for (int i = 0; i < temp.length; i++) {
            headers.add(temp[i]);

        }
    }

    private static int[] buildReorderMap(int[] newOrder, int numberColumns) {
        int[] ret = new int[numberColumns];
        Set<Integer> unused = new HashSet<>();
        for (int i = 0; i < newOrder.length; i++) {
            int value = newOrder[i];
            ret[i] = value;
            unused.remove(value);
        }
        if(!unused.isEmpty())  {
            int[] asInts = new int[unused.size()];
            int index = 0;
            for (Integer value : unused) {
                asInts[index++] =  value;
            }
            Arrays.sort(asInts);
            int index2 = newOrder.length;
            for (int i = 0; i < asInts.length; i++) {
                ret[index2++] = asInts[i];
              }
         }
        return ret;

    }

    private static void writeColumnsAndHeaders(File output, List<String> headers, List<String[]> columns) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(output));
            out.println(valuesToLine(headers.toArray()));
            for (String[] column : columns) {
                out.println(valuesToLine(column));
            }
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    /**
     * sort arrays of strings by columns
     */
    static class ColumnComparator implements Comparator< String[]> {
        int[]   sortOrder;
        public ColumnComparator(int[] sortOrder) {
            this.sortOrder = sortOrder;
        }

        @Override
        public int compare(String[] o1, String[] o2) {
            int ret = 0;
            for (int i = 0; i < sortOrder.length; i++) {
                int index = sortOrder[i];
                ret = o1[index].compareTo(o2[index]);
                if(ret != 0)
                    return ret;

            }
            return o1.toString().compareTo(o2.toString());
        }
    }

    public static void main(String[] args) {
        File input = new File(args[0]);
        File output = new File(args[1]);
        int[] newOrder = parseInts(args[2]);
        int[] sortOrder = null;
        if (args.length > 3)
            sortOrder = parseInts(args[3]);
        List<String> headers = new ArrayList<>();
        List<String[]> columns = readColumnsAndHeaders(input, headers);
        int numberColumns = headers.size();
        int[] reorderMap = buildReorderMap(newOrder, numberColumns);
        reorderList(headers, reorderMap);
        for (String[] cols : columns) {
            reorderArray(cols, reorderMap);
        }
        if(sortOrder != null)
            Collections.sort(columns,new ColumnComparator(sortOrder));

        writeColumnsAndHeaders(output, headers, columns);
    }


}
