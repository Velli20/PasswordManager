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

import android.os.Bundle;

import com.velli.passwordmanager.filepicker.ActivityFilePicker;

/**
 * Created by Hp on 6.12.2015.
 */
public class ActivityFilePickerExtended extends ActivityFilePicker {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationBase.setActivityStatus(ApplicationBase.ACTIVITY_FILE_PICKER, ApplicationBase.STATUS_VISIBLE, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationBase.setActivityStatus(ApplicationBase.ACTIVITY_FILE_PICKER, ApplicationBase.STATUS_VISIBLE, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        ApplicationBase.setActivityStatus(ApplicationBase.ACTIVITY_FILE_PICKER, ApplicationBase.STATUS_ON_PAUSE, isChangingConfigurations());
    }

    @Override
    public void onStop() {
        super.onStop();
        ApplicationBase.setActivityStatus(ApplicationBase.ACTIVITY_FILE_PICKER, ApplicationBase.STATUS_ON_STOP, isChangingConfigurations());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationBase.setActivityStatus(ApplicationBase.ACTIVITY_FILE_PICKER, ApplicationBase.STATUS_ON_DESTROY, isChangingConfigurations());
    }
}
