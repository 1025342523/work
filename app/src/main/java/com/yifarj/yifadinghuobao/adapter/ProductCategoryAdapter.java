package com.yifarj.yifadinghuobao.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.helper.AbsRecyclerViewAdapter;
import com.yifarj.yifadinghuobao.model.entity.ProductCategoryListEntity;

import java.util.List;


/**
 * ProductCategoryAdapter
 *
 * @auther Czech.Yuan
 * @date 2017/7/17 15:26
 */
public class ProductCategoryAdapter extends AbsRecyclerViewAdapter {
    public List<ProductCategoryListEntity.ValueEntity> data;

    public ProductCategoryAdapter(RecyclerView recyclerView, List<ProductCategoryListEntity.ValueEntity> data) {
        super(recyclerView);
        this.data = data;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).
                inflate(R.layout.item_product_category, parent, false));
    }


    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            ProductCategoryListEntity.ValueEntity item = data.get(position);
            itemViewHolder.tvPCName.setText(item.Name);
            itemViewHolder.rlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemViewHolder.rlItem.setSelected(true);
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

        TextView tvPCName;
        RelativeLayout rlItem;

        public ItemViewHolder(View itemView) {

            super(itemView);
            tvPCName = $(R.id.tvPCName);
            rlItem = $(R.id.rlItem);
        }
    }
}
