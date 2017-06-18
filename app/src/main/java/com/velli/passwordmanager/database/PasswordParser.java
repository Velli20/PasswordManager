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

import android.database.Cursor;

import com.velli.passwordmanager.CreditCardInfo;
import com.velli.passwordmanager.Password;

/**
 * Created by Hp on 8.12.2015.
 */
public class PasswordParser {

    public static Password parsePassword(Cursor cursor) {
        final Password entry = new Password();

        if (cursor.getInt(cursor.getColumnIndex(Constants.KEY_ENTRY_TYPE)) == Constants.ENTRY_TYPE_CREDIT_CARD) {
            final CreditCardInfo card = parseCreditCard(cursor);

            entry.setDescription(cursor.getString(cursor.getColumnIndex(Constants.KEY_DESCRIPTION)));
            entry.setCreditCard(card);

        } else {
            entry.setInfo(cursor.getString(cursor.getColumnIndex(Constants.KEY_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(Constants.KEY_URL)),
                    cursor.getString(cursor.getColumnIndex(Constants.KEY_USERNAME)),
                    cursor.getString(cursor.getColumnIndex(Constants.KEY_PASSWORD)),
                    cursor.getString(cursor.getColumnIndex(Constants.KEY_NOTES)));
            entry.setAppPackageName(cursor.getString(cursor.getColumnIndex(Constants.KEY_APP_PACKAGE_NAME)));

            if (cursor.getInt(cursor.getColumnIndex(Constants.KEY_ENTRY_TYPE)) == Constants.ENTRY_TYPE_WIFI_PASSWORD) {
                entry.setWifiSecurity(cursor.getString(cursor.getColumnIndex(Constants.KEY_WIFI_SECURITY)));
                entry.setNetworkSSID(cursor.getString(cursor.getColumnIndex(Constants.KEY_WIFI_SSID)));
                entry.setIsWifiPassword(true);
            }
        }

        entry.setLoginType(cursor.getString(cursor.getColumnIndex(Constants.KEY_LOGIN_TYPE)), cursor.getInt(cursor.getColumnIndex(Constants.KEY_ICON)));
        entry.setStarred(cursor.getInt(cursor.getColumnIndex(Constants.KEY_STARRED)) == 1);
        entry.setRowId((int) cursor.getLong(cursor.getColumnIndex(Constants.KEY_ID)));
        entry.setGroup(cursor.getString(cursor.getColumnIndex(Constants.KEY_GROUP)));

        return entry;
    }

    public static CreditCardInfo parseCreditCard(Cursor cursor) {
        final CreditCardInfo card = new CreditCardInfo();
        card.setCardType(cursor.getString(cursor.getColumnIndex(Constants.KEY_CARD_TYPE)));
        card.setCardNumber(cursor.getString(cursor.getColumnIndex(Constants.KEY_CARD_NUMBER)));
        card.setCardExpirationDate(cursor.getString(cursor.getColumnIndex(Constants.KEY_CARD_EXPIRATION_DATE)));
        card.setCardCSV(cursor.getString(cursor.getColumnIndex(Constants.KEY_CARD_CSV)));

        return card;
    }
}
