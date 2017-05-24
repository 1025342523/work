package com.yifarj.yifadinghuobao.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
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
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel_Table;
import com.yifarj.yifadinghuobao.model.entity.GoodsListEntity;
import com.yifarj.yifadinghuobao.model.entity.ProductUnitEntity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.DateUtil;

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
    public boolean onbind;
    private boolean type;

    public GoodsListAdapter(RecyclerView recyclerView, List<GoodsListEntity.ValueEntity> data, boolean type) {
        super(recyclerView);
        this.data = data;
        this.type = type;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).
                inflate(type ? R.layout.item_goods_list : R.layout.item_goods_list_grid, parent, false));
    }


    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {

        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            GoodsListEntity.ValueEntity goodsBean = data.get(position);
            onbind = true;
            String unitName = null;
            int unitId = 0;
            double productPrice;
            int priceSystemID;
            if (goodsBean.ProductPictureList.size() > 0) {
                Glide.with(getContext())
                        .load(AppInfoUtil.genPicUrl(goodsBean.ProductPictureList.get(0).Path))
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.default_image)
                        .dontAnimate()
                        .into(itemViewHolder.itemImg);
            }
            itemViewHolder.tvPackSpec.setText(goodsBean.PackSpec);
            itemViewHolder.tvCode.setText("编号：" + goodsBean.Code.substring(goodsBean.Code.length() - 4, goodsBean.Code.length()));
            itemViewHolder.tvName.setText(goodsBean.Name);
            for (ProductUnitEntity.ValueEntity unit : goodsBean.ProductUnitList) {
                if (unit.IsBasic) {
//                    itemViewHolder.tvUnit.setText(unit.Name);
                    unitName = unit.Name;
                    unitId = unit.Id;
                    LogUtils.e(goodsBean.Name + "：" + unitName);
                }
            }
            productPrice = goodsBean.Price1;
            priceSystemID = goodsBean.PriceSystemList.get(1).Id;
            for (GoodsListEntity.ValueEntity.PriceSystemListEntity price : goodsBean.PriceSystemList) {

                if (price.IsOrderMeetingPrice) {
                    switch (price.Id) {
                        case 0:
                            productPrice = goodsBean.Price0;
                            priceSystemID = 0;
                            break;
                        case 1:
                            productPrice = goodsBean.Price1;
                            priceSystemID = 1;
                            break;
                        case 2:
                            productPrice = goodsBean.Price2;
                            priceSystemID = 2;
                            break;
                        case 3:
                            productPrice = goodsBean.Price3;
                            priceSystemID = 3;
                            break;
                        case 4:
                            productPrice = goodsBean.Price4;
                            priceSystemID = 4;
                            break;
                        case 5:
                            productPrice = goodsBean.Price5;
                            priceSystemID = 5;
                            break;
                        case 6:
                            productPrice = goodsBean.Price6;
                            priceSystemID = 6;
                            break;
                        case 7:
                            productPrice = goodsBean.Price7;
                            priceSystemID = 7;
                            break;
                        case 8:
                            productPrice = goodsBean.Price8;
                            priceSystemID = 8;
                            break;
                        case 9:
                            productPrice = goodsBean.Price9;
                            priceSystemID = 9;
                            break;
                        case 10:
                            productPrice = goodsBean.Price10;
                            priceSystemID = 10;
                            break;
                    }
                }

            }

            itemViewHolder.tvPrice.setText(productPrice + "元/" + unitName);

            String tempUnitName = unitName;
            double tempProductPrice = productPrice;
            int tempUnitId = unitId;
            int tempPriceSystemID = priceSystemID;
            if (type) {
                RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class).where(SaleGoodsItemModel_Table.ProductId.eq(goodsBean.Id)))
                        .queryList()
                        .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                            @Override
                            public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModel) throws Exception {
                                if (saleGoodsItemModel != null && saleGoodsItemModel.size() > 0) {
                                    itemViewHolder.btnEle.setCount(saleGoodsItemModel.get(0).Quantity);
                                } else {
                                    itemViewHolder.btnEle.setCount(0);
                                }
                                onbind = false;
                            }
                        });
            }
//            RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class)
//                    .where(SaleGoodsItemModel_Table.ProductId.eq(goodsBean.Id)))
//                    .queryList().subscribe(new Consumer<List<SaleGoodsItemModel>>() {
//                @Override
//                public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModels) throws Exception {
//                    LogUtils.e(saleGoodsItemModels + "\n长度" + saleGoodsItemModels.size());
//                    if (saleGoodsItemModels.size() > 0) {
//                        SaleGoodsItemModel mItem = saleGoodsItemModels.get(0);
//                        LogUtils.e(mItem.ProductName + "New设置AnimShopButton的当前Count：" + mItem.Quantity);
//                        itemViewHolder.btnEle.setCount(mItem.Quantity);
////                        mItem.update().subscribe(new Consumer<Boolean>() {
////                            @Override
////                            public void accept(@NonNull Boolean aBean) throws Exception {
////                                LogUtils.e(mItem.ProductName + "设置AnimShopButton的当前Count：" + mItem.Quantity);
////                                itemViewHolder.btnEle.setCount(mItem.Quantity);
////                            }
////                        });
//                    }
//
//                }
//            });

            if (type) {
                itemViewHolder.btnEle.setOnAddDelListener(new IOnAddDelListener() {
                    @Override
                    public void onAddSuccess(int i) {
                        RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class)
                                .where(SaleGoodsItemModel_Table.ProductId.eq(goodsBean.Id)))
                                .queryList().subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                            @Override
                            public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModels) throws Exception {
                                LogUtils.e(saleGoodsItemModels + "\n长度" + saleGoodsItemModels.size());
                                if (saleGoodsItemModels.size() > 0) {
                                    SaleGoodsItemModel mItem = saleGoodsItemModels.get(0);
                                    mItem.Quantity = i;
                                    mItem.CurrentPrice = mItem.UnitPrice * mItem.Quantity;
                                    mItem.update().subscribe(new Consumer<Boolean>() {
                                        @Override
                                        public void accept(@NonNull Boolean aBean) throws Exception {
                                            LogUtils.e(mItem.ProductName + "：数量修改为" + i);
                                        }
                                    });
                                } else {
                                    add(goodsBean, i, tempProductPrice, tempUnitName, tempUnitId, tempPriceSystemID);
                                }

                            }
                        });
                    }

                    @Override
                    public void onAddFailed(int i, FailType failType) {
                        LogUtils.e(goodsBean.Name + "添加失败" + i + "\nFailType" + failType);
                    }

                    @Override
                    public void onDelSuccess(int i) {
                        RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class)
                                .where(SaleGoodsItemModel_Table.ProductId.eq(goodsBean.Id)))
                                .queryList().subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                            @Override
                            public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModels) throws Exception {
                                LogUtils.e(saleGoodsItemModels + "\n长度" + saleGoodsItemModels.size());
                                if (saleGoodsItemModels.size() > 0) {
                                    SaleGoodsItemModel mItem = saleGoodsItemModels.get(0);
                                    mItem.Quantity = i;
                                    mItem.CurrentPrice = mItem.UnitPrice * mItem.Quantity;
                                    mItem.update().subscribe(new Consumer<Boolean>() {
                                        @Override
                                        public void accept(@NonNull Boolean aBean) throws Exception {
                                            LogUtils.e(mItem.ProductName + "：数量修改为" + i);
                                        }
                                    });
                                } else {
                                    add(goodsBean, i, tempProductPrice, tempUnitName, tempUnitId, tempPriceSystemID);
                                }

                            }
                        });
                    }

                    @Override
                    public void onDelFaild(int i, FailType failType) {
                        LogUtils.e(goodsBean.Name + "减少失败" + i + "\nFailType" + failType);
                    }
                });
            }
        }

        super.onBindViewHolder(holder, position);
    }

    private void add(GoodsListEntity.ValueEntity goodsBean, int count, double productPrice, String unitName, int unitId, int priceSystemID) {
        SaleGoodsItemModel itemModel = new SaleGoodsItemModel();
        itemModel.CurrentPrice = productPrice;
        if (goodsBean.ProductPictureList != null && goodsBean.ProductPictureList.size() > 0) {
            itemModel.Path = goodsBean.ProductPictureList.get(0).Path;
        }
        itemModel.PriceSystemId = priceSystemID;
        itemModel.PackSpec = goodsBean.PackSpec;
        itemModel.Code = goodsBean.Code;
        itemModel.ProductName = goodsBean.Name;
        itemModel.BasicUnitName = unitName;
        itemModel.ProductUnitName = unitName;
        itemModel.BasicUnitPrice = productPrice;
        itemModel.UnitPrice = productPrice;
        itemModel.Discount = 1.0f;
        itemModel.SalesType = 1;
        itemModel.TaxRate = 1.0;
        itemModel.UnitId = unitId;
        itemModel.Quantity = count;
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
        itemModel.insert()
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        LogUtils.e("Item插入数据成功\n用时：" + DateUtil.getFormatTime(aLong));
                    }
                });
        itemModel.save().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
                LogUtils.e("Item保存数据成功");
            }
        });
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
                    goodsUnitModel.insert().subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(@NonNull Long aLong) throws Exception {
                            LogUtils.e("Unit插入数据成功\n用时：" + DateUtil.getFormatTime(aLong));
                        }
                    });

                });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ItemViewHolder extends ClickableViewHolder {

        ImageView itemImg;
        TextView tvName;
        TextView tvPackSpec;
        TextView tvPrice;
        TextView tvCode;
        AnimShopButton btnEle;

        public ItemViewHolder(View itemView) {

            super(itemView);

            itemImg = $(R.id.item_img);
            tvName = $(R.id.tv_name);
            tvPackSpec = $(R.id.tv_PackSpec);
            tvPrice = $(R.id.tv_price);
            btnEle = $(R.id.btnEle);
            tvCode = $(R.id.tv_Code);
        }
    }
}
