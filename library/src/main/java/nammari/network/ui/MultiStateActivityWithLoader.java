package nammari.network.ui;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.util.SparseIntArray;

import nammari.network.util.LoaderErrorAwareHelper;

/**
 * Created by nammari on 11/29/14.
 */
public abstract class MultiStateActivityWithLoader extends MultiStateActivity implements LoaderErrorAwareHelper.LoaderErrorAwareUI {




    private SparseIntArray loadersStatus;

    protected final void restLoadersState() {
        loadersStatus = LoaderErrorAwareHelper.createNewLoaderStatusArray(this);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        return getSupportLoaderManager();
    }


    @Override
    public boolean isFragmentResumed() {
        return true;
    }
}
