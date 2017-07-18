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
import com.yifarj.yifadinghuobao.database.model.GoodsUnitModel_Table;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel_Table;
import com.yifarj.yifadinghuobao.model.entity.GoodsListEntity;
import com.yifarj.yifadinghuobao.model.entity.ProductUnitEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.ui.fragment.goods.TabGoodsFragment;
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
    private int orderCount=0;
    private TabGoodsFragment context;

    public GoodsListAdapter(RecyclerView recyclerView, List<GoodsListEntity.ValueEntity> data, boolean type,TabGoodsFragment context) {
        super(recyclerView);
        this.data = data;
        this.type = type;
        this.context = context;
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
            if (goodsBean.Code.length() <= 6) {
                itemViewHolder.tvCode.setText("编号：" + goodsBean.Code);
            } else {
                itemViewHolder.tvCode.setText("编号：" + goodsBean.Code.substring(goodsBean.Code.length() - 4, goodsBean.Code.length()));
            }
            itemViewHolder.tvName.setText(goodsBean.Name);
            for (ProductUnitEntity.ValueEntity unit : goodsBean.ProductUnitList) {
                if (unit.IsBasic) {
                    unitName = unit.Name;
                    unitId = unit.Id;
                    LogUtils.e(goodsBean.Name + "：" + unitName);
                }
            }
            itemViewHolder.tvPrice.setText(goodsBean.MemoryPrice + "元/" + unitName);

            String tempUnitName = unitName;
            int tempUnitId = unitId;
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

            if (type) {
                // 查询购物车商品
                RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class).where())
                        .queryList()
                        .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                            @Override
                            public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModels) throws Exception {
                                LogUtils.e("saleGoodsItemModels："+saleGoodsItemModels.size());
                                orderCount = saleGoodsItemModels.size();
                                if(orderCount>0){
                                    context.setRightIcon(View.VISIBLE,orderCount);
                                }else if(orderCount==0){
                                    context.setRightIcon(View.GONE,0);
                                }
                                LogUtils.e("orderCount："+orderCount);
                            }
                        });

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
                                            LogUtils.e(mItem.ProductName + "：数量增加为" + i);
                                            if(i==1){
                                                orderCount=orderCount+1;
                                                if (orderCount>0){
                                                    context.setRightIcon(View.VISIBLE,orderCount);
                                                    LogUtils.e("orderCount："+orderCount);
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    add(goodsBean, i, tempUnitName, tempUnitId);
                                    LogUtils.e("onAddSuccess---add---"+goodsBean.Name);
                                    if(i==1){
                                        orderCount=orderCount+1;
                                        if (orderCount>0){
                                            context.setRightIcon(View.VISIBLE,orderCount);
                                            LogUtils.e("orderCount："+orderCount);
                                        }
                                    }
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
                                            LogUtils.e(mItem.ProductName + "：数量减少为" + i);
                                            if(i==0){
                                                delete(goodsBean);
                                                orderCount=orderCount-1;
                                                if (orderCount>0){
                                                    context.setRightIcon(View.VISIBLE,orderCount);
                                                    LogUtils.e("orderCount："+orderCount);
                                                }else if(orderCount==0){
                                                    context.setRightIcon(View.GONE,0);
                                                    LogUtils.e("orderCount："+orderCount);
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    add(goodsBean, i, tempUnitName, tempUnitId);
                                    LogUtils.e("onDelSuccess---add---"+goodsBean.Name);
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

    private void add(GoodsListEntity.ValueEntity goodsBean, int count, String unitName, int unitId) {
        SaleGoodsItemModel itemModel = new SaleGoodsItemModel();
        itemModel.CurrentPrice = goodsBean.MemoryPrice;
        if (goodsBean.ProductPictureList != null && goodsBean.ProductPictureList.size() > 0) {
            itemModel.Path = goodsBean.ProductPictureList.get(0).Path;
        }
        itemModel.PriceSystemId = DataSaver.getPriceSystemId();
        itemModel.PackSpec = goodsBean.PackSpec;
        itemModel.Code = goodsBean.Code;
        itemModel.ProductName = goodsBean.Name;
        itemModel.BasicUnitName = unitName;
        itemModel.ProductUnitName = unitName;
        itemModel.BasicUnitPrice = goodsBean.MemoryPrice;
        itemModel.UnitPrice = goodsBean.MemoryPrice;
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

    private void delete(GoodsListEntity.ValueEntity goodsBean) {
        RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class)
                .where(SaleGoodsItemModel_Table.ProductId.eq(goodsBean.Id)))
                .queryList().subscribe(new Consumer<List<SaleGoodsItemModel>>() {
            @Override
            public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModels) throws Exception {
                LogUtils.e("saleGoodsItemModels.size()：" + saleGoodsItemModels.size());
                if (saleGoodsItemModels.size() > 0) {
                    SaleGoodsItemModel mItem = saleGoodsItemModels.get(0);
                    mItem.delete().subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(@NonNull Boolean aBean) throws Exception {
                            LogUtils.e(mItem.ProductName + "：删除\n" + aBean);
                        }
                    });
                }

            }
        });
        RXSQLite.rx(SQLite.select().from(GoodsUnitModel.class)
                .where(GoodsUnitModel_Table.ProductId.eq(goodsBean.Id)))
                .queryList()
                .subscribe(new Consumer<List<GoodsUnitModel>>() {
                    @Override
                    public void accept(@NonNull List<GoodsUnitModel> goodsUnitModels) throws Exception {
                        if (goodsUnitModels != null && goodsUnitModels.size() > 0) {
                            Flowable.fromIterable(goodsUnitModels)
                                    .forEach(new Consumer<GoodsUnitModel>() {
                                        @Override
                                        public void accept(@NonNull GoodsUnitModel goodsUnitModel) throws Exception {
                                            GoodsUnitModel mUnitModel = goodsUnitModel;
                                            mUnitModel.delete().subscribe(new Consumer<Boolean>() {
                                                @Override
                                                public void accept(@NonNull Boolean aBoolean) throws Exception {
                                                    LogUtils.e("单位：" + mUnitModel.Name + "删除成功");
                                                }
                                            });
                                        }
                                    });
                            LogUtils.e("删除成功");
                        }
                    }
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
