package nammari.network.ui;

/**
 * Created by nammari on 8/17/14.
 */
public abstract class RefreshableMultiStateFragment extends MultiStateFragment  {

    @Override
    protected boolean supportSwipeToRefresh() {
        return true;
    }


}
