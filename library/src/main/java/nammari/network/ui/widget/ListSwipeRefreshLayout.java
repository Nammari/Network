package nammari.network.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;


public class ListSwipeRefreshLayout extends SwipeRefreshLayout {


    public ListSwipeRefreshLayout(Context context) {
        super(context);
    }

    public ListSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private AbsListView abs;


    /**
     * As mentioned above, we need to override this method to properly signal when a
     * 'swipe-to-refresh' is possible.
     *
     * @return true if the {@link android.widget.ListView} is visible and can scroll up.
     */
    @Override
    public boolean canChildScrollUp() {
        if (abs == null) {
            return super.canChildScrollUp();
        }

        if (abs.getVisibility() == View.VISIBLE) {
            return canListViewScrollUp(abs);
        } else {
            return false;
        }
    }


    @Override
    public void addView(View child, int index, LayoutParams params) {
        super.addView(child, index, params);
        if (child instanceof AbsListView) {
            abs = (AbsListView) child;
        }
    }


    /**
     * Utility method to check whether a {@link android.widget.AbsListView} can scroll up from it's current position.
     * Handles platform version differences, providing backwards compatible functionality where
     * needed.
     */
    private static boolean canListViewScrollUp(AbsListView listView) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            // For ICS and above we can call canScrollVertically() to determine this
            return ViewCompat.canScrollVertically(listView, -1);
        } else {
            // Pre-ICS we need to manually check the first visible item and the child view's top
            // value
            return listView.getChildCount() > 0 &&
                    (listView.getFirstVisiblePosition() > 0
                            || listView.getChildAt(0).getTop() < listView.getPaddingTop());
        }
    }

}