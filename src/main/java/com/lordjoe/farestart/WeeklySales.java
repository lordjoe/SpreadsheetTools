package com.lordjoe.farestart;


import com.lordjoe.spreadsheet.SpreadsheetUtilities;
import com.lordjoe.utilities.FileUtilities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * com.lordjoe.farestart.WeeklySales
 * User: Steve
 * Date: 1/11/19
 */
public class WeeklySales {

    SimpleDateFormat SHEET_FORMAT = new SimpleDateFormat("MM.dd");
    public Date date;
    private Map<RestaurantLocation, Map<DayOfWeek,DailySales>> locationSales = new HashMap<>();

    public WeeklySales(File f) {
         this(FileUtilities.readInLines(f));
    }

    public WeeklySales(String[] lines) {
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            String[] items = line.split(",");
            DailySales sales = new DailySales(items);
            Map<DayOfWeek,DailySales> mySales =  locationSales.get(sales.location) ;
            if(mySales == null)  {
                mySales = new HashMap<>();
                locationSales.put(sales.location,mySales) ;
            }
            mySales.put(sales.weekday,sales);
        }
    }

    public  Map<DayOfWeek,DailySales> getLocationSales(RestaurantLocation loc)   {
        return    locationSales.get(loc);
    }

    public Calendar getWeekStart()  {
        for (RestaurantLocation restaurantLocation : locationSales.keySet()) {
            Map<DayOfWeek, DailySales> dayOfWeekDailySalesMap = locationSales.get(restaurantLocation);
            for (DayOfWeek dayOfWeek : dayOfWeekDailySalesMap.keySet()) {
                DailySales ds = dayOfWeekDailySalesMap.get(dayOfWeek);
                return SpreadsheetUtilities.weekStartFromDate(ds.date);
            }
        }
        return null;
    }

    public  DailySales getDailySales(RestaurantLocation loc,DayOfWeek day)   {
        Map<DayOfWeek, DailySales> dm = locationSales.get(loc);
        if(dm == null)
            return null;
        return dm.get(day);

    }
    public String getSheetName() {
        Calendar weekStart = getWeekStart();
        Date d = new Date(weekStart.getTimeInMillis() + 100);
          return "Week of " +  SHEET_FORMAT.format(d);
    }

    public static void main(String[] args) {
        File f = new File(args[0]) ;
        WeeklySales ws = new WeeklySales(f);
        for (RestaurantLocation value : RestaurantLocation.values()) {
            Map<DayOfWeek,DailySales> sales = ws.getLocationSales(value);
            if(sales != null) {
                for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                    DailySales ds = sales.get(dayOfWeek);
                    if(ds != null) {
                        System.out.println(ds.asCVSString());
                    }
                }
            }
        }

    }

 }
