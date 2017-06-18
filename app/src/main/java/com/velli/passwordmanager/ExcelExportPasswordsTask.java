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

import android.content.Context;
import android.os.AsyncTask;

import com.velli.passwordmanager.database.OnGetPasswordsListener;
import com.velli.passwordmanager.database.PasswordDatabaseHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelExportPasswordsTask implements OnGetPasswordsListener {
    private static final int TYPE_PASSWORD = 0;
    private static final int TYPE_CREDIT_CARD = 1;
    private static final int TYPE_WIFI_PASSWORD = 2;
    private Context mContext;
    private File mOutputFile;
    private OnFileSavedListener mListener;
    private WritableCellFormat timesBoldUnderline;
    private WritableCellFormat times;

    public ExcelExportPasswordsTask(Context context) {
        mContext = context;
    }

    public ExcelExportPasswordsTask setFile(File file) {
        this.mOutputFile = file;
        return this;
    }

    public void writeOut() {
        PasswordDatabaseHandler.getInstance().getAllPasswords(this);
    }

    @Override
    public void onGetPasswords(ArrayList<Password> list) {
        if (list != null) {
            CreateSpreadSheetTask task = new CreateSpreadSheetTask(list);
            task.execute(mOutputFile);
        }

    }

    public void setOnFileSavedListener(OnFileSavedListener l) {
        mListener = l;
    }

    private void createLabel(WritableSheet sheet, int type) throws WriteException {
        // Lets create a times font
        final WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
        // Define the cell format
        times = new WritableCellFormat(times10pt);
        // Lets automatically wrap the cells
        times.setWrap(true);

        // create create a bold font with unterlines
        final WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false, UnderlineStyle.SINGLE);
        timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
        // Lets automatically wrap the cells
        timesBoldUnderline.setWrap(true);

        CellView cv = new CellView();
        cv.setFormat(times);
        cv.setFormat(timesBoldUnderline);

        if (type == TYPE_CREDIT_CARD) {
            addCaption(sheet, 0, 0, mContext.getString(R.string.new_entry_description));
            addCaption(sheet, 1, 0, mContext.getString(R.string.new_credit_card_card_type));
            addCaption(sheet, 2, 0, mContext.getString(R.string.new_credit_card_card_number));
            addCaption(sheet, 3, 0, mContext.getString(R.string.new_credit_card_expiration_date));
            addCaption(sheet, 4, 0, mContext.getString(R.string.new_credit_card_csv));
            addCaption(sheet, 5, 0, mContext.getString(R.string.new_entry_group));
        } else if (type == TYPE_WIFI_PASSWORD) {
            addCaption(sheet, 0, 0, mContext.getString(R.string.new_entry_description));
            addCaption(sheet, 1, 0, mContext.getString(R.string.new_entry_label));
            addCaption(sheet, 2, 0, mContext.getString(R.string.new_entry_wifi_security));
            addCaption(sheet, 3, 0, mContext.getString(R.string.new_entry_wifi_ssid));
            addCaption(sheet, 4, 0, mContext.getString(R.string.new_entry_password));
            addCaption(sheet, 5, 0, mContext.getString(R.string.new_entry_notes));
            addCaption(sheet, 6, 0, mContext.getString(R.string.new_entry_group));
        } else {
            addCaption(sheet, 0, 0, mContext.getString(R.string.new_entry_description));
            addCaption(sheet, 1, 0, mContext.getString(R.string.new_entry_label));
            addCaption(sheet, 2, 0, mContext.getString(R.string.new_entry_url));
            addCaption(sheet, 3, 0, mContext.getString(R.string.new_entry_username));
            addCaption(sheet, 4, 0, mContext.getString(R.string.new_entry_password));
            addCaption(sheet, 5, 0, mContext.getString(R.string.new_entry_notes));
            addCaption(sheet, 6, 0, mContext.getString(R.string.new_entry_group));

        }
    }

    private void addCaption(WritableSheet sheet, int column, int row, String s) throws WriteException {
        Label label;
        label = new Label(column, row, s, timesBoldUnderline);
        sheet.addCell(label);
    }

    private void addLabel(WritableSheet sheet, int column, int row, String s) throws WriteException {
        Label label;
        label = new Label(column, row, s, times);
        sheet.addCell(label);
    }

    public interface OnFileSavedListener {
        void onFileSaved(File file);
    }

    private class CreateSpreadSheetTask extends AsyncTask<File, Void, Boolean> {
        private ArrayList<Password> mPasswords;

        public CreateSpreadSheetTask(ArrayList<Password> list) {
            mPasswords = list;
        }

        @Override
        protected Boolean doInBackground(File... params) {
            final WorkbookSettings wbSettings = new WorkbookSettings();
            WritableWorkbook workbook;
            try {
                workbook = Workbook.createWorkbook(params[0], wbSettings);
                workbook.createSheet(mContext.getString(R.string.export_passwords_sheet_passwords), 0);
                workbook.createSheet(mContext.getString(R.string.export_passwords_sheet_credit_cards), 1);
                workbook.createSheet(mContext.getString(R.string.export_passwords_sheet_wifi_passwords), 2);
            } catch (IOException e) {
                return false;
            }
            final WritableSheet passwordSheet = workbook.getSheet(0);
            final WritableSheet creditCardSheet = workbook.getSheet(1);
            final WritableSheet wifiPasswordSheet = workbook.getSheet(2);
            final int size = mPasswords.size();

            wbSettings.setLocale(new Locale("en", "EN"));

            try {
                createLabel(passwordSheet, TYPE_PASSWORD);
                createLabel(creditCardSheet, TYPE_CREDIT_CARD);
                createLabel(wifiPasswordSheet, TYPE_WIFI_PASSWORD);
            } catch (WriteException e1) {
                e1.printStackTrace();
                return false;
            }
            int passPos = 0;
            int creditCardPos = 0;
            int wifiPassPos = 0;
            for (int i = 0; i < size; i++) {
                Password entry = mPasswords.get(i);
                try {
                    if (entry.isCreditCard()) {
                        final CreditCardInfo card = entry.getCreditCard();

                        addLabel(creditCardSheet, 0, creditCardPos + 1, entry.getDescription());
                        addLabel(creditCardSheet, 1, creditCardPos + 1, card.getCardType());
                        addLabel(creditCardSheet, 2, creditCardPos + 1, card.getCardNumber());
                        addLabel(creditCardSheet, 3, creditCardPos + 1, card.getCardExpirationDate());
                        addLabel(creditCardSheet, 4, creditCardPos + 1, card.getCardCSV());
                        addLabel(creditCardSheet, 5, creditCardPos + 1, entry.getGroup());
                    } else if (entry.isWifiPassword()) {
                        addLabel(wifiPasswordSheet, 0, wifiPassPos + 1, entry.getDescription());
                        addLabel(wifiPasswordSheet, 1, wifiPassPos + 1, entry.getLoginType());
                        addLabel(wifiPasswordSheet, 2, wifiPassPos + 1, entry.getWifiSecurity());
                        addLabel(wifiPasswordSheet, 3, wifiPassPos + 1, entry.getNetworkSSID());
                        addLabel(wifiPasswordSheet, 4, wifiPassPos + 1, entry.getPassword());
                        addLabel(wifiPasswordSheet, 5, wifiPassPos + 1, entry.getNote());
                        addLabel(wifiPasswordSheet, 6, wifiPassPos + 1, entry.getGroup());
                    } else {
                        addLabel(passwordSheet, 0, passPos + 1, entry.getDescription());
                        addLabel(passwordSheet, 1, passPos + 1, entry.getLoginType());
                        addLabel(passwordSheet, 2, passPos + 1, entry.getUrl());
                        addLabel(passwordSheet, 3, passPos + 1, entry.getUsername());
                        addLabel(passwordSheet, 4, passPos + 1, entry.getPassword());
                        addLabel(passwordSheet, 5, passPos + 1, entry.getNote());
                        addLabel(passwordSheet, 6, passPos + 1, entry.getGroup());

                        passPos++;
                    }


                } catch (WriteException e) {
                }
            }

            try {
                workbook.write();
                workbook.close();
            } catch (IOException | WriteException e) {
                e.printStackTrace();
            }


            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (mListener != null) {
                mListener.onFileSaved(mOutputFile);
            }
            mListener = null;
            mContext = null;
        }

    }
}
