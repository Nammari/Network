package nammari.network.loader;

import android.content.Context;

import nammari.network.beans.EndlessApiBaseBean;
import nammari.network.logger.Logger;
import nammari.network.util.StringUtils;

/**
 * Created by nammari on 8/12/14.
 */
public abstract class ApiEndlessNetworkLoader<T extends EndlessApiBaseBean> extends
        EndlessNetworkLoader<T> {


    public ApiEndlessNetworkLoader(Context context, boolean isLoading, boolean hasMore, boolean hasError, String maxId, String sinceId) {
        super(context, isLoading, hasMore, hasError);
        this.maxId = maxId;
        this.sinceId = sinceId;
    }

    private volatile String maxId;
    private volatile String sinceId;

    @Override
    public String getErrorMessage() {
        if (data != null && data.getErrors() != null && !data.getErrors().isEmpty()) {
            String str = data.getErrors().get(0);
            if (!StringUtils.isBlink(str))
                return str;
        }
        return super.getErrorMessage();
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public boolean containsError() {
        return data == null ? super.containsError()
                : !data.isSuccess();
    }

    @Override
    protected final T deliverResultToListener(T data) {
        Logger.logDebug("hasMore", "deliverd");
        if (data != null && !data.isSuccess()) {
            setHasError(true);
        }

        if (this.data == null) {
            this.data = data;
        } else {

            // we have a previous data

            if (data != null) {
                if (data.getDataList() != null) {
                    if (this.data.getDataList() == null) {
                        this.data.setDataList(data.getDataList());
                    } else {
                        // add
                        if (data.getDataList() != null) {
                            if (useMax.get()) {
                                this.data.getDataList()
                                        .addAll(data.getDataList());
                            } else {
                                this.data.getDataList().addAll(0, data.getDataList());
                            }

                        }
                    }
                }
                this.data.setErrors(data.getErrors());
                this.data.setSuccess(data.getSuccess());
            }

        }
        if (data != null) {
            if (useMax.get()) {
                maxId = StringUtils.isBlink(data.getMax()) ? maxId :
                        data.getMax();
            } else {
                sinceId = StringUtils.isBlink(data.getSince()) ? sinceId : data.getSince();
            }
            if (StringUtils.isBlink(sinceId)) {
                sinceId = StringUtils.isBlink(data.getSince()) ? sinceId : data.getSince();
            }
        }
        return returnNewObjectIfNotNull(this.data);
    }


    @Override
    protected final boolean hasMoreResults(T data) {

        return true;
    }


    protected abstract T returnNewObjectIfNotNull(T data);


    @Override
    protected final void updateNextLoadParameters(T data) {

    }

    public String getMaxId() {
        return maxId;
    }

    public String getSinceId() {
        return sinceId;
    }

    /**
     * call your webservice/api/network call here
     *
     * @return result of loaded data
     */
    protected final T doInBackground() {
        Logger.logDebug("useMax", "" + useMax.get());
        Logger.logDebug("mm", "" + getMaxId());
        Logger.logDebug("ss", "" + getSinceId());
        return doInBackground(getMaxId(), getSinceId());
    }

    protected abstract T doInBackground(String maxId, String sinceId);


}
