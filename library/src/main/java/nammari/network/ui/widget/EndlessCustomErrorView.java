package nammari.network.ui.widget;

import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by nammari on 4/16/16.
 */
public class EndlessCustomErrorView {

    private final WeakReference<View> root;
    private final WeakReference<View> loadingContainer;
    private final WeakReference<View> retryButton;
    private final WeakReference<View> errroContainer;

    public EndlessCustomErrorView(View root, View loadingContainer, View retryButton, View errroContainer) {
        this.root = new WeakReference<>(root);
        this.loadingContainer = new WeakReference<>(loadingContainer);
        this.retryButton = new WeakReference<>(retryButton);
        this.errroContainer = new WeakReference<>(errroContainer);
    }


    public View getRoot() {
        return root.get();
    }

    public View getLoadingContainer() {
        return loadingContainer.get();
    }

    public View getRetryButton() {
        return retryButton.get();
    }

    public View getErrroContainer() {
        return errroContainer.get();
    }
}
