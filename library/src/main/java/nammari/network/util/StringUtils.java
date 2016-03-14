package nammari.network.util;

/**
 * Created by nammari on 7/12/14.
 */
public class StringUtils {


    public static final boolean isBlink(String str){
        return str == null || str.length()==0 || str.trim().length()==0;
    }

    public static final boolean isBlinkOrNullStringHardCoded(String str){
        return isBlink(str) || "null".equalsIgnoreCase(str.trim());
    }

}
