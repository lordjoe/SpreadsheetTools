package com.lordjoe.farestart;

import com.lordjoe.spreadsheet.SpreadsheetUtilities;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.*;

import static com.lordjoe.spreadsheet.SpreadsheetUtilities.*;

/**
 * com.lordjoe.farestart.WeeklyReportHandler
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
    private static int TOTAL_INDEX = index++;


    public static final DateFormat US_DATE = new SimpleDateFormat("MM/dd/yy");

    protected static void copyDataFromLastWeek(Sheet target) {
        Row previous = null;
        Row current = null;
        Iterator<Row> rowIterator = target.rowIterator();
        int index = 1;
        while (rowIterator.hasNext()) {
            previous = current;
            current = rowIterator.next();
            boolean hidden = SpreadsheetUtilities.isRowHidden(current);
            if (hidden && previous != null) {
                copyRowNumbers(previous, current);
            }
            if (current != null)
                fixFormulas(current, index);
            index++;

        }
    }

    public static void fixSixWeekGraph(Workbook workbook) {
        XSSFSheet graphSheet = (XSSFSheet) workbook.getSheet("Six Week Trend");
        int graphIndex = workbook.getSheetIndex(graphSheet);
        int historyStart = graphIndex - 6;
        String[] prev = new String[6];
        for (int i = 0; i < prev.length; i++) {
            prev[i] = workbook.getSheetAt(i + historyStart).getSheetName();
        }
        for (int i = 0; i < prev.length; i++) {
            int column = i + 2;
            Sheet target = workbook.getSheet(prev[i]);
            fixSummaryColumn(graphSheet, target, column);
        }
        refreshCharts(graphSheet);
    }

    private static void fixSummaryColumn(Sheet sheet, Sheet target, int column) {
        String name = sheet.getSheetName();
        String sheetName = target.getSheetName();
        Cell labelCall = sheet.getRow(2).getCell(column);
        String dataRange = datesFromSheet(target);
        labelCall.setCellValue(dataRange);
        for (int i = 4; i < 25; i += 2) {
            Cell cell = sheet.getRow(i).getCell(column);
            String formula = cell.getCellFormula();
            int index = formula.lastIndexOf("!");
            if (index > -1) {
                String end = formula.substring(index);
                cell.setCellFormula("\'" + sheetName + "\'" + end);
            }

        }
    }


    public static void fix12WeekGraph(Workbook workbook) {
        fixMultiGraphs(workbook, 2);
    }

    public static void fixSixMonthGraph(Workbook workbook) {
        fixMultiGraphs(workbook, 4);
    }

    private static String[] WORKBOOK_INTERVAL_NAMES = {
            "Error",
            "Error",
            "12 Week Trend",
            "Error",
            "Six Month Trend",
            "Error",
    };

    public static void fixMultiGraphs(Workbook workbook, int sheetsPerGraph) {
        XSSFSheet graphStart = (XSSFSheet) workbook.getSheet("12 Week Trend");
        int graphIndex = workbook.getSheetIndex(graphStart);
        //     XSSFSheet mySheet = (XSSFSheet)workbook.getSheet(WORKBOOK_INTERVAL_NAMES[sheetsPerGraph]);

        for (int i = 0; i < 6; i++) {
            int column = i + 2;
            Sheet[] targets = getMultiTargetSheets(workbook, i, sheetsPerGraph, graphIndex);
            fixMultiSummaryColumn(graphStart, targets, column);
        }
        SpreadsheetUtilities.evaluateSheet(graphStart);
        refreshCharts(graphStart);
    }


    private static Sheet[] getTargetSheets(Workbook workbook, int index, int sheetsPerGraph, int graphIndex) {
        List<Sheet> holder = new ArrayList<>();
        int startIndex = workbook.getSheetIndex("Six Week Trend");

        int batchStart = startIndex - sheetsPerGraph * (6 - index);
        for (int i = 0; i < sheetsPerGraph; i++) {
            holder.add(workbook.getSheetAt(batchStart + i));
        }


        Sheet[] ret = new Sheet[holder.size()];
        for (int i = 0; i < ret.length; i++) {
            Sheet sheet = holder.get(i);
            String name = sheet.getSheetName();
            ret[i] = sheet;
        }
        return ret;
    }

    private static Sheet[] getMultiTargetSheets(Workbook workbook, int index, int sheetsPerGraph, int graphIndex) {
        List<Sheet> holder = new ArrayList<>();
        int startIndex = workbook.getSheetIndex("12 Week Trend");

        int batchStart = startIndex - sheetsPerGraph * (6 - index);
        for (int i = 0; i < sheetsPerGraph; i++) {
            holder.add(workbook.getSheetAt(batchStart + i));
        }


        Sheet[] ret = new Sheet[holder.size()];
        for (int i = 0; i < ret.length; i++) {
            Sheet sheet = holder.get(i);
            String name = sheet.getSheetName();
            ret[i] = sheet;
        }
        return ret;
    }

    private static final Map<String, Integer> ChartToLine = createMap();

    private static Map<String, Integer> createMap() {
        Map<String, Integer> result = new HashMap<String, Integer>();
        result.put("Maslow's", 5);
        result.put("Community Table", 7);
        result.put("Rise", 9);
        result.put("2100 Cafe", 11);
        result.put("PT Cafe", 13);
        result.put("Catering", 15);
        result.put("FS Restaurant", 17);
        result.put("Guest Chef Night", 19);
        result.put("Community Meals", 21);
        result.put("School Meals", 23);
        result.put("All Company Trend", 25);
        return Collections.unmodifiableMap(result);
    }

    public static final int FIRST_COLUMN = 2;
    public static final int LAST_COLUMN = 7;

    /**
     * makes copies of the charts
     *
     * @param mySheet    - sheet to copy
     * @param graphStart source
     */
    private static void refreshChartsWithClone(XSSFSheet sheet, XSSFSheet graphStart) {
        int numOfPoints = 6;

        XSSFDrawing originalDrawing = graphStart.createDrawingPatriarch();
        //    List<XSSFChart> charts = originalDrawing.getCharts();

        XSSFDrawing patriarch = sheet.createDrawingPatriarch();
        List<XSSFChart> charts = patriarch.getCharts();

        for (XSSFChart chart : charts) {
            try {
                // clone
                XSSFChart xssfChart = patriarch.importChart(chart);
                convertChart(sheet, xssfChart);
            } catch (IOException e) {
                throw new RuntimeException(e);

            } catch (XmlException e) {
                throw new RuntimeException(e);

            }
            //                System.out.println("Refreshing Graph " + id);
        }

    }


    public static void refreshCharts(XSSFSheet xSheet) {
        int numOfPoints = 6;
        //          System.out.println("Refreshing " + sheet.getSheetName());
        XSSFDrawing patriarch = xSheet.createDrawingPatriarch();

        List<XSSFChart> charts = patriarch.getCharts();
        for (XSSFChart chart : charts) {
            //                System.out.println("Refreshing Graph " + id);
            convertChart(xSheet, chart);
        }

    }


//    /**
//     * Imports the chart from the <code>srcChart</code> into this drawing.
//     *
//     * @param srcChart
//     *            the source chart to be cloned into this drawing.
//     * @return the newly created chart.
//     * @throws XmlException
//     * @throws IOException
//     */
//    public XSSFChart importChart(XSSFChart srcChart) throws IOException, XmlException {
//        CTTwoCellAnchor anchor = ((XSSFDrawing) srcChart.getParent()).getCTDrawing().getTwoCellAnchorArray(0);
//        CTMarker from = (CTMarker) anchor.getFrom().copy();
//        CTMarker to = (CTMarker) anchor.getTo().copy();
//        XSSFClientAnchor destAnchor = new XSSFClientAnchor(from, to);
//        destAnchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
//        XSSFChart destChart = createChart(destAnchor);
//        destChart.getCTChartSpace().set(srcChart.getCTChartSpace().copy());
//        destChart.getCTChart().set(srcChart.getCTChart().copy());
//        return destChart;
//    }

    /**
     * update a chart to reflect new data
     *
     * @param sheet
     * @param chart
     */
    private static void convertChart(XSSFSheet sheet, XSSFChart chart) {
        XSSFRichTextString titleText = chart.getTitleText();
        String name = titleText.getString();
        CellRangeAddress region = new CellRangeAddress(1, 1, FIRST_COLUMN, LAST_COLUMN);
        XDDFCategoryDataSource categoriesData = XDDFDataSourcesFactory.fromStringCellRange(sheet, region);
        int caletoryCount = categoriesData.getPointCount();

        try {
            List<XDDFChartData> chartSeries = chart.getChartSeries();
            for (XDDFChartData chartSery : chartSeries) {
                List<XDDFChartData.Series> series = chartSery.getSeries();
                for (XDDFChartData.Series series1 : series) {
                    try {
                        int row = ChartToLine.get(name) - 1;
                        XDDFNumericalDataSource<Double> valuesData2 = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(row, row, FIRST_COLUMN, LAST_COLUMN));
                        int valueCount = valuesData2.getPointCount();
                        series1.replaceData(categoriesData, valuesData2);
                    } catch (Exception e) {
                        System.out.println("error");

                    }
                }
                chart.plot(chartSery);
                //         chart.setCommited(true);
            }
        } catch (Exception e) {
            System.out.println("Could not Convert Chart");
        }

    }

    private static List<String> getTableRowStrings(int rowPlus1, Sheet sheet) {
        List<String> ret = new ArrayList<>();
        Row row = sheet.getRow(rowPlus1 - 1);
        for (int i = FIRST_COLUMN; i < LAST_COLUMN + 1; i++) {
            ret.add(row.getCell(i).getStringCellValue());

        }
        return ret;
    }

    private static List<Double> getTableRowValues(int rowPlus1, Sheet sheet) {
        List<Double> ret = new ArrayList<>();
        Row row = sheet.getRow(rowPlus1 - 1);
        for (int i = FIRST_COLUMN; i < LAST_COLUMN + 1; i++) {
            ret.add(row.getCell(i).getNumericCellValue());

        }
        return ret;
    }

    private static void fixMultiSummaryColumn(Sheet sheet, Sheet[] targets, int column) {
        String name = sheet.getSheetName();
        String[] sheetnames = new String[targets.length];
        for (int i = 0; i < targets.length; i++) {
            String sheetName = targets[i].getSheetName();
            sheetnames[i] = sheetName;
        }
        Cell headerCell = sheet.getRow(1).getCell(column);
        switch (targets.length) {
            case 2:
                headerCell.setCellValue("Week " + 2 * (column - 1));
                break;
            case 4:
                headerCell.setCellValue("Month " + 1 * (column - 1));
                break;
            default:
                throw new IllegalArgumentException("Cannot handle case of " + targets.length + " weeks");
        }
        Cell labelCall = sheet.getRow(2).getCell(column);
        String dataRange = datesFromSheets(targets);
        labelCall.setCellValue(dataRange);
        for (int i = 4; i < 25; i += 2) {
            Cell cell = sheet.getRow(i).getCell(column);
            String formula = cell.getCellFormula();
            int index = formula.lastIndexOf("!");
            if (index > -1) {
                String end = formula.substring(index);
                String newFormula = buildFormulaFromSheetNames(sheetnames, end);
                cell.setCellFormula(newFormula);
            }

        }
        Drawing<?> drawing = sheet.getDrawingPatriarch();
        if (drawing instanceof XSSFDrawing) {
            for (XSSFChart chart : ((XSSFDrawing) drawing).getCharts()) {
                chart.setCommited(false);
            }

        }
    }

    private static String buildFormulaFromSheetNames(String[] sheetnames, String end) {

        StringBuilder sb = new StringBuilder();
        String sheetName = sheetnames[0];
        sb.append("\'" + sheetName + "\'" + end);
        for (int i = 1; i < sheetnames.length; i++) {
            sb.append("+");
            sheetName = sheetnames[i];
            sb.append("\'" + sheetName + "\'" + end);
        }
        return sb.toString();
    }


    public static final SimpleDateFormat AlternateMMDDFormat = new SimpleDateFormat("MM.dd");
    public static final SimpleDateFormat FullYearFormat = new SimpleDateFormat("MM/dd/yyyy");
    public static final SimpleDateFormat MMDDFormat = new SimpleDateFormat("MM/dd");
    public static final SimpleDateFormat[] POSSIBLE_FORMATS = {
            MMDDFormat,
            AlternateMMDDFormat,
            MMDDFormat
    };

    private static String datesFromSheet(Sheet sheet) {
        Row row = sheet.getRow(0);
        Cell labelCall = row.getCell(0);  // upper right has dates
        String text = labelCall.getStringCellValue();
        if (!text.startsWith("Week of "))
            throw new IllegalArgumentException("bad cell " + text + " not Week of ...");
        text = text.substring("Week of ".length());
        Date start = dateFromText(text);
        Date end = addDaysToDate(start, 6);
        StringBuilder sb = new StringBuilder();
        sb.append(MMDDFormat.format(start));
        //      sb.append("-");
        //      sb.append(MMDDFormat.format(end));

        return sb.toString();
    }

    private static String datesFromSheets(Sheet[] sheets) {
        Sheet sheet = sheets[0];
        Row row = sheet.getRow(0);
        Cell labelCall = row.getCell(0);  // upper right has dates
        String text = labelCall.getStringCellValue();
        if (!text.startsWith("Week of "))
            throw new IllegalArgumentException("bad cell " + text + " not Week of ...");
        text = text.substring("Week of ".length());
        Date start = dateFromText(text);
        Date end = addDaysToDate(start, (7 * sheets.length) - 1);
        StringBuilder sb = new StringBuilder();
        sb.append(MMDDFormat.format(start));
        sb.append("-");
        sb.append(MMDDFormat.format(end));

        return sb.toString();
    }

    private static Date addDaysToDate(Date start, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(start.getTime());
        cal.add(Calendar.DATE, days);
        return new Date(cal.getTimeInMillis());
    }


    private static Date dateFromText(String text) {
        ParseException last = null;
        for (int i = 0; i < POSSIBLE_FORMATS.length; i++) {
            try {
                Date ret = POSSIBLE_FORMATS[i].parse(text);
                return ret;
            } catch (ParseException e) {
                last = e;
            }
        }
        throw new RuntimeException(last);
    }

    private static void fixFormulas(Row current, int index) {
        boolean isRatioRow = false;
        String test = "C" + (index - 2) + "-" + "C" + (index - 1);
        String TestType = current.getCell(1).getStringCellValue();
        boolean isDataRow = TestType.length() > 0;
        for (int j = current.getFirstCellNum(); j <= current.getLastCellNum(); j++) {
            Cell cell = current.getCell(j);
            if (cell == null)
                continue;
            CellType cellType = cell.getCellType();
            if (cellType == CellType.FORMULA) {
                String text = cell.getCellFormula();
                if (text.contains(test))
                    isRatioRow = true;
                if (text.contains("/")) {
                    String newText = fixDivision(text);
                    cell.setCellFormula(newText);
                }
            }

        }
        if (isRatioRow) {
            Cell cell = current.getCell(9);
            String newText = fixDivision("(J" + (index - 2) + "-" + "J" + (index - 1) + ")/J" + (index - 2));
            cell.setCellType(CellType.FORMULA);
        //    System.out.println("row " + index + " cell J " + newText);
            cell.setCellFormula(newText);
        } else {
            if (isDataRow) {
                Cell cell = current.getCell(9);
                cell.setCellType(CellType.FORMULA);
                String formula = "SUM(C" + index + ":H" + index + ")";
                cell.setCellFormula(formula);
         //       System.out.println("row " + index + " cell J " + formula);

            }
        }
    }

    private static String fixDivision(String text) {
        if (text.startsWith("IF"))
            return text; // already ifxed
        int divisorLoc = text.indexOf("/") + 1;
        String divisor = text.substring(divisorLoc);
        StringBuilder sb = new StringBuilder();
        sb.append("IF(");
        sb.append(divisor);
        sb.append("<=0,\"\",");
        sb.append(text);
        sb.append(")");
        return sb.toString();

    }

    // copy numbers not formulas
    public static void copyRowNumbers(Row srcRow, Row destRow) {
        for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {
            if (j == 1)
                continue; // type label
            Cell oldCell = srcRow.getCell(j);
            Cell newCell = destRow.getCell(j);
            if (oldCell != null) {
                CellType cellType = oldCell.getCellType();

                if (cellType == CellType.NUMERIC || cellType == CellType.BLANK || cellType == CellType.STRING) {
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
            index++;
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
                if (typeRow.equalsIgnoreCase("v LW")) {
                    SpreadsheetUtilities.setRowHidden(row, false);
                    fixVersusRow(newSheet, row, index);
                }
                if (typeRow.equalsIgnoreCase("v Forecast")) {
                    SpreadsheetUtilities.setRowHidden(row, false);
                    fixForcastRow(newSheet, row, index);
                }
                if (typeRow.equalsIgnoreCase("v Budget")) {
                    SpreadsheetUtilities.setRowHidden(row, false);
                    fixBudgetRow(newSheet, row, index);
                }

            }


        }

    }

    private static void fixForcastRow(Sheet mySheet, Row row, int rownum) {
        fixBudgetRow(mySheet, row, rownum);
        fixVersusRow(mySheet, row, rownum);

    }

    private static void fixBudgetRow(Sheet mySheet, Row row, int rownum) {
        row.getCell(0).setCellType(CellType.BLANK);
        for (int i = 2; i < 10; i++) {
            Cell fixed = row.getCell(i);
            CellType cellType = fixed.getCellType();
            if (cellType != CellType.NUMERIC) {
                fixed.setCellType(CellType.NUMERIC);
                double value = findEarlierValue(mySheet, rownum - 1, i);
                fixed.setCellValue(value);
            } else {
                double value = fixed.getNumericCellValue();
                if (value == 0) {
                    value = findEarlierValue(mySheet, rownum - 1, i);
                    fixed.setCellValue(value);
                }
            }

        }
        fixVersusRow(mySheet, row, rownum);

    }

    private static double findEarlierValue(Sheet mySheet, int rowNum, int columnNum) {
        double ret = 0;
        Workbook wb = mySheet.getWorkbook();
        for (int j = wb.getSheetIndex(mySheet) - 1; j > -1; j--) {
            Sheet sh = wb.getSheetAt(j);
            String sheetName = sh.getSheetName();
            if (sheetName.startsWith("Week")) {
                Row row = sh.getRow(rowNum);
                if (row == null)
                    continue;
                Cell test = row.getCell(columnNum);
                if (test == null)
                    continue;
                ret = test.getNumericCellValue();
                if (ret > 0)
                    return ret;
            }

        }
        return ret;
    }


    private static void fixVersusRow(Sheet mySheet, Row row, int rownum) {
        Row dataRow = mySheet.getRow(rownum - 2);
        Row vsRow = mySheet.getRow(rownum - 1);
        for (int i = 2; i < 10; i++) {
            Cell fixed = row.getCell(i);
            Cell test = dataRow.getCell(i);
            Cell vstest = vsRow.getCell(i);
            if (test == null || vstest == null) {
                fixed.setCellType(CellType.BLANK);
            } else {
                if (isComputable(test) && isComputable(vstest)) {
                    CellType cellType = test.getCellType();
                    fixed.setCellType(CellType.FORMULA);
                    char column = (char) ('A' + i);
                    fixed.setCellFormula(fixDivision(column, rownum + 1));
                } else {
                    fixed.setCellType(CellType.BLANK);
                }

            }

        }

    }

    public static boolean isComputable(Cell test) {
        switch (test.getCellType()) {
            case NUMERIC:
            case FORMULA:
                return true;
            default:
                return false;
        }
    }


    private static String fixDivision(char column, int row) {
        String divisor = "" + column + (row - 1);
        String numerator = "(" + column + (row - 2) + "-" + divisor + ")";
        StringBuilder sb = new StringBuilder();
        sb.append("IF(");
        sb.append(divisor);
        sb.append("<=0,\"\",");
        sb.append(numerator + "/" + divisor);
        sb.append(")");
        return sb.toString();

    }


    private static void populateCountRow(Row row, RestaurantLocation loc, WeeklySales ws) {
        row.getCell(0).setCellType(CellType.BLANK);
        for (DayOfWeek day : DayOfWeek.values()) {
            setDailySales(row, ws, loc, day);
        }

        fixTotalFormula(row);
    }

    private static void setDailySales(Row row, WeeklySales ws, RestaurantLocation loc, DayOfWeek day) {
        DailySales dailySales = ws.getDailySales(loc, day);
        int value = day.getValue() + 1;
        Cell cell = row.getCell(value);
        if (dailySales != null && dailySales.hasSales()) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(dailySales.getGuests());
        } else {
            cell.setCellType(CellType.BLANK);
        }

    }

    private static void setDailyNetSales(Row row, WeeklySales ws, RestaurantLocation loc, DayOfWeek day) {
        //   row.getCell(0).setCellType(CellType.BLANK);
        DailySales dailySales = ws.getDailySales(loc, day);
        int value = day.getValue() + 1;
        Cell cell = row.getCell(value);

        if (dailySales != null && dailySales.hasSales()) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(dailySales.netSales);
        } else {
            cell.setCellType(CellType.BLANK);
        }

    }

    private static void populateNetSalesRow(Row row, RestaurantLocation loc, WeeklySales ws) {
        for (DayOfWeek day : DayOfWeek.values()) {
            setDailyNetSales(row, ws, loc, day);
        }
        fixTotalFormula(row);


    }

    private static void fixTotalFormula(Row row) {
        int rownum = row.getRowNum() + 1;
        Cell total = row.getCell(TOTAL_INDEX);
        CellStyle cs = total.getCellStyle();
        CellType cellType = total.getCellType();
        if (cellType == CellType.FORMULA) {
            String formula = total.getCellFormula();
            formula = null;
        } else {
            total.setCellType(CellType.FORMULA);
            String formula = "SUM(C" + rownum + ":H" + rownum + ")";
            total.setCellFormula(formula);
        }
    }

    /**
     * in this version we throw out sheets rather than copying
     *
     * @param workbook
     * @param sixWeeks
     */
    private static void pastReports(Workbook workbook, File sixWeeks, int retainedReports) {
        try {
            int index = getIndexOfLastWeekData(workbook);
            Set<String> retained = new HashSet<>();
            for (int i = 0; i < retainedReports; i++) {
                int ago = retainedReports - 1 - i;
                String name = makeNameFromAgo(ago);
                int index1 = index - ago;
                Sheet datatypeSheet = workbook.getSheetAt(index1);    // this is the second to last
                workbook.setSheetName(index1, name);
                retained.add(name);
            }
            String trendName = "Six Week Trend";
            retained.add(trendName);
            Sheet SixWeekTrend = workbook.getSheet("Six Week Trend");
//
//            fixSixWeekGraph(workbook);
            dropUnRetainedSheets(workbook, retained);

            if (retainedReports >= 12) {
//                Sheet sheet12 = workbook.cloneSheet(workbook.getSheetIndex(SixWeekTrend));
//                String name12 = WORKBOOK_INTERVAL_NAMES[2];
//                workbook.setSheetName(workbook.getSheetIndex(sheet12), name12);
//                int lastIndex = workbook.getNumberOfSheets() - 1;
//                workbook.setSheetOrder(name12, lastIndex);
//                workbook.setSheetHidden(lastIndex, false);
                workbook.setSheetName(workbook.getSheetIndex(SixWeekTrend), "12 Week Trend");
                fix12WeekGraph(workbook);
            }
//
//            if (retainedReports >= 24) {
//                Sheet sixMonth = workbook.cloneSheet(workbook.getSheetIndex(SixWeekTrend));
//                String nameSixMonth = WORKBOOK_INTERVAL_NAMES[4];
//                workbook.setSheetName(workbook.getSheetIndex(sixMonth), nameSixMonth);
//                int lastIndex = workbook.getNumberOfSheets() - 1;
//                workbook.setSheetOrder(nameSixMonth, lastIndex);
//                workbook.setSheetHidden(lastIndex, false);
//
//                fixSixMonthGraph(workbook);
//            }
//
            workbook.setForceFormulaRecalculation(true);

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            evaluator.clearAllCachedResultValues();

            try {
                evaluator.evaluateAll();
            } catch (Exception e) {
                // throw new RuntimeException(e);

            }

            System.out.println(sixWeeks.getAbsolutePath());
            FileOutputStream stream = new FileOutputStream(sixWeeks);
            workbook.write(stream);
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    private static void dropUnRetainedSheets(Workbook workbook, Set<String> retained) {
        Set<String> removed = new HashSet<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet s = workbook.getSheetAt(i);
            String sheetName = s.getSheetName();
            if (!retained.contains(sheetName)) {
                removed.add(sheetName);
                //  System.out.println("dropping sheet " + sheetName);
            }
        }
        for (String s : removed) {
            int toRemove = workbook.getSheetIndex(s);
            if (toRemove >= 0)
                workbook.removeSheetAt(toRemove);

        }
    }

    public static String makeNameFromAgo(int ago) {
        switch (ago) {
            case 0:
                return "This Week";
            case 1:
                return "Last Week";
            default:
                return "" + ago + " Weeks ago ";
        }
    }


    public static File findLatestSalesFile(File dir) {
        File[] files = dir.listFiles();
        Date retDate = null;
        File ret = null;
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String name = file.getName();
            if (!name.toLowerCase().startsWith(SALES_FILE_PREFIX))
                continue;
            Date date = dateFromSalesFile(name);
            if (retDate == null || retDate.before(date)) {
                retDate = date;
                ret = file;
            }
        }
        return ret;
    }

    public static final String DAFE_F_S = "yyyyMMdd";
    public static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat(DAFE_F_S);
    public static final String WORKSHEET_F_S = "MMddyy";
    public static final SimpleDateFormat WORKSHEET_DATE_FORMAT = new SimpleDateFormat(WORKSHEET_F_S);
    public static final String SALES_FILE_PREFIX = "Sales, Guests, Checks, Entrees by Day ".toLowerCase();
    public static final String WORKSHEET_PREFIX = "Flash Workbook".toLowerCase();
    public static final String SUMMARY_PREFIX = "TwelveWeekSummary";

    public static Date dateFromSalesFile(String name) {
        try {
            int prefixLength = SALES_FILE_PREFIX.length();
            name = name.substring(prefixLength, prefixLength + DAFE_F_S.length());
            Date ret = FILE_DATE_FORMAT.parse(name);
            return ret;
        } catch (ParseException e) {
            throw new RuntimeException(e);

        }

    }

    private static File buildInputFromSales(File weeklySalesFile) {
        Date salesDate = dateFromSalesFile(weeklySalesFile.getName());
        salesDate = plusDays(salesDate, -7);
        File dir = weeklySalesFile.getParentFile();
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String name = file.getName();
            if (!name.toLowerCase().startsWith(WORKSHEET_PREFIX))
                continue;
            Date date = dateFromWorksheet(name);
            int diff = differenceInDays(salesDate, date);
            if (Math.abs(diff) < 3)
                return file;
        }
        return getOnlyWorksheet(dir) ;
    }

    private static File getOnlyWorksheet(File dir) {
        File[] files = dir.listFiles();
        File ret = null;
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String name = file.getName();
            if (!name.toLowerCase().startsWith(WORKSHEET_PREFIX))
                continue;
             if(name.toLowerCase().endsWith(".xlsx")) {
                 if(ret == null)
                     ret = file;
                 else
                     throw new IllegalArgumentException("ambiguous worksheet found");

             }
        }
        return ret;

    }

    private static File buildOutputFromSales(File weeklySalesFile) {
        return buildFileFromSales(weeklySalesFile, WORKSHEET_PREFIX);
    }


    private static File buildSummaryFromSales(File weeklySalesFile) {
        return buildFileFromSales(weeklySalesFile, SUMMARY_PREFIX);
    }

    private static File buildFileFromSales(File weeklySalesFile, String prefix) {
        Date salesDate = dateFromSalesFile(weeklySalesFile.getName());
        File dir = weeklySalesFile.getParentFile();
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append(" ");
        sb.append(WORKSHEET_DATE_FORMAT.format(salesDate));

        sb.append(".xlsx");

        return new File(dir, sb.toString());
    }


    public static final long ONE_DAY_MILLISEC = 1000 * 60 * 60 * 24;

    public static int differenceInDays(Date start, Date end) {
        return (int) ((start.getTime() - end.getTime() + ONE_DAY_MILLISEC / 2) / ONE_DAY_MILLISEC);
    }


    public static Date plusDays(Date start, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(start.getTime());
        cal.add(Calendar.DATE, days);
        return new Date(cal.getTimeInMillis());
    }

    private static Date dateFromWorksheet(String name) {
        try {
            int prefixLength = WORKSHEET_PREFIX.length();
            name = name.substring(prefixLength);
            name = name.replace(".xlsx", "");
            name = name.trim();
            name = name.substring(0, WORKSHEET_F_S.length());
            Date ret = WORKSHEET_DATE_FORMAT.parse(name);
            return ret;
        } catch (ParseException e) {
            throw new RuntimeException(e);

        }

    }


    public static void main(String[] args) {
        try {
            /*

            Exception in thread "main" java.lang.RuntimeException: java.io.IOException: Zip bomb detected! The file would exceed the max. ratio of compressed file size to the size of the expanded data.
This may indicate that the file is used to inflate memory usage and thus could pose a security risk.
You can adjust this limit via ZipSecureFile.setMinInflateRatio() if you need to work with files which exceed this limit.
Uncompressed size: 535118, Raw/compressed size: 5341, ratio: 0.009981
Limits: MIN_INFLATE_RATIO: 0.010000, Entry: xl/styles.xml

             */
            ZipSecureFile.setMinInflateRatio(0.001); //
            String currentDir = System.getProperty("user.dir");
            File dir = new File(currentDir);
            File weeklySalesFile = findLatestSalesFile(dir);

            File inputSheet = buildInputFromSales(weeklySalesFile);
            File outputSheet = buildOutputFromSales(weeklySalesFile);
            File summaryFile = buildSummaryFromSales(weeklySalesFile);

            Workbook workbook = readWorkbook(inputSheet);
            WeeklySales ws = new WeeklySales(weeklySalesFile);

            //      listSheetNamesSheets(workbook);
            int numberOfSheets = workbook.getNumberOfSheets();
            int index = getIndexOfLastWeekData(workbook);
            Sheet newSheet = workbook.cloneSheet(index);
            String sheetName = ws.getSheetName();
            workbook.setSheetName(workbook.getSheetIndex(newSheet), sheetName);

            copyDataFromLastWeek(newSheet);
            workbook.setSheetOrder(sheetName, index + 1);
            fillFromWeeklySales(newSheet, ws);

            fixSixWeekGraph(workbook);

            SpreadsheetUtilities.evaluateSheet(newSheet);
            newSheet.setForceFormulaRecalculation(true);

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            evaluator.clearAllCachedResultValues();
            evaluator.evaluateAll();

            FileOutputStream os = new FileOutputStream(outputSheet);
            workbook.write(os);
            os.close();

            pastReports(workbook, summaryFile, 12);

        } catch (IOException e) {
            throw new RuntimeException(e);

        }

    }


}
