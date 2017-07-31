package com.yifarj.yifadinghuobao.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.helper.AbsRecyclerViewAdapter;
import com.yifarj.yifadinghuobao.model.entity.TraderEntity;

import java.util.List;


/**
 * AddressListAdapter
 *
 * @auther Czech.Yuan
 * @date 2017/7/31 16:04
 */
public class AddressListAdapter extends AbsRecyclerViewAdapter {
    public List<TraderEntity.ValueEntity.TraderDeliveryAddressListEntity> data;

    public AddressListAdapter(RecyclerView recyclerView, List<TraderEntity.ValueEntity.TraderDeliveryAddressListEntity> data) {
        super(recyclerView);
        this.data = data;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).
                inflate(R.layout.item_address_list, parent, false));
    }


    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {

        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            TraderEntity.ValueEntity.TraderDeliveryAddressListEntity goodsBean = data.get(position);
            itemViewHolder.tvAddress.setText(goodsBean.Address);
            if (position == 0) {
                itemViewHolder.ivDefault.setImageResource(R.drawable.ic_address);
            }
        }

        super.onBindViewHolder(holder, position);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ItemViewHolder extends ClickableViewHolder {

        TextView tvAddress;
        ImageView ivDefault;
        TextView tvDelete;
        TextView tvEdit;

        public ItemViewHolder(View itemView) {

            super(itemView);

            tvAddress = $(R.id.tvAddress);
            ivDefault = $(R.id.ivDefault);
            tvDelete = $(R.id.tvDelete);
            tvEdit = $(R.id.tvEdit);
        }
    }
}
