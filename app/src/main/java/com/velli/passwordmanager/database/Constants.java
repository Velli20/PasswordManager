/*
 *
 *  * MIT License
 *  *
 *  * Copyright (c) [2017] [velli20]
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package com.velli.passwordmanager.database;

/**
 * Created by Hp on 6.12.2015.
 */
public class Constants {
    public static final int DATABASE_VERSION = 2;

    public static final int RESULT_DELETED = 0;
    public static final int RESULT_ERROR_PASSWORD_NOT_ADDED = 1;
    public static final int RESULT_PASSWORD_ADDED = 2;
    public static final int RESULT_OK = 3;
    public static final int RESULT_ERROR_DATABASE_NOT_OPEN = 4;
    public static final int RESULT_GROUP_ADDED= 5;
    public static final int RESULT_ERROR_GROUP_ALREADY_EXISTS = 6;
    public static final int RESULT_ERROR_GROUP_NOT_ADDED = 7;
    public static final int RESULT_ERROR_INVALID_CHARACTER = 8;



    public static final int ENTRY_TYPE_PASSWORD = 0;
    public static final int ENTRY_TYPE_CREDIT_CARD = 1;
    public static final int ENTRY_TYPE_WIFI_PASSWORD = 2;

    public static final String DATABASE_NAME = "manager.PassCrypt.db";
    public static final String DATABASE_FILE_EXTENSION = ".PassCrypt.db";

    public static final String TABLE_ENTRIES = "entries";
    public static final String TABLE_USAGE_HISTORY = "info";
    public static final String TABLE_GROUPS = "groups";
    public static final String TABLE_LOGIN_TYPES= "logintypes";

    public static final String KEY_ID = "rowid";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_URL = "url";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_NOTES = "notes";
    public static final String KEY_ICON = "icon";
    public static final String KEY_LOGIN_TYPE = "logintype";
    public static final String KEY_STARRED = "starred";
    public static final String KEY_GROUP = "type";
    public static final String KEY_VALUE = "value";
    public static final String KEY_APP_PACKAGE_NAME = "apppackagename";

    public static final String KEY_CARD_TYPE = "cardtype";
    public static final String KEY_CARD_NUMBER = "cardnumber";
    public static final String KEY_CARD_EXPIRATION_DATE = "cardexpirationdate";
    public static final String KEY_CARD_CSV = "cardcsv";
    public static final String KEY_CARD_NAME = "cardname";

    public static final String KEY_WIFI_SSID = "wifissid";
    public static final String KEY_WIFI_SECURITY = "wifisecurity";

    public static final String KEY_ENTRY_TYPE = "entrytype";
    public static final String KEY_DATE_LOGIN = "logindate";


    public static final String SELECT_QUERY_DATE_LOGIN = "SELECT " + KEY_DATE_LOGIN + " FROM " + TABLE_USAGE_HISTORY
            + " ORDER BY " + KEY_DATE_LOGIN + " DESC LIMIT 3";
    public static final String SELECT_QUERY_GROUPS = "SELECT " + KEY_VALUE + ", " + KEY_ID + " FROM " + TABLE_GROUPS + " ORDER BY " + KEY_VALUE + " ASC";
    public static final String SELECT_QUERY_LOGIN_TYPES = "SELECT " + KEY_VALUE + ", " + KEY_ID + " FROM " + TABLE_LOGIN_TYPES + " ORDER BY " + KEY_VALUE + " ASC";

    public static final String columnsSelection = KEY_DESCRIPTION + ", "
            + KEY_URL + ", "
            + KEY_USERNAME + ", "
            + KEY_PASSWORD + ", "
            + KEY_NOTES + ", "
            + KEY_ICON + ", "
            + KEY_LOGIN_TYPE + ", "
            + KEY_STARRED + ", "
            + KEY_GROUP + ", "
            + KEY_APP_PACKAGE_NAME + ", "
            + KEY_CARD_TYPE + ", "
            + KEY_CARD_NUMBER + ", "
            + KEY_CARD_EXPIRATION_DATE + ", "
            + KEY_CARD_CSV + ", "
            + KEY_CARD_NAME + ", "
            + KEY_ENTRY_TYPE + ", "
            + KEY_WIFI_SSID + ", "
            + KEY_WIFI_SECURITY + ", "
            + KEY_ID;
}
