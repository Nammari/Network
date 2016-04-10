package nammari.network.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * support empty view for recyclerview
 * Created by nammari on 4/10/16.
 * email : nammariahmad@gmail.com
 */
public class EmptyViewRecyclerView extends RecyclerView {

    View emptyView;

    public EmptyViewRecyclerView(Context context) {
        super(context);
    }

    public EmptyViewRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyViewRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    AdapterDataObserver adapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            Adapter<?> adapter = getAdapter();
            if (adapter != null && emptyView != null) {
                if (adapter.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    EmptyViewRecyclerView.this.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    EmptyViewRecyclerView.this.setVisibility(View.VISIBLE);
                }
            }
        }
    };


    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }


    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if (adapter != null) {
            adapter.registerAdapterDataObserver(adapterDataObserver);
        }
        adapterDataObserver.onChanged();
    }
}
