package com.tone.library.refreshview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tone.library.R;

/**
 * Created by zhaotong on 2016/10/10.
 */
public class HeaderView extends FrameLayout {
    private ImageView mHeaderImage;
    private ProgressBar mHeaderProgress;
    private RotateAnimation mRotateUpAnim;
    private RotateAnimation mRotateDownAnim;
    private TextView mHeaderText;
    private TextView mSubHeaderText;
    private String mPullLabel = "下拉刷新";
    private String mRefreshingLabel = "正在刷新";
    private String mReleaseLabel = "释放刷新";

    public HeaderView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.header_view, this);
        mHeaderText = (TextView) findViewById(R.id.pull_to_refresh_text);
        mHeaderProgress = (ProgressBar) findViewById(R.id.pull_to_refresh_progress);
        mSubHeaderText = (TextView) findViewById(R.id.pull_to_refresh_sub_text);
        mHeaderImage = (ImageView) findViewById(R.id.pull_to_refresh_image);

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        mRotateUpAnim.setDuration(180);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        mRotateDownAnim.setDuration(180);
        mRotateDownAnim.setFillAfter(true);
    }

    public void reset() {
        mHeaderText.setText(mPullLabel);
        mHeaderImage.setVisibility(View.VISIBLE);
        mHeaderImage.clearAnimation();
        mHeaderProgress.setVisibility(View.GONE);
    }

    public void onPullToRefresh() {
        mHeaderText.setText(mPullLabel);
        mHeaderImage.clearAnimation();
        mHeaderImage.setVisibility(View.VISIBLE);
        mHeaderImage.startAnimation(mRotateDownAnim);
        mHeaderProgress.setVisibility(View.GONE);

    }

    public void onReleaseToRefresh() {
        mHeaderText.setText(mReleaseLabel);
        mHeaderImage.setVisibility(View.VISIBLE);
        mHeaderProgress.setVisibility(View.GONE);
        mHeaderImage.clearAnimation();
        mHeaderImage.startAnimation(mRotateUpAnim);
    }

    public void onRefresh() {
        mHeaderText.setText(mRefreshingLabel);
        mHeaderImage.clearAnimation();
        mHeaderImage.setVisibility(View.GONE);
        mHeaderProgress.setVisibility(View.VISIBLE);
    }
}
