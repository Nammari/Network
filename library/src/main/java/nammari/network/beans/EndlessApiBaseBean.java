package nammari.network.beans;

import java.util.List;

/**
 * Created by nammari on 8/13/14.
 */

public abstract class EndlessApiBaseBean<F> extends BaseApiBean {


    public EndlessApiBaseBean() {
    }

    public EndlessApiBaseBean(EndlessApiBaseBean<F> obj) {
        if (obj != null) {
            setDataList(obj.getDataList());
            setErrors(obj.getErrors());
            setSuccess(obj.isSuccess());
        }
    }

    public abstract List<F> getDataList();

    public abstract void setDataList(List<F> data);

    //next
    public abstract String getMax();
    //previous
    public abstract String getSince();


}
