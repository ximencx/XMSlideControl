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
import android.view.ViewParent;
import android.widget.FrameLayout;


public class SlideControlBackLayout extends FrameLayout {
    private final SlideProxyView slideProxyView;
    private final OnSlideListener onSlideListener;
    private int canSlideWidth;
    private boolean enable = true;

    private float downX;
    private float moveX;
    private boolean startDrag = false;

    SlideControlBackLayout(@NonNull Context context, int canSlideWidth, IDrawSlide slideView, OnSlideListener onSlideListener) {
        super(context);
        this.canSlideWidth = canSlideWidth;
        this.onSlideListener = onSlideListener;
        slideProxyView = new SlideProxyView(context, slideView);
        addView(slideProxyView);
    }


    SlideControlBackLayout attachToActivity(@NonNull Activity activity) {
        ViewParent parent = getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(this);
        }
        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();

        decor.addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return this;
    }

    private void onBack() {
        if (onSlideListener != null) {
            onSlideListener.onSlideBack();
        }
    }


    private void setSlideViewY(SlideProxyView view, int y) {
        if (!view.getSlideView().scrollVertical()) {
            scrollTo(0, 0);
            return;
        }
        scrollTo(0, -(y - view.getHeight() / 2));
    }

    //region 手势控制
    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!enable) {
            return false;
        }

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (motionEvent.getRawX() <= canSlideWidth) {
                    return true;
                }
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!enable) {
            return super.onTouchEvent(motionEvent);
        }

        float currentX = motionEvent.getRawX();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float currentY = motionEvent.getRawY();
                if (currentY > Utils.d2p(getContext(), 100) && currentX <= canSlideWidth) {
                    downX = currentX;
                    startDrag = true;
                    slideProxyView.updateRate(0, false);
                    setSlideViewY(slideProxyView, (int) (motionEvent.getRawY()));
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (startDrag) {
                    moveX = currentX - downX;
                    if (Math.abs(moveX) <= slideProxyView.getSlideView().getShowViewWidth() * 2) {
                        slideProxyView.updateRate(Math.abs(moveX) / 2, false);
                    } else {
                        slideProxyView.updateRate(slideProxyView.getSlideView().getShowViewWidth(), false);
                    }
                    setSlideViewY(slideProxyView, (int) (motionEvent.getRawY()));
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (startDrag && Math.abs(moveX) >= slideProxyView.getSlideView().getShowViewWidth() * 2) {
                    onBack();
                    slideProxyView.updateRate(0, false);
                } else {
                    slideProxyView.updateRate(0, startDrag);
                }
                moveX = 0;
                startDrag = false;
                break;
        }

        return startDrag || super.onTouchEvent(motionEvent);
    }
    //endregion


}
