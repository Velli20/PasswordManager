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

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

import java.util.ArrayList;

/**
 * Created by Hp on 9.12.2015.
 */
public class GetSearchSuggestionsTask extends AsyncTask<Void, Void, String[]> {
    private SQLiteDatabase mDb;
    private OnGetSearchSuggestionsListener mListener;
    private String mQuery;
    private String[] mKeysToMatch;

    public GetSearchSuggestionsTask(String query, SQLiteDatabase db, OnGetSearchSuggestionsListener l) {
        mQuery = query;
        mDb = db;
        mListener = l;
    }

    public GetSearchSuggestionsTask matchFromKeys(String[] keys) {
        mKeysToMatch = keys;
        return this;
    }

    @Override
    protected String[] doInBackground(Void... params) {
        if (mDb != null && mDb.isOpen() && mQuery != null) {
            final StringBuilder columnsSelection = new StringBuilder();
            final StringBuilder query = new StringBuilder();
            final StringBuilder keys = new StringBuilder();

            final ArrayList<String> results = new ArrayList<>();
            final int keyCount = mKeysToMatch != null ? mKeysToMatch.length : 1;
            int maxSearchResultCount = 10;


            if (mKeysToMatch != null) {
                String comma = "";
                for (String key : mKeysToMatch) {
                    columnsSelection.append(comma);
                    comma = ", ";
                    columnsSelection.append(key);
                    keys.append("(" + key + " Like '" + mQuery + "%')");
                }
            } else {
                columnsSelection.append(Constants.KEY_DESCRIPTION);
                keys.append(Constants.KEY_DESCRIPTION + " Like '" + mQuery + "%'");
            }

            query.append("SELECT " + columnsSelection.toString())
                    .append(" FROM " + Constants.TABLE_ENTRIES)
                    .append(" WHERE(")
                    .append(keys.toString())
                    .append(")");

            final Cursor cursor;

            try {
                cursor = mDb.rawQuery(query.toString(), null);
            } catch (SQLiteException ignored) {
                return new String[0];
            }


            String s;
            while (cursor.moveToNext() && maxSearchResultCount > 0) {
                for (int i = 0; i < keyCount; i++) {
                    s = cursor.getString(i);
                    if (!results.contains(s)) {
                        results.add(cursor.getString(i));
                    }
                }
                maxSearchResultCount--;
            }

            cursor.close();
            return results.toArray(new String[results.size()]);
        }
        return new String[0];
    }

    @Override
    protected void onPostExecute(String[] result) {
        if (mListener != null) {
            mListener.onGetSearchSuggestions(result);
            mListener = null;
        }
        mDb = null;
    }
}
