package com.yifarj.yifadinghuobao.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.helper.AbsRecyclerViewAdapter;
import com.yifarj.yifadinghuobao.model.entity.ProductCategoryListEntity;

import java.util.List;


/**
 * 商品类别子集
 *
 * @auther Czech.Yuan
 * @date 2017/7/25 10:55
 */
public class ProductCategoryChildAdapter extends AbsRecyclerViewAdapter {
    public List<ProductCategoryListEntity.ValueEntity> data;
    private OnChildClickListener mOnChildClickListener;

    public ProductCategoryChildAdapter(RecyclerView recyclerView, List<ProductCategoryListEntity.ValueEntity> data) {
        super(recyclerView);
        this.data = data;
    }

    public interface OnChildClickListener {
        void onChildClick(int position);
    }


    public void setOnChildClickListener(OnChildClickListener listener) {
        mOnChildClickListener = listener;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).
                inflate(R.layout.item_product_category_child, parent, false));
    }


    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            ProductCategoryListEntity.ValueEntity item = data.get(position);
            itemViewHolder.tvName.setText(item.Name);
            itemViewHolder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnChildClickListener != null) {
                        mOnChildClickListener.onChildClick(position);
                    }
                }
            });
        }

        super.onBindViewHolder(holder, position);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ItemViewHolder extends ClickableViewHolder {

        TextView tvName;
        Button btn;

        public ItemViewHolder(View itemView) {

            super(itemView);
            tvName = $(R.id.tvName);
            btn = $(R.id.btn);
        }
    }
}
