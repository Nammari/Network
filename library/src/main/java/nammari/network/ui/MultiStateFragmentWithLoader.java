package nammari.network.ui;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.util.SparseIntArray;

import nammari.network.util.LoaderErrorAwareHelper;

/**
 * This class provide you with easy way to handle Error/Retry UI when using
 * Loaders.
 *
 * Loaders should implement ErrorAwareLoader interface .
 *
 *
 *
 * @author Nammari
 *
 */

/*
 * subclass shoud provide all it ErrorAwareLoader ids that involved in
 * Error/retry
 */

/**
 * Created by nammari on 8/12/14.
 */
public abstract class MultiStateFragmentWithLoader extends MultiStateFragment implements LoaderErrorAwareHelper.LoaderErrorAwareUI {


    protected MultiStateFragmentWithLoader() {
    }


    private SparseIntArray loadersStatus;

    protected final void restLoadersState() {
        loadersStatus = LoaderErrorAwareHelper.createNewLoaderStatusArray(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        restLoadersState();
        showLoadingUI(true);
    }

    @Override
    protected final void onErrorRetry() {

        LoaderErrorAwareHelper.handleRetryErrorLoader(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        fireLoader();
    }

    protected void fireLoader() {

    }

    @Override
    public final void showMainUI(boolean animate) {

        showMainView(animate);
    }

    @Override
    public void showErrorUI(boolean animate) {
        // try to get an error message first
        setErrorText(LoaderErrorAwareHelper.getErrorMessageFromLoaders(this));
        // show the error
        showErrorView(animate);
    }

    @Override
    public final void showLoadingUI(boolean animate) {
        showLoadingView(animate);
    }

    @Override
    public final SparseIntArray getLoadersStatus() {

        return loadersStatus;
    }


    @Override
    public final LoaderManager getCorrectLoaderManager() {
        return getLoaderManager();
    }


    @Override
    public final boolean isFragmentResumed() {
        return isResumed();
    }
}
