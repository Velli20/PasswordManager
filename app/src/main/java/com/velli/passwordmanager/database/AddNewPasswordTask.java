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

import android.content.ContentValues;
import android.os.AsyncTask;

import com.velli.passwordmanager.CreditCardInfo;
import com.velli.passwordmanager.Password;
import com.velli.passwordmanager.collections.NavigationDrawerConstants;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * Created by Hp on 8.12.2015.
 */
public class AddNewPasswordTask extends AsyncTask<Password, Void, Integer> {
    private final boolean mUpdate;
    private SQLiteDatabase mDb;
    private Password entry;

    public AddNewPasswordTask(Password password, SQLiteDatabase db, boolean update){
        mUpdate = update;
        mDb = db;
        entry = password;
    }

    @Override
    protected Integer doInBackground(Password... params) {
        int rowId = -1;

        if(mDb == null || !mDb.isOpen()) {
            return Constants.RESULT_ERROR_DATABASE_NOT_OPEN;
        }
        if(entry != null){
            ContentValues values = new ContentValues();

            values.put(Constants.KEY_DESCRIPTION, entry.getDescription());
            values.put(Constants.KEY_STARRED, entry.isStarred() ? 1 : 0);
            values.put(Constants.KEY_GROUP, entry.getGroup());
            values.put(Constants.KEY_ICON, entry.getLoginIcon());
            values.put(Constants.KEY_LOGIN_TYPE, entry.getLoginType());

            if(entry.isCreditCard()){
                final CreditCardInfo card = entry.getCreditCard();
                values.put(Constants.KEY_CARD_NUMBER, card.getCardNumber());
                values.put(Constants.KEY_CARD_EXPIRATION_DATE, card.getCardExpirationDate());
                values.put(Constants.KEY_CARD_CSV, card.getCardCSV());
                values.put(Constants.KEY_ENTRY_TYPE, Constants.ENTRY_TYPE_CREDIT_CARD);
                values.put(Constants.KEY_CARD_TYPE, card.getCardType());
            } else {
                if(entry.getLoginIcon() == NavigationDrawerConstants.LABEL_WIFI) {
                    values.put(Constants.KEY_WIFI_SSID, entry.getNetworkSSID());
                    values.put(Constants.KEY_WIFI_SECURITY, entry.getWifiSecurity());
                    values.put(Constants.KEY_ENTRY_TYPE, Constants.ENTRY_TYPE_WIFI_PASSWORD);
                } else {
                    values.put(Constants.KEY_URL, entry.getUrl());
                    values.put(Constants.KEY_USERNAME, entry.getUsername());
                    values.put(Constants.KEY_ENTRY_TYPE, Constants.ENTRY_TYPE_PASSWORD);
                }
                values.put(Constants.KEY_PASSWORD, entry.getPassword());
                values.put(Constants.KEY_NOTES, entry.getNote());
                values.put(Constants.KEY_APP_PACKAGE_NAME, entry.getAppPackageName());

            }

            if(mUpdate){
                rowId = mDb.update(Constants.TABLE_ENTRIES, values, Constants.KEY_ID + "=?", new String[] {String.valueOf(entry.getRowId())});
            } else {
                if (entry.getRowId() != -1) {
                    values.put(Constants.KEY_ID, entry.getRowId());
                }

                rowId = (int) mDb.insert(Constants.TABLE_ENTRIES, null, values);
            }
        }

        return rowId == -1 ? Constants.RESULT_ERROR_PASSWORD_NOT_ADDED : Constants.RESULT_PASSWORD_ADDED;
    }

    @Override
    protected void onPostExecute(Integer result){
        PasswordDatabaseHandler.getInstance().notifyCallbacks(Constants.TABLE_ENTRIES, result);
        mDb = null;
    }
}
