package  com.lordjoe.routing;

/**
 * com.lordjoe.farestart.com.lordjoe.routing.LabeledLocation
 * User: Steve
 * Date: 10/19/2017
 */
public class LabeledLocation {
    public final String name;
    public final LatLon loc;


    public LabeledLocation(String name, LatLon loc) {
        this.name = name;
        this.loc = loc;
    }
    public LabeledLocation(String name, String lat,String lon) {
        this.name = name;
        this.loc = new LatLon(lat,lon);
    }
    public LabeledLocation(String name, double lat,double lon) {
        this.name = name;
        this.loc = new LatLon(lat,lon);
    }

    @Override
    public String toString() {
        return name;
    }


    public   double distance( LabeledLocation y)  {
        return  loc.distance(y.loc);
    }

}
