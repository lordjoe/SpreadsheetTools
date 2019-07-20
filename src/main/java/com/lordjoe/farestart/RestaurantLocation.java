package com.lordjoe.farestart;

import com.lordjoe.spreadsheet.SpreadsheetUtilities;

import java.util.HashMap;
import java.util.Map;

/**
 * com.lordjoe.spreadsheet.RestaurantLocation
 * User: Steve
 * Date: 1/11/19
 */
public enum RestaurantLocation {
    MASLOWS(211),
    RISE_COFFEE(212),
    COMMUNITY_SALAD(213),
    COMMUNITY_SANTA_FE(214),
    COMMUNITY_BOWLS(215),
     FARESTART_CATERING(221),
    CAFE_2100(232),
    PT_CAFE(235),
    FS_RESTAURANT(251),
    GUEST_CHEF_NIGHT(261),
    SCHOOL_MEALS(271),
    COMMUNITY_MEALS(281),
    COMMUNITY_TABLE(-1), // thios is the sum of community_salad,santa_fe,bowls
    ;

    public static final RestaurantLocation[] CommunityTables = { COMMUNITY_SALAD,COMMUNITY_SANTA_FE,    COMMUNITY_BOWLS };

    private static Map<Integer, RestaurantLocation> byId = new HashMap<>();
    private static Map<String, RestaurantLocation> byName = new HashMap<>();


    static {
        for (RestaurantLocation value : RestaurantLocation.values()) {
            byId.put(value.id, value);
            byName.put(value.toString(), value);
        }
        byName.put("2100 Café", CAFE_2100);
        byName.put("PT Café", PT_CAFE);
        byName.put("Rise", RISE_COFFEE);
        byName.put("Catering", FARESTART_CATERING);
      }

    public static RestaurantLocation byId(int id) {
        return byId.get(id);
    }

    public static RestaurantLocation get(String name) {
        RestaurantLocation ret = byName.get(name);
        if (ret != null)
            return ret;
        if(name.startsWith("2100 Caf"))
            return CAFE_2100;
        if(name.startsWith("PT Caf"))
            return PT_CAFE;
        return RestaurantLocation.valueOf(name.replace(" ", "_").toUpperCase());
    }


    public final int id;

    RestaurantLocation(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        String item = super.toString();
        switch(this) {
            case PT_CAFE:
                return "PT Café";
            case CAFE_2100:
                return "2100 Café";

        }
        item = item.replace("_", " ").toLowerCase();
        return SpreadsheetUtilities.toCamelCase(item);
    }

}
