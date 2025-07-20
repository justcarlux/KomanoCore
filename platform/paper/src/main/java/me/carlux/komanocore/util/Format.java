package me.carlux.komanocore.util;

import java.text.DecimalFormat;

public class Format {

    private static final DecimalFormat TWO_DECIMAL_FORMAT = new DecimalFormat("#.##");

    public static String coordinateDecimal(double value) {
        return TWO_DECIMAL_FORMAT.format(value);
    }

    public static String distanceDecimal(double value) {
        return TWO_DECIMAL_FORMAT.format(value);
    }

}
