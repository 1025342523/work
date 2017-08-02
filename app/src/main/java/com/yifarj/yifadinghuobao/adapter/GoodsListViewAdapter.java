package com.yifarj.yifadinghuobao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.yifarj.yifadinghuobao.database.model.GoodsUnitModel;
import com.yifarj.yifadinghuobao.database.model.GoodsUnitModel_Table;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel_Table;
import com.yifarj.yifadinghuobao.model.entity.GoodsListEntity;
import com.yifarj.yifadinghuobao.model.entity.ProductUnitEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.ui.fragment.goods.TabGoodsFragment;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.DateUtil;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * GoodsListViewAdapter
 *
 * @auther Czech.Yuan
 * @date 2017/5/15 15:56
 */
public class GoodsListViewAdapter extends BaseAdapter {
    public List<GoodsListEntity.ValueEntity> data;
    public boolean onbind;
    private boolean type;
    private int orderCount = 0;
    private int icon;
    private TabGoodsFragment context;
    private BaseActivity activity;
    private Context currentContext;

    public GoodsListViewAdapter(List<GoodsListEntity.ValueEntity> data, TabGoodsFragment context, int icon, BaseActivity activity, boolean type) {
        this.data = data;
        this.type = type;
        this.context = context;
        this.icon = icon;
        this.activity = activity;
        currentContext = context == null ? activity.getApplicationContext() : context.getContext();
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data == null ? null : data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final GoodsListEntity.ValueEntity goodsBean = data.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(currentContext).inflate(R.layout.item_goods_list, parent, false);
            holder.itemImg = (ImageView) convertView.findViewById(R.id.item_img);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvPackSpec = (TextView) convertView.findViewById(R.id.tv_PackSpec);
            holder.tvCode = (TextView) convertView.findViewById(R.id.tv_Code);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
            holder.btnEle = (AnimShopButton) convertView.findViewById(R.id.btnEle);
            holder.tvIcon = (TextView) convertView.findViewById(R.id.tv_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        onbind = true;
        String unitName = null;
        int unitId = 0;
        holder.itemImg.setImageResource(R.drawable.default_image);
        if (goodsBean.ProductPictureList != null && goodsBean.ProductPictureList.size() > 0) {
            Glide.with(currentContext)
                    .load(AppInfoUtil.genPicUrl(goodsBean.ProductPictureList.get(0).Path))
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.default_image)
                    .dontAnimate()
                    .into(holder.itemImg);
        }

        switch (icon) {
            case 0:
                break;
            case 1:
                holder.tvIcon.setVisibility(View.VISIBLE);
                holder.tvIcon.setText("促销");
                break;
            case 2:
                holder.tvIcon.setVisibility(View.VISIBLE);
                holder.tvIcon.setText("新品");
                break;
            case 3:
                holder.tvIcon.setVisibility(View.VISIBLE);
                holder.tvIcon.setText("推荐");
                break;
        }

        holder.tvPackSpec.setText(goodsBean.PackSpec);
        if (goodsBean.Code.length() <= 6) {
            holder.tvCode.setText("编号：" + goodsBean.Code);
        } else {
            holder.tvCode.setText("编号：" + goodsBean.Code.substring(goodsBean.Code.length() - 4, goodsBean.Code.length()));
        }
        holder.tvName.setText(goodsBean.Name);
        for (ProductUnitEntity.ValueEntity unit : goodsBean.ProductUnitList) {
            if (unit.IsBasic) {
                unitName = unit.Name;
                unitId = unit.Id;
                LogUtils.e(goodsBean.Name + "：" + unitName);
            }
        }
        holder.tvPrice.setText(goodsBean.MemoryPrice + "元/" + unitName);

        String tempUnitName = unitName;
        int tempUnitId = unitId;
        if (type) {
            RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class)
                    .where(SaleGoodsItemModel_Table.ProductId.eq(goodsBean.Id)))
                    .queryList()
                    .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                        @Override
                        public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModel) throws Exception {
                            if (saleGoodsItemModel != null && saleGoodsItemModel.size() > 0) {
                                holder.btnEle.setCount(saleGoodsItemModel.get(0).Quantity);
                            } else {
                                holder.btnEle.setCount(0);
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
                            LogUtils.e("saleGoodsItemModels：" + saleGoodsItemModels.size());
                            orderCount = saleGoodsItemModels.size();
                            if (orderCount > 0) {
                                if (context != null) {
                                    context.setRightIcon(View.VISIBLE, orderCount);
                                } else if (activity != null) {
                                    activity.setRightIcon(View.VISIBLE, orderCount);
                                }
                            } else if (orderCount == 0) {
                                if (context != null) {
                                    context.setRightIcon(View.GONE, 0);
                                } else if (activity != null) {
                                    activity.setRightIcon(View.GONE, 0);
                                }
                            }
                            LogUtils.e("orderCount：" + orderCount);
                        }
                    });

            holder.btnEle.setOnAddDelListener(new IOnAddDelListener() {
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
                                        if (i == 1) {
                                            orderCount = orderCount + 1;
                                            if (orderCount > 0) {
                                                if (context != null) {
                                                    context.setRightIcon(View.VISIBLE, orderCount);
                                                } else if (activity != null) {
                                                    activity.setRightIcon(View.VISIBLE, orderCount);
                                                }
                                                LogUtils.e("orderCount：" + orderCount);
                                            }
                                        }
                                    }
                                });
                            } else {
                                add(goodsBean, i, tempUnitName, tempUnitId);
                                LogUtils.e("onAddSuccess---add---" + goodsBean.Name);
                                if (i == 1) {
                                    orderCount = orderCount + 1;
                                    if (orderCount > 0) {
                                        if (context != null) {
                                            context.setRightIcon(View.VISIBLE, orderCount);
                                        } else if (activity != null) {
                                            activity.setRightIcon(View.VISIBLE, orderCount);
                                        }
                                        LogUtils.e("orderCount：" + orderCount);
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
                                        if (i == 0) {
                                            delete(goodsBean);
                                            orderCount = orderCount - 1;
                                            if (orderCount > 0) {
                                                if (context != null) {
                                                    context.setRightIcon(View.VISIBLE, orderCount);
                                                } else if (activity != null) {
                                                    activity.setRightIcon(View.VISIBLE, orderCount);
                                                }
                                                LogUtils.e("orderCount：" + orderCount);
                                            } else if (orderCount == 0) {
                                                if (context != null) {
                                                    context.setRightIcon(View.GONE, 0);
                                                } else if (activity != null) {
                                                    activity.setRightIcon(View.GONE, 0);
                                                }
                                                LogUtils.e("orderCount：" + orderCount);
                                            }
                                        }
                                    }
                                });
                            } else {
                                add(goodsBean, i, tempUnitName, tempUnitId);
                                LogUtils.e("onDelSuccess---add---" + goodsBean.Name);
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
        return convertView;
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


    static class ViewHolder {
        ImageView itemImg;
        TextView tvName;
        TextView tvPackSpec;
        TextView tvPrice;
        TextView tvCode;
        AnimShopButton btnEle;
        TextView tvIcon;
    }

}
