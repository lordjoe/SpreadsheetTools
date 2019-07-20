package com.lordjoe.spreadsheet;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.plaf.synth.Region;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.*;

/**
 * Main.Java.com.lordjoe.spreadsheet.SpreadsheetUtilities
 * User: Steve
 * Date: 1/11/19
 */
public class SpreadsheetUtilities {

    public static Workbook readWorkbook(File infile) {
        try {
            FileInputStream excelFile = new FileInputStream(infile);
            Workbook workbook = new XSSFWorkbook(excelFile);
            return workbook;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void evaluateSheet(Sheet s) {
        Workbook wb = s.getWorkbook();
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
        for (Row r : s) {
            for (Cell c : r) {
                if (c.getCellType() == CellType.FORMULA) {
                    evaluator.evaluateFormulaCell(c);
                }
            }
        }

    }

    /**
     * return a map of sheets by name
     *
     * @param w
     * @return
     */
    public static Map<String, Sheet> getSheets(Workbook w) {
        Map<String, Sheet> ret = new HashMap<>();
        Iterator<Sheet> sheetIterator = w.sheetIterator();
        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();
            ret.put(sheet.getSheetName(), sheet);
        }
        return ret;
    }


    /**
     * return a map of sheets by name
     *
     * @param w
     * @return
     */
    public static void listSheetNamesSheets(Workbook w) {
        Iterator<Sheet> sheetIterator = w.sheetIterator();
        int index = 0;
        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();
            System.out.println(sheet.getSheetName() + " number " + index++);
        }

    }


    public static Sheet cloneSheet(Workbook w, Sheet s, String name) {
        int index = w.getSheetIndex(s);
        Sheet sheet = w.cloneSheet(index);
        w.setSheetName(w.getSheetIndex(sheet), name);
        return sheet;
    }

    /**
     * taken from https://coderanch.com/t/420958/open-source/Copying-sheet-excel-file-excel
     *
     * @author jk
     * getted from http://jxls.cvs.sourceforge.net/jxls/jxls/src/java/org/jxls/util/Util.java?revision=1.8&view=markup
     * by Leonid Vysochyn
     * and modified (adding styles copying)
     */
    public static void copySheets(Sheet newSheet, Sheet sheet) {
        copySheets(newSheet, sheet, true);
    }

    public static void copySheets(Sheet sheet, Sheet newSheet, boolean copyStyle) {
        int maxColumnNum = 0;
        Map<Integer, CellStyle> styleMap = (copyStyle)
                ? new HashMap<Integer, CellStyle>() : null;

        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
            Row srcRow = sheet.getRow(i);
            Row destRow = newSheet.createRow(i);
            if (srcRow != null) {
                copyRow(sheet, newSheet, srcRow, destRow, styleMap);
                if (srcRow.getLastCellNum() > maxColumnNum) {
                    maxColumnNum = srcRow.getLastCellNum();
                }
            }
        }
        for (int i = 0; i <= maxColumnNum; i++) {
            newSheet.setColumnWidth(i, sheet.getColumnWidth(i));
        }
    }

    public static void copyRow(Sheet srcSheet, Sheet destSheet, Row srcRow, Row destRow, Map<Integer, CellStyle> styleMap) {
        Set mergedRegions = new TreeSet();
        destRow.setHeight(srcRow.getHeight());
        for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {
            Cell oldCell = srcRow.getCell(j);
            Cell newCell = destRow.getCell(j);
            if (oldCell != null) {
                if (newCell == null) {
                    newCell = destRow.createCell(j);
                }
                copyCell(oldCell, newCell, styleMap);
//                Region mergedRegion = getMergedRegion(srcSheet, srcRow.getRowNum(), oldCell.getCellNum());
//                if (mergedRegion != null) {
////                    Region newMergedRegion = new Region( destRow.getRowNum(), mergedRegion.getColumnFrom(),
////                            destRow.getRowNum() + mergedRegion.getRowTo() - mergedRegion.getRowFrom(), mergedRegion.getColumnTo() );
//                    Region newMergedRegion = new Region(mergedRegion.getRowFrom(), mergedRegion.getColumnFrom(),
//                            mergedRegion.getRowTo(), mergedRegion.getColumnTo());
//                    if (isNewMergedRegion(newMergedRegion, mergedRegions)) {
//                        mergedRegions.add(newMergedRegion);
//                        destSheet.addMergedRegion(newMergedRegion);
//                    }
//                }
            }
        }

    }

//    public static Region getMergedRegion(Sheet sheet, int rowNum, short cellNum) {
//        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
//            Region merged = sheet.getMergedRegion(i);
//            if (merged.contains(rowNum, cellNum)) {
//                return merged;
//            }
//        }
//        return null;
//    }


    private static boolean isNewMergedRegion(Region region, Collection mergedRegions) {
        return !mergedRegions.contains(region);
    }

    public static void copyCell(Cell oldCell, Cell newCell) {
        CellType cellType = oldCell.getCellType();
        switch (cellType) {
            case STRING:
                newCell.setCellValue(oldCell.getStringCellValue());
                break;
            case NUMERIC:
                newCell.setCellValue(oldCell.getNumericCellValue());
                break;
            case BLANK:
                newCell.setCellType(CellType.BLANK);
                break;
            case BOOLEAN:
                newCell.setCellValue(oldCell.getBooleanCellValue());
                break;
            case ERROR:
                newCell.setCellErrorValue(oldCell.getErrorCellValue());
                break;
            case FORMULA:
                newCell.setCellFormula(oldCell.getCellFormula());
                break;
            default:
                break;
        }

    }

    public static boolean isRowHidden(Row row) {
        CellStyle rowStyle = row.getRowStyle();
        if (rowStyle != null) {
            return rowStyle.getHidden();
        }
        if (row instanceof XSSFRow) {
            XSSFRow xrow = (XSSFRow) row;
            String test = xrow.toString();
            return test.contains("hidden=\"1\"");
        }
        return false;
    }

    public static void setRowHidden(Row row, boolean doit) {
           boolean hidden = isRowHidden(row);
        if (hidden == doit)
            return;
        CellStyle rowStyle = row.getRowStyle();
        if (rowStyle != null) {
            rowStyle.setHidden(doit);
        }
        if (row instanceof XSSFRow) {
            XSSFRow xrow = (XSSFRow) row;
            xrow.getCTRow().setHidden(doit);
             return;
        }

    }

    public static void copyCell(Cell oldCell, Cell newCell, Map<Integer, CellStyle> styleMap) {
        if (styleMap != null) {
            if (oldCell.getSheet().getWorkbook() == newCell.getSheet().getWorkbook()) {
                newCell.setCellStyle(oldCell.getCellStyle());
            } else {
                int stHashCode = oldCell.getCellStyle().hashCode();
                CellStyle newCellStyle = styleMap.get(stHashCode);
                if (newCellStyle == null) {
                    newCellStyle = newCell.getSheet().getWorkbook().createCellStyle();
                    newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
                    styleMap.put(stHashCode, newCellStyle);
                }
                newCell.setCellStyle(newCellStyle);
            }
        }
        CellType cellType = oldCell.getCellType();
        switch (cellType) {
            case STRING:
                newCell.setCellValue(oldCell.getStringCellValue());
                break;
            case NUMERIC:
                newCell.setCellValue(oldCell.getNumericCellValue());
                break;
            case BLANK:
                newCell.setCellType(CellType.BLANK);
                break;
            case BOOLEAN:
                newCell.setCellValue(oldCell.getBooleanCellValue());
                break;
            case ERROR:
                newCell.setCellErrorValue(oldCell.getErrorCellValue());
                break;
            case FORMULA:
                newCell.setCellFormula(oldCell.getCellFormula());
                break;
            default:
                break;
        }

    }

    public static int getIndexOfLastWeekData(Workbook workbook) {
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        int index = 0;
        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();
            if (sheet.getSheetName().startsWith("Six Week"))
                return index - 1;
            index++;
        }
        throw new IllegalArgumentException("no six week profile");

    }


    public static Calendar weekStartFromDate(Calendar d) {
        Calendar ret = Calendar.getInstance();
        ret.setTimeInMillis(d.getTimeInMillis());
        int i = ret.get(Calendar.DAY_OF_WEEK) - 1;    // Day of week starts at 1
        DayOfWeek dw = DayOfWeek.of(i);  // ^(*&)&(*^%(*%( count os 1..7
        while (dw != DayOfWeek.MONDAY) {
            ret.add(Calendar.DATE, -1);
            i = (ret.get(Calendar.DAY_OF_WEEK) - 1) % 7;
            dw = DayOfWeek.of(i);
        }
        return ret;
    }

    public static String toCamelCase(final String init) {
        if (init == null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).toUpperCase());
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length() == init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }


    public static void main(String[] args) {
        try {
            File inputSheet = new File(args[0]);
            File outputSheet = new File(args[1]);

            Workbook workbook = readWorkbook(inputSheet);

            listSheetNamesSheets(workbook);
            int numberOfSheets = workbook.getNumberOfSheets();
            int index = getIndexOfLastWeekData(workbook);
            Sheet datatypeSheet = workbook.getSheetAt(index);    // this is the second to last
            String oldName = datatypeSheet.getSheetName();
            String sheetName = "Week of 01.6";
            Sheet newSheet = cloneSheet(workbook, datatypeSheet, sheetName);
            workbook.setSheetOrder(sheetName, index + 1);

            FileOutputStream os = new FileOutputStream(outputSheet);
            workbook.write(os);
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);

        }

    }

}


