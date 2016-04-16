package nammari.network.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import nammari.network.R;
import nammari.network.loader.EndlessNetworkLoader;
import nammari.network.loader.ErrorAwareLoader;
import nammari.network.logger.Logger;
import nammari.network.ui.widget.CustomErrorView;
import nammari.network.ui.widget.EndlessCustomErrorView;
import nammari.network.ui.widget.ItemDecorationAlbumColumns;
import nammari.network.ui.widget.PaddingItemDecoration;
import nammari.network.util.LoaderErrorAwareHelper;

/**
 * Created by nammari on 8/12/14.
 */
public abstract class EndlessMultiStateAbsListFragment<T> extends
        MultiStateAbsFragmentWithLoader implements LoaderCallbacks<T>,
        OnClickListener {

    // to update ui
    protected Handler mHandler;
    // wrapper adapter
    private NetworkWrapperAdapter adapter;


    // /**
    // * Some times we can't fire the loaded in the onActivityCreated method for
    // * example in this project SearchResultFragment can't have the query
    // * parameter when creating the activity it will be provided in
    // * onHandleSearch ( after ) the fragment created
    // *
    // */
    // protected boolean fireLoaderInOnActivityCreated = true;

    public EndlessMultiStateAbsListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setRetainInstance(true);
        // http://stackoverflow.com/questions/10456077/nullpointerexception-in-fragmentmanager

        if (getListType() == LIST_TYPE.GRID_VIEW) {
            setHasOptionsMenu(true);
        }

    }

    public static abstract class GridEndlessMultiStateFragment<T> extends EndlessMultiStateAbsListFragment<T> {
        public GridEndlessMultiStateFragment() {

        }

        @Override
        protected LIST_TYPE getListType() {

            return LIST_TYPE.GRID_VIEW;
        }
    }

    public static abstract class ListEndlessMultiStateFragment<T> extends EndlessMultiStateAbsListFragment<T> {
        public ListEndlessMultiStateFragment() {

        }

        @Override
        protected LIST_TYPE getListType() {

            return LIST_TYPE.LIST_VIEW;
        }
    }

    public static abstract class StaggeredGridEndlessMultiStateFragment<T> extends EndlessMultiStateAbsListFragment<T> {
        public StaggeredGridEndlessMultiStateFragment() {

        }

        @Override
        protected LIST_TYPE getListType() {

            return LIST_TYPE.STAGGERED_GRID;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (supportSwipeToRefreshLayout() && getSwipeRefreshLayout() != null) {
            setRefreshForSwipeRefreshLayout(isLoading());
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandler = new Handler();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        RecyclerView list = getRecyclerView();
        if (includeItemDecoration()) {
            int paddingBetweenItems = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2.0f, getResources().getDisplayMetrics());
            if (getListType() == LIST_TYPE.GRID_VIEW || getListType() == LIST_TYPE.STAGGERED_GRID) {
                list.addItemDecoration(new ItemDecorationAlbumColumns(paddingBetweenItems, getGridSpan()));
            } else {
                list.addItemDecoration(new PaddingItemDecoration(paddingBetweenItems, 0));
            }
        }
        if (supportEndless()) {
            list.addOnScrollListener(new EndlessRecyclerViewScrollListener());
        }
    }


    public class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {


        public EndlessRecyclerViewScrollListener() {
            if (getListType() == LIST_TYPE.STAGGERED_GRID) {
                visibleThreshold = visibleThreshold * getStaggeredSpanCount();
            }
        }

        private int visibleThreshold = 5;

        public int getLastVisibleItemPosition(int[] lastVisibleItemPositions) {
            int maxPosition = 0;
            for (int i = 0; i < lastVisibleItemPositions.length; i++) {
                if (i == 0) {
                    maxPosition = lastVisibleItemPositions[i];
                } else if (lastVisibleItemPositions[i] > maxPosition) {
                    maxPosition = lastVisibleItemPositions[i];
                }
            }
            return maxPosition;
        }


        public int getFirstVisibleItemPosition(int[] firstVisibleItemPositions) {

            int minPosition = 0;
            for (int i = 0; i < firstVisibleItemPositions.length; i++) {
                if (i == 0) {
                    minPosition = firstVisibleItemPositions[i];
                } else if (firstVisibleItemPositions[i] < minPosition) {
                    minPosition = firstVisibleItemPositions[i];
                }
            }
            return minPosition;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            boolean scrollReachedTarget;
            if (getListType() == LIST_TYPE.STAGGERED_GRID) {
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                int[] lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null);
                int[] firstVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null);
                int lastVisibleItemPosition = getLastVisibleItemPosition(lastVisibleItemPositions);
                int firstVisibleITemPosition = getFirstVisibleItemPosition(firstVisibleItemPositions);
                int visibleItemCount = lastVisibleItemPosition - firstVisibleITemPosition;
                int totalItemCount = layoutManager.getItemCount();
                scrollReachedTarget = firstVisibleITemPosition + visibleItemCount + visibleThreshold >= totalItemCount;
            } else {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.findLastVisibleItemPosition() - layoutManager.findFirstVisibleItemPosition();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                scrollReachedTarget = firstVisibleItem + visibleItemCount + visibleThreshold >= totalItemCount;
            }


            if (!isLoading() && !hasError() && hasMoreResults()
                    && scrollReachedTarget) {
                if (isAdded()) {
                    final EndlessNetworkLoader<?> loader = (EndlessNetworkLoader) getLoaderManager().getLoader(
                            getEndlessNetworkLoaderId());
                    loader.setUseMax(true);
                }
                loadMore();
            }

            if (supportSwipeToRefreshLayout()) {

                int topRowVerticalPosition =
                        (recyclerView.getChildCount() == 0) ?
                                0 : recyclerView.getChildAt(0).getTop();
                getSwipeRefreshLayout().setEnabled(topRowVerticalPosition >= 0);
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

    }

    protected boolean includeItemDecoration() {
        return true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (supportEndless()) {
            adapter = new NetworkWrapperAdapter(getMainAdapter(getActivity()),
                    getActivity());
            setListAdapter(adapter);

            Logger.logInfo("from onActivityCreated", "from onActivityCreate");
            showLoadingView(true);
            getLoaderManager().initLoader(getEndlessNetworkLoaderId(), null, this);
        }
    }

    protected EndlessCustomErrorView getAdapterErrorLoadingView(Context context) {
        return null;
    }

    protected class NetworkWrapperAdapter extends RecyclerView.Adapter {

        private static final int VIEW_TYPE_LOADING = 22;
        private final MainNetworkAdapter<T> mainAdapter;
        private final LayoutInflater inflater;

        public NetworkWrapperAdapter(MainNetworkAdapter<T> mainAdapter,
                                     Context context) {
            super();
            this.mainAdapter = mainAdapter;
            this.inflater = LayoutInflater.from(context);
        }


        @Override
        public int getItemCount() {
            return mainAdapter.getItemCount()
                    + (
                    supportEndless()
                            ?
                            (

                                    (
                                            (
                                                    isLoading() && mainAdapter.getItemCount() == 0
                                            )// ...this
                                                    // is
                                                    // the first
                                                    // load
                                                    || hasMoreResults() // ...or there's another
                                                    // page
                                                    || hasError() // ...or there's an error
                                                    ? 1 : 0
                                    )
                            )
                            : 0);
        }


        @Override
        public long getItemId(int position) {
            return getItemViewType(position) == VIEW_TYPE_LOADING ? -1
                    : mainAdapter.getItemId(position);
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_LOADING) {
                EndlessCustomErrorView customErrorView = getAdapterErrorLoadingView(parent.getContext());
                if (customErrorView == null) {
                    return new ListLoadingViewHolder(inflater.inflate(R.layout.nammarinetwork__list_endless_loading_view, parent, false));
                } else {
                    return new ListLoadingViewHolder(customErrorView.getRoot(), customErrorView.getLoadingContainer(), customErrorView.getRetryButton(), customErrorView.getErrroContainer());
                }

            } else {
                return mainAdapter.onCreateViewHolder(parent, viewType);
            }
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == VIEW_TYPE_LOADING) {
                ListLoadingViewHolder holder1 = (ListLoadingViewHolder) holder;
                if (hasError() && !isLoading()) {
                    // show error
                    holder1.loading_container
                            .setVisibility(View.GONE);
                    holder1.error_container.setVisibility(View.VISIBLE);
                    holder1.button1
                            .setOnClickListener(EndlessMultiStateAbsListFragment.this);
                } else {
                    // show loading
                    holder1.error_container
                            .setVisibility(View.GONE);
                    holder1.loading_container.setVisibility(View.VISIBLE);
                }
            } else {

                mainAdapter.onBindViewHolder(holder, position);
            }

        }


        public void addData(T data) {

            if (mainAdapter != null) {
                mainAdapter.deliverNewResult(data);
            }
        }


        @Override
        public int getItemViewType(int position) {

            if (mainAdapter.getItemCount() == 0
                    || position >= mainAdapter.getItemCount()) {
                return VIEW_TYPE_LOADING;
            } else {
                return mainAdapter.getItemViewType(position);
            }

        }


        public MainNetworkAdapter<? extends T> getMainAdapter() {
            return mainAdapter;
        }

    }


    static class ListLoadingViewHolder extends RecyclerView.ViewHolder {
        View loading_container;
        View button1;
        View error_container;

        public ListLoadingViewHolder(View itemView) {
            super(itemView);
            loading_container = itemView.findViewById(R.id.nammarinetwork__loading_container);
            button1 = itemView.findViewById(R.id.nammarinetwork__button1);
            error_container = itemView.findViewById(R.id.nammarinetwork__error_container);
        }

        public ListLoadingViewHolder(View itemView, View loadingContainer, View retryButton, View errorView) {
            super(itemView);
            loading_container = loadingContainer;
            button1 = retryButton;
            error_container = errorView;
        }

    }


    @Override
    public void onLoadFinished(Loader<T> arg0, T arg1) {


        if (arg0.getId() == getEndlessNetworkLoaderId()) {
            if (adapter != null) {
                adapter.addData(arg1);
                adapter.notifyDataSetChanged();
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        ErrorAwareLoader __loader = (ErrorAwareLoader) arg0;
        Logger.logDebug("hasError", "" + __loader.containsError());
        LoaderErrorAwareHelper.updateSingleLoaderStatus(this,
                __loader);
        refreshActionbar();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                setRefreshForSwipeRefreshLayout(false);

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nammarinetwork__button1) {
            if (!isLoading()) {
                loadMore();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }

                    }
                });

            }
        }
    }

    /**
     * @author nammari
     *         This class used to force users of this class to use it as base
     *         class for their main adapter such that when the loader finishes
     *         his task the loaded data can be assigned to the main adapter
     */
    public static abstract class MainNetworkAdapter<T> extends RecyclerView.Adapter {
        protected LayoutInflater inflater;
        protected Context mContext;

        public MainNetworkAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
            this.mContext = context;
        }

        public abstract void deliverNewResult(T data);

        public abstract void clearData();
    }

    // get the id for the network loader
    protected abstract int getEndlessNetworkLoaderId();

    // get the main adapter that contains your custom views
    protected abstract MainNetworkAdapter<T> getMainAdapter(Context context);

    // loader access
    @SuppressWarnings("unchecked")
    protected boolean isLoading() {

        if (isAdded()) {
            final Loader<?> loader = getLoaderManager().getLoader(
                    getEndlessNetworkLoaderId());

            if (loader != null) {
                return ((EndlessNetworkLoader<T>) loader).isLoading();
            }
        }
        return true;
    }

    protected boolean isLoaderStatusUndefined() {
        if (isAdded()) {
            try {
                int status = loadersStatus.get(getEndlessNetworkLoaderId());
                return status == LoaderErrorAwareHelper.STATUS_UNDEFINED;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected boolean hasError() {
        if (isAdded()) {
            final Loader<?> loader = getLoaderManager().getLoader(
                    getEndlessNetworkLoaderId());

            if (loader != null) {
                return ((EndlessNetworkLoader<T>) loader).hasError();
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected boolean hasMoreResults() {
        if (isAdded()) {
            final Loader<?> loader = getLoaderManager().getLoader(
                    getEndlessNetworkLoaderId());

            if (loader != null) {
                return ((EndlessNetworkLoader<T>) loader).hasMoreResults();
            }
        }
        return true;
    }

    protected void loadMore() {

        if (isAdded()) {
            final Loader<?> loader = getLoaderManager().getLoader(
                    getEndlessNetworkLoaderId());
            if (loader != null) {
                loader.forceLoad();
            }
        }

        refreshActionbar();

    }

    @SuppressWarnings("unchecked")
    protected void refresh(boolean forceRefresh) {
        if (!supportEndless()) {
            return;
        }
        if (isLoading() && !forceRefresh) {
            return;
        }
        if (isAdded()) {
            EndlessNetworkLoader<?> loader = (EndlessNetworkLoader) getLoaderManager().getLoader(
                    getEndlessNetworkLoaderId());
            loader.init();
            loader.refresh();
            loader.setUseMax(false);
        }

        loadMore();
    }


    private final Runnable mForceRefresh = new Runnable() {
        @Override
        public void run() {
            refresh(true);
        }
    };

    protected final void refreshActionbar() {
        mHandler.post(mRefreshActionbarUI);
    }

    private final Runnable mRefreshActionbarUI = new Runnable() {

        @Override
        public void run() {
            if (getActivity() != null) {
                (getActivity())
                        .supportInvalidateOptionsMenu();
            }

        }
    };

    public NetworkWrapperAdapter getAdapter() {
        return adapter;
    }


    @Override
    public void showErrorUI(boolean animate) {
        RecyclerView.Adapter adapter = supportEndless() ? getAdapter().getMainAdapter() : getRecyclerView().getAdapter();
        if (adapter.getItemCount() > 1) {
            // show the list
            showRecylcerView(isResumed());
            return;
        }
        super.showErrorUI(animate);
    }

    protected boolean supportEndless() {
        return true;
    }

    @Override
    protected void onSwipeRefresh() {
        super.onSwipeRefresh();
        mHandler.post(mForceRefresh);
        RecyclerView.Adapter adapter = supportEndless() ? getAdapter() : getRecyclerView().getAdapter();
        if (adapter.getItemCount() > 1) {
            showMainUI(true);
        }
    }

    @Override
    protected final GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
        if (!supportEndless()) {
            return super.getSpanSizeLookup();
        }
        return new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return ((adapter.getItemViewType(position) == NetworkWrapperAdapter.VIEW_TYPE_LOADING) ? getGridSpan() : 1);
            }
        };
    }
}
