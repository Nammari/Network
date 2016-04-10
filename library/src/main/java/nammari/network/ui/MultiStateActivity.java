package nammari.network.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import nammari.network.R;
import nammari.network.util.StringUtils;

/**
 * Created by nammari on 11/29/14.
 */
public abstract class MultiStateActivity extends ActionBarActivity {


    static final int INTERNAL_PROGRESS_CONTAINER_ID = 0x00ff0001;
    static final int INTERNAL_MAIN_CONTAINER_ID = 0x00ff0002;
    static final int INTERNAL_ERROR_CONTAINER_ID = 0x00ff0003;
    static final int INTERNAL_EMPTY_CONAINER_ID = 0x00ff0004;

    public enum INTERNAL_VIEW_TYPE {
        MAIN, LOADING, ERROR, EMPTY
    }

    ;


    protected abstract int getMainViewLayoutId();


    TextView mStandardEmptyView;
    View mProgressContainer;
    View mMainContainer;
    View mErrorView;
    View mEmptyView;
    TextView mErrorText;
    SwipeRefreshLayout mSwipeToRefresh;

    INTERNAL_VIEW_TYPE currentVisibileView = INTERNAL_VIEW_TYPE.MAIN;


    protected abstract void onErrorRetry();

    protected void setErrorText(CharSequence text) {
        if (mErrorText == null)
            return;
        if (!StringUtils.isBlink((String) text)) {
            mErrorText.setText(text);
        } else {
            mErrorText.setText(R.string.no_connection);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout root = new FrameLayout(this);
        //empty container
        View emptyView = getEmptyView();
        if (emptyView != null) {
            emptyView.setId(INTERNAL_EMPTY_CONAINER_ID);
            emptyView.setVisibility(View.GONE);
            root.addView(emptyView, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mEmptyView = emptyView;
        }
        // ------------------------------Error
        // container------------------------------------

        LinearLayout eframe = new LinearLayout(this);
        eframe.setId(INTERNAL_ERROR_CONTAINER_ID);
        eframe.setOrientation(LinearLayout.VERTICAL);

        eframe.setGravity(Gravity.CENTER);
        eframe.setVisibility(View.GONE);
        ImageView error_image = new ImageView(this);
        error_image.setImageResource(R.drawable.alert_error);
        final float scale = getResources().getDisplayMetrics().density;

        eframe.addView(error_image, new FrameLayout.LayoutParams(
                (int) (scale * 75), (int) (scale * 75)));
        // ViewGroup.LayoutParams.WRAP_CONTENT,
        // ViewGroup.LayoutParams.WRAP_CONTENT));
        mErrorText = new TextView(this);
        mErrorText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        mErrorText.setText(R.string.no_connection);
        mErrorText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams errorTextLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        // errorTextLayoutParams.weight = 1.0f;

        eframe.addView(mErrorText, errorTextLayoutParams);

        Button retry = new Button(this);
        retry.setText(R.string.retry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onErrorRetry();
            }
        });
        eframe.addView(retry, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(eframe, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        mErrorView = eframe;

        // ------------------------------Progress
        // container------------------------------------

        LinearLayout pframe = new LinearLayout(this);
        pframe.setId(INTERNAL_PROGRESS_CONTAINER_ID);
        pframe.setOrientation(LinearLayout.VERTICAL);
        pframe.setVisibility(View.GONE);
        pframe.setGravity(Gravity.CENTER);

        ProgressBar progress = new ProgressBar(this, null,
                android.R.attr.progressBarStyle);
        pframe.addView(progress, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mProgressContainer = pframe;
        root.addView(pframe, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));


        //Main container

        FrameLayout lframe = new FrameLayout(this);
        lframe.setId(INTERNAL_MAIN_CONTAINER_ID);

        int layoutId = getMainViewLayoutId();
        View lv = null;

        if (layoutId != -1) {


            if (supportSwipeToRefresh()) {
                mSwipeToRefresh = new SwipeRefreshLayout(this);
                View v = LayoutInflater.from(this).inflate(getMainViewLayoutId(), mSwipeToRefresh, false);
                mSwipeToRefresh.addView(v, new SwipeRefreshLayout.LayoutParams(
                                SwipeRefreshLayout.LayoutParams.FILL_PARENT,
                                SwipeRefreshLayout.LayoutParams.FILL_PARENT
                        )
                );

                SwipeRefreshLayout.OnRefreshListener listener = getOnRefreshListener();
                if (listener != null) {
                    mSwipeToRefresh.setOnRefreshListener(listener);
                }
                lv = mSwipeToRefresh;
            } else {
                lv = LayoutInflater.from(this).inflate(getMainViewLayoutId(), lframe, false);
            }
        }
        if (lv != null) {
            lframe.addView(lv, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
        }

        root.addView(lframe, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));

        mMainContainer = lframe;
        // ------------------------------------------------------------------

        root.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));

        setContentView(root);
        if (supportSwipeToRefresh()) {
            mSwipeToRefresh.setColorSchemeResources(getSwipeToRefreshColorSchemeResource1(), getSwipeToRefreshColorSchemeResource2(), getSwipeToRefreshColorSchemeResource3(), getSwipeToRefreshColorSchemeResource4());
        }
    }

    public SwipeRefreshLayout getSwipeToRefresh() {
        return mSwipeToRefresh;
    }


    protected int getSwipeToRefreshColorSchemeResource1() {
        return R.color.schema1;
    }

    protected int getSwipeToRefreshColorSchemeResource2() {
        return R.color.schema2;
    }

    protected int getSwipeToRefreshColorSchemeResource3() {
        return R.color.schema3;
    }

    protected int getSwipeToRefreshColorSchemeResource4() {
        return R.color.schema4;
    }


    protected boolean supportSwipeToRefresh() {
        return false;
    }

    protected SwipeRefreshLayout.OnRefreshListener getOnRefreshListener() {
        return null;
    }

    public View getMainView() {
        return mMainContainer;
    }


    protected void showMainView(boolean animate) {

        setViewShown(INTERNAL_VIEW_TYPE.MAIN, animate);
    }

    protected void showErrorView(boolean animate) {

        setViewShown(INTERNAL_VIEW_TYPE.ERROR, animate);
    }

    protected void showLoadingView(boolean animate) {

        setViewShown(INTERNAL_VIEW_TYPE.LOADING, animate);
    }

    protected View getEmptyView() {
        return null;
    }

    /**
     * Control whether the list is being displayed. You can make it not
     * displayed if you are waiting for the initial data to show in it. During
     * this time an indeterminant progress indicator will be shown instead.
     * If true, the list view is shown; if false, the progress
     * indicator. The initial value is true.
     *
     * @param animate If true, an animation will be used to transition to the new
     *                state.
     */
    private void setViewShown(INTERNAL_VIEW_TYPE type, boolean animate) {
        if (mProgressContainer == null) {
            throw new IllegalStateException(
                    "Can't be used with a custom content view");
        }
        if (mErrorView == null) {
            throw new IllegalStateException(
                    "Can't be used with a custom content view");
        }
        if (currentVisibileView == type)
            return;
        INTERNAL_VIEW_TYPE previous = currentVisibileView;
        currentVisibileView = type;

        if (animate) {
            switch (type) {
                case ERROR:
                    if (previous == INTERNAL_VIEW_TYPE.LOADING) {
                        mProgressContainer.startAnimation(AnimationUtils
                                .loadAnimation(this,
                                        android.R.anim.fade_out));
                    } else if (previous == INTERNAL_VIEW_TYPE.MAIN) {
                        mMainContainer.startAnimation(AnimationUtils
                                .loadAnimation(this,
                                        android.R.anim.fade_out));
                    } else {
                        if (mEmptyView != null) {
                            mEmptyView.startAnimation(AnimationUtils
                                    .loadAnimation(this,
                                            android.R.anim.fade_out));
                        }
                    }
                    mErrorView.startAnimation(AnimationUtils.loadAnimation(
                            this, android.R.anim.fade_in));
                    break;
                case MAIN:
                    if (previous == INTERNAL_VIEW_TYPE.LOADING) {
                        mProgressContainer.startAnimation(AnimationUtils
                                .loadAnimation(this,
                                        android.R.anim.fade_out));
                    } else if (previous == INTERNAL_VIEW_TYPE.ERROR) {
                        mErrorView.startAnimation(AnimationUtils.loadAnimation(
                                this, android.R.anim.fade_out));
                    } else {
                        if (mEmptyView != null) {
                            mEmptyView.startAnimation(AnimationUtils.loadAnimation(
                                    this, android.R.anim.fade_out));
                        }
                    }
                    mMainContainer.startAnimation(AnimationUtils.loadAnimation(
                            this, android.R.anim.fade_in));

                    break;
                case LOADING:
                    if (previous == INTERNAL_VIEW_TYPE.MAIN) {
                        mMainContainer.startAnimation(AnimationUtils
                                .loadAnimation(this,
                                        android.R.anim.fade_out));
                    } else if (previous == INTERNAL_VIEW_TYPE.ERROR) {
                        mErrorView.startAnimation(AnimationUtils.loadAnimation(
                                this, android.R.anim.fade_out));
                    } else {
                        if (mEmptyView != null) {
                            mEmptyView.startAnimation(AnimationUtils.loadAnimation(
                                    this, android.R.anim.fade_out));
                        }
                    }
                    mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                            this, android.R.anim.fade_in));

                    break;
                case EMPTY:
                    if (previous == INTERNAL_VIEW_TYPE.MAIN) {
                        mMainContainer.startAnimation(AnimationUtils
                                .loadAnimation(this,
                                        android.R.anim.fade_out));
                    } else if (previous == INTERNAL_VIEW_TYPE.LOADING) {
                        mProgressContainer.startAnimation(AnimationUtils
                                .loadAnimation(this,
                                        android.R.anim.fade_out));
                    } else {
                        mErrorView.startAnimation(AnimationUtils.loadAnimation(
                                this, android.R.anim.fade_out));
                    }
                    mEmptyView.startAnimation(AnimationUtils.loadAnimation(
                            this, android.R.anim.fade_in));
                    break;
            }

        } else {
            mErrorView.clearAnimation();
            mProgressContainer.clearAnimation();
            mMainContainer.clearAnimation();
            if (mEmptyView != null) {
                mEmptyView.clearAnimation();
            }
        }

        switch (type) {
            case MAIN: {
                mErrorView.setVisibility(View.GONE);
                mProgressContainer.setVisibility(View.GONE);
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.GONE);
                }
                mMainContainer.setVisibility(View.VISIBLE);
            }
            break;

            case LOADING: {
                mErrorView.setVisibility(View.GONE);
                mMainContainer.setVisibility(View.GONE);
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.GONE);
                }
                mProgressContainer.setVisibility(View.VISIBLE);
            }
            break;
            case ERROR: {
                mMainContainer.setVisibility(View.GONE);
                mProgressContainer.setVisibility(View.GONE);
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.GONE);
                }
                mErrorView.setVisibility(View.VISIBLE);
            }
            case EMPTY: {
                mMainContainer.setVisibility(View.GONE);
                mProgressContainer.setVisibility(View.GONE);
                mErrorView.setVisibility(View.GONE);
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.VISIBLE);
                }
            }
            break;

        }
    }

}
