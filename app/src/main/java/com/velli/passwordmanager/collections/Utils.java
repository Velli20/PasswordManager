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

package com.velli.passwordmanager.collections;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.velli.passwordmanager.CreditCardInfo;
import com.velli.passwordmanager.R;
import com.velli.passwordmanager.roboto.RobotoTextView;
import com.velli.passwordmanager.Password;
import com.velli.passwordmanager.listeners.OnScreenshotSavedListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.MeasureSpec;

public class Utils {
	
	public static final String PASSWORD_VALID_CHARACTERS_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String PASSWORD_VALID_CHARACTERS_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
	public static final String PASSWORD_VALID_SPECIAL_CHARACTERS = "!@#$%^&*";
	public static final String PASSWORD_VALID_NUMBERS = "0123456879";
	

	
	public static String[] PASSWORD_VALID_CHARACHTERS = {PASSWORD_VALID_CHARACTERS_LOWERCASE, PASSWORD_VALID_CHARACTERS_UPPERCASE,
		PASSWORD_VALID_NUMBERS, PASSWORD_VALID_SPECIAL_CHARACTERS};
	

	
	public static class RandomStringGenerator {
		private final Random random = new Random();
		private final char[] buf;
		private char[] valid_chars;
		
		
		
		public RandomStringGenerator(int length, char[] chars) {            
			valid_chars = chars;
			buf = new char[length];
		}

		public String nextString() {
			for (int idx = 0; idx < buf.length; ++idx){
				buf[idx] = valid_chars[random.nextInt(valid_chars.length)];
			}
			return new String(buf);
		}
	}

	
	public static int[] getLoginLabelIconArray(){
		final int icons[] = {R.drawable.ic_type_social, 
				R.drawable.ic_type_email, 
				R.drawable.ic_type_cloud, 
				R.drawable.ic_type_shop,
				R.drawable.ic_type_web, 
				R.drawable.ic_type_work,
				R.drawable.ic_type_bank,
				R.drawable.ic_type_app,
				R.drawable.ic_type_gaming,
				R.drawable.ic_type_messaging,
				R.drawable.ic_type_wifi};
		return icons;
	}

	
	public static int[] getLoginLabelArray(){
		final int labels[] = {R.string.label_social,
				R.string.label_email,
				R.string.label_cloud,
				R.string.label_e_commercial,
				R.string.label_web,
				R.string.label_work,
				R.string.label_bank,
				R.string.label_app,
				R.string.label_gaming,
				R.string.label_messaging,
				R.string.label_wifi};
		return labels;
	}
	
	public static String compositeEmailPassword(Resources res, Password pass){
		StringBuilder builder = new StringBuilder();
		builder.append(res.getString(R.string.new_entry_description));
		builder.append(": ");
		builder.append(pass.getDescription() + "\n");
		builder.append(res.getString(R.string.new_entry_label));
		builder.append(": ");
		builder.append(pass.getLoginType() + "\n");
		builder.append(res.getString(R.string.new_entry_url));
		builder.append(": ");
		builder.append(pass.getUrl() + "\n");
		builder.append(res.getString(R.string.new_entry_username));
		builder.append(": ");
		builder.append(pass.getUsername() + "\n");
		builder.append(res.getString(R.string.new_entry_password));
		builder.append(": ");
		builder.append(pass.getDescription() + "\n");
		builder.append(res.getString(R.string.new_entry_group));
		builder.append(": ");
		builder.append(pass.getGroup() + "\n");
		return builder.toString();
	}
	
	public static SpannableStringBuilder boldString(String stringToSpan, String pattern){
		final SpannableStringBuilder s = new SpannableStringBuilder(stringToSpan);

		if(pattern.isEmpty()){
			return s;
		}
		
		final Matcher m;
		try {
			 m = Pattern.compile("(?i)" + pattern).matcher(stringToSpan);
		} catch(PatternSyntaxException e){
			return s;
		}

		while(m.find()){
			final int start = m.start();
			final int end = m.end();
			
			s.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}		
		
		return s;
	}
	
	public static String upperCaseFirstLetter(String s){
		if(s.length() > 0){
			return s.substring(0,1).toUpperCase(Locale.getDefault()) + s.substring(1);
		} else {
			return s;
		}
	}
	
	@NonNull
	public static void createScreenshot(Password entry, Context context, OnScreenshotSavedListener listener){
		ScreenshotTask task = (new Utils()).new ScreenshotTask(entry, context, listener);
		task.execute();
		
	}

	public static Password copyPassword(Password toCopy){
		if(toCopy == null){
			return null;
		}
		final Password copy = new Password();
		
		copy.setDescription(toCopy.getDescription());
		copy.setLoginType(toCopy.getLoginType(), toCopy.getLoginIcon());
		copy.setUrl(toCopy.getUrl());
		copy.setUsername(toCopy.getUsername());
		copy.setPassword(toCopy.getPassword());
		copy.setNote(toCopy.getNote());
		copy.setGroup(toCopy.getGroup());
		copy.setAppPackageName(toCopy.getAppPackageName());
		copy.setNetworkSSID(toCopy.getNetworkSSID());
		copy.setWifiSecurity(toCopy.getWifiSecurity());
		copy.setCreditCard(toCopy.getCreditCard());
		return copy;
	}
	
	private class ScreenshotTask extends AsyncTask<Void, Void, String> {
		private Password mEntry;
		private Context mContext;
		private OnScreenshotSavedListener mListener;
		
		public ScreenshotTask(Password entry, Context context, OnScreenshotSavedListener listener){
			mEntry = entry;
			mContext = context;
			mListener = listener;
		}

		@SuppressLint("InflateParams")
		@Override
		protected String doInBackground(Void... params) {
			final boolean isCreditCard = mEntry.isCreditCard();
			final LayoutInflater inflater = LayoutInflater.from(mContext);
			final View v = inflater.inflate(isCreditCard ? R.layout.utils_credit_card_scrrenshot_view : R.layout.utils_password_screenshot_view, null);
			final int icons[] = getLoginLabelIconArray();
			
			Drawable d = null;


			if(isCreditCard){
				final CreditCardInfo card = mEntry.getCreditCard();
				
				d = mContext.getResources().getDrawable(R.drawable.ic_type_credit_card);
				d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
				
				((RobotoTextView)v.findViewById(R.id.details_view_card)).setText(card.getCardType());
				((RobotoTextView)v.findViewById(R.id.details_view_card_number)).setText(card.getCardNumber());
				((RobotoTextView)v.findViewById(R.id.details_view_card_exp_date)).setText(card.getCardExpirationDate());
				((RobotoTextView)v.findViewById(R.id.details_view_card_csv)).setText(card.getCardCSV());
			} else {
                if(mEntry.getLoginIcon() >= 0) {
                    d = mContext.getResources().getDrawable(icons[mEntry.getLoginIcon()]);
                    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                }
				if(mEntry.isWifiPassword()){
					((RobotoTextView)v.findViewById(R.id.details_view_url)).setText(mEntry.getWifiSecurity());
					((RobotoTextView)v.findViewById(R.id.details_view_url_title)).setText(R.string.new_entry_wifi_security);
					((RobotoTextView)v.findViewById(R.id.details_view_username)).setText(mEntry.getNetworkSSID());
					((RobotoTextView)v.findViewById(R.id.details_view_username_title)).setText(R.string.new_entry_wifi_ssid);
				} else {
					if(mEntry.getUrl() == null || mEntry.getUrl().isEmpty()){
						v.findViewById(R.id.details_view_url).setVisibility(View.GONE);
					} else {
						((RobotoTextView)v.findViewById(R.id.details_view_url)).setText(mEntry.getUrl());
					}
					((RobotoTextView)v.findViewById(R.id.details_view_username)).setText(mEntry.getUsername());
				}
				if(mEntry.getPassword() == null || mEntry.getPassword().isEmpty()){
					v.findViewById(R.id.details_view_password).setVisibility(View.GONE);
					v.findViewById(R.id.details_view_password_title).setVisibility(View.GONE);
				}
				if(mEntry.getNote() == null || mEntry.getNote().isEmpty()){
					v.findViewById(R.id.details_view_notes).setVisibility(View.GONE);
					v.findViewById(R.id.details_view_notes_title).setVisibility(View.GONE);
				}
				((RobotoTextView)v.findViewById(R.id.details_view_password)).setText(mEntry.getPassword());
				((RobotoTextView)v.findViewById(R.id.details_view_notes)).setText(mEntry.getNote());
			}
		
			((RobotoTextView)v.findViewById(R.id.details_view_description)).setText(mEntry.getDescription());
			((RobotoTextView)v.findViewById(R.id.details_view_description)).setCompoundDrawables(null, null, d, null);
			((RobotoTextView)v.findViewById(R.id.details_view_login_type)).setText(mEntry.getLoginType());
			((RobotoTextView)v.findViewById(R.id.details_view_group)).setText(mEntry.getGroup());
			
			v.setDrawingCacheEnabled(true); 
			v.measure(MeasureSpec.makeMeasureSpec(getScreenSize().x, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight()); 
            v.buildDrawingCache(true);
			
            final Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
            final String root = Environment.getExternalStorageDirectory().toString();
            final File myDir = new File(root + "/Password Keeper");    
		   
            v.setDrawingCacheEnabled(false); // clear drawing cache
            myDir.mkdirs();
            
            final String fname = "password-"+ mEntry.getDescription() +".jpg";
            final File file = new File (myDir, fname);
            
            if(file.exists()){
            	file.delete();
            }
            
            try {
                final FileOutputStream out = new FileOutputStream(file);
                b.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                b.recycle();
                v.destroyDrawingCache();

             } catch (Exception e) {
                e.printStackTrace();
                return null;
             }
			return file.getAbsolutePath();
		}
		
		private Point getScreenSize(){
			final Point point = new Point();
			final WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
			final Display display = wm.getDefaultDisplay();
			
			display.getSize(point);
			return point;
		}
		
		@Override
		protected void onPostExecute(String path){
			if(mListener != null){
				mListener.onScreenshotSaved(path);
			}
			mListener = null;
		}
		
	}

}
