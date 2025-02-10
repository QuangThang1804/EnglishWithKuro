package com.hus.englishapp.kuro.util;

public interface Constants {

    public static final String X_TOTAL_COUNT = "X-Total-Count";

    interface RESPONSE_CODE {
        Integer SUCCESS = 0;
        Integer FAILURE = 1;
        Integer EXISTS = 2;
        Integer POPUP_ERROR = 3;
    }
    interface DIM_AREA {
        String PROVINCE = "P";
        String DISTRICT = "D";
        String VILLAGE = "V";
    }
}
