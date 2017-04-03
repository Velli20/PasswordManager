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
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import java.text.DateFormatSymbols;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Hp on 9.12.2015.
 */
public class Utils {


    public static String getDateTimeString (long millis) {
        final GregorianCalendar c = new GregorianCalendar();
        final String[] months = new DateFormatSymbols().getShortMonths();
        final String[] weekDays = new DateFormatSymbols().getWeekdays();
        final StringBuilder time = new StringBuilder();

        c.setTimeInMillis(millis);
        c.setFirstDayOfWeek(GregorianCalendar.MONDAY);

        return time.append(weekDays[c.get(GregorianCalendar.DAY_OF_WEEK)]).append(", ")
                .append(months[c.get(GregorianCalendar.MONTH)]).append(" ")
                .append(String.valueOf(c.get(GregorianCalendar.DAY_OF_MONTH)))
                .append(", ")
                .append(String.format(Locale.getDefault(), "%2d:%02d", c.get(GregorianCalendar.HOUR_OF_DAY), c.get(GregorianCalendar.MINUTE)))
                .toString();
    }

    public static Point getScreenSize(Context c){
        final Point point = new Point();
        final WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();

        display.getSize(point);
        return point;
    }
}
