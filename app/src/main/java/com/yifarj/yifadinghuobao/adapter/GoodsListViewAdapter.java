package com.yifarj.yifadinghuobao.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.yifarj.yifadinghuobao.database.model.ReturnGoodsUnitModel;
import com.yifarj.yifadinghuobao.database.model.ReturnGoodsUnitModel_Table;
import com.yifarj.yifadinghuobao.database.model.ReturnListItemModel;
import com.yifarj.yifadinghuobao.database.model.ReturnListItemModel_Table;
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
    private int saleType = 0;
    private SparseArray<View> vmap = new SparseArray<View>();

    public GoodsListViewAdapter(List<GoodsListEntity.ValueEntity> data, TabGoodsFragment context, int icon, BaseActivity activity, boolean type, int saleType) {
        this.data = data;
        this.type = type;
        this.context = context;
        this.icon = icon;
        this.activity = activity;
        currentContext = context == null ? activity.getBaseContext() : context.getContext();
        this.saleType = saleType;
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
        if (vmap.get(position) == null) {
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
            vmap.put(position, convertView);
        } else {
            convertView = vmap.get(position);
            holder = (ViewHolder) convertView.getTag();
        }
        onbind = true;
        String unitName = null;
        int unitId = 0;
        holder.itemImg.setImageResource(R.drawable.default_image);
        if (goodsBean.ProductPictureList != null && goodsBean.ProductPictureList.size() > 0) {
            Glide.with(currentContext)
                    .load(AppInfoUtil.genPicUrl(goodsBean.ProductPictureList.get(0).Path))
                    .fitCenter()
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
            if (saleType == 1) {
                // 查询退货清单中是否有当前商品
                RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class)
                        .where(ReturnListItemModel_Table.ProductId.eq(goodsBean.Id)))
                        .queryList()
                        .subscribe(new Consumer<List<ReturnListItemModel>>() {
                            @Override
                            public void accept(@NonNull List<ReturnListItemModel> returnListItemModels) throws Exception {
                                if (returnListItemModels != null && returnListItemModels.size() > 0) {
                                    holder.btnEle.setCount(returnListItemModels.get(0).Quantity);
                                } else {
                                    holder.btnEle.setCount(0);
                                }
                                onbind = false;
                            }
                        });
            } else {
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
        }

        if (type) {
            if (saleType == 1) {
                // 查询退货清单商品
                RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class).where())
                        .queryList()
                        .subscribe(new Consumer<List<ReturnListItemModel>>() {
                            @Override
                            public void accept(@NonNull List<ReturnListItemModel> returnListItemModels) throws Exception {
                                LogUtils.e("returnListItemModel：" + returnListItemModels.size());
                                orderCount = returnListItemModels.size();
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
                holder.btnEle.setHintText("退货");
                holder.btnEle.setOnAddDelListener(new IOnAddDelListener() {
                    @Override
                    public void onAddSuccess(int i) {
                        RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class)
                                .where(ReturnListItemModel_Table.ProductId.eq(goodsBean.Id)))
                                .queryList().subscribe(new Consumer<List<ReturnListItemModel>>() {
                            @Override
                            public void accept(@NonNull List<ReturnListItemModel> returnListItemModels) throws Exception {
                                LogUtils.e(returnListItemModels + "\n长度" + returnListItemModels.size());
                                if (returnListItemModels.size() > 0) {
                                    ReturnListItemModel mItem = returnListItemModels.get(0);
                                    mItem.SalesType = 2;
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
                                    addReturnList(goodsBean, i, tempUnitName, tempUnitId);
                                    LogUtils.e("onAddReturnListSuccess---add---" + goodsBean.Name);
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
                        RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class)
                                .where(ReturnListItemModel_Table.ProductId.eq(goodsBean.Id)))
                                .queryList().subscribe(new Consumer<List<ReturnListItemModel>>() {
                            @Override
                            public void accept(@NonNull List<ReturnListItemModel> returnListItemModels) throws Exception {
                                LogUtils.e(returnListItemModels + "\n长度" + returnListItemModels.size());
                                if (returnListItemModels.size() > 0) {
                                    ReturnListItemModel mItem = returnListItemModels.get(0);
                                    mItem.Quantity = i;
                                    mItem.CurrentPrice = mItem.UnitPrice * mItem.Quantity;
                                    mItem.update().subscribe(new Consumer<Boolean>() {
                                        @Override
                                        public void accept(@NonNull Boolean aBean) throws Exception {
                                            LogUtils.e(mItem.ProductName + "：数量减少为" + i);
                                            if (i == 0) {
                                                deleteReturnList(goodsBean);
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
                                    addReturnList(goodsBean, i, tempUnitName, tempUnitId);
                                    LogUtils.e("onDelReturnListSuccess---add---" + goodsBean.Name);
                                }

                            }
                        });
                    }

                    @Override
                    public void onDelFaild(int i, FailType failType) {
                        LogUtils.e(goodsBean.Name + "减少失败" + i + "\nFailType" + failType);
                    }
                });

            } else {
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

    private void addReturnList(GoodsListEntity.ValueEntity goodsBean, int count, String unitName, int unitId) {
        ReturnListItemModel itemModel = new ReturnListItemModel();
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
        itemModel.SalesType = 2;
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
                    ReturnGoodsUnitModel goodsUnitModel = new ReturnGoodsUnitModel();
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

    private void deleteReturnList(GoodsListEntity.ValueEntity goodsBean) {
        RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class)
                .where(ReturnListItemModel_Table.ProductId.eq(goodsBean.Id)))
                .queryList().subscribe(new Consumer<List<ReturnListItemModel>>() {
            @Override
            public void accept(@NonNull List<ReturnListItemModel> returnListItemModel) throws Exception {
                LogUtils.e("returnListItemModel.size()：" + returnListItemModel.size());
                if (returnListItemModel.size() > 0) {
                    ReturnListItemModel mItem = returnListItemModel.get(0);
                    mItem.delete().subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(@NonNull Boolean aBean) throws Exception {
                            LogUtils.e(mItem.ProductName + "：删除\n" + aBean);
                        }
                    });
                }

            }
        });
        RXSQLite.rx(SQLite.select().from(ReturnGoodsUnitModel.class)
                .where(ReturnGoodsUnitModel_Table.ProductId.eq(goodsBean.Id)))
                .queryList()
                .subscribe(new Consumer<List<ReturnGoodsUnitModel>>() {
                    @Override
                    public void accept(@NonNull List<ReturnGoodsUnitModel> goodsUnitModels) throws Exception {
                        if (goodsUnitModels != null && goodsUnitModels.size() > 0) {
                            Flowable.fromIterable(goodsUnitModels)
                                    .forEach(new Consumer<ReturnGoodsUnitModel>() {
                                        @Override
                                        public void accept(@NonNull ReturnGoodsUnitModel goodsUnitModel) throws Exception {
                                            ReturnGoodsUnitModel mUnitModel = goodsUnitModel;
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

    public void updataView(int position, int quantity, ListView mListView) {
        //得到第一个可显示控件的位置
        int visibleFirstPosition = mListView.getFirstVisiblePosition();
        int visibleLastPosition = mListView.getLastVisiblePosition();
        //只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
        if (position >= visibleFirstPosition && position <= visibleLastPosition) {
            //得到要更新的item的view
            View view = mListView.getChildAt(position - visibleFirstPosition);
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.btnEle.setCount(quantity);
//                    getView(position,view,mListView);
        }
    }

}
