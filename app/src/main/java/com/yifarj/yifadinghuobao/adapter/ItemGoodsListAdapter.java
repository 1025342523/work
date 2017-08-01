package com.yifarj.yifadinghuobao.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.helper.AbsRecyclerViewAdapter;
import com.yifarj.yifadinghuobao.model.entity.ProductUnitEntity;
import com.yifarj.yifadinghuobao.model.entity.SaleGoodsItem;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


/**
 * ItemGoodsListAdapter
 *
 * @auther Czech.Yuan
 * @date 2017/5/21 15:24
 */
public class ItemGoodsListAdapter extends AbsRecyclerViewAdapter {
    public List<SaleGoodsItem.ValueEntity> data;

    public ItemGoodsListAdapter(RecyclerView recyclerView, List<SaleGoodsItem.ValueEntity> data) {
        super(recyclerView);
        this.data = data;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).
                inflate(R.layout.item_order_goods_list, parent, false));
    }


    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {

        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            SaleGoodsItem.ValueEntity goodsBean = data.get(position);
            /*Glide.with(getContext())
                    .load(AppInfoUtil.genPicUrl(goodsBean.ImagePath))
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.default_image)
                    .dontAnimate()
                    .into(itemViewHolder.itemImg);*/

            itemViewHolder.tvName.setText(goodsBean.ProductName);
            itemViewHolder.tvPrice.setText(String.valueOf(goodsBean.TotalPrice));
//            itemViewHolder.tvCode.setText(goodsBean.Code);
            itemViewHolder.tvPackSpec.setText(goodsBean.PackSpec);

            String quantity = String.valueOf(goodsBean.Quantity);
            Flowable.fromIterable(goodsBean.ProductUnitList)
                    .forEach(new Consumer<ProductUnitEntity.ValueEntity>() {
                        @Override
                        public void accept(@NonNull ProductUnitEntity.ValueEntity valueEntity) throws Exception {
                            if (valueEntity.Id == goodsBean.UnitId) {
//                                itemViewHolder.tvUnit.setText(goodsBean.UnitPrice + "/" + valueEntity.Name);
                                itemViewHolder.tvCount.setText(quantity + valueEntity.Name);
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

//        ImageView itemImg;
        TextView tvName;
//        TextView tvUnit;
        TextView tvPrice;
        TextView tvCount;
//        TextView tvCode;
        TextView tvPackSpec;

        public ItemViewHolder(View itemView) {

            super(itemView);

//            itemImg = $(R.id.item_img);
            tvName = $(R.id.tv_name);
//            tvUnit = $(R.id.tv_unit);
            tvPrice = $(R.id.tv_price);
            tvCount = $(R.id.tvCount);
//            tvCode = $(R.id.tv_code);
            tvPackSpec = $(R.id.tv_packSpec);
        }
    }
}
