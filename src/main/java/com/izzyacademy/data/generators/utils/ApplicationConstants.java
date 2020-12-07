package com.izzyacademy.data.generators.utils;

import java.util.Map;

public class ApplicationConstants {

    private static final Map<String, String> env = System.getenv();

    private static final String DEFAULT_MIN_ORDER_INTERVAL_SECONDS = "1";
    private static final String DEFAULT_MAX_ORDER_INTERVAL_SECONDS = "5";

    private static final String DEFAULT_SHIPMENT_INTERVAL_SECONDS = "5";
    private static final String DEFAULT_SHIPMENT_COUNT = "10";

    private static final String DEFAULT_ORDER_RETURN_INTERVAL_SECONDS = "5";
    private static final String DEFAULT_ORDER_RETURN_AGE_SECONDS = "5";
    private static final String DEFAULT_ORDER_RETURN_COUNT = "10";

    // How many seconds to wait before placing orders
    public static final int MIN_ORDER_INTERVAL_SECONDS;
    public static final int MAX_ORDER_INTERVAL_SECONDS;

    // How many seconds orders are shipped
    public static final int SHIPMENT_INTERVAL_SECONDS;
    public static final int SHIPMENT_COUNT;

    // How many seconds orders are returned
    public static final int ORDER_RETURN_INTERVAL_SECONDS;
    public static final int ORDER_RETURN_AGE_SECONDS;
    public static final int ORDER_RETURN_COUNT;

    static {

        final Map<String, String> env = System.getenv();

        MIN_ORDER_INTERVAL_SECONDS = getInt("MIN_ORDER_INTERVAL_SECONDS", DEFAULT_MIN_ORDER_INTERVAL_SECONDS);
        MAX_ORDER_INTERVAL_SECONDS = getInt("MAX_ORDER_INTERVAL_SECONDS", DEFAULT_MAX_ORDER_INTERVAL_SECONDS);

        SHIPMENT_INTERVAL_SECONDS = getInt("SHIPMENT_INTERVAL_SECONDS", DEFAULT_SHIPMENT_INTERVAL_SECONDS);
        SHIPMENT_COUNT = getInt("SHIPMENT_COUNT", DEFAULT_SHIPMENT_COUNT);

        ORDER_RETURN_INTERVAL_SECONDS = getInt("ORDER_RETURN_INTERVAL_SECONDS", DEFAULT_ORDER_RETURN_INTERVAL_SECONDS);
        ORDER_RETURN_AGE_SECONDS = getInt("ORDER_RETURN_AGE_SECONDS", DEFAULT_ORDER_RETURN_AGE_SECONDS);
        ORDER_RETURN_COUNT = getInt("ORDER_RETURN_COUNT", DEFAULT_ORDER_RETURN_COUNT);
    }

    private static double getDouble(final String key, String defaultValue) {
        return Double.parseDouble(env.getOrDefault(key, defaultValue));
    }

    private static long getLong(final String key, String defaultValue) {
        return Long.parseLong(env.getOrDefault(key, defaultValue));
    }

    private static int getInt(final String key, String defaultValue) {
        return Integer.parseInt(env.getOrDefault(key, defaultValue));
    }

    private static boolean getBoolean(final String key, String defaultValue) {
        return Boolean.parseBoolean(env.getOrDefault(key, defaultValue));
    }

    private static String getString(final String key, String defaultValue) {
        return env.getOrDefault(key, defaultValue);
    }
}
