package com.vgs.android.cardformdemo;


import android.provider.BaseColumns;

// Define a contract class to define our names for DB URIs/tables/columns.
public final class CardStorageContract {

    private CardStorageContract() {
    }

    public static class CardEntry implements BaseColumns {
        public static final String TABLE_NAME = "cards";
        public static final String CARDS_CARDIDENTIFIER = "CARDS_CARDIDENTIFIER";

        // 'tokenized' card details
        public static final String CARDS_CARDTYPE = "CARDS_CARDTYPE";
        public static final String CARDS_CCN = "CARDS_CCN";
        public static final String CARDS_CVV = "CARDS_CVV";
        public static final String CARDS_MONTH = "CARDS_MONTH";
        public static final String CARDS_YEAR = "CARDS_YEAR";

        // 'tokenized' personal details
        public static final String CARDS_POST_CODE = "CARDS_POST_CODE";
        public static final String CARDS_COUNTRYCODE = "CARDS_COUNTRYCODE";
        public static final String CARDS_MOBILE = "CARDS_MOBILE";
    }
}
