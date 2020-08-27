/*
 * Copyright 2019. SHENQINCI(沈钦赐)<946736079@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xm.slide;

import android.app.Activity;
import android.support.annotation.NonNull;


public class SlideManager {
    private OnSlideListener onSlideListener;
    private int canSlideWidth;
    private Activity activity;
    private SlideControlForwardLayout slideControlForwardLayout;
    private SlideControlBackLayout slideControlBackLayout;
    private boolean isEnableSlide = true;

    public static SlideManager create(@NonNull Activity activity) {
        SlideManager slideManager = new SlideManager();
        slideManager.activity = activity;
        return slideManager;
    }

    public SlideManager setCanSlideWidth(int canSlideWidth) {
        this.canSlideWidth = canSlideWidth;
        return this;
    }

    public SlideManager useDefaultSlideWidth() {
        this.canSlideWidth = Utils.getScreenWidth(activity) / 3;
        return this;
    }

    public SlideManager onSlide(OnSlideListener onSlideListener) {
        this.onSlideListener = onSlideListener;
        return this;
    }

    public SlideManager useSlideBack() {
        ImpDrawSlideBack slideView = new ImpDrawSlideBack(activity);
        slideControlBackLayout = new SlideControlBackLayout(activity, canSlideWidth, slideView, onSlideListener).create();
        return this;

    }

    public SlideManager useSlideForward() {
        ImpDrawSlideForward slideView = new ImpDrawSlideForward(activity);
        slideControlForwardLayout = new SlideControlForwardLayout(activity, canSlideWidth, slideView, onSlideListener).create();
        return this;
    }

    public boolean isEnableSlide() {
        return isEnableSlide;
    }

    public void setEnableSlide(boolean isEnable) {
        isEnableSlide = isEnable;
        if (slideControlBackLayout != null) {
            slideControlBackLayout.setEnable(isEnable);
        }
        if (slideControlForwardLayout != null) {
            slideControlForwardLayout.setEnable(isEnable);
        }
    }

    public void onDestroy() {
        if (slideControlBackLayout != null) {
            slideControlBackLayout.onDestroy();
        }
        if (slideControlForwardLayout != null) {
            slideControlForwardLayout.onDestroy();
        }
        if (onSlideListener != null) {
            onSlideListener = null;
        }
    }
}
