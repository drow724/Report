package com.report.utils;

public class CurrencyUtils {

    public static String sanitize(String str) {
        return str.replaceAll("[^0-9+-]", "");
    }
}
