package nammari.network.beans;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * Created by ahmad.nammari on 12/24/15.
 */
@JsonObject
public class BaseApiBean {

    @JsonField(name = "success")
    int success;
    @JsonField(name = "errors")
    List<String> errors;
    @JsonField(name = "message")
    String message;


    public BaseApiBean() {
    }


    public int getSuccess() {
        return success;
    }

    public boolean isSuccess() {
        return 1 == success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "BaseApiBean{" +
                "success=" + success +
                ", errors=" + errors +
                ", message='" + message + '\'' +
                '}';
    }
}
