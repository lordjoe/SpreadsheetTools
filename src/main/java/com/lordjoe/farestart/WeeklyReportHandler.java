package com.lordjoe.farestart;

import com.lordjoe.spreadsheet.SpreadsheetUtilities;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import static com.lordjoe.spreadsheet.SpreadsheetUtilities.*;

/**
 * com.lordjoe.spreadsheet.WeeklyReportHandler
 * User: Steve
 * Date: 1/11/19
 */
public class WeeklyReportHandler {

    private static int index = 2;
    private static int MONDAY_INDEX = index++;
    private static int TUESDAY_INDEX = index++;
    private static int WEDNESDAY_INDEX = index++;
    private static int THURSDAY_INDEX = index++;
    private static int FRIDAY_INDEX = index++;
    private static int SATURDAY_INDEX = index++;
    private static int SUNDAY_INDEX = index++;


    public static final DateFormat US_DATE = new SimpleDateFormat("MM/dd/yy");

    protected static void copyDataFromLastWeek(Sheet target) {
        Row previous = null;
        Row current = null;
        Iterator<Row> rowIterator = target.rowIterator();
        int index = 0;
        while (rowIterator.hasNext()) {
            previous = current;
            current = rowIterator.next();
            boolean hidden = SpreadsheetUtilities.isRowHidden(current);
            if (hidden && previous != null) {
                copyRowNumbers(target, previous, current);
            }
            index++;

        }
    }

    // copy numbers not formulas
    public static void copyRowNumbers(Sheet srcSheet, Row srcRow, Row destRow) {
        for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {
            Cell oldCell = srcRow.getCell(j);
            Cell newCell = destRow.getCell(j);
            if (oldCell != null) {
                CellType cellType = oldCell.getCellType();
                if (cellType == CellType.NUMERIC) {
                    if (newCell == null) {
                        newCell = destRow.createCell(j);
                    }
                    copyCell(oldCell, newCell);
                } else {
                    if (cellType == CellType.BLANK)
                        if (newCell == null) {
                            newCell = destRow.createCell(j);
                        }
                    copyCell(oldCell, newCell);
                }
            }
        }

    }

    private static void fillFromWeeklySales(Sheet newSheet, WeeklySales ws) {
        Calendar thisWeek = ws.getWeekStart();
        String startDate = US_DATE.format(new Date(thisWeek.getTimeInMillis()));
        String firstCell = "Week of " + startDate;
        Cell cell = newSheet.getRow(0).getCell(0);
        cell.setCellValue(firstCell);
        int index = 0;
        RestaurantLocation loc = null;
        Iterator<Row> rowIterator = newSheet.rowIterator();
        Row headers = rowIterator.next();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Cell title = row.getCell(0);
            if (title != null) {
                String txt = title.getStringCellValue();
                if (txt.length() > 0) {
                    loc = RestaurantLocation.get(txt);
                }
            }
            Cell type = row.getCell(1);
            if (type != null) {
                String typeRow = type.getStringCellValue();
                if (typeRow.equalsIgnoreCase("Net Sales"))
                    populateNetSalesRow(row, loc, ws);
                if (typeRow.equalsIgnoreCase("Count"))
                    populateCountRow(row, loc, ws);

            }

            index++;

        }

    }

    private static void populateCountRow(Row row, RestaurantLocation loc, WeeklySales ws) {
        DailySales dailySales;
        dailySales = ws.getDailySales(loc, DayOfWeek.MONDAY);
        if (dailySales != null) {
            Cell cell = row.getCell(MONDAY_INDEX);
            cell.setCellValue(dailySales.guests);
        }
        dailySales = ws.getDailySales(loc, DayOfWeek.TUESDAY);
        if (dailySales != null) {
            Cell cell = row.getCell(TUESDAY_INDEX);
            cell.setCellValue(dailySales.guests);
        }
        dailySales = ws.getDailySales(loc, DayOfWeek.WEDNESDAY);
        if (dailySales != null) {
            Cell cell = row.getCell(WEDNESDAY_INDEX);
            cell.setCellValue(dailySales.guests);
        }
        dailySales = ws.getDailySales(loc, DayOfWeek.THURSDAY);
        if (dailySales != null) {
            Cell cell = row.getCell(THURSDAY_INDEX);
            cell.setCellValue(dailySales.guests);
        }
        dailySales = ws.getDailySales(loc, DayOfWeek.FRIDAY);
        if (dailySales != null) {
            Cell cell = row.getCell(FRIDAY_INDEX);
            cell.setCellValue(dailySales.guests);
        }
        dailySales = ws.getDailySales(loc, DayOfWeek.SATURDAY);
        if (dailySales != null) {
            Cell cell = row.getCell(SATURDAY_INDEX);
            cell.setCellValue(dailySales.guests);
        }
        dailySales = ws.getDailySales(loc, DayOfWeek.SUNDAY);
        if (dailySales != null) {
            Cell cell = row.getCell(SUNDAY_INDEX);
            cell.setCellValue(dailySales.guests);
        }
    }

    private static void populateNetSalesRow(Row row, RestaurantLocation loc, WeeklySales ws) {
        DailySales dailySales;
        dailySales = ws.getDailySales(loc, DayOfWeek.MONDAY);
        if (dailySales != null) {
            Cell cell = row.getCell(MONDAY_INDEX);
            cell.setCellValue(dailySales.netSales);
        }
        dailySales = ws.getDailySales(loc, DayOfWeek.TUESDAY);
        if (dailySales != null) {
            Cell cell = row.getCell(TUESDAY_INDEX);
            cell.setCellValue(dailySales.netSales);
        }
        dailySales = ws.getDailySales(loc, DayOfWeek.WEDNESDAY);
        if (dailySales != null) {
            Cell cell = row.getCell(WEDNESDAY_INDEX);
            cell.setCellValue(dailySales.netSales);
        }
        dailySales = ws.getDailySales(loc, DayOfWeek.THURSDAY);
        if (dailySales != null) {
            Cell cell = row.getCell(THURSDAY_INDEX);
            cell.setCellValue(dailySales.netSales);
        }
        dailySales = ws.getDailySales(loc, DayOfWeek.FRIDAY);
        if (dailySales != null) {
            Cell cell = row.getCell(FRIDAY_INDEX);
            cell.setCellValue(dailySales.netSales);
        }
        dailySales = ws.getDailySales(loc, DayOfWeek.SATURDAY);
        if (dailySales != null) {
            Cell cell = row.getCell(SATURDAY_INDEX);
            cell.setCellValue(dailySales.netSales);
        }
        dailySales = ws.getDailySales(loc, DayOfWeek.SUNDAY);
        if (dailySales != null) {
            Cell cell = row.getCell(SUNDAY_INDEX);
            cell.setCellValue(dailySales.netSales);
        }

    }


    private static void makeSixWeeks(Workbook workbook, File sixWeeks) {
        try {
            int index = getIndexOfLastWeekData(workbook);
            Workbook sixW = new XSSFWorkbook();
            for (int i = 0; i < 6; i++) {
                int ago = 5 - i;
                String name = makeNameFromAgo(ago);
                Sheet datatypeSheet = workbook.getSheetAt(index - ago);    // this is the second to last
                Sheet toCopy = sixW.createSheet(name);
                SpreadsheetUtilities.copySheets(datatypeSheet, toCopy, true);
            }
            String trendName = "Six Week Trend";
            Sheet summary = sixW.createSheet(trendName);
            Sheet copySummary = workbook.getSheet(trendName);
            SpreadsheetUtilities.copySheets(summary, copySummary, true);
            sixW.write(new FileOutputStream(sixWeeks));
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    public static String makeNameFromAgo(int ago) {
        switch (ago) {
            case 0:
                return "This Week";
            case 1:
                return "Last Week";
            default:
                return "" + ago + "Weeks ago";
        }
    }

    public static void main(String[] args) {
        try {
            File inputSheet = new File(args[0]);
            File weeklySalesFile = new File(args[1]);
            File outputSheet = new File(args[2]);
            File sixWeeks = new File(args[2]);

            Workbook workbook = readWorkbook(inputSheet);
            WeeklySales ws = new WeeklySales(weeklySalesFile);

            //      listSheetNamesSheets(workbook);
            int numberOfSheets = workbook.getNumberOfSheets();
            int index = getIndexOfLastWeekData(workbook);
            Sheet newSheet = workbook.cloneSheet(index);
            String sheetName = ws.getSheetName();
            workbook.setSheetName(workbook.getSheetIndex(newSheet), sheetName);

            copyDataFromLastWeek(newSheet);
            newSheet.setForceFormulaRecalculation(true);
            workbook.setSheetOrder(sheetName, index + 1);
            fillFromWeeklySales(newSheet, ws);
            SpreadsheetUtilities.evaluateSheet(newSheet);
            //      makeSixWeeks(workbook,sixWeeks);

            FileOutputStream os = new FileOutputStream(outputSheet);
            workbook.write(os);
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);

        }

    }


}
