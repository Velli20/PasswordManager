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

import java.util.ArrayList;

/**
 * Created by Hp on 8.12.2015.
 */
public class GetGroupsTask extends AsyncTask<Void, Void, ArrayList<String>> {
    private SQLiteDatabase mDb;
    private OnGetGroupsListener mListener;

    public GetGroupsTask(SQLiteDatabase db, OnGetGroupsListener listener){
        mDb = db;
        mListener = listener;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        if(mDb == null || !mDb.isOpen()){
            return null;
        }
        final Cursor cursor = mDb.rawQuery(Constants.SELECT_QUERY_GROUPS, null);
        final ArrayList<String> list = new ArrayList<>();

        if(cursor != null && cursor.moveToFirst()){
            do{
                list.add(cursor.getString(0));
            } while(cursor.moveToNext());
        }
        if(cursor != null){
            cursor.close();
        }
        return list;
    }

    @Override
    protected void onPostExecute(ArrayList<String> list){
        if (mListener != null) {
            mListener.onGetValues(list);
            mListener = null;
        }
        mDb = null;
    }

}
