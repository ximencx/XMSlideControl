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
import android.view.MotionEvent;
import android.view.ViewGroup;

public class SlideControlForwardLayout {
    private final SlideProxyView slideProxyView;
    private final OnSlideListener onSlideListener;
    private int canSlideWidth;
    private boolean enable = true;

    private float downX;
    private float moveX;
    private float downY;
    private float moveY;
    private boolean startDrag = false;
    private boolean isResult = false;

    private Context context;

    public SlideControlForwardLayout(@NonNull Context context, int canSlideWidth, IDrawSlide slideView, OnSlideListener onSlideListener) {
        this.context = context;
        this.canSlideWidth = canSlideWidth;
        this.onSlideListener = onSlideListener;
        slideProxyView = new SlideProxyView(context, slideView);
    }


    public SlideControlForwardLayout create() {
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

    private void onForward() {
        if (onSlideListener != null) {
            onSlideListener.onSlideForward();
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

    private int getForwardCanSlideWidth() {
        return Utils.getScreenWidth(context) - canSlideWidth;
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
//                if (motionEvent.getRawX() >= getForwardCanSlideWidth()) {
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
        float currentY = motionEvent.getRawY();

        switch (motionEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if (currentY > Utils.d2p(context, 100) && currentX >= getForwardCanSlideWidth()) {
                    downX = currentX;
                    downY = currentY;
                    startDrag = true;
                } else {
                    startDrag = false;
                    return false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (startDrag) {
                    moveX = currentX - downX;
                    moveY = currentY - downY;
                    if (startDrag && Math.abs(moveX) > Math.abs(moveY)) {
                        if (moveX < 0 && Math.abs(moveX) <= slideProxyView.getSlideView().getShowViewWidth() * 2) {
                            slideProxyView.updateRate(Math.abs(moveX) / 2, false);
                        } else if (moveX < 0) {
                            slideProxyView.updateRate(slideProxyView.getSlideView().getShowViewWidth(), false);
                        }
                        setSlideViewY(slideProxyView, (int) currentY);
                        isResult = true;
                    } else {
                        isResult = false;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (startDrag && moveX < 0 && Math.abs(moveX) >= slideProxyView.getSlideView().getShowViewWidth() * 2) {
                    onForward();
                    slideProxyView.updateRate(0, false);
                    isResult = true;
                } else if (startDrag && moveX < 0) {
                    slideProxyView.updateRate(0, startDrag);
                    isResult = true;
                } else {
                    isResult = false;
                }
                moveX = 0;
                moveY = 0;
                startDrag = false;
                break;
        }
        //Log.v("::::", "MotionEvent" + motionEvent.getAction() + "startDrag:" + startDrag + "movex" + moveX);
        return isResult;
    }
    //endregion
}
