package com.yifarj.yifadinghuobao.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mcxtzhang.lib.AnimShopButton;
import com.mcxtzhang.lib.IOnAddDelListener;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.model.entity.GoodsListEntity;
import com.yifarj.yifadinghuobao.model.entity.ProductUnitEntity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;

import java.util.List;


/**
 * GoodsListAdapter
 *
 * @auther Czech.Yuan
 * @date 2017/5/15 15:56
 */
public class GoodsListAdapter extends AbsRecyclerViewAdapter {
    public List<GoodsListEntity.ValueEntity> data;

    public GoodsListAdapter(RecyclerView recyclerView, List<GoodsListEntity.ValueEntity> data) {
        super(recyclerView);
        this.data = data;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).
                inflate(R.layout.item_goods_list, parent, false));
    }


    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {

        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            GoodsListEntity.ValueEntity goodsBean = data.get(position);

            Glide.with(getContext())
                    .load(AppInfoUtil.genPicUrl(goodsBean.ProductPictureList.get(0).Path))
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.default_image)
                    .dontAnimate()
                    .into(itemViewHolder.itemImg);

            itemViewHolder.tvName.setText(goodsBean.Name);
            for (ProductUnitEntity.ValueEntity unit : goodsBean.ProductUnitList) {
                if (unit.IsBasic) {
                    itemViewHolder.tvUnit.setText(unit.Name);
                }
            }

            for (GoodsListEntity.ValueEntity.PriceSystemListEntity price : goodsBean.PriceSystemList) {
                double productPrice = 0;
                if (price.IsOrderMeetingPrice) {
                    switch (price.Id) {
                        case 0:
                            productPrice = goodsBean.Price0;
                            break;
                        case 1:
                            productPrice = goodsBean.Price1;
                            break;
                        case 2:
                            productPrice = goodsBean.Price2;
                            break;
                        case 3:
                            productPrice = goodsBean.Price3;
                            break;
                        case 4:
                            productPrice = goodsBean.Price4;
                            break;
                        case 5:
                            productPrice = goodsBean.Price5;
                            break;
                        case 6:
                            productPrice = goodsBean.Price6;
                            break;
                        case 7:
                            productPrice = goodsBean.Price7;
                            break;
                        case 8:
                            productPrice = goodsBean.Price8;
                            break;
                        case 9:
                            productPrice = goodsBean.Price9;
                            break;
                        case 10:
                            productPrice = goodsBean.Price10;
                            break;
                    }
                }
                itemViewHolder.tvPrice.setText(String.valueOf(productPrice));
            }

            itemViewHolder.btnEle.setOnAddDelListener(new IOnAddDelListener() {
                @Override
                public void onAddSuccess(int i) {

                }

                @Override
                public void onAddFailed(int i, FailType failType) {

                }

                @Override
                public void onDelSuccess(int i) {

                }

                @Override
                public void onDelFaild(int i, FailType failType) {

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

        ImageView itemImg;
        TextView tvName;
        TextView tvUnit;
        TextView tvPrice;
        AnimShopButton btnEle;

        public ItemViewHolder(View itemView) {

            super(itemView);

            itemImg = $(R.id.item_img);
            tvName = $(R.id.tv_name);
            tvUnit = $(R.id.tv_unit);
            tvPrice = $(R.id.tv_price);
            btnEle = $(R.id.btnEle);
        }
    }
}
