package nammari.network.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;

import nammari.network.util.AppUtil;

/**
 * Created by nammari on 8/12/14.
 */
public abstract class RefreshableEndlessMultiStateAbsListFragment<T> extends EndlessMultiStateAbsListFragment<T> {

    private static final String PREF_NAME = "network_lib";
    private static final String BACKGROUND_FLAG = "from_bk";

    protected RefreshableEndlessMultiStateAbsListFragment() {
    }

    @Override
    protected boolean supportSwipeToRefreshLayout() {
        return true;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        SwipeRefreshLayout swipeRefreshLayout1 = getSwipeRefreshLayout();
        if (swipeRefreshLayout1 == null) {
            return;
        }
//        swipeRefreshLayout1.setRefreshing(isLoading());
        setRefreshForSwipeRefreshLayout(isLoading());
    }

    public abstract static class GridRefreshableEndlessMultiStateFragment<T> extends RefreshableEndlessMultiStateAbsListFragment<T> {


        protected GridRefreshableEndlessMultiStateFragment() {
        }

        @Override
        protected final LIST_TYPE getListType() {
            return LIST_TYPE.GRID_VIEW;
        }
    }


    public abstract static class ListRefreshableEndlessMultiStateFragment<T> extends RefreshableEndlessMultiStateAbsListFragment<T> {

        protected ListRefreshableEndlessMultiStateFragment() {
        }

        @Override
        protected final LIST_TYPE getListType() {
            return LIST_TYPE.LIST_VIEW;
        }
    }

    public abstract static class StaggeredGridRefreshableEndlessMultiStateFragment<T> extends RefreshableEndlessMultiStateAbsListFragment<T> {

        protected StaggeredGridRefreshableEndlessMultiStateFragment() {
        }

        @Override
        protected final LIST_TYPE getListType() {
            return LIST_TYPE.STAGGERED_GRID;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getActivity().getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(BACKGROUND_FLAG, false)) {
            setRefreshForSwipeRefreshLayout(true);
            refresh(true);
        }
    }

    @Override
    public void onStop() {
        SharedPreferences prefs = getActivity().getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(BACKGROUND_FLAG,
                AppUtil.isApplicationSentToBackground(getActivity())).apply();
        super.onStop();
    }


}
