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
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.helper.AbsRecyclerViewAdapter;
import com.yifarj.yifadinghuobao.database.model.GoodsUnitModel;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel;
import com.yifarj.yifadinghuobao.model.entity.GoodsListEntity;
import com.yifarj.yifadinghuobao.model.entity.ProductUnitEntity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


/**
 * GoodsListAdapter
 *
 * @auther Czech.Yuan
 * @date 2017/5/15 15:56
 */
public class GoodsListAdapter extends AbsRecyclerViewAdapter {
    public List<GoodsListEntity.ValueEntity> data;
    private String unitName;
    private double productPrice;

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
            if (goodsBean.ProductPictureList.size() > 0) {
                Glide.with(getContext())
                        .load(AppInfoUtil.genPicUrl(goodsBean.ProductPictureList.get(0).Path))
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.default_image)
                        .dontAnimate()
                        .into(itemViewHolder.itemImg);
            }

            itemViewHolder.tvName.setText(goodsBean.Name);
            for (ProductUnitEntity.ValueEntity unit : goodsBean.ProductUnitList) {
                if (unit.IsBasic) {
                    itemViewHolder.tvUnit.setText(unit.Name);
                    unitName = unit.Name;
                }
            }
            productPrice = goodsBean.Price1;
            for (GoodsListEntity.ValueEntity.PriceSystemListEntity price : goodsBean.PriceSystemList) {

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
//                    RXSQLite.rx(SQLite.select()
//                            .from(SaleGoodsItemModel.class))
//                            .queryList()
//                            .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
//                                @Override
//                                public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModels) throws Exception {
//                                    if (saleGoodsItemModels.)
//                                }
//                            });
                    RXSQLite.rx(SQLite.select()
                            .from(SaleGoodsItemModel.class))
                            .queryStreamResults()
                            .subscribe(new Consumer<SaleGoodsItemModel>() {
                                @Override
                                public void accept(@NonNull SaleGoodsItemModel saleGoodsItemModel) throws Exception {
                                    if (saleGoodsItemModel.ProductId == goodsBean.Id) {
                                        return;
                                    }
                                    add(goodsBean);
                                }
                            });

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

    private void add(GoodsListEntity.ValueEntity goodsBean) {
        SaleGoodsItemModel itemModel = new SaleGoodsItemModel();
        itemModel.CurrentPrice = productPrice;
        itemModel.Path = goodsBean.ProductPictureList.get(0).Path;
        itemModel.UnitName = unitName;
        itemModel.Discount = 1.0f;
        itemModel.SalesType = 1;
        itemModel.TaxRate = 1.0;
        itemModel.WarehouseId = goodsBean.DefaultWarehouseId;
        itemModel.ProductId = goodsBean.Id;
        itemModel.LocationId = goodsBean.DefaultLocationId;
        itemModel.PackSpec = goodsBean.PackSpec;
        itemModel.Price0 = goodsBean.Price0;
        itemModel.Price1 = goodsBean.Price1;
        itemModel.Price2 = goodsBean.Price2;
        itemModel.Price3 = goodsBean.Price3;
        itemModel.Price4 = goodsBean.Price4;
        itemModel.Price5 = goodsBean.Price5;
        itemModel.Price6 = goodsBean.Price6;
        itemModel.Price7 = goodsBean.Price7;
        itemModel.Price8 = goodsBean.Price8;
        itemModel.Price9 = goodsBean.Price9;
        itemModel.Price10 = goodsBean.Price10;
        itemModel.MinSalesQuantity = goodsBean.MinSalesQuantity;
        itemModel.MaxSalesQuantity = goodsBean.MaxSalesQuantity;
        itemModel.MinSalesPrice = goodsBean.MinSalesPrice;
        itemModel.MaxPurchasePrice = goodsBean.MaxPurchasePrice;
        itemModel.DefaultLocationName = goodsBean.DefaultLocationName;
        itemModel.OweRemark = goodsBean.Remark;
        itemModel.save();
        Flowable.fromIterable(goodsBean.ProductUnitList)
                .forEach(valueEntity -> {
                    GoodsUnitModel goodsUnitModel = new GoodsUnitModel();
                    goodsUnitModel.Id = valueEntity.Id;
                    goodsUnitModel.ProductId = valueEntity.ProductId;
                    goodsUnitModel.Name = valueEntity.Name;
                    goodsUnitModel.Factor = valueEntity.Factor;
                    goodsUnitModel.BasicFactor = valueEntity.BasicFactor;
                    goodsUnitModel.IsBasic = valueEntity.IsBasic;
                    goodsUnitModel.IsDefault = valueEntity.IsDefault;
                    goodsUnitModel.BreakupNotify = valueEntity.BreakupNotify;
                    goodsUnitModel.Ordinal = valueEntity.Ordinal;
                    goodsUnitModel.save();
                });
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
