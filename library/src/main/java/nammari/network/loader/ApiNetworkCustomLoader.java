package nammari.network.loader;

import android.content.Context;

import java.util.concurrent.atomic.AtomicBoolean;

import nammari.network.beans.BaseApiBean;


/**
 * Created by nammari on 8/12/14.
 */
public abstract class ApiNetworkCustomLoader<T extends BaseApiBean> extends
        CustomLoaderImp<T> implements ErrorAwareLoader {

    public ApiNetworkCustomLoader(Context context, T cashedData) {
        super(context, cashedData);
        mError = new AtomicBoolean(false);
        mLoading = new AtomicBoolean(false);
    }

    @Override
    public int getLoaderId() {
        return getId();
    }

    private AtomicBoolean mError;
    private AtomicBoolean mLoading;

    @Override
    public final T loadInBackground() {
        T result = null;
        try {
            mLoading.set(true);
            result = doLoadInBackground();
            if (!result.isSuccess()) {
                mError.set(true);
            } else {
                mError.set(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            mError.set(true);

        }
        return result;
    }

    protected abstract T doLoadInBackground() throws Exception;

    @Override
    public boolean containsError() {
        return data == null ? mError.get()
                : !data.isSuccess();
    }

    @Override
    public void retryTask() {

        data = null;
        forceLoad();
    }

    @Override
    public String getErrorMessage() {
        if (data != null && data.getErrors() != null && !data.getErrors().isEmpty()) {
            return data.getErrors().get(0);
        }
        return null;
    }


    public void refresh() {
        // clear cache hint from cache manager

    }

    @Override
    public void deliverResult(T data) {
        mLoading.set(false);
        super.deliverResult(data);
    }

    @Override
    public final boolean isLoading() {

        return mLoading.get();
    }
}
