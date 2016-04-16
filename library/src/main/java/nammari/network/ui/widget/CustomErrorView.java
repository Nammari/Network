package nammari.network.ui.widget;

import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by nammari on 4/16/16.
 */
public class CustomErrorView {

    private final WeakReference<View> customErrorView;
    private final WeakReference<View> customRetryView;

    public CustomErrorView(View customErrorView, View customRetryView) {
        this.customErrorView = new WeakReference<>(customErrorView);
        this.customRetryView = new WeakReference<>(customRetryView);
    }

    public View getCustomErrorView() {
        return customErrorView.get();
    }

    public View getCustomRetryView() {
        return customRetryView.get();
    }
}
