package com.lordjoe.blocks_in_bloom;

import com.lordjoe.loeb.State;

/**
 * com.lordjoe.blocks_in_bloom.BlockAddress
 * User: Steve
 * Date: 8/22/19
 */
public class BlockAddress {

    public static final String HEADER =
            "1st Year,Block,Address,city,State,Quadrant,# houses,# yrs,# addl houses,Total,LatLon".replace(",","\t");

    public final int year;
    public final String street;
    public final String block;
    public final String city = "Rochester";
    public final State state = State.NEW_YORK;
    public final String quadrent;
    public final int number_houses;
    public final int number_years;
    public final int additional_houses;
    public final int total;
     private LatLong latLon;


    public BlockAddress(int year, String street, String block, String quadrent, int number_houses, int number_years, int additional_houses, int total) {
        this.year = year;
        this.street = street;
        this.block = block;
        this.quadrent = quadrent;
        this.number_houses = number_houses;
        this.number_years = number_years;
        this.additional_houses = additional_houses;
        this.total = total;
    }

    public BlockAddress(String s) {
        String splitString = ",";
        if(s.contains("\t"))
            splitString = "\t";
        String[] items = s.split(splitString);
        int index = 0;
        if (items.length >= index && items[index].length() > 0)
            this.year = Integer.parseInt(items[index++]);
        else
            this.year = 1;
        this.block = items[index++];
        this.street = items[index++];
        index++; // ingnore city
        index++; // ingnore state
        this.quadrent = items[index++];
        ;
        if (items.length >= index && items[index].length() > 0)
            this.number_houses = Integer.parseInt(items[index++]);
        else
            this.number_houses = 0;

        if (items.length > index && items[index].length() > 0)
            this.number_years = Integer.parseInt(items[index++]);
        else
            this.number_years = 0;

        if (items.length > index && items[index].length() > 0)
            this.additional_houses = Integer.parseInt(items[index++]);
        else
            this.additional_houses = 0;

        if (items.length > index && items[index].length() > 0)
            this.total = Integer.parseInt(items[index++]);
        else
            this.total = 0;

        if (items.length > index && items[index].length() > 0)
              setLatLon(new LatLong(items[index]));

    }

    public String asAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(street);
        sb.append(" " + city + ",");
        sb.append(state);

        return sb.toString();
    }


    public String asTSV() {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(year));
        sb.append("\t");
        sb.append(block);
        sb.append("\t");
        sb.append(street);
        sb.append("\t");
        sb.append(city);
        sb.append("\t");
        sb.append(state.getAbbreviation());
        sb.append("\t");
        sb.append(quadrent);
        sb.append("\t");
        sb.append(Integer.toString(number_houses));
        sb.append("\t");
        sb.append(Integer.toString(number_years));
        sb.append("\t");
        sb.append(Integer.toString(additional_houses));
        sb.append("\t");
        sb.append(Integer.toString(total));
        sb.append("\t");
        if(latLon != null)
            sb.append(latLon.toString()) ;
        else
            sb.append("");

        return sb.toString();
    }

    public LatLong getLatLon() {
        return latLon;
    }

    public void setLatLon(LatLong latLon) {
        this.latLon = latLon;
    }
}
