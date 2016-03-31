package nammari.network.ui;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.util.SparseIntArray;

import nammari.network.util.LoaderErrorAwareHelper;

/**
 * Created by nammari on 8/12/14.
 */
public abstract class MultiStateAbsFragmentWithLoader extends MultiStateAbsFragment implements LoaderErrorAwareHelper.LoaderErrorAwareUI {


    public MultiStateAbsFragmentWithLoader() {

    }

    protected SparseIntArray loadersStatus;

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
    public final void showMainUI(boolean animate) {

        showRecylcerView(animate);
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
