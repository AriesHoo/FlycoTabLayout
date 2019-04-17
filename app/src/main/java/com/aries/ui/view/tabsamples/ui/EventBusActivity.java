package com.aries.ui.view.tabsamples.ui;

import android.os.Bundle;
import android.view.View;

import com.aries.ui.view.tabsamples.R;

import org.simple.eventbus.EventBus;

import androidx.annotation.Nullable;

/**
 * @Author: AriesHoo on 2019/4/17 10:35
 * @E-Mail: AriesHoo@126.com
 * @Function:
 * @Description:
 */
public class EventBusActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_bus);
        findViewById(R.id.btn_sendEventBus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(2, "switchTab");
                finish();
            }
        });
    }
}
