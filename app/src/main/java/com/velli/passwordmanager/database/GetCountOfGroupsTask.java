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

import com.velli.passwordmanager.collections.ListItemGroup;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;


/**
 * Created by Hp on 8.12.2015.
 */

/**
 * Returns list of password titles which is by groups
 */

public class GetCountOfGroupsTask extends AsyncTask<Void, Void, ArrayList<ListItemGroup>> {
    private SQLiteDatabase mDb;

    private OnGetGroupsListTaskListener mListener;

    public GetCountOfGroupsTask(SQLiteDatabase db, OnGetGroupsListTaskListener listener) {
        mListener = listener;
        mDb = db;
    }

    @Override
    protected ArrayList<ListItemGroup> doInBackground(Void... params) {
        if (mDb == null || !mDb.isOpen()) {
            return null;
        }

        final Cursor cursorGroups = mDb.rawQuery(Constants.SELECT_QUERY_GROUPS, null);
        final ArrayList<String> list = new ArrayList<>();
        final ArrayList<ListItemGroup> groupsList = new ArrayList<>();

        ListItemGroup group;


        if (cursorGroups != null && cursorGroups.moveToFirst()) {
            do {
                list.add(cursorGroups.getString(0));

                final String s = cursorGroups.getString(0);
                final String query = "SELECT " + Constants.columnsSelection + " FROM " + Constants.TABLE_ENTRIES + " WHERE(" + Constants.KEY_GROUP + " = '" + s + "')";
                final Cursor cursor = mDb.rawQuery(query, null);

                if (cursor != null) {
                    group = new ListItemGroup();
                    group.primary = s;
                    group.secondary = String.valueOf(cursor.getCount());
                    group.rowId = cursorGroups.getInt(1);

                    if (cursor.moveToFirst()) {
                        do {
                            group.addPassword(cursor.getString(0), cursor.getInt(16));
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    groupsList.add(group);
                }
            } while (cursorGroups.moveToNext());
        }
        if (cursorGroups != null) {
            cursorGroups.close();
        }


        return groupsList;
    }

    @Override
    protected void onPostExecute(ArrayList<ListItemGroup> list) {
        if (mListener != null) {
            mListener.onGetValuesCount(list);
            mListener = null;
        }
        mDb = null;
    }
}
