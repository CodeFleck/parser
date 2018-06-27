package com.ef.util;

import java.time.format.DateTimeFormatter;

public class WalletHubUtil {

    private static final String DEFAULT_FILEPATH = "/access.log";
    private static final String START_DATE_PARAM = "--startDate=";
    private static final String DURATION_PARAM = "--duration=";
    private static final String THRESHOLD_PARAM = "--threshold=";
    private static final String ACCESS_LOG_PARAM = "--accesslog=";
    private static final String HOURLY = "hourly";
    private static final String DAILY = "daily";
    private static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String getFilePath() {
        return DEFAULT_FILEPATH;
    }

    public static String getStartDateParam() {
        return START_DATE_PARAM;
    }

    public static String getDurationParam() {
        return DURATION_PARAM;
    }

    public static String getThresholdParam() {
        return THRESHOLD_PARAM;
    }

    public static String getAccessLogParam() {
        return ACCESS_LOG_PARAM;
    }

    public static String getHourly() {
        return HOURLY;
    }

    public static String getDaily() {
        return DAILY;
    }

    public static DateTimeFormatter getLogFormatter() {
        return LOG_FORMATTER;
    }

    public static DateTimeFormatter getInputFormatter() {
        return INPUT_FORMATTER;
    }

    private WalletHubUtil(){
        throw new AssertionError();
    }
}
