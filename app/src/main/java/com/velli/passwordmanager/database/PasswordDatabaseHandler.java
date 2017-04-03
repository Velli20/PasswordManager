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

import java.util.ArrayList;

import com.velli.passwordmanager.ApplicationBase;
import com.velli.passwordmanager.Password;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;
import net.sqlcipher.database.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import static com.velli.passwordmanager.database.Constants.*;

public class PasswordDatabaseHandler extends SQLiteOpenHelper {
	private static final String TAG = "DatabaseHandler ";
    private static final boolean DEBUG = false;

	private static PasswordDatabaseHandler sInstance;
    private ArrayList<OnDatabaseEditedListener> mEditedCallbacks = new ArrayList<>();

	private SQLiteDatabase mDb;

    private PasswordDatabaseHandler(Context context) {
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    	final LoadLibraries libsTask = new LoadLibraries(context);
    	libsTask.execute();
	}

    public static PasswordDatabaseHandler getInstance(){
        if(sInstance == null) sInstance = getSync();
        return sInstance;
    }

    private static synchronized PasswordDatabaseHandler getSync() {
        if(sInstance == null) sInstance = new PasswordDatabaseHandler(ApplicationBase.getAppContext());
        return sInstance;
    }

    public static String getDatabaseName(){
    	return DATABASE_NAME;
    }


	@Override
	public void onCreate(SQLiteDatabase db) {

		String TABLE = "CREATE TABLE " + TABLE_ENTRIES + "(" + KEY_DESCRIPTION + " TEXT, " +
				KEY_URL + " TEXT, " + KEY_USERNAME + " TEXT, " + KEY_PASSWORD + " TEXT, " +
				KEY_NOTES + " TEXT, " + KEY_ICON + " INTEGER, " + KEY_LOGIN_TYPE + " TEXT, " + KEY_STARRED +
				" INTEGER, " + KEY_GROUP + " TEXT," + KEY_APP_PACKAGE_NAME + " TEXT, " + KEY_CARD_TYPE + " TEXT, "
				+ KEY_CARD_NUMBER + " TEXT, " + KEY_CARD_EXPIRATION_DATE + " TEXT, " + KEY_CARD_CSV + " TEXT, " 
				+ KEY_CARD_NAME + " TEXT, " + KEY_ENTRY_TYPE + " INTEGER, " 
				+ KEY_WIFI_SSID + " TEXT, " + KEY_WIFI_SECURITY + " TEXT)";
		
		String INFO = "CREATE TABLE " + TABLE_USAGE_HISTORY + "(" + KEY_DATE_LOGIN + " TEXT)";
		String GROUPS= "CREATE TABLE " + TABLE_GROUPS + "(" + KEY_VALUE + " TEXT)";
		
		
		db.execSQL(TABLE);
		db.execSQL(INFO);
		db.execSQL(GROUPS);
	}

 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		int upgradeTo = oldVersion + 1;
		
        while (upgradeTo <= newVersion)
        {
            switch (upgradeTo)
            {
                case 2:
                    db.execSQL("ALTER TABLE " + TABLE_ENTRIES + " ADD COLUMN " + KEY_WIFI_SECURITY + " TEXT");
                    db.execSQL("ALTER TABLE " + TABLE_ENTRIES + " ADD COLUMN " + KEY_WIFI_SSID + " TEXT");
                    break;
                
            }
            upgradeTo++;
        }
        
       
	}
	
	public void openDatabase(char[] password, OnDatabaseUnlockedListener listener){
		if(DEBUG){
			Log.i(TAG, TAG + "openDatabase()");
		}
	    UnlockDatabase unlockTask = new UnlockDatabase(listener);
	    unlockTask.execute(String.valueOf(password));	    
	}
	

		
	public boolean isDatabaseOpen(){
		return mDb != null && mDb.isOpen();
	}
	
	public void closeDatabase(){
		if(isDatabaseOpen()){
			mDb.close();
		}
	}
	
	public void registerOnDatabaseEditedListener(OnDatabaseEditedListener callback){
		if(callback != null && !mEditedCallbacks.contains(callback)){
			mEditedCallbacks.add(callback);
		}
	}
	
	public void unregisterOnDatabaseEditedListener(OnDatabaseEditedListener callback){
		if(callback != null){
			mEditedCallbacks.remove(callback);
		}
	}
	
	public void notifyCallbacks(String table, long rowId){
		for(OnDatabaseEditedListener callback : mEditedCallbacks){
			if(callback != null){
				callback.onDatabaseHasBeenEdited(table, rowId);
			}
		}
	}

	public void addNewGroup(String newGroup){
		new AddNewGroupTask(mDb, newGroup).execute();
	}

	public void addNewPassword(Password entry){
		new AddNewPasswordTask(entry, mDb, false).execute(entry);
	}

	public void addNewPasswords(ArrayList<Password> passwords, OnPasswordsAddedListener l) {
		new AddPasswordsTask(passwords, mDb, l).execute();
	}

	public void updatePassword(Password entry){
		new AddNewPasswordTask(entry, mDb, true).execute();
	}

	
	public void getPassword(int id, OnGetPasswordListener callback){
		String query = "SELECT " + columnsSelection + " FROM " + (TABLE_ENTRIES) + " WHERE(" + KEY_ID + " = " 
		+ String.valueOf(id) + ")";
		
		new GetPasswordTask(mDb, callback).execute(query);
	}
	
	public void deleteValue(int rowId, boolean loginType){	
		DeleteTask task = new DeleteTask(mDb, rowId, (loginType ? TABLE_LOGIN_TYPES : TABLE_GROUPS));
		task.execute();
	}

	
	public void deletePassword(int rowId){
		new DeleteTask(mDb, rowId, (TABLE_ENTRIES)).execute();
	}

	
	public void deletePasswords(Integer[] rowids){
		new DeleteTask(mDb, rowids, (TABLE_ENTRIES)).execute();
	}
	
	public void searchFromPasswords(OnGetPasswordsListener callback, String toSearch){
		String query = "SELECT " + columnsSelection + " FROM " + TABLE_ENTRIES + " WHERE( (" + KEY_DESCRIPTION + " LIKE '%" + toSearch + "%') "
				+ " OR " + "(" + KEY_USERNAME + " LIKE '%" + toSearch + "%')" 
				+ " OR " + "(" + KEY_URL + " LIKE '%" + toSearch + "%')" +")";
        new GetPasswordListTask(mDb, callback).execute(query);
	}

    public void getSearchSuggestions(String query, OnGetSearchSuggestionsListener l) {
        new GetSearchSuggestionsTask(query, mDb, l).execute();
    }
	
	public void getAllPasswords(OnGetPasswordsListener callback){
		String query = "SELECT " + columnsSelection + " FROM " + (TABLE_ENTRIES) + " ORDER BY " + KEY_DESCRIPTION + " Collate NOCASE";
        new GetPasswordListTask(mDb, callback).execute(query);
	}
	
	public void getAllStarredPasswords(OnGetPasswordsListener callback){
		String query = "SELECT " + columnsSelection + " FROM " + TABLE_ENTRIES + " WHERE(" + KEY_STARRED + " = 1 )" + " ORDER BY " + KEY_DESCRIPTION + " Collate NOCASE";
        new GetPasswordListTask(mDb, callback).execute(query);
	}
	
	public void getAllPasswordsByGroup(OnGetPasswordsListener callback, String group){
		String query = "SELECT " + columnsSelection + " FROM " + TABLE_ENTRIES + " WHERE(" + KEY_GROUP + " = '" + group + "') ORDER BY " + KEY_DESCRIPTION + " Collate NOCASE";
        new GetPasswordListTask(mDb, callback).execute(query);
	}
	
	public void getAllPasswordsByLabel(OnGetPasswordsListener callback, int iconPos, String label){
		final String query;
		
		if(iconPos != -1){
			query = "SELECT " + columnsSelection + " FROM " + TABLE_ENTRIES + " WHERE(" + KEY_ICON + " = '" + String.valueOf(iconPos) + "') ORDER BY " + KEY_DESCRIPTION + " Collate NOCASE";
		} else {
			query = "SELECT " + columnsSelection + " FROM " + TABLE_ENTRIES + " WHERE(" + KEY_GROUP + " = '" + (label == null? "" : label) + "') ORDER BY " + KEY_DESCRIPTION + " Collate NOCASE";
		} 
		new GetPasswordListTask(mDb, callback).execute(query);
	}

	
	public void changePassword(char[] newkey, OnDatabaseUnlockedListener listener){
		if(mDb != null && isDatabaseOpen()){
			mDb.execSQL("PRAGMA rekey = '" + String.valueOf(newkey) + "'");
			System.gc();
		}
		UnlockDatabase unlockTask = new UnlockDatabase(listener);
	    unlockTask.execute(String.valueOf(newkey));	
	}
	
	public void getLastLoginDate(OnGetLastLoginDateListener listener){
		new GetLastLoginDateTask(mDb, listener).execute();
	}
	
	public void getGroups(OnGetGroupsListener listener){
		new GetGroupsTask(mDb, listener).execute();
	}
	
	public void getGroupsList(OnGetGroupsListTaskListener listener){
		 new GetCountOfGroupsTask(mDb, listener).execute();
	}
	
	public int checkIfValueExists(boolean groups, String value){
		final String query = "SELECT * FROM " + (groups ? TABLE_GROUPS : TABLE_LOGIN_TYPES) + " WHERE(" + KEY_VALUE + " LIKE '" + value + "%')";

		if(mDb != null){
			final Cursor c;
			try {
				c = mDb.rawQuery(query, null);
			} catch (SQLiteException e){
				return RESULT_ERROR_INVALID_CHARACTER;
			}
			if(c.moveToFirst()){
				c.close();
				return RESULT_ERROR_GROUP_ALREADY_EXISTS;
			} else {
				c.close();
			}
		}
		return RESULT_OK;
	}

	public static int checkIfGroupExists(String groupName, SQLiteDatabase db){
		final String query = "SELECT * FROM " + (Constants.TABLE_GROUPS) + " WHERE(" + Constants.KEY_VALUE + " LIKE '" + groupName + "%')";

		if(db != null){
			final net.sqlcipher.Cursor c;
			try {
				c = db.rawQuery(query, null);
			} catch (SQLiteException e){
				return Constants.RESULT_ERROR_INVALID_CHARACTER;
			}
			if(c.moveToFirst()){
				c.close();
				return Constants.RESULT_ERROR_GROUP_ALREADY_EXISTS;
			} else {
				c.close();
			}
		}
		return Constants.RESULT_OK;
	}
	
	private class LoadLibraries extends AsyncTask<Void, Void, Void> {
		private Context mContext;
		
		public LoadLibraries(Context c){
			mContext = c;
		}
		@Override
		protected Void doInBackground(Void... params) {
			SQLiteDatabase.loadLibs(mContext);
			return null;
		}
		
	}

	

	
	private class UnlockDatabase extends AsyncTask<String, Void, Boolean> {
		private OnDatabaseUnlockedListener mListener;
		
		public UnlockDatabase(OnDatabaseUnlockedListener listener){
			mListener = listener;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			closeDatabase();
			
			try {
				mDb = getWritableDatabase(params[0].toCharArray());
			} catch (SQLiteException e) {
				return false;
			} catch (Exception i){
				return false;
			}
			
			if(DEBUG){
				Log.i(TAG, TAG + "UnlockDatabase doInBackground() database version: " + mDb.getVersion());
			}
			if(mDb.getVersion() != DATABASE_VERSION) {
				onUpgrade(mDb, mDb.getVersion(), DATABASE_VERSION);
			}
				
			if (mDb != null && mDb.isOpen()) {
				ContentValues values = new ContentValues();
				values.put(KEY_DATE_LOGIN, String.valueOf(System.currentTimeMillis()));
				mDb.insert(TABLE_USAGE_HISTORY, null, values);
			}

			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result){
			if(mListener != null){
				mListener.onDatabaseUnlocked(result);
				mListener = null;
			}
		}
	}


}
