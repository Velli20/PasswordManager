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
import android.os.AsyncTask;

import net.sqlcipher.database.SQLiteDatabase;

import static com.velli.passwordmanager.database.Constants.SELECT_QUERY_DATE_LOGIN;

/**
 * Created by Hp on 8.12.2015.
 */
public class GetLastLoginDateTask extends AsyncTask<Void, Void, String> {
    private OnGetLastLoginDateListener mListenerLoginDate;
    private SQLiteDatabase mDb;

    public GetLastLoginDateTask(SQLiteDatabase db, OnGetLastLoginDateListener listener) {
        mListenerLoginDate = listener;
        mDb = db;
    }

    @Override
    protected String doInBackground(Void... params) {
        if (mDb == null || !mDb.isOpen()) {
            return "ERROR database not open!";
        }
        Cursor cursor = mDb.rawQuery(SELECT_QUERY_DATE_LOGIN, null);
        String s = "-";

        if (cursor != null && cursor.getCount() > 1) {
            cursor.moveToPosition(1);
            s = cursor.getString(0);
            cursor.close();
        } else if (cursor != null && cursor.getCount() == 1) {
            cursor.moveToFirst();
            s = cursor.getString(0);
            cursor.close();
        } else if (cursor != null) {
            cursor.close();
        }

        return s;
    }

    @Override
    protected void onPostExecute(String date) {
        if (mListenerLoginDate != null) {
            try {
                mListenerLoginDate.onLastLoginDate(Long.valueOf(date));
            } catch (NumberFormatException ignored) {
            }
        }

        mListenerLoginDate = null;
        mDb = null;
    }
}
