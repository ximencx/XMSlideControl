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

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;


class SlideProxyView extends View {
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private IDrawSlide slideView;
    private ValueAnimator animator;
    private float rate = 0;//曲线的控制点

    SlideProxyView(Context context, @NonNull IDrawSlide slideView) {
        super(context);
        setSlideView(slideView);
    }

    public IDrawSlide getSlideView() {
        return slideView;
    }

    public SlideProxyView setSlideView(@NonNull IDrawSlide slideView) {
        this.slideView = slideView;
        setLayoutParams(new ViewGroup.LayoutParams(slideView.getViewWidth(), slideView.getViewHeight()));
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        slideView.onDraw(canvas, rate);
    }

    public void updateRate(float updateRate, boolean hasAnim) {
        if (updateRate > getWidth()) {
            updateRate = getWidth();
        }
        if (rate == updateRate) {
            return;
        }
        cancelAnim();
        if (!hasAnim) {
            rate = updateRate;
            invalidate();
            if (rate == 0) {
                setVisibility(GONE);
            } else {
                setVisibility(VISIBLE);
            }
        }

        animator = ValueAnimator.ofFloat(rate, updateRate);
        animator.setDuration(200);
        animator.addUpdateListener(animation -> {
            rate = (Float) animation.getAnimatedValue();
            postInvalidate();
            if (rate == 0) {
                setVisibility(GONE);
            } else {
                setVisibility(VISIBLE);
            }

        });
        animator.setInterpolator(DECELERATE_INTERPOLATOR);
        animator.start();
    }

    private void cancelAnim() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        cancelAnim();
        if (rate != 0) {
            rate = 0;
            invalidate();
        }
        super.onDetachedFromWindow();
    }


}