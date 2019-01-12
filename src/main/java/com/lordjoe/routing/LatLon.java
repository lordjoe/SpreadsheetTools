package  com.lordjoe.routing;

/**
 * com.lordjoe.farestart.com.lordjoe.routing.LatLon
 * User: Steve
 * Date: 10/19/2017
 */
public class LatLon {
    public final double lat;
    public final double lon;

    public LatLon(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public LatLon(String latStr, String lonStr) {
        this(Double.parseDouble(latStr), Double.parseDouble(lonStr));
    }

    /**
     *
     * @param latLonStr    lat,lon
     */
    public LatLon(String latLonStr ) {
        String[] split = latLonStr.split(",");
        this.lat = Double.parseDouble(split[0]);
        this.lon =  Double.parseDouble(split[1]);
       }

    @Override
    public String toString() {
        return Double.toString(lat) + "," + Double.toString(lon);
    }

    public   double distance( LatLon y)  {
        return  distance(this,y);
    }


    public static double distance(LatLon x,LatLon y)  {
        return  distance(x.lat,x.lon,y.lat,y.lon);
    }

    public static final double EARTH_RADIAN_METERS = 40_075_000;

    /**
     * http://www.geodatasource.com/developers/java
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @param unit
     * @return
     */
    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = dist * EARTH_RADIAN_METERS;
        return (dist);
    }


}
