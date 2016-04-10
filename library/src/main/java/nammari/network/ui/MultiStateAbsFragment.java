package nammari.network.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import nammari.network.ui.widget.EmptyViewRecyclerView;
import nammari.network.ui.widget.ListSwipeRefreshLayout;
import nammari.network.util.StringUtils;

/**
 * support ( loading , listview,Error) views
 * Created by nammari on 8/12/14.
 */
public abstract class MultiStateAbsFragment extends Fragment {


    static final int INTERNAL_PROGRESS_CONTAINER_ID = 0x00ff0002;
    static final int INTERNAL_LIST_CONTAINER_ID = 0x00ff0003;
    static final int INTERNAL_ERROR_CONTAINER_ID = 0x00ff0004;
    static final int INTERNAL_REFRESH_LAYOUT_ID = 0x00ff0005;

    public enum INTERAL_VIEW_TYPE {
        LIST, LOADING, ERROR
    }

    ;

    public enum LIST_TYPE {
        LIST_VIEW, GRID_VIEW, STAGGERED_GRID
    }


    private SwipeRefreshLayout.OnRefreshListener swipeListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            onSwipeRefresh();
        }
    };

    protected void onSwipeRefresh() {

    }

    protected abstract LIST_TYPE getListType();

    final private Handler mHandler = new Handler();

    final private Runnable mRequestFocus = new Runnable() {
        public void run() {
            recyclerView.focusableViewAvailable(recyclerView);
        }
    };


    RecyclerView.Adapter mAdapter;
    EmptyViewRecyclerView recyclerView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    View mProgressContainer;
    View mAbsListContainer;
    View mErrorView;
    TextView mErrorText;
    INTERAL_VIEW_TYPE currentVisibleView = INTERAL_VIEW_TYPE.LIST;

    public MultiStateAbsFragment() {
    }

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


    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
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
     *
     * @return Return the View for the fragment's UI.
     */
    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Context context = getActivity();

        FrameLayout root = new FrameLayout(context);

        // ------------------------------Error
        // container------------------------------------

        LinearLayout eframe = new LinearLayout(context);
        eframe.setId(INTERNAL_ERROR_CONTAINER_ID);
        eframe.setOrientation(LinearLayout.VERTICAL);

        eframe.setGravity(Gravity.CENTER);
        eframe.setVisibility(View.GONE);
        ImageView error_image = new ImageView(context);
        error_image.setImageResource(R.drawable.alert_error);
        final float scale = getResources().getDisplayMetrics().density;

        eframe.addView(error_image, new FrameLayout.LayoutParams(
                (int) (scale * 75), (int) (scale * 75)));
        // ViewGroup.LayoutParams.WRAP_CONTENT,
        // ViewGroup.LayoutParams.WRAP_CONTENT));
        mErrorText = new TextView(context);
        mErrorText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        mErrorText.setText(R.string.no_connection);
        mErrorText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams errorTextLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        // errorTextLayoutParams.weight = 1.0f;

        eframe.addView(mErrorText, errorTextLayoutParams);

        Button retry = new Button(context);
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

        root.addView(pframe, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));

        // -----------------------------LIST
        // container-------------------------------------

        FrameLayout lframe = new FrameLayout(context);
        lframe.setId(INTERNAL_LIST_CONTAINER_ID);

        EmptyViewRecyclerView lv = new EmptyViewRecyclerView(getActivity());
        lv.setEmptyView(getEmptyView());
        switch (getListType()) {
            case LIST_VIEW:
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                lv.setLayoutManager(linearLayoutManager);
                break;
            case GRID_VIEW:
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), getGridSpan());
                if (getSpanSizeLookup() != null) {
                    gridLayoutManager.setSpanSizeLookup(getSpanSizeLookup());
                }
                lv.setLayoutManager(gridLayoutManager);
                break;
            case STAGGERED_GRID:
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(getStaggeredSpanCount(), getStaggeredLayoutManagerOrientation());
                lv.setLayoutManager(staggeredGridLayoutManager);
                break;

        }
        lv.setId(android.R.id.list);


        if (supportSwipeToRefreshLayout()) {
            swipeRefreshLayout = new ListSwipeRefreshLayout(getActivity());
            swipeRefreshLayout.addView(lv, new SwipeRefreshLayout.LayoutParams(
                            SwipeRefreshLayout.LayoutParams.FILL_PARENT,
                            SwipeRefreshLayout.LayoutParams.FILL_PARENT
                    )
            );
            swipeRefreshLayout.setId(INTERNAL_REFRESH_LAYOUT_ID);
            swipeRefreshLayout.setOnRefreshListener(swipeListener);
            lframe.addView(swipeRefreshLayout, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
        } else {
            lframe.addView(lv, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
        }
        root.addView(lframe, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));

        // ------------------------------------------------------------------

        root.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));

        return root;
    }

    protected int getGridSpan() {
        return 1;
    }

    protected int getStaggeredSpanCount() {
        return 1;
    }

    protected int getStaggeredLayoutManagerOrientation() {
        return StaggeredGridLayoutManager.VERTICAL;
    }

    protected GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
        return null;
    }


    protected boolean supportSwipeToRefreshLayout() {
        return false;
    }

    /**
     * Attach to list view once the view hierarchy has been created.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ensureAbsList();
        if (supportSwipeToRefreshLayout()) {
            swipeRefreshLayout.setColorSchemeResources(getSwipeToRefreshColorSchemeResource1(), getSwipeToRefreshColorSchemeResource2(), getSwipeToRefreshColorSchemeResource3(), getSwipeToRefreshColorSchemeResource4());

        }


    }

    /**
     * Detach from list view.
     */
    @Override
    public void onDestroyView() {
        mHandler.removeCallbacks(mRequestFocus);
        recyclerView = null;
        swipeRefreshLayout = null;
        mErrorView = mProgressContainer = mAbsListContainer = null;
        mErrorText = null;
        super.onDestroyView();
    }

    /**
     * * Provide the adapter for the recyclerview.
     *
     * @param adapter RecyclerView.Adapter
     */
    public void setListAdapter(RecyclerView.Adapter adapter) {
        boolean hadAdapter = mAdapter != null;
        mAdapter = adapter;
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
            if (!(currentVisibleView == INTERAL_VIEW_TYPE.LIST) && !hadAdapter) {
                // The list was hidden, and previously didn't have an
                // adapter. It is now time to show it.
                showRecylcerView(getView().getWindowToken() != null);
            }
        }
    }


    /**
     * @return recyclerview Get the activity's recyclerview  widget.
     */
    public RecyclerView getRecyclerView() {
        ensureAbsList();
        return recyclerView;
    }


    protected void showRecylcerView(boolean animate) {

        setViewShown(INTERAL_VIEW_TYPE.LIST, animate);
    }

    protected void showErrorView(boolean animate) {

        setViewShown(INTERAL_VIEW_TYPE.ERROR, animate);
    }

    protected void showLoadingView(boolean animate) {
        setViewShown(INTERAL_VIEW_TYPE.LOADING, animate);
    }

    /**
     * Control whether the list is being displayed. You can make it not
     * displayed if you are waiting for the initial data to show in it. During
     * this time an indeterminant progress indicator will be shown instead.
     * shown   If true, the list view is shown; if false, the progress
     * indicator. The initial value is true.
     *
     * @param animate If true, an animation will be used to transition to the new
     *                state.
     */
    private void setViewShown(INTERAL_VIEW_TYPE type, boolean animate) {
        ensureAbsList();
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
        INTERAL_VIEW_TYPE previous = currentVisibleView;
        currentVisibleView = type;

        if (animate) {
            switch (type) {
                case ERROR:
                    if (previous == INTERAL_VIEW_TYPE.LOADING) {
                        mProgressContainer.startAnimation(AnimationUtils
                                .loadAnimation(getActivity(),
                                        android.R.anim.fade_out));
                    } else {
                        mAbsListContainer.startAnimation(AnimationUtils
                                .loadAnimation(getActivity(),
                                        android.R.anim.fade_out));
                    }
                    mErrorView.startAnimation(AnimationUtils.loadAnimation(
                            getActivity(), android.R.anim.fade_in));
                    break;
                case LIST:
                    if (previous == INTERAL_VIEW_TYPE.LOADING) {
                        mProgressContainer.startAnimation(AnimationUtils
                                .loadAnimation(getActivity(),
                                        android.R.anim.fade_out));
                    } else {
                        mErrorView.startAnimation(AnimationUtils.loadAnimation(
                                getActivity(), android.R.anim.fade_out));
                    }
                    mAbsListContainer.startAnimation(AnimationUtils.loadAnimation(
                            getActivity(), android.R.anim.fade_in));

                    break;
                case LOADING:
                    if (previous == INTERAL_VIEW_TYPE.LIST) {
                        mAbsListContainer.startAnimation(AnimationUtils
                                .loadAnimation(getActivity(),
                                        android.R.anim.fade_out));
                    } else {
                        mErrorView.startAnimation(AnimationUtils.loadAnimation(
                                getActivity(), android.R.anim.fade_out));
                    }
                    mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                            getActivity(), android.R.anim.fade_in));
                    break;
            }

        } else {
            mErrorView.clearAnimation();
            mProgressContainer.clearAnimation();
            mAbsListContainer.clearAnimation();
        }

        switch (type) {
            case LIST:

                mAbsListContainer.setVisibility(View.VISIBLE);
                mErrorView.setVisibility(View.GONE);
                mProgressContainer.setVisibility(View.GONE);

                break;

            case LOADING:
                mErrorView.setVisibility(View.GONE);
                mAbsListContainer.setVisibility(View.GONE);
                mProgressContainer.setVisibility(View.VISIBLE);
                break;
            case ERROR:
                mAbsListContainer.setVisibility(View.GONE);
                mProgressContainer.setVisibility(View.GONE);
                mErrorView.setVisibility(View.VISIBLE);
                break;

        }
    }

    /**
     * * Get the ListAdapter associated with this activity's ListView.
     *
     * @return ListAdapter
     */
    public RecyclerView.Adapter getListAdapter() {
        return mAdapter;
    }

    private void ensureAbsList() {

        if (recyclerView != null) {
            return;
        }
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        if (root instanceof EmptyViewRecyclerView) {
            recyclerView = (EmptyViewRecyclerView) root;
        } else {
            mProgressContainer = root
                    .findViewById(INTERNAL_PROGRESS_CONTAINER_ID);
            mAbsListContainer = root.findViewById(INTERNAL_LIST_CONTAINER_ID);
            swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(INTERNAL_REFRESH_LAYOUT_ID);
            View rawListView = root.findViewById(android.R.id.list);
            recyclerView = (EmptyViewRecyclerView) rawListView;
        }
        if (mAdapter != null) {
            RecyclerView.Adapter adapter = mAdapter;
            mAdapter = null;
            setListAdapter(adapter);
        } else {
            // We are starting without an adapter, so assume we won't
            // have our data right away and start with the progress indicator.
            if (mProgressContainer != null) {

                showLoadingView(false);
            }
        }
        mHandler.post(mRequestFocus);
    }


    protected View getEmptyView() {
        return null;
    }

    public void setRefreshForSwipeRefreshLayout(boolean refreshing) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(refreshing);
    }
}
