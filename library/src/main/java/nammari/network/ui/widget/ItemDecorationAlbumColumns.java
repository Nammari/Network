package nammari.network.ui.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewUtils;
import android.view.View;

/**
 * Created by nammari on 1/21/16.
 */
public class ItemDecorationAlbumColumns extends RecyclerView.ItemDecoration {

    private int mSizeGridSpacingPx;
    private int mGridSize;

    private boolean mNeedLeftSpacing = false;

    public ItemDecorationAlbumColumns(int gridSpacingPx, int gridSize) {
        mSizeGridSpacingPx = gridSpacingPx;
        mGridSize = gridSize;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        boolean isRtl = ViewUtils.isLayoutRtl(view);
        int frameWidth = (int) ((parent.getWidth() - (float) mSizeGridSpacingPx * (mGridSize - 1)) / mGridSize);
        int padding = parent.getWidth() / mGridSize - frameWidth;
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
        if (itemPosition < mGridSize) {
            outRect.top = 0;
        } else {
            outRect.top = mSizeGridSpacingPx;
        }
        if (itemPosition % mGridSize == 0) {
            outRect.left = isRtl ? padding : 0;
            outRect.right = isRtl ? 0 : padding;
            mNeedLeftSpacing = !isRtl;
        } else if ((itemPosition + 1) % mGridSize == 0) {
            mNeedLeftSpacing = isRtl;
            outRect.right = isRtl ? padding : 0;
            outRect.left = isRtl ? 0 : padding;
        } else if (mNeedLeftSpacing) {
            mNeedLeftSpacing = isRtl;
            if (isRtl) {
                outRect.right = mSizeGridSpacingPx - padding;
                if ((itemPosition + 2) % mGridSize == 0) {
                    outRect.left = mSizeGridSpacingPx - padding;
                } else {
                    outRect.left = mSizeGridSpacingPx / 2;
                }
            } else {
                outRect.left = mSizeGridSpacingPx - padding;
                if ((itemPosition + 2) % mGridSize == 0) {
                    outRect.right = mSizeGridSpacingPx - padding;
                } else {
                    outRect.right = mSizeGridSpacingPx / 2;
                }
            }
        } else if ((itemPosition + 2) % mGridSize == 0) {
            mNeedLeftSpacing = isRtl;
            outRect.left = isRtl ? mSizeGridSpacingPx - padding : mSizeGridSpacingPx / 2;
            outRect.right = isRtl ? mSizeGridSpacingPx / 2 : mSizeGridSpacingPx - padding;
        } else {
            mNeedLeftSpacing = isRtl;
            outRect.left = mSizeGridSpacingPx / 2;
            outRect.right = mSizeGridSpacingPx / 2;
        }
        outRect.bottom = 0;
    }
}
