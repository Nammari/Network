package nammari.network.beans;


import java.util.List;

/**
 * Created by ahmad.nammari on 12/24/15.
 */
public abstract class BaseApiBean {



    public BaseApiBean() {
    }


    public abstract boolean isSuccess();

    public abstract void setSuccess(boolean success);

    public abstract List<String> getErrors();

    public abstract void setErrors(List<String> errors);

}
