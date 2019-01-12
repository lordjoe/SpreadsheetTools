package com.lordjoe.whitepages;

import com.lordjoe.loeb.contributer.PhoneNumber;
import com.lordjoe.utilities.StringUtilities;
import org.json.JSONObject;

/**
 * com.lordjoe.whitepages.WhitePagesPhone
 * User: Steve
 * Date: 4/23/2018
 */
public class WhitePagesPhone extends PhoneNumber {

    private String lineType;
    public WhitePagesPhone(JSONObject number) {
        super(PhoneNumber.conditionNumber((String)number.get("phone_number")));
        lineType = StringUtilities.getPropertyString(number,"line_type");
    }

    public String getLineType() {
        return lineType;
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }
}
