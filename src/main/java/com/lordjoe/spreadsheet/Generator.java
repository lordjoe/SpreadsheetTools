package com.lordjoe.spreadsheet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * com.lordjoe.spreadsheet.Generator
 * User: Steve
 * Date: 8/4/2018
 * run with one argument to make a list of column numbert to reorder
 * run with 2 arguments FileName and numberColumns to make a test tabbed file with 100,000 rows
 * run with 2 arguments FileName and numberColumns and numberRows to make a test tabbed file with numberRows rows
 *
 */
public class Generator {

    public static final Random RND = new Random();

    private static void generateTestData(File output, int nColumns,int NRows) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(output)) ;
            writeColumnHeaders(out,nColumns);
            for (int j = 0; j < NRows ; j++) {
                writeRow(out,nColumns);
            }
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    private static void writeRow(PrintWriter out, int nColumns) {
        for (int j = 0; j < nColumns - 1; j++) {
            out.print(generateCellData() + "\t");

        }
        out.println(generateCellData());
    }

    private static String generateCellData() {
           StringBuilder sb = new StringBuilder();
        for (int i = 0; i <4; i++) {
            char TargetChar = (char) (RND.nextInt(26) + (int)'A');
            sb.append( TargetChar);
        }
             return sb.toString();
    }

    private static void writeColumnHeaders(PrintWriter out, int nColumns) {
        for (int j = 0; j < nColumns - 1; j++) {
            out.print("col" + j + "\t");

        }
        out.println("col" + (nColumns - 1));
    }

    private static void generateOrdering(int ncolumns) {
        for (int j = 0; j < ncolumns - 1; j++) {
            System.out.print(j + ",");

        }
        System.out.println(ncolumns - 1);

    }


    public static void main(String[] args) {
        File output = null;
        int NColumns = 0;
        int NRows = 100000;
        switch(args.length)  {
            case 1:
                generateOrdering(Integer.parseInt(args[0])) ;
                break;
            case  3 :
                NRows = Integer.parseInt(args[2]);
            case  2 :
                output= new File(args[0]);
                NColumns = Integer.parseInt(args[1]);
                 generateTestData(output, NColumns,NRows) ;
                break;
            default:
                throw new UnsupportedOperationException("Fix This"); // ToDo
        }

    }


}
