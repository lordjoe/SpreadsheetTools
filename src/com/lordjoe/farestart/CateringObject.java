package com.lordjoe.farestart;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * com.lordjoe.farestart.CateringObject
 * User: Steve
 * Date: 4/17/2017
 */
public class CateringObject {
    public static String[] oldColumns = {
            "Event Date",
            "Planned",
            "Guaranteed",
            "Actual",
            "Client/Organization",
            "Account Category",
            "Party Name",
            "Theme",
            "Category",
            "Food",
            "Beverage",
            "Liquor",
            "Equipment",
            "Labor",
            "Room",
            "Other",
            "Subtotal",
            "Serv Chg",
            "Tax",
            "Total",
            "Status",
            "Paid",
    };
    public static String[] newColumns = {
            "Event Date",
            "Planned",
            "Guaranteed",
            "Actual",
            "Guest Count",
            "Client/Organization",
            "Account",
            "Account Category",
            "Business Type",
            "Party Name",
            "Theme",
            "Event Type",
            "Category",
            "Event Category",
            "Food",
            "Beverage",
            "Liquor",
            "Equipment",
            "Labor",
            "Room",
            "Other",
            "Subtotal",
            "Serv Chg",
            "Tax",
            "Total",
    };




    public static void remapData(Map<String, Map<String, Object>> remappedValues, List<CateringObject> items) {
        for (String s : remappedValues.keySet()) {
            Map<String, Object> remap = remappedValues.get(s);
            String mappedColumn = RemapType.fromString(s).getRemapTo();
            for (CateringObject item : items) {
                String original = item.getRawColumnData(s);
                if(original == null)
                    continue;
                Object mapped =  remap.get(original);
                if(mapped == null)
                    mapped =   original = "_FIX";
                item.setRealColumnData(mappedColumn,mapped) ;
            }
        }
    }

    public static void remapData(CateringObject item,AdditionalAccounts addedAccounts)   {
        RemapType[] values = RemapType.values();
        for (int i = 0; i < values.length; i++) {
            RemapType value = values[i];
            String original = value.toString();
            String remapped = value.getRemapTo();
            String originalValue = item.getRawColumnData(original);
            Object newValue = null;
            switch(value) {
                case Theme:
                    newValue = Event_Type.remap(originalValue,item) ;
                    if(newValue == Event_Type.Unmapped)  {
                        newValue =  originalValue + "=>Unmapped";

                    }
                    break;
                case Account_Category:
                    newValue = Account.remap(originalValue,item,addedAccounts) ;
                    if(newValue == Account.Unmapped)  {
                        newValue =  originalValue + "=>Unmapped";

                    }
                    break;
                case Party_Name:
                    newValue = Business_Type.remap(originalValue,item,addedAccounts) ;
                    if(newValue == Business_Type.Unmapped)  {
                        newValue =  originalValue + "=>Unmapped";

                    }
                    break;
                case Category:
                    newValue = Category.remap(originalValue,item) ;
                    if(newValue == Category.Unmapped)  {
                        newValue =  originalValue + "=>Unmapped";

                    }
                    break;

            }
            item.setRealColumnData(remapped,newValue);

        }
    }

    public static Map<String,Map<String,Object>> getRemappedValues(Map<String, Set<String>> colValues) {
        Map<String,Map<String,Object>>  ret = new HashMap<String, Map<String,Object>>();
        for (RemapType s : RemapType.values()) {
            String mappedColumn = s.getRemapTo();
            Set<String> strings = colValues.get(s);
            Map<String,Object> remap = new HashMap<String, Object>();
            for (String string : strings) {
                Object remapped;
                switch(s)  {
                    case Account_Category:

                }

            }
            ret.put(s.toString(),remap) ;


        }
        return ret;
    }


    private final String[] columns;
    private final Map<String,String> rawValues = new HashMap<String, String>();
    private final Map<String,Object> realValues = new HashMap<String, Object>();

    public CateringObject(String[] cols , String[] items) {
         if (items.length != cols.length)
            throw new IllegalStateException("problem"); // ToDo change
        this.columns = cols;
        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            String col = cols[i];
            if(item.length() > 0) {
                rawValues.put(col, item);
                realValues.put(col, item);
            }
        }
    }

    public int getYear() {
        String date = getRawColumnData("Event Date");
        if(!date.contains("/"))
            return 0;
        String substring = date.substring(date.lastIndexOf("/") + 1);
        return Integer.parseInt(substring);
    }

    public String[] getColumns() {
        return columns;
    }

    public String getRawColumnData(String col)   {
        if(!rawValues.containsKey(col))
            return null;
        return rawValues.get(col);
    }


    public Object getRealColumnData(String col)   {
        if(!realValues.containsKey(col))
            return null;
        return realValues.get(col);
    }

    public void setRealColumnData(String col,Object data)   {
        realValues.put(col,data);
    }

    public void writeColumnData(PrintWriter out,String[] headers)  {
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            Object o = getRealColumnData(header);
            if(o != null)
                out.print(o.toString());
             if(i < headers.length - 1)
                 out.print("\t");
        }
        out.println();
    }

    public static  void writeColumnHeader(PrintWriter out,String[] headers)  {
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
                  out.print(header);
            if(i < headers.length - 1)
                out.print("\t");
        }
        out.println();
    }


}
