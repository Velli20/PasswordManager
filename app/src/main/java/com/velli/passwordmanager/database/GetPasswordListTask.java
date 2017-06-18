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
import net.sqlcipher.database.SQLiteException;

import java.util.ArrayList;

/**
 * Created by Hp on 8.12.2015.
 */
public class GetPasswordListTask extends AsyncTask<String, Void, ArrayList<Password>> {
    private SQLiteDatabase mDb;
    private OnGetPasswordsListener mCallback;

    public GetPasswordListTask(SQLiteDatabase db, OnGetPasswordsListener callback) {
        mDb = db;
        mCallback = callback;
    }

    @Override
    protected ArrayList<Password> doInBackground(String... params) {
        if (mDb == null || !mDb.isOpen()) {
            return null;
        }
        final ArrayList<Password> list = new ArrayList<>();
        final String selectQuery = params[0];

        final Cursor cursor;

        try {
            cursor = mDb.rawQuery(selectQuery, null);
        } catch (SQLiteException e) {
            return list;
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(PasswordParser.parsePassword(cursor));
            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    @Override
    protected void onPostExecute(ArrayList<Password> list) {
        if (mCallback != null) {
            mCallback.onGetPasswords(list);
            mCallback = null;
        }
        mDb = null;
    }
}
