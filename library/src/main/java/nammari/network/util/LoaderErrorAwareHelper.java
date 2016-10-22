package nammari.network.util;

import android.support.v4.app.LoaderManager;
import android.util.SparseIntArray;

import nammari.network.loader.ErrorAwareLoader;

/**
 * Created by nammari on 8/12/14.
 */
public class LoaderErrorAwareHelper {

    // LOADER STATUSES
    public static final int STATUS_UNDEFINED = 0x01;
    public static final int STATUS_LOADED_SUCCESSFULLY = 0x02;
    public static final int STATUS_LOADED_WITH_ERROR = 0x03;

    /**
     * Check if at least this framgent has an error ( here we check in the
     * status array not from the loader it self by using containsError to keep
     * every thing synchronized)
     *
     * @param listener LoaderErrorAwareUI
     * @return true if at least one of loaders contains error
     */
    public static boolean atLeastOneLoaderHasError(
            LoaderErrorAwareUI listener) {

        if (listener == null) {
            throw new IllegalArgumentException("listener cant be null");
        }

        boolean result = false;

        if ((listener.getLoadersStatus() == null || listener.getLoadersStatus()
                .size() == 0))
            return result;

        final int size = listener.getLoadersStatus().size();
        SparseIntArray status = listener.getLoadersStatus();
        for (int i = 0; i < size; i++) {

            if (status.get(status.keyAt(i)) == STATUS_LOADED_WITH_ERROR) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Any loader report an error as it status should be retried
     *
     * @param listener LoaderErrorAwareUI
     */
    public static void retryLoadersWithError(LoaderErrorAwareUI listener) {

        if (listener == null) {
            throw new IllegalArgumentException("listener cant be null");
        }
        if ((listener.getLoadersStatus() == null || listener.getLoadersStatus()
                .size() == 0))

            return;

        final int size = listener.getLoadersStatus().size();
        SparseIntArray status = listener.getLoadersStatus();
        int loaderId;
        for (int i = 0; i < size; i++) {
            loaderId = status.keyAt(i);
            if (status.get(loaderId) == STATUS_LOADED_WITH_ERROR) {

                final ErrorAwareLoader loader = (ErrorAwareLoader) listener
                        .getCorrectLoaderManager().getLoader(loaderId);

                if (loader != null && !loader.isLoading()) {
                    loader.retryTask();
                }// end if
            }// end if
        }// end for
    }

    /**
     * Create a brand new Status array with STATUS_UNDEFINED(i.e no loaded yet)
     * for all loader ids
     *
     * @param listener LoaderErrorAwareUI
     * @return SparseArray map loader id to it status ( STATUS_UNDEFINED,STATUS_LOADED_SUCCESSFULLY,STATUS_LOADED_WITH_ERROR)
     */
    public static SparseIntArray createNewLoaderStatusArray(
            LoaderErrorAwareUI listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cant be null");
        }

        if (listener.getLoaderIds() == null
                || listener.getLoaderIds().length == 0) {
            // no need to create a status array
            return null;
        }

        final int size = listener.getLoaderIds().length;
        SparseIntArray result = new SparseIntArray(size);
        for (int i = 0; i < size; ++i) {
            result.put(listener.getLoaderIds()[i], STATUS_UNDEFINED);
        }
        return result;
    }

    /**
     * update the loader status we send in the parameter then update the UI
     * accordingly Note:This method should be called in the onLoadFinished only
     * once at the beginning.
     *
     * @param listener LoaderErrorAwareUI
     * @param loader   ErrorAwareLoader
     */
    public static void updateSingleLoaderStatus(
            LoaderErrorAwareUI listener, ErrorAwareLoader loader) {
        updateSingleLoaderStatus(listener, loader, false);

    }

    public static void updateSingleLoaderStatus(LoaderErrorAwareUI listener, ErrorAwareLoader loader, boolean forceNoAnimation) {

        if (listener == null) {
            throw new IllegalArgumentException("listener cant be null");
        }
        if (listener == null || listener.getLoadersStatus() == null
                || loader == null) {
            return;
        }
        SparseIntArray loaderStatus = listener.getLoadersStatus();
        // update status for the incoming loader
        loaderStatus.put(loader.getLoaderId(),
                loader.containsError() ? STATUS_LOADED_WITH_ERROR
                        : STATUS_LOADED_SUCCESSFULLY);

        if (atLeastOnLoaderIsLoadingInBackground(listener)) {
            return;
        }
        int undefined_loaders_counter = 0;
        int loaders_with_error_counter = 0;
        final int count = loaderStatus.size();
        for (int i = 0; i < count; i++) {
            switch (loaderStatus.get(loaderStatus.keyAt(i))) {
                case STATUS_UNDEFINED:
                    ++undefined_loaders_counter;
                    break;
                case STATUS_LOADED_WITH_ERROR:
                    ++loaders_with_error_counter;
                    break;
                case STATUS_LOADED_SUCCESSFULLY:

                    // do nothing
                    break;
                default:
                    throw new IllegalStateException(
                            "How can loader status be something differenct that the predefined values for value is "
                                    + loaderStatus.get(loaderStatus.keyAt(i)));
            }// end switch
        }// end for

        if (undefined_loaders_counter == 0) {
            // we know the status of all loader they either success or fail
            // @least one loader with error
            if (loaders_with_error_counter > 0) {

                listener.showErrorUI(forceNoAnimation ? false : listener.isFragmentResumed());
                return;
            }
            // all of them loaded successfully

            listener.showMainUI(forceNoAnimation ? false : listener.isFragmentResumed());
            return;
        }
    }


    public static boolean atLeastOnLoaderIsLoadingInBackground(
            LoaderErrorAwareUI listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cant be null");
        }
        boolean result = false;
        if (listener.getLoaderIds() == null
                || listener.getLoaderIds().length == 0) {
            return result;
        }
        final int[] loaderIds = listener.getLoaderIds();
        final int size = loaderIds.length;
        for (int i = 0; i < size; ++i) {
            final ErrorAwareLoader loader = (ErrorAwareLoader) listener
                    .getCorrectLoaderManager().getLoader(loaderIds[i]);
            if (loader != null && loader.isLoading()) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Get Error Message from the first loader that has an error and not null
     *
     * @param listener LoaderErrorAwareUI
     * @return error message
     */
    public static String getErrorMessageFromLoaders(
            LoaderErrorAwareUI listener) {

        if (listener.getLoaderIds() == null
                || listener.getLoaderIds().length == 0) {
            return null;
        }

        String result = null;
        final int count = listener.getLoaderIds().length;
        // loop loaders and try to get an error message
        for (int i = 0; i < count; i++) {
            final ErrorAwareLoader loader = (ErrorAwareLoader) listener
                    .getCorrectLoaderManager().getLoader(listener.getLoaderIds()[i]);
            if (loader != null
                    && loader.containsError()
                    && !StringUtils.isBlink(loader
                    .getErrorMessage())) {
                result = loader.getErrorMessage();
                break;
            }// end if
        }// end for
        return result;
    }

    public static void handleRetryErrorLoader(LoaderErrorAwareUI listener) {

        if (listener == null) {
            throw new IllegalArgumentException("listener cant be null");
        }
        if (!LoaderErrorAwareHelper.atLeastOneLoaderHasError(listener)) {
            listener.showMainUI(listener.isFragmentResumed());
            return;
        }

        // show loading
        listener.showLoadingUI(listener.isFragmentResumed());
        // retry any loader with error
        LoaderErrorAwareHelper.retryLoadersWithError(listener);
    }

    /**
     * listener have the capablitiy of show 3 type of views only one at a time
     * and provide loaderids for it loaded loader and status array
     *
     * @author Nammari
     */


    public  interface LoaderErrorAwareUI {
        public void showMainUI(boolean animate);

        public void showErrorUI(boolean animate);

        public void showLoadingUI(boolean animate);

        public boolean isFragmentResumed();

        public int[] getLoaderIds();

        public LoaderManager getCorrectLoaderManager();

        public SparseIntArray getLoadersStatus();
    }


}
