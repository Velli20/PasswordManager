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

import net.sqlcipher.database.SQLiteDatabase;


/**
 * Created by Hp on 8.12.2015.
 */
public class AddNewGroupTask extends AsyncTask<String, Void, Integer> {
    private final String mGroup;
    private SQLiteDatabase mDb;

    public AddNewGroupTask(SQLiteDatabase db, String group) {
        mDb = db;
        mGroup = group;
    }

    @Override
    protected Integer doInBackground(String... params) {
        if (mDb == null || !mDb.isOpen()) {
            return Constants.RESULT_ERROR_DATABASE_NOT_OPEN;
        } else if (mGroup == null) {
            return Constants.RESULT_ERROR_INVALID_CHARACTER;
        }

        // Check if there is already group with given name
        int result = PasswordDatabaseHandler.checkIfGroupExists(mGroup, mDb);

        if (result == Constants.RESULT_OK) {
            ContentValues values = new ContentValues();
            values.put(Constants.KEY_VALUE, mGroup);


            return mDb.insert(Constants.TABLE_GROUPS, null, values) != -1 ?
                    Constants.RESULT_GROUP_ADDED : Constants.RESULT_ERROR_GROUP_NOT_ADDED;

        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer rowid) {
        PasswordDatabaseHandler.getInstance().notifyCallbacks(Constants.TABLE_GROUPS, rowid);
    }


}
