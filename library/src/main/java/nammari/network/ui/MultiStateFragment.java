package nammari.network.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;

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
import nammari.network.ui.widget.CustomErrorView;
import nammari.network.util.StringUtils;


/**
 * Created by nammari on 8/12/14.
 */
public abstract class MultiStateFragment extends Fragment {


    static final int INTERNAL_PROGRESS_CONTAINER_ID = 0x00ff0001;
    static final int INTERNAL_MAIN_CONTAINER_ID = 0x00ff0002;
    static final int INTERNAL_ERROR_CONTAINER_ID = 0x00ff0003;
    static final int INTERNAL_EMPTY_LAYOUT_ID = 0x00ff0004;

    public enum INTERNAL_VIEW_TYPE {
        MAIN, LOADING, ERROR, EMPTY
    }

    protected abstract int getMainViewLayoutId();


    TextView mStandardEmptyView;
    View mProgressContainer;
    View mMainContainer;
    View mErrorView;
    View mEmptyView;
    TextView mErrorText;
    SwipeRefreshLayout mSwipeToRefresh;

    INTERNAL_VIEW_TYPE currentVisibleView = INTERNAL_VIEW_TYPE.MAIN;

    public MultiStateFragment() {
    }

    protected abstract void onErrorRetry();

    protected void setErrorText(CharSequence text) {
        if (mErrorText == null)
            return;
        if (!StringUtils.isBlank((String) text)) {
            mErrorText.setText(text);
        } else {
            mErrorText.setText(R.string.nammarinetwork__general_error);
        }

    }

    /**
     * Provide default implementation to return a simple list view. Subclasses
     * can override to replace with their own layout. If doing so, the returned
     * view hierarchy <em>must</em> have a ListView whose id is
     * {@link android.R.id#list android.R.id.list} and can optionally have a
     * sibling view id {@link android.R.id#empty android.R.id.empty} that is to
     * be shown when the list is empty.
     * If you are overriding this method with your own custom content, consider
     * including the standard layout {@link android.R.layout#list_content} in
     * your layout file, so that you continue to retain all of the standard
     * behavior of ListFragment. In particular, this is currently the only way
     * to have the built-in indeterminant progress state be shown.
     */
    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Context context = getActivity();

        FrameLayout root = new FrameLayout(context);
        //------------------------EMPTY  container
        View emptyView = getEmptyView(context);
        if (emptyView != null) {
            emptyView.setId(INTERNAL_EMPTY_LAYOUT_ID);
            emptyView.setVisibility(View.GONE);
            root.addView(emptyView, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mEmptyView = emptyView;
        }

        // ------------------------------Error
        // container------------------------------------
        CustomErrorView customErrorView = getCustomErrorView(context);
        if (customErrorView == null) {
            LinearLayout eframe = new LinearLayout(context);
            eframe.setId(INTERNAL_ERROR_CONTAINER_ID);
            eframe.setOrientation(LinearLayout.VERTICAL);
            eframe.setGravity(Gravity.CENTER);
            eframe.setVisibility(View.GONE);
            ImageView error_image = new ImageView(context);
            error_image.setImageResource(R.drawable.nammarinetwork__alert_error);
            final float scale = getResources().getDisplayMetrics().density;

            eframe.addView(error_image, new FrameLayout.LayoutParams(
                    (int) (scale * 75), (int) (scale * 75)));
            mErrorText = new TextView(context);
            mErrorText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
            mErrorText.setText(R.string.nammarinetwork__general_error);
            mErrorText.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams errorTextLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            // errorTextLayoutParams.weight = 1.0f;

            eframe.addView(mErrorText, errorTextLayoutParams);

            Button retry = new Button(context);
            retry.setText(R.string.nammarinetwork__retry);
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
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mErrorView = eframe;
        } else {
            customErrorView.getCustomErrorView().setId(INTERNAL_ERROR_CONTAINER_ID);
            customErrorView.getCustomErrorView().setVisibility(View.GONE);
            mErrorView = customErrorView.getCustomErrorView();
            mErrorText = (TextView) customErrorView.getCustomErrorView().findViewById(android.R.id.text1);
            root.addView(customErrorView.getCustomErrorView(), new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            if (customErrorView.getCustomRetryView() != null) {
                customErrorView.getCustomRetryView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onErrorRetry();
                    }
                });
            }
        }

        // ------------------------------Progress
        // container------------------------------------

        View customProgressLayout = getCustomProgressView(context);
        if (customProgressLayout == null) {
            LinearLayout pframe = new LinearLayout(context);
            pframe.setId(INTERNAL_PROGRESS_CONTAINER_ID);
            pframe.setOrientation(LinearLayout.VERTICAL);
            pframe.setVisibility(View.GONE);
            pframe.setGravity(Gravity.CENTER);

            ProgressBar progress = new ProgressBar(context, null,
                    android.R.attr.progressBarStyle);
            pframe.addView(progress, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            mProgressContainer = pframe;
            root.addView(pframe, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            customProgressLayout.setId(INTERNAL_PROGRESS_CONTAINER_ID);
            customProgressLayout.setVisibility(View.GONE);
            mProgressContainer = customProgressLayout;
            root.addView(customProgressLayout, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }


        //Main container

        FrameLayout lframe = new FrameLayout(context);
        lframe.setId(INTERNAL_MAIN_CONTAINER_ID);

        int layoutId = getMainViewLayoutId();
        View lv = null;

        if (layoutId != -1) {


            if (supportSwipeToRefresh()) {
                mSwipeToRefresh = new SwipeRefreshLayout(context);
                View v = inflater.inflate(getMainViewLayoutId(), mSwipeToRefresh, false);
                mSwipeToRefresh.addView(v, new SwipeRefreshLayout.LayoutParams(
                                SwipeRefreshLayout.LayoutParams.MATCH_PARENT,
                                SwipeRefreshLayout.LayoutParams.MATCH_PARENT
                        )
                );

                SwipeRefreshLayout.OnRefreshListener listener = getOnRefreshListener();
                if (listener != null) {
                    mSwipeToRefresh.setOnRefreshListener(listener);
                }
                lv = mSwipeToRefresh;
            } else {
                lv = inflater.inflate(getMainViewLayoutId(), lframe, false);
            }
        }

        lframe.addView(lv, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        root.addView(lframe, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mMainContainer = lframe;
        // ------------------------------------------------------------------

        root.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        return root;
    }

    public SwipeRefreshLayout getSwipeToRefresh() {
        return mSwipeToRefresh;
    }

    /**
     * Attach to list view once the view hierarchy has been created.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (supportSwipeToRefresh())
            mSwipeToRefresh.setColorSchemeResources(getSwipeToRefreshColorSchemeResource1(), getSwipeToRefreshColorSchemeResource2(), getSwipeToRefreshColorSchemeResource3(), getSwipeToRefreshColorSchemeResource4());
        super.onViewCreated(view, savedInstanceState);

    }

    protected int getSwipeToRefreshColorSchemeResource1() {
        return R.color.nammarinetwork__schema1;
    }

    protected int getSwipeToRefreshColorSchemeResource2() {
        return R.color.nammarinetwork__schema2;
    }

    protected int getSwipeToRefreshColorSchemeResource3() {
        return R.color.nammarinetwork__schema3;
    }

    protected int getSwipeToRefreshColorSchemeResource4() {
        return R.color.nammarinetwork__schema4;
    }

    protected View getEmptyView(Context context) {
        return null;
    }

    /**
     * Detach from list view.
     */
    @Override
    public void onDestroyView() {


        mErrorView = null;
        mEmptyView = null;
        mProgressContainer = null;
        mMainContainer = null;
        mErrorText = null;
        mStandardEmptyView = null;
        mSwipeToRefresh = null;
        super.onDestroyView();
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


    protected void showEmptyView(boolean animate) {
        setViewShown(INTERNAL_VIEW_TYPE.EMPTY, animate);
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
        if (currentVisibleView == type)
            return;
        INTERNAL_VIEW_TYPE previous = currentVisibleView;
        currentVisibleView = type;

        if (animate) {
            switch (type) {
                case ERROR: {
                    if (previous == INTERNAL_VIEW_TYPE.LOADING) {
                        mProgressContainer.startAnimation(AnimationUtils
                                .loadAnimation(getActivity(),
                                        android.R.anim.fade_out));
                    } else if (previous == INTERNAL_VIEW_TYPE.MAIN) {
                        mMainContainer.startAnimation(AnimationUtils
                                .loadAnimation(getActivity(),
                                        android.R.anim.fade_out));
                    } else {
                        if (mEmptyView != null) {
                            mEmptyView.startAnimation(AnimationUtils
                                    .loadAnimation(getActivity(),
                                            android.R.anim.fade_out));
                        }
                    }
                    mErrorView.startAnimation(AnimationUtils.loadAnimation(
                            getActivity(), android.R.anim.fade_in));
                }
                break;
                case MAIN: {
                    if (previous == INTERNAL_VIEW_TYPE.LOADING) {
                        mProgressContainer.startAnimation(AnimationUtils
                                .loadAnimation(getActivity(),
                                        android.R.anim.fade_out));
                    } else if (previous == INTERNAL_VIEW_TYPE.ERROR) {
                        mErrorView.startAnimation(AnimationUtils.loadAnimation(
                                getActivity(), android.R.anim.fade_out));
                    } else {
                        if (mEmptyView != null) {
                            mEmptyView.startAnimation(AnimationUtils.loadAnimation(
                                    getActivity(), android.R.anim.fade_out));
                        }
                    }
                    mMainContainer.startAnimation(AnimationUtils.loadAnimation(
                            getActivity(), android.R.anim.fade_in));
                }
                break;
                case LOADING: {
                    if (previous == INTERNAL_VIEW_TYPE.MAIN) {
                        mMainContainer.startAnimation(AnimationUtils
                                .loadAnimation(getActivity(),
                                        android.R.anim.fade_out));
                    } else if (previous == INTERNAL_VIEW_TYPE.ERROR) {
                        mErrorView.startAnimation(AnimationUtils.loadAnimation(
                                getActivity(), android.R.anim.fade_out));
                    } else {
                        if (mEmptyView != null) {
                            mEmptyView.startAnimation(AnimationUtils.loadAnimation(
                                    getActivity(), android.R.anim.fade_out));
                        }
                    }
                    mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                            getActivity(), android.R.anim.fade_in));
                }
                break;
                case EMPTY: {
                    if (previous == INTERNAL_VIEW_TYPE.MAIN) {
                        mMainContainer.startAnimation(AnimationUtils
                                .loadAnimation(getActivity(),
                                        android.R.anim.fade_out));
                    } else if (previous == INTERNAL_VIEW_TYPE.LOADING) {
                        mProgressContainer.startAnimation(AnimationUtils
                                .loadAnimation(getActivity(),
                                        android.R.anim.fade_out));
                    } else {
                        mErrorView.startAnimation(AnimationUtils.loadAnimation(
                                getActivity(), android.R.anim.fade_out));
                    }
                    mEmptyView.startAnimation(AnimationUtils.loadAnimation(
                            getActivity(), android.R.anim.fade_in));
                }
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
            break;
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


    protected View getCustomProgressView(Context context) {
        return null;
    }


    protected CustomErrorView getCustomErrorView(Context context) {
        return null;
    }


}
