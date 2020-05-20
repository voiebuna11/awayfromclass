package com.example.afc.app;

public class Config {
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String CHAT_MESSAGE = "chatMessage";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    // link to server files
    public static final String KEY_LINK = "http://bc3d487d.ngrok.io/";

    // Sharedpref file name
    public static final String PREF_NAME = "AFCPref";

    // All Shared Preferences Keys
    public static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_USER = "us_user";
    public static final String KEY_EMAIL = "us_email";
    public static final String KEY_ID = "us_id";

    public static final String KEY_TYPE = "us_type";
    public static final String KEY_FNAME = "us_first_name";
    public static final String KEY_LNAME = "us_last_name";
    public static final String KEY_PHONE = "us_phone";
    public static final String KEY_CITY = "us_city";
    public static final String KEY_YEAR = "us_year";
    public static final String KEY_SPEC = "us_spec";
    public static final String KEY_PIC = "us_pic";
    public static final String KEY_CHAT_ID = "us_chat_id";

    public static final String DB_NAME = "afc_escanor.db";
    public static final String DB_CHAT_TABLE = "chat_messages";
    public static final String DB_SEARCH_HISTORY_TABLE = "search_history";
}
