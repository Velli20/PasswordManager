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

package com.velli.passwordmanager;

import android.os.AsyncTask;

import com.velli.passwordmanager.database.OnGetPasswordsListener;

import java.io.File;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;

/**
 * Created by Hp on 6.1.2016.
 */
public class ExcelImportPasswordsTask {

    public ExcelImportPasswordsTask(File inputFile, OnGetPasswordsListener l) {
        new ReaderTask(inputFile, l).execute();
    }

    private class ReaderTask extends AsyncTask<Void, Void, ArrayList<Password>> {
        private File mFile;
        private String mLabels[];
        private OnGetPasswordsListener mListener;

        public ReaderTask(File file, OnGetPasswordsListener l) {
            mFile = file;
            mListener = l;
            int labels[] = com.velli.passwordmanager.collections.Utils.getLoginLabelArray();
            int size = labels.length;
            mLabels = new String[size];

            for(int i = 0; i < size; i++) {
                mLabels[i] = ApplicationBase.getAppContext().getString(labels[i]);
            }
        }

        @Override
        protected ArrayList<Password> doInBackground(Void... params) {
            Workbook w;
            ArrayList<Password> passwords = new ArrayList<>();

            try {
                w = Workbook.getWorkbook(mFile);
                // Get the first sheet
                Sheet sheet = w.getSheet(0);

                Password password;
                for(int j = 0; j < sheet.getRows(); j++) {
                    password = new Password();

                    String label = sheet.getCell(1, j).getContents();
                    password.setDescription(sheet.getCell(0, j).getContents());
                    password.setLoginType(label, getIconPosition(label));
                    password.setUrl(sheet.getCell(2, j).getContents());
                    password.setUsername(sheet.getCell(3, j).getContents());
                    password.setPassword(sheet.getCell(4, j).getContents());
                    password.setNote(sheet.getCell(5, j).getContents());
                    password.setGroup(sheet.getCell(6, j).getContents());
                    passwords.add(password);
                }
            } catch(Exception ignore) {}
            return passwords;
        }

        private int getIconPosition(String label) {
            if(label == null) {
                return -1;
            }
            int lenght = mLabels.length;
            for (int i = 0; i < lenght; i++) {
                if(label.equals(mLabels[i])) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        protected void onPostExecute(ArrayList<Password> list) {
            if(mListener != null) {
                mListener.onGetPasswords(list);
                mListener = null;
            }
        }
    }
}
