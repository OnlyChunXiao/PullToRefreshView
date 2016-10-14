package com.tone.library.refreshview;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tone.library.R;

/**
 * Created by zhaotong on 2016/10/10.
 */
public class FooterView extends FrameLayout {
    private TextView pull_to_refresh_footer_text;
    private ProgressBar pull_to_refresh_footer_progress;
    public FooterView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.footer_view, this);
        pull_to_refresh_footer_progress = (ProgressBar) findViewById(R.id.pull_to_refresh_footer_progress);
        pull_to_refresh_footer_text = (TextView) findViewById(R.id.pull_to_refresh_footer_text);
        pull_to_refresh_footer_progress.setVisibility(GONE);
    }

    public void onLoading(){
        pull_to_refresh_footer_progress.setVisibility(VISIBLE);
        pull_to_refresh_footer_text.setText("更多加载中");
    }

    public void onPullToLoad(){
        pull_to_refresh_footer_progress.setVisibility(GONE);
        pull_to_refresh_footer_text.setText("上拉加载更多");
    }

    public void onError(){
        pull_to_refresh_footer_progress.setVisibility(GONE);
        pull_to_refresh_footer_text.setText("加载失败，点击重新加载");
    }
    public void onLoadAll(){
        pull_to_refresh_footer_progress.setVisibility(GONE);
        pull_to_refresh_footer_text.setText("没有更多了");
    }
}
