package nammari.network.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by nammari on 8/12/14.
 */
public abstract class EndlessNetworkLoader<T> extends AsyncTaskLoader<T>
        implements ErrorAwareLoader {
    public EndlessNetworkLoader(Context context, boolean isLoading,
                                boolean hasMore, boolean hasError) {
        super(context);
        this.isLoading = new AtomicBoolean();
        this.hasMore = new AtomicBoolean();
        this.hasError = new AtomicBoolean();
        this.useMax = new AtomicBoolean(true);
        init();
        this.isLoading.set(isLoading);
        this.hasError.set(hasError);
        this.hasMore.set(hasMore);
    }

    public void init() {
        this.isLoading.set(false);
        this.hasError.set(false);
        this.hasMore.set(true);
        this.useMax.set(true);
    }

    protected T data;
    private final AtomicBoolean isLoading;
    private final AtomicBoolean hasMore;
    private final AtomicBoolean hasError;
    protected final AtomicBoolean useMax;

    public void setUseMax(boolean flag) {
        this.useMax.set(flag);
    }

    @Override
    public final T loadInBackground() {
        T result = null;
        isLoading.set(true);
        try {
            result = doInBackground();
            hasError.set(false);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            hasError.set(true);
            hasMore.set(false);
        }

        return result;
    }

    /**
     * call your webservice/api/network call here
     *
     * @return result of loaded data
     * @throws Exception while executing doInBackground
     */
    protected abstract T doInBackground() throws Exception;


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (data != null) {

            deliverResult(null);
        } else {
            forceLoad();
        }
    }

    @Override
    public final void deliverResult(T data) {
        // Log.d("deliverResult", "called");
        isLoading.set(false);
        if (data != null)
            hasMore.set(hasMoreResults(data));

        if (hasMore.get()) {
            updateNextLoadParameters(data);
        }
        if (isReset()) {
            return;
        }
        if (isStarted()) {

            this.data = deliverResultToListener(data);
            // Log.d("isStarted(", "" + this.data);
            super.deliverResult(this.data);
        }
    }

    /**
     * @param data new data loader
     * @return data you want the callback listener to receive
     */
    protected abstract T deliverResultToListener(T data);

    /**
     * Provide next load logic . i.e increment page count ( page = page +1) data
     * : new loaded data
     * @param data  T
     */
    protected abstract void updateNextLoadParameters(T data);

    /**
     * Provide has more result logic i.e
     *
     * @param data loaded data
     * @return true if the the loader has more result to load
     */
    protected abstract boolean hasMoreResults(T data);

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        isLoading.set(false);
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStartLoading();
        data = null;
    }

    public boolean isLoading() {
        return isLoading.get();
    }

    public boolean hasMoreResults() {
        return hasMore.get();
    }

    public boolean hasError() {
        return hasError.get();
    }

    public T getData() {

        return data;
    }

    protected void setHasError(boolean vale) {
        hasError.set(vale);
    }

    @Override
    public int getLoaderId() {

        return getId();
    }

    @Override
    public boolean containsError() {
        return hasError();
    }

    @Override
    public String getErrorMessage() {


        // if (data != null && data.getError() != null
        // && data.getError().getMessage() != null) {
        // return data.getError().getMessage();
        // }
        return null;
    }

    @Override
    public void retryTask() {
        init();
        data = null;
        forceLoad();
    }

    public void refresh() {
        // clear cache hint from cache manager

    }


}
