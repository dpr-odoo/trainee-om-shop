package odoo.controls.recycler;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.List;

public class EasyRecyclerView extends RecyclerView {
    public static final String TAG = EasyRecyclerView.class.getSimpleName();
    private Context mContext;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private int mLayout = -1;
    private EasyRecyclerViewAdapter adapter;

    public EasyRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public EasyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EasyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        linear();
    }

    public EasyRecyclerView linear() {
        linearLayoutManager = new LinearLayoutManager(mContext);
        setLayoutManager(linearLayoutManager);
        return this;
    }

    public EasyRecyclerView grid(int spanCount) {
        return grid(spanCount, LinearLayout.VERTICAL, false);
    }

    public EasyRecyclerView grid(int spanCount, int orientation) {
        return grid(spanCount, orientation, false);
    }

    public EasyRecyclerView grid(int spanCount, int orientation, boolean reversLayout) {
        gridLayoutManager = new GridLayoutManager(mContext, spanCount, orientation, reversLayout);
        setLayoutManager(gridLayoutManager);
        return this;
    }

    public EasyRecyclerView staggered_grid(int spanCount) {
        return staggered_grid(spanCount, LinearLayout.VERTICAL);
    }

    public EasyRecyclerView staggered_grid(int spanCount, int orientation) {
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(spanCount, orientation);
        setLayoutManager(staggeredGridLayoutManager);
        return this;
    }

    public EasyRecyclerView setLayout(int layout) {
        mLayout = layout;
        return this;
    }

    public void setOnItemViewClickListener(EasyRecyclerViewAdapter.OnItemViewClickListener listener) {
        if (adapter != null) {
            adapter.setOnItemViewClickListener(listener);
        }
    }

    public void setOnViewBindListener(EasyRecyclerViewAdapter.OnViewBindListener listener) {
        if (adapter != null) {
            adapter.setOnViewBindListener(listener);
        }
    }

    public void changeCursor(List<Object> rows) {
        if (mLayout != -1) {
            if (adapter == null) {
                adapter = new EasyRecyclerViewAdapter(mLayout, rows);
                setAdapter(adapter);
            } else {
                adapter.changeCursor(rows);
            }
        } else {
            Log.e(TAG, "Ignoring create view. No layout defined");
        }
    }

}