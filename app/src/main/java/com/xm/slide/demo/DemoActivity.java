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

package com.xm.slide.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.xm.slide.OnSlideListener;
import com.xm.slide.SlideManager;


public class DemoActivity extends AppCompatActivity {
    private SlideManager slideManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        findViewById(R.id.btn_new_activity).setOnClickListener(v -> {
            if (slideManager == null) {
                return;
            }
            if (slideManager.isEnableSlide()) {
                slideManager.setEnableSlide(false);
                ((TextView) findViewById(R.id.btn_new_activity)).setText("slide enable false");
            } else {
                slideManager.setEnableSlide(true);
                ((TextView) findViewById(R.id.btn_new_activity)).setText("slide enable true");
            }


        });

        //开启滑动关闭
        slideManager = SlideManager.create(this).useDefaultSlideWidth()
                .onSlide(new OnSlideListener() {
                    @Override
                    public void onSlideBack() {
                        Toast.makeText(DemoActivity.this, "onSlideBack", Toast.LENGTH_SHORT).show();
                        //onBackPressed();
                    }

                    @Override
                    public void onSlideForward() {
                        Toast.makeText(DemoActivity.this, "onSlideForward", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(DemoActivity.this, DemoActivity.class));
                        //overridePendingTransition(R.anim.fade_right_in, R.anim.fade_left_out);
                    }
                }).useSlideBack().useSlideForward();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_left_in, R.anim.fade_right_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (slideManager != null) {
            slideManager.onDestroy();
        }
    }
}
