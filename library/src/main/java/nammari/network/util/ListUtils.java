package nammari.network.util;

import java.util.List;

/**
 * Created by nammari on 9/22/14.
 */
public class ListUtils {


    public static String implode(List<String> items, String separator) {

        if (items == null || items.isEmpty()) {
            return null;
        }
        String delimiter = "";
        StringBuilder builder = new StringBuilder();
        for (String item : items) {
            builder.append(delimiter).append(item);
            delimiter = separator;
        }
        return builder.toString();
    }

}
