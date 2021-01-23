package com.lordjoe.blocks_in_bloom;

import java.util.*;

/**
 * com.lordjoe.blocks_in_bloom.PageBuilder
 * User: Steve
 * Date: 8/22/19
 */
public class PageBuilder {

    public static final String MAP_HEADER =
            "      function initMap() {\n" +
                    "        var rochesterLatLng = {lat: 43.162500, lng: -77.618289};\n" +
                       "        var map = new google.maps.Map(document.getElementById('map'), {\n" +
                    "          zoom: 14,\n" +
                    "          center: rochesterLatLng\n" +
                    "        });" +
                    "       // Create the DIV to hold the control and call the CenterControl()\n" +
                    "        // constructor passing in this DIV.\n" +
                    "        var centerControlDiv = document.createElement('div');\n" +
                    "        var centerControl = new CenterControl(centerControlDiv, map);\n" +
                    "\n" +
                    "        centerControlDiv.index = 1;\n" +
                    "        map.controls[google.maps.ControlPosition.TOP_CENTER].push(centerControlDiv);\n";

    public static final String HEADER =
            "<html>\n" +
                    "  <head>\n" +
                    "    <meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\">\n" +
                    "    <meta charset=\"utf-8\">\n" +
                    "    <title>Rochester Blocks In Bloom</title>\n" +
                    "    <style>\n" +
                    "      #map {\n" +
                    "        height: 100%;\n" +
                    "      }\n" +
                    "      #legend {\n" +
                    "        font-family: Arial, sans-serif;\n" +
                    "        background: #fff;\n" +
                    "        padding: 10px;\n" +
                    "        margin: 10px;\n" +
                    "        border: 3px solid #000;\n" +
                    "      }\n" +
                    "      #legend h3 {\n" +
                    "        margin-top: 0;\n" +
                    "      }\n" +
                    "      #legend img {\n" +
                    "        vertical-align: middle;\n" +
                       "      }\n" +
                     "      html, body {\n" +
                    "        height: 100%;\n" +
                    "        margin: 0;\n" +
                    "        padding: 0;\n" +
                    "      }\n" +
                    "      }\n" +
                    "    </style>\n" +
                    "  </head>\n" +
                    "  <body>\n" +
                    "    <div id=\"map\"></div>\n" +
                    "   <div id=\"legend\"><h3>Year Started</h3></div>\n" +
                    "    <script>\n";

    public static final String LEGEND_CONTROL =
            "\n" +
                    "      /**\n" +
                    "       * The CenterControl adds a control to the map that recenters the map on\n" +
                    "       * Chicago.\n" +
                    "       * This constructor takes the control DIV as an argument.\n" +
                    "       * @constructor\n" +
                    "       */\n" +
                    "      function CenterControl(controlDiv, map) {\n" +
                    "\n" +
                    "        // Set CSS for the control border.\n" +
                    "        var controlUI = document.createElement('div');\n" +
                    "        controlUI.style.backgroundColor = '#fff';\n" +
                    "        controlUI.style.border = '2px solid #fff';\n" +
                    "        controlUI.style.borderRadius = '3px';\n" +
                    "        controlUI.style.boxShadow = '0 2px 6px rgba(0,0,0,.3)';\n" +
                    "        controlUI.style.cursor = 'pointer';\n" +
                    "        controlUI.style.marginBottom = '22px';\n" +
                    "        controlUI.style.textAlign = 'center';\n" +
                      "        controlDiv.appendChild(controlUI);\n" +
                     "        // Set CSS for the control interior.\n" +
                    "        var controlText = document.createElement('div');\n" +
                    "        controlText.style.color = 'rgb(25,25,25)';\n" +
                    "        controlText.style.fontFamily = 'Roboto,Arial,sans-serif';\n" +
                    "        controlText.style.fontSize = '16px';\n" +
                    "        controlText.style.lineHeight = '38px';\n" +
                    "        controlText.style.paddingLeft = '5px';\n" +
                    "        controlText.style.paddingRight = '5px';\n" +
                    "        controlText.innerHTML = \'Blocks in Bloom Locations Since ";

    public static final String LEGEND_CONTROL2 = "\';\n" +
                    "        controlUI.appendChild(controlText);\n" +
                     "      }\n";

    public static final String Footer =
            "        </script>\n" +
                    "<script async defer\n" +
                    "    src=\"https://maps.googleapis.com/maps/api/js?key=" + KeyHolder.KEY + "&callback=initMap\">\n" +
                    "    </script>\n" +
                    "  </body>\n" +
                    "</html>\n";

    public static String buildPage(Map<BlockAddress, LatLong> toLatLon) {
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER);
        sb.append(buildInitMap(toLatLon));
        sb.append(Footer);

        return sb.toString();
    }

    public static final String BUILDLEGEND =
            "        var legend = document.getElementById('legend');\n" +
                    "        for (var key in icons) {\n" +
                    "          var type = icons[key];\n" +
                    "          var name = type.name;\n" +
                    "          var icon = type.icon;\n" +
                    "          var div = document.createElement('div');\n" +
                    "          div.innerHTML = '<img src=\"' + icon + '\"> ' + name;\n" +
                    "          legend.appendChild(div);\n" +
                    "        }\n" +
                    "\n" +
                    "        map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(legend);" ;

    public static String buildInitMap(Map<BlockAddress, LatLong> toLatLon) {

        List<Integer> usedYears = buildUsedYears(toLatLon);
        int startYear = usedYears.get(0);
        StringBuilder sb = new StringBuilder();
        sb.append(MAP_HEADER);
        sb.append(LEGEND_CONTROL);
        sb.append(startYear);
        sb.append(LEGEND_CONTROL2);
        sb.append("\n") ;
         sb.append(buildIcons(usedYears));
        for (BlockAddress address : toLatLon.keySet()) {
            sb.append(buildAddressMarker(address, toLatLon.get(address)));
        }
        sb.append(BUILDLEGEND);
        sb.append("\n}\n");
        return sb.toString();
    }

    private static String buildIcons(List<Integer> usedYears) {
        StringBuilder sb = new StringBuilder();
        sb.append("      var icons = {\n" );
        for (Integer usedYear : usedYears) {
           int year = usedYear;
            sb.append("    year" + year + ": {\n");
            sb.append("      name: \'" + year + "\', \n");
            sb.append( "          icon: \'"  + yearToUrl( year)   + "\'\n");
            sb.append("      }, \n");
        }
        sb.append("        };\n") ;
        return sb.toString();
    }

    private static List<Integer> buildUsedYears(Map<BlockAddress, LatLong> toLatLon) {
        Set<Integer> ret = new HashSet<>();
        for (BlockAddress address : toLatLon.keySet()) {
            ret.add(address.year);
        }
        List realRet = new ArrayList<>(ret);
        Collections.sort(realRet);
        return realRet;


    }

    private static Set<String> usedNames = new HashSet<>();

    public static final String[] colors = {
            "yellow" ,
            "red",
            "ltblue" ,
            "green" ,
             "purple" ,
            "pink" ,
            "orange" ,
      } ;

    public static String yearToUrl(int year) {
         year = year % colors.length;
         return "http://maps.google.com/mapfiles/ms/icons/" + colors[year] +  "-dot.png"; //".png"; //
    }

    private static String buildAddressMarker(BlockAddress address, LatLong latLong) {
        StringBuilder sb = new StringBuilder();
   //     String myName = address.street.replace(".","");
        String myName = address.block.replace(".","");
        String name = myName.replace(" ", "").replace("-","").
                replace("/","");
        if(Character.isDigit(name.charAt(0)))
            name = "x" + name;
        if (usedNames.contains(name))
            return null;
         usedNames.add(name) ;
          sb.append("\n");
        sb.append("      var " + name + " = {lat: " +  latLong.lat +  ", lng: " +    latLong.lon + " };\n");
        sb.append("      var marker = new google.maps.Marker({\n" +
                "          position: " + name + ",\n" +
                "          map: map,\n" +
                "          title: '" + myName + "',\n" +
         //       "          label: '" + myName + "',\n" +
                "          icon: { \n" +
                "            url: \'"  + yearToUrl(address.year) + "\',\n" +
                "            }\n" +
                "        });");
        return sb.toString();
    }

}
