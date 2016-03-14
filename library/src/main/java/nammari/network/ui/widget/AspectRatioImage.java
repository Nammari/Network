package nammari.network.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by nammari on 11/11/14.
 */
public class AspectRatioImage extends ImageView {

    public AspectRatioImage(Context context) {
        super(context);
    }

    double ratio = 1.0;

    public AspectRatioImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setRatio(double ratio) {
        this.ratio = ratio;
        invalidate();
    }

    public double getRatio() {
        return ratio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(getMeasuredWidth(), (int) (getMeasuredWidth() / ratio));
    }
}
