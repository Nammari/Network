package nammari.network.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by nammari on 8/12/14.
 */
public abstract class CustomLoaderImp<T> extends AsyncTaskLoader<T> {
    public CustomLoaderImp(Context context, T cashedData) {
        super(context);
        this.data = cashedData;
    }

    protected T data;

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (data != null) {
            deliverResult(data);
        }

        if (takeContentChanged() || data == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        // clear data
        data = null;
        // Note that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.

    }

    @Override
    public void deliverResult(T data) {
        if (isReset()) {
            return;
        }
        // intercept the result so we can put it in the cache .
        this.data = data;
        if (isStarted()) {
            // IF the loader is currently started , we can immediately
            // deliver its result.
            super.deliverResult(data);
        }
    }

}
