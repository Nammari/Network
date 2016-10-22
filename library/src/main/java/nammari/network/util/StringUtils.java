package nammari.network.util;

/**
 * Created by nammari on 7/12/14.
 */
public class StringUtils {


    public static final boolean isBlank(String str){
        return str == null || str.length()==0 || str.trim().length()==0;
    }

    public static final boolean isBlankOrNullStringHardCoded(String str){
        return StringUtils.isBlank(str) || "null".equalsIgnoreCase(str.trim());
    }

}
