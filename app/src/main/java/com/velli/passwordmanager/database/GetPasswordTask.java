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

import android.os.AsyncTask;

import com.velli.passwordmanager.Password;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

/**
 * Created by Hp on 8.12.2015.
 */
public class GetPasswordTask extends AsyncTask<String, Void, Password> {
    private SQLiteDatabase mDb;
    private OnGetPasswordListener mCallback;

    public GetPasswordTask(SQLiteDatabase db, OnGetPasswordListener callback) {
        mDb = db;
        mCallback = callback;
    }

    @Override
    protected Password doInBackground(String... params) {
        if (mDb == null || !mDb.isOpen()) {
            return null;
        }
        String selectQuery = params[0];
        Cursor cursor = mDb.rawQuery(selectQuery, null);
        Password password = null;

        if (cursor != null && cursor.moveToFirst()) {
            password = PasswordParser.parsePassword(cursor);

            cursor.close();
        } else if (cursor != null) {
            cursor.close();
        }

        return password;
    }

    @Override
    protected void onPostExecute(Password entry) {
        if (mCallback != null) {
            mCallback.onGetPassword(entry);
            mCallback = null;
        }
        mDb = null;
    }
}
