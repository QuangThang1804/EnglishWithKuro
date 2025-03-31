package com.hus.englishapp.kuro.util;

public class DataUtil {
    public static final char DEFAULT_ESCAPE_CHAR_QUERY = '\\';

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.trim().isEmpty() || "".equalsIgnoreCase(string);
    }
    
    public static String makeLikeQuery(String s) {
        if (isNullOrEmpty(s)) {
            return null;
        }

        // Escape các ký tự đặc biệt cho Oracle
        s = s.trim()
                .replace("\\", DEFAULT_ESCAPE_CHAR_QUERY + "\\") // Escape backslash
                .replace("%", DEFAULT_ESCAPE_CHAR_QUERY + "%")   // Escape %
                .replace("_", DEFAULT_ESCAPE_CHAR_QUERY + "_");  // Escape _

        // Thêm dấu % vào đầu và cuối chuỗi
        return "%" + s + "%";
    }
}
