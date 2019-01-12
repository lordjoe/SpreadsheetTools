package com.lordjoe.farestart;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;

import static com.lordjoe.spreadsheet.SpreadsheetUtilities.toCamelCase;

/**
 * com.lordjoe.farestart.DailySales
 * User: Steve
 * Date: 1/11/19
 */
public class DailySales {
    static int columnIndex = 0;
    public static final int LOCATIONID = columnIndex++;
    public static final int LOCATIONNAME = columnIndex++;
    public static final int DOB = columnIndex++;
    public static final int DAY_OF_WEEK = columnIndex++;
    public static final int NET_SALES = columnIndex++;
    public static final int GROSS_SALES = columnIndex++;
    public static final int GUESTS = columnIndex++;
    public static final int CHECKS = columnIndex++;
    public static final int ENTREES = columnIndex++;

    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy");


    public final RestaurantLocation location;
    public final Calendar date = Calendar.getInstance();
    public final DayOfWeek weekday;
    public final double netSales;
    public final double grossSales;
    public final int guests;
    public final int checks;
    public final int entrees;

    public DailySales(RestaurantLocation location, Date date, DayOfWeek weekday, double netSales, double grossSales, int guests, int checks, int entrees) {
        this.location = location;
        date.setTime(date.getTime());
        this.weekday = weekday;
        this.netSales = netSales;
        this.grossSales = grossSales;
        this.guests = guests;
        this.checks = checks;
        this.entrees = entrees;
    }

    public DailySales(String[] items) {
        try {
            int index = 0;
            location = RestaurantLocation.byId(Integer.parseInt(items[index++]));
            index++; // ignore name
            date.setTime(FORMAT.parse(items[index++]));
            weekday = DayOfWeek.valueOf(items[index++].toUpperCase());
            netSales = Double.parseDouble(items[index++]);
            grossSales = Double.parseDouble(items[index++]);
            guests = Integer.parseInt(items[index++]);
            checks = Integer.parseInt(items[index++]);
            entrees = Integer.parseInt(items[index++]);
        } catch (ParseException e) {
            throw new RuntimeException(e);

        }
    }

    public String asCVSString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(location.id));
        sb.append(",");
        sb.append(location.toString());
        sb.append(",");
        sb.append(FORMAT.format(date));
        sb.append(",");
        sb.append(toCamelCase(weekday.toString()));
        sb.append(",");
        sb.append(String.format("%.2f", netSales));
        sb.append(",");
        sb.append(String.format("%.2f", grossSales));
        sb.append(",");
        sb.append(Integer.toString(guests));
        sb.append(",");
        sb.append(Integer.toString(checks));
        sb.append(",");
        sb.append(Integer.toString(entrees));


        return sb.toString();
    }
}
