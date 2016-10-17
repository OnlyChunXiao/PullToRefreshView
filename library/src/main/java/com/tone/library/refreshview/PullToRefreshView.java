package com.tone.library.refreshview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by zhaotong on 2016/10/9.
 */
public class PullToRefreshView extends FrameLayout {

    private final String TAG = getClass().getSimpleName();

    private ViewDragHelper viewDragHelper;
    private Context context;
    private View scrollView;
    private FooterView footerView;
    private HeaderView headerView;
    private boolean canRefresh = true;
    private boolean canLoad = true;
    private int headerHeight = 0;
    private int footerHeight = 0;
    private int maxDragValue = 0;
    private int refreshHeight = 0;
    //滑动过程中，现在的顶部位置
    private int currentTop = 0;
    //现在所处的状态
    private State currentState;
    //用户按屏幕时的初始点坐标
    private float initY;
    //现在的屏幕坐标点
    private float currentY = 0;
    private onRefreshListener listener;

    public void setOnRefreshListener(onRefreshListener listener) {
        this.listener = listener;
    }

    public PullToRefreshView(Context context) {
        super(context);
        init(context);
    }

    public PullToRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullToRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        viewDragHelper = ViewDragHelper.create(this, 1f, callback);
        currentState = State.IDLE;
    }


    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == scrollView;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            currentTop = top;
            headerView.offsetTopAndBottom(dy);
            if (canLoad && top < 0)
                footerView.offsetTopAndBottom(dy);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (isTop(scrollView) && canRefresh && dy < -1 && currentState == State.REFRESHING && top < headerHeight * 4 / 5) {
                setState(State.IDLE);
            }

            if (isTop(scrollView) && canRefresh) {
                if (top < (refreshHeight)) {
                    //下拉刷新
                    if (currentState == State.IDLE || currentState == State.RELEASE_TO_REFRESH) {
                        setState(State.PULL_TO_REFRESH);
                    }
                } else {
                    //释放刷新
                    if (currentState == State.PULL_TO_REFRESH) {
                        setState(State.RELEASE_TO_REFRESH);
                    }
                }
            }

            if (isBottom(scrollView) && canLoad) {
                if (dy < -1 && top < 0) {
                    if (currentState != State.PULL_TO_LOAD && currentState != State.LOADING) {
                        setState(State.PULL_TO_LOAD);
                    }
                }
                if (dy >= 3 && top >= -footerHeight / 3) {
                    if (currentState == State.PULL_TO_LOAD || currentState == State.LOADING)
                        setState(State.IDLE);
                }
            }

            if (top < -footerHeight) {
                return -footerHeight;
            }
            if (top > maxDragValue) {
                return maxDragValue;
            }
            return top;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return maxDragValue;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (isTop(scrollView) && canRefresh) {
                if (currentState == State.RELEASE_TO_REFRESH || currentState == State.PULL_TO_REFRESH) {
                    if (currentTop < refreshHeight) {
                        setState(State.IDLE);
                    } else {
                        setState(State.REFRESHING);
                    }
                }
                if (currentState == State.REFRESHING) {
                    if (currentTop < headerHeight) {
                        setState(State.IDLE);
                    } else {
                        setState(State.REFRESHING);
                    }
                }
            }
            if (isBottom(scrollView) && canLoad) {
                if (currentState == State.LOADING) {
                    if (currentTop > -footerHeight) {
                        setState(State.IDLE);
                    }
                }
                if (currentState == State.PULL_TO_LOAD) {
                    if (currentTop < -footerHeight / 3) {
                        setState(State.LOADING);
                    } else {
                        setState(State.IDLE);
                    }
                }

            }

        }

    };

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 1) {
            throw new RuntimeException("The child of PullToRefresh should be only one!!!");
        }
        scrollView = getChildAt(0);
        headerView = new HeaderView(context);
        footerView = new FooterView(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(headerView, params);
        addView(footerView, params);
        scrollView.bringToFront();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }
        headerHeight = headerView.getMeasuredHeight();
        footerHeight = footerView.getMeasuredHeight();
        maxDragValue = headerHeight * 4;
        refreshHeight = (int) (headerHeight * 1.5);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
      }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (scrollView != null) {
            scrollView.layout(0, currentTop, scrollView.getMeasuredWidth(), scrollView.getMeasuredHeight() + currentTop);
        }
        if (headerView != null) {
            headerView.layout(0, currentTop - headerHeight, headerView.getMeasuredWidth(), currentTop);
        }
        if (footerView != null) {
            footerView.layout(0, bottom + currentTop, footerView.getMeasuredWidth(), bottom + currentTop + footerHeight);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!canRefresh) {
            return super.dispatchTouchEvent(event);
        }
        final int action = MotionEventCompat.getActionMasked(event);
        if (currentState == State.REFRESHING) {
            viewDragHelper.processTouchEvent(event);
            return true;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initY = event.getRawY();
                viewDragHelper.processTouchEvent(event);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                viewDragHelper.processTouchEvent(event);
                break;

            case MotionEvent.ACTION_MOVE:
                currentY = event.getRawY();
                int dy = (int) (currentY - initY);
                //到顶部，并且是向下拉
                if (isTop(scrollView) && canRefresh) {
                    if (dy > 1 || currentTop > 0) {
                        viewDragHelper.processTouchEvent(event);
                        event.setAction(MotionEvent.ACTION_CANCEL);
                        super.dispatchTouchEvent(event);
                        return true;
                    }

                }
                if (isBottom(scrollView) && dy < -1 && canLoad) {
                    viewDragHelper.processTouchEvent(event);
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    super.dispatchTouchEvent(event);
                    return true;
                }
                if (!isTop(scrollView) && !isBottom(scrollView)) {
                    if (headerView.getTop() != -headerHeight || footerView.getTop() != scrollView.getMeasuredHeight())
                        setState(State.IDLE);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (currentState != State.IDLE) {
                    viewDragHelper.processTouchEvent(event);
                }
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean smoothSlideTo(int targetPosition) {
        if (viewDragHelper.smoothSlideViewTo(scrollView, scrollView.getLeft(), targetPosition)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }


    enum State {
        IDLE(1), PULL_TO_REFRESH(2), RELEASE_TO_REFRESH(3), REFRESHING(4), LOADING(6), PULL_TO_LOAD(5);
        public int value;

        State(int intValue) {
            value = intValue;
        }

        int getValue() {
            return value;
        }
    }

    private boolean b=true;
    public void onComplete(boolean refresh, boolean hasMore, boolean success) {
        if (refresh) {
            setState(State.IDLE);
        } else {
            if (!hasMore) {
                if (isBottom(scrollView)) {
                    smoothSlideTo(-footerHeight);
                    if (success)
                        footerView.onLoadAll();
                    else {
                        footerView.onError();
                        b= success;
                        footerView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!b) {
                                    setState(State.LOADING);
                                    b=!b;
                                }
                            }
                        });
                    }
                }
            } else {
                setState(State.IDLE);
            }
        }
    }

    private void setState(State state) {
        currentState = state;
        switch (currentState) {
            case IDLE:
                smoothSlideTo(0);
                headerView.reset();
                footerView.reset();
                break;
            case PULL_TO_REFRESH:
                headerView.onPullToRefresh();
                break;
            case RELEASE_TO_REFRESH:
                headerView.onReleaseToRefresh();
                break;
            case LOADING:
                smoothSlideTo(-footerHeight);
                footerView.onLoading();
                if (listener != null)
                    listener.onLoadMore();
                break;
            case REFRESHING:
                smoothSlideTo(headerHeight);
                headerView.onRefresh();
                if (listener != null)
                    listener.onRefresh();
                break;
            case PULL_TO_LOAD:
                footerView.onPullToLoad();
                break;
        }
    }


    public interface onRefreshListener {
        void onRefresh();

        void onLoadMore();
    }

    private boolean isTop(View view) {
        return !ViewCompat.canScrollVertically(view, -1);
    }

    private boolean isBottom(View view) {
        return !ViewCompat.canScrollVertically(view, 1);
    }

    public void setCanLoad(boolean canLoad) {
        this.canLoad = canLoad;
    }

    public void setCanRefresh(boolean canRefresh) {
        this.canRefresh = canRefresh;
    }
}
