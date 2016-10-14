package com.tone.sample;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tone.library.refreshview.PullToRefreshView;

public class ScrollViewActivity extends AppCompatActivity {
    private PullToRefreshView pullToRefreshView;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what==1) {
                pullToRefreshView.onComplete(true,false,false);
            }
            if (what==2) {
                pullToRefreshView.onComplete(false,false,false);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollview);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pulltorefreshview);
        pullToRefreshView.setOnRefreshListener(new PullToRefreshView.onRefreshListener() {
            @Override
            public void onRefresh() {
                handler.sendEmptyMessageDelayed(1,3000);
            }

            @Override
            public void onLoadMore() {
                handler.sendEmptyMessageDelayed(2,3000);
            }
        });
    }

}
