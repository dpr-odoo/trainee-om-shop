package odoo.controls.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class EasyRecyclerViewAdapter extends
        EasyRecyclerView.Adapter<EasyRecyclerViewAdapter.EasyRecyclerViewHolder> {

    private List<Object> items = new ArrayList<>();
    private int mLayout = -1;
    private OnViewBindListener mOnViewBindListener;
    private OnItemViewClickListener mOnItemViewClickListener;

    public EasyRecyclerViewAdapter(int layout_id, List<Object> rows) {
        mLayout = layout_id;
        items.addAll(rows);
    }

    @Override
    public EasyRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(mLayout, viewGroup, false);
        return new EasyRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EasyRecyclerViewHolder holder, int i) {
        if (mOnViewBindListener != null) {
            mOnViewBindListener.onViewBind(i, holder.mView, items.get(i));
            if (mOnItemViewClickListener != null) {
                holder.bind(i, items.get(i), mOnItemViewClickListener);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void changeCursor(List<Object> rows) {
        items.clear();
        items.addAll(rows);
        notifyDataSetChanged();
    }


    public class EasyRecyclerViewHolder extends EasyRecyclerView.ViewHolder {
        protected View mView;
        protected OnItemViewClickListener listener;
        protected int position = -1;
        protected Object data;

        public EasyRecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void bind(int position, Object data, OnItemViewClickListener listener) {
            this.listener = listener;
            this.position = position;
            this.data = data;
            mView.setTag(this);
            mView.setOnClickListener(clickListener);
        }

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyRecyclerViewHolder holder = (EasyRecyclerViewHolder) v.getTag();
                if (holder.listener != null) {
                    holder.listener.onItemViewClick(holder.position, holder.mView, holder.data);
                }
            }
        };
    }

    public EasyRecyclerViewAdapter setOnViewBindListener(OnViewBindListener listener) {
        mOnViewBindListener = listener;
        return this;
    }

    public EasyRecyclerViewAdapter setOnItemViewClickListener(OnItemViewClickListener listener) {
        mOnItemViewClickListener = listener;
        return this;
    }

    public interface OnViewBindListener {
        void onViewBind(int position, View view, Object data);
    }

    public interface OnItemViewClickListener {
        void onItemViewClick(int position, View view, Object data);
    }
}