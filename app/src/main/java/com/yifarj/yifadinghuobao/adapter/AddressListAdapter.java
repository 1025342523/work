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
    private DeleteClickListener mDeleteClickListener;
    private EditClickListener mEditClickListener;

    public AddressListAdapter(RecyclerView recyclerView, List<TraderEntity.ValueEntity.TraderDeliveryAddressListEntity> data) {
        super(recyclerView);
        this.data = data;
    }

    public interface DeleteClickListener {
        void deleteClick(int position);
    }

    public interface EditClickListener {
        void editClick(int position);
    }

    public void setDeleteClickListener(DeleteClickListener l) {
        this.mDeleteClickListener = l;
    }

    public void setEditClickListener(EditClickListener l) {
        this.mEditClickListener = l;
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
//            if (position == 0) {
//                itemViewHolder.ivDefault.setImageResource(R.drawable.ic_address);
//            }

            itemViewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDeleteClickListener != null) {
                        mDeleteClickListener.deleteClick(holder.getLayoutPosition());
                    }
                }
            });

            itemViewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mEditClickListener != null) {
                        mEditClickListener.editClick(holder.getLayoutPosition());
                    }
                }
            });
        }

        super.onBindViewHolder(holder, position);
    }

    public void setData(List<TraderEntity.ValueEntity.TraderDeliveryAddressListEntity> list) {
        data = list;
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
