package com.lordjoe.blocks_in_bloom;

/**
 * com.lordjoe.blocks_in_bloom.LatLong
 * User: Steve
 * Date: 8/22/19
 */
public class LatLong {


    public final double lat;
    public final double lon;

    public LatLong(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public LatLong(String s) {
        if(s.startsWith("?"))      {
            lat = 0;
            lon = 0;
            return;
        }
        s = s.replace("\"","");
        String[] split = s.split(",");
        this.lat = Double.parseDouble(split[0]);
        this.lon = Double.parseDouble(split[1]);;
    }

    public boolean isZero() {
        return lat==0 && lon == 0;
    }

    @Override
    public String toString() {
        return  Double.toString(lat)  +"," + Double.toString(lon);
    }
}
