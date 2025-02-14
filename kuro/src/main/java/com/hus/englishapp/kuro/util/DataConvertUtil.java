package com.hus.englishapp.kuro.util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class DataConvertUtil {

    private static final Logger logger = LogManager.getLogger(DataConvertUtil.class);

    public static String safeToString(Object obj1) {
        return safeToString(obj1, "");
    }

    public static Date safeToDate(Object obj1) {
        if (obj1 == null) {
            return null;
        }
        return (Date) obj1;
    }

    public static Long safeToLong(Object obj1, Long defaultValue) {
        if (obj1 == null) {
            return defaultValue;
        }
        if (obj1 instanceof BigDecimal) {
            return ((BigDecimal) obj1).longValue();
        }
        if (obj1 instanceof BigInteger) {
            return ((BigInteger) obj1).longValue();
        }
        if (obj1 instanceof Float) {
            return ((Float) obj1).longValue();
        }
        try {
            return Long.parseLong(obj1.toString());
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static String safeToString(Object obj1, String defaultValue) {
        if (obj1 == null) {
            return defaultValue;
        }

        return obj1.toString();
    }

    public static Long safeToLong(Object obj1) {
        return safeToLong(obj1, 0L);
    }

    public static Double safeToDouble(Object obj1, Double defaultValue) {
        if (obj1 == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(obj1.toString());
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static boolean isNullOrEmpty(final Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static void setParams(Query query, Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            Set<Map.Entry<String, Object>> set = params.entrySet();
            Iterator<Map.Entry<String, Object>> iterator = set.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> obj = iterator.next();
                query.setParameter(obj.getKey(), obj.getValue());
            }
        }
    }

    public static int safeToInt(Object obj1, Integer defaultValue) {
        if (obj1 == null) {
            return defaultValue;
        }
        if (obj1 instanceof Float) {
            return ((Float) obj1).intValue();
        }
        try {
            return Integer.parseInt(obj1.toString());
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static int safeToInt(Object obj1) {
        return safeToInt(obj1, 0);
    }

    public static boolean safeToBoolean(Object obj1, Boolean defaultValue) {
        if (obj1 == null) {
            return defaultValue;
        }
        try {
            return obj1.equals("1");
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static boolean safeToBoolean(Object obj1) {
        return safeToBoolean(obj1, true);
    }
}

