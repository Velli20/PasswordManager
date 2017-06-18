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

import net.sqlcipher.database.SQLiteDatabase;

import static com.velli.passwordmanager.database.Constants.KEY_ID;

/**
 * Created by Hp on 8.12.2015.
 */
public class DeleteTask extends AsyncTask<Void, Void, Integer> {
    private final int mRowId;
    private final String mTable;
    private final Integer mRowIds[];
    private SQLiteDatabase mDb;

    public DeleteTask(SQLiteDatabase db, int rowId, String table) {
        mRowId = rowId;
        mTable = table;
        mRowIds = null;
        mDb = db;
    }


    public DeleteTask(SQLiteDatabase db, Integer rowids[], String table) {
        mRowIds = rowids;
        mTable = table;
        mRowId = -1;
        mDb = db;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        if (mDb == null || !mDb.isOpen()) {
            return Constants.RESULT_ERROR_DATABASE_NOT_OPEN;
        }
        if (mRowIds != null) {
            int length = mRowIds.length;

            for (int i = 0; i < length; i++) {
                mDb.delete(mTable, KEY_ID + "=?", new String[]{String.valueOf(mRowIds[i])});
            }
            return Constants.RESULT_DELETED;
        } else if (mDb != null) {
            mDb.delete(mTable, KEY_ID + "=?", new String[]{String.valueOf(mRowId)});
            return Constants.RESULT_DELETED;
        }
        return Constants.RESULT_ERROR_DATABASE_NOT_OPEN;

    }

    @Override
    protected void onPostExecute(Integer result) {
        PasswordDatabaseHandler.getInstance().notifyCallbacks(mTable, result);
        mDb = null;
    }


}