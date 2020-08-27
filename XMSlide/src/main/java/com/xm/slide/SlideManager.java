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

    public static SlideManager create(@NonNull Activity activity) {
        SlideManager slideManager = new SlideManager();
        slideManager.activity = activity;
        return slideManager;
    }


    public SlideManager useDefaultSlideWidth() {
        canSlideWidth = Utils.d2p(activity, 50);
        return this;
    }

    public SlideManager onSlide(OnSlideListener onSlideListener) {
        this.onSlideListener = onSlideListener;
        return this;
    }

    public SlideManager useSlideBack() {
        ImpDrawSlideBack slideView = new ImpDrawSlideBack(activity);
        slideControlBackLayout = new SlideControlBackLayout(activity, canSlideWidth, slideView, onSlideListener).attachToActivity(activity);
        return this;

    }

    public SlideManager useSlideForward() {
        ImpDrawSlideForward slideView = new ImpDrawSlideForward(activity);
        slideControlForwardLayout = new SlideControlForwardLayout(activity, canSlideWidth, slideView, onSlideListener).attachToActivity(activity);
        return this;
    }
}
