package nammari.network.ui.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;


public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {

    /**
     * A StickyListHeadersListView whose parent view should be this SwipeRefreshLayout
     */
    private View mTargetView;

    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTargetView(View targetView) {
        mTargetView = targetView;
    }

    @Override
    public boolean canChildScrollUp() {
//        if (mTargetView != null) {
//            // In order to scroll a StickyListHeadersListView up:
//            // Firstly, the wrapped ListView must have at least one item
//            return (mTargetView.getChildCount() > 0) &&
//                    // And then, the first visible item must not be the first item
//                    ((mTargetView.getFirstVisiblePosition() > 0) ||
//                            // If the first visible item is the first item,
//                            // (we've reached the first item)
//                            // make sure that its top must not cross over the padding top of the wrapped ListView
//                            (mTargetView.getChildAt(0).getTop() < 0));
//
//            // If the wrapped ListView is empty or,
//            // the first item is located below the padding top of the wrapped ListView,
//            // we can allow performing refreshing now
//        } else {
//            // Fall back to default implementation
//            return super.canChildScrollUp();
//        }
        boolean result;
//        if (mTargetView != null) {
//            result =mTargetView.canScrollVertically(-1);
//        } else {
            result= super.canChildScrollUp();
//        }

        return result;
    }
}