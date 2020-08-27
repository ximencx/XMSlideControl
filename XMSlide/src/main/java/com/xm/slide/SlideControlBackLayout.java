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
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;


public class SlideControlBackLayout {
    private final SlideProxyView slideProxyView;
    private final OnSlideListener onSlideListener;
    private int canSlideWidth;
    private boolean enable = true;

    private float downX;
    private float moveX;
    private boolean startDrag = false;

    private Context context;

    public SlideControlBackLayout(@NonNull Context context, int canSlideWidth, IDrawSlide slideView, OnSlideListener onSlideListener) {
        this.context = context;
        this.canSlideWidth = canSlideWidth;
        this.onSlideListener = onSlideListener;
        slideProxyView = new SlideProxyView(context, slideView);
    }


    public SlideControlBackLayout create() {
        ViewGroup decor = (ViewGroup) ((Activity) context).getWindow().getDecorView();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        decor.addView(slideProxyView, params);
        return this;
    }

    public void onDestroy() {
        ViewGroup decor = (ViewGroup) ((Activity) context).getWindow().getDecorView();
        decor.removeView(slideProxyView);
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    private void onBack() {
        if (onSlideListener != null) {
            onSlideListener.onSlideBack();
        }
    }

    private void setSlideViewY(SlideProxyView view, int y) {
        if (!view.getSlideView().scrollVertical()) {
            view.scrollTo(0, 0);
            return;
        }
        //Log.v("::::", "y:" + y);
        view.scrollTo(0, -(y - slideProxyView.getSlideView().getShowViewWidth()));
    }

//    //region 手势控制
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
//        if (!enable) {
//            return false;
//        }
//
//        switch (motionEvent.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                if (motionEvent.getRawX() <= canSlideWidth) {
//                    return true;
//                }
//        }
//        return super.onInterceptTouchEvent(motionEvent);
//    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!enable) {
            return false;
        }

        float currentX = motionEvent.getRawX();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float currentY = motionEvent.getRawY();
                if (currentY > Utils.d2p(context, 100) && currentX <= canSlideWidth) {
                    downX = currentX;
                    startDrag = true;
                    slideProxyView.updateRate(0, false);
                    setSlideViewY(slideProxyView, (int) (motionEvent.getRawY()));
                } else {
                    return false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (startDrag) {
                    moveX = currentX - downX;
                    if (moveX > 0 && Math.abs(moveX) <= slideProxyView.getSlideView().getShowViewWidth() * 2) {
                        slideProxyView.updateRate(Math.abs(moveX) / 2, false);
                    } else if (moveX > 0) {
                        slideProxyView.updateRate(slideProxyView.getSlideView().getShowViewWidth(), false);
                    }
                    setSlideViewY(slideProxyView, (int) (motionEvent.getRawY()));
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (startDrag && moveX > 0 && Math.abs(moveX) >= slideProxyView.getSlideView().getShowViewWidth() * 2) {
                    onBack();
                    slideProxyView.updateRate(0, false);
                } else if (moveX > 0) {
                    slideProxyView.updateRate(0, startDrag);
                }
                moveX = 0;
                startDrag = false;
                break;
        }
        //Log.v("::::", "MotionEvent" + motionEvent.getAction() + "startDrag:" + startDrag + "movex" + moveX);
        return startDrag;
    }
    //endregion


}
