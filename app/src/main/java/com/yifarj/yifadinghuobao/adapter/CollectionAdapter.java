package com.yifarj.yifadinghuobao.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mcxtzhang.lib.AnimShopButton;
import com.mcxtzhang.lib.IOnAddDelListener;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.helper.AbsRecyclerViewAdapter;
import com.yifarj.yifadinghuobao.database.model.CollectionItemModel;
import com.yifarj.yifadinghuobao.database.model.CollectionItemModel_Table;
import com.yifarj.yifadinghuobao.database.model.GoodsUnitModel;
import com.yifarj.yifadinghuobao.database.model.GoodsUnitModel_Table;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel_Table;
import com.yifarj.yifadinghuobao.model.entity.GoodsListEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.ui.activity.main.CollectionActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.DateUtil;
import com.yifarj.yifadinghuobao.utils.PreferencesUtil;
import com.yifarj.yifadinghuobao.view.CzechYuanDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * CollectionAdapter
 * <p>
 * Created by zydx-pc on 2017/7/20.
 */
public class CollectionAdapter extends AbsRecyclerViewAdapter {
    private List<CollectionItemModel> itemData;
    public static Map<Integer, Set<Integer>> selectedMap = new HashMap<Integer, Set<Integer>>();
    private CollectionActivity context;
    private AddShoppingCartClickListener mAddShoppingCartClickListener;
    private int orderCount = 0;

    public CollectionAdapter(RecyclerView recyclerView, List<CollectionItemModel> mItemData, CollectionActivity context) {
        super(recyclerView);
        this.itemData = mItemData;
        this.context = context;
    }

    public interface AddShoppingCartClickListener {
        void onAddShoppingCartClickListener(View view, CollectionItemModel goodsEntity);
    }

    public void setAddShoppingCartClickListener(AddShoppingCartClickListener l) {
        this.mAddShoppingCartClickListener = l;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).
                inflate(R.layout.item_collection_list, parent, false));
    }


    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            CollectionItemModel goodsBean = itemData.get(position);
            selectedMap.clear();

            // 查询购物车里是否有当前商品
            RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class)
                    .where(SaleGoodsItemModel_Table.ProductId.eq(goodsBean.ProductId)))
                    .queryList()
                    .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                        @Override
                        public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModel) throws Exception {
                            if (saleGoodsItemModel != null && saleGoodsItemModel.size() > 0) {
//                                itemViewHolder.btnEle.setCount(saleGoodsItemModel.get(0).Quantity);
                                itemViewHolder.addShopCart.setImageResource(R.drawable.ic_add_shoppingcart_selected);
                            } else {
                                itemViewHolder.addShopCart.setImageResource(R.drawable.ic_add_shoppingcart_default);
                            }
                        }
                    });

            // 查询购物车商品
            RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class).where())
                    .queryList()
                    .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                        @Override
                        public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModels) throws Exception {
                            LogUtils.e("saleGoodsItemModels：" + saleGoodsItemModels.size());
                            orderCount = saleGoodsItemModels.size();
                            if (orderCount > 0) {
                                context.setRightIcon(View.VISIBLE, orderCount);
                            } else if (orderCount == 0) {
                                context.setRightIcon(View.GONE, 0);
                            }
                            LogUtils.e("orderCount：" + orderCount);
                        }
                    });

            if (goodsBean.Path != null) {
                Glide.with(getContext())
                        .load(AppInfoUtil.genPicUrl(goodsBean.Path))
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.default_image)
                        .dontAnimate()
                        .into(itemViewHolder.itemImg);
            }

            itemViewHolder.llDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CzechYuanDialog mDialog = new CzechYuanDialog(getContext(), 200, 100, R.style.CzechYuanDialog);
                    mDialog.setContent("确定删除？");
                    mDialog.setConfirmClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RXSQLite.rx(SQLite.select().from(CollectionItemModel.class)
                                    .where(CollectionItemModel_Table.ProductId.eq(goodsBean.ProductId)))
                                    .queryList().subscribe(new Consumer<List<CollectionItemModel>>() {
                                @Override
                                public void accept(@NonNull List<CollectionItemModel> collectionItemModels) throws Exception {
                                    LogUtils.e("collectionItemModels.size()：" + collectionItemModels.size());
                                    if (collectionItemModels.size() > 0) {
                                        CollectionItemModel mItem = collectionItemModels.get(0);
                                        mItem.delete().subscribe(new Consumer<Boolean>() {
                                            @Override
                                            public void accept(@NonNull Boolean aBean) throws Exception {
                                                LogUtils.e(mItem.ProductName + "：删除\n" + aBean);
                                                itemData.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, getItemCount());
                                            }
                                        });
                                        ToastUtils.showShortSafe("删除成功");
                                    }
                                }
                            });
                        }
                    });
                }
            });

            itemViewHolder.tvName.setText(goodsBean.ProductName);
            itemViewHolder.tvCode.setText("编号：" + goodsBean.Code.substring(goodsBean.Code.length() - 4, goodsBean.Code.length()));
            itemViewHolder.tvPackSpec.setText(goodsBean.PackSpec);
            itemViewHolder.tvBasicPrice.setText(goodsBean.BasicUnitPrice + "元/" + goodsBean.BasicUnitName);
            itemViewHolder.addShopCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mAddShoppingCartClickListener != null) {
                        mAddShoppingCartClickListener.onAddShoppingCartClickListener(view, goodsBean);
                    }
                }
            });
            itemViewHolder.btnEle.setOnAddDelListener(new IOnAddDelListener() {
                @Override
                public void onAddSuccess(int i) {
                    RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class)
                            .where(SaleGoodsItemModel_Table.ProductId.eq(goodsBean.ProductId)))
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
                                                context.setRightIcon(View.VISIBLE, orderCount);
                                                LogUtils.e("orderCount：" + orderCount);
                                            }
                                        }
                                    }
                                });
                            } else {
                                add(goodsBean, i);
                                LogUtils.e("onAddSuccess---add---" + goodsBean.ProductName);
                                if (i == 1) {
                                    orderCount = orderCount + 1;
                                    if (orderCount > 0) {
                                        context.setRightIcon(View.VISIBLE, orderCount);
                                        LogUtils.e("orderCount：" + orderCount);
                                    }
                                }
                            }

                        }
                    });
                }

                @Override
                public void onAddFailed(int i, FailType failType) {
                    LogUtils.e(goodsBean.ProductName + "添加失败" + i + "\nFailType" + failType);
                }

                @Override
                public void onDelSuccess(int i) {
                    RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class)
                            .where(SaleGoodsItemModel_Table.ProductId.eq(goodsBean.ProductId)))
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
                                            delete(goodsBean.ProductId);
                                            orderCount = orderCount - 1;
                                            if (orderCount > 0) {
                                                context.setRightIcon(View.VISIBLE, orderCount);
                                                LogUtils.e("orderCount：" + orderCount);
                                            } else if (orderCount == 0) {
                                                context.setRightIcon(View.GONE, 0);
                                                LogUtils.e("orderCount：" + orderCount);
                                            }
                                        }
                                    }
                                });
                            } else {
                                add(goodsBean, i);
                                LogUtils.e("onDelSuccess---add---" + goodsBean.ProductName);
                            }

                        }
                    });
                }

                @Override
                public void onDelFaild(int i, FailType failType) {
                    LogUtils.e(goodsBean.ProductName + "减少失败" + i + "\nFailType" + failType);
                }
            });

        }
        super.onBindViewHolder(holder, position);
    }

    private void add(CollectionItemModel goodsBean, int count) {
        SaleGoodsItemModel itemModel = new SaleGoodsItemModel();
        itemModel.CurrentPrice = goodsBean.CurrentPrice;
        itemModel.Path = goodsBean.Path;
        itemModel.PriceSystemId = DataSaver.getPriceSystemId();
        itemModel.PackSpec = goodsBean.PackSpec;
        itemModel.Code = goodsBean.Code;
        itemModel.ProductName = goodsBean.ProductName;
        itemModel.BasicUnitName = goodsBean.BasicUnitName;
        itemModel.ProductUnitName = goodsBean.ProductUnitName;
        itemModel.BasicUnitPrice = goodsBean.BasicUnitPrice;
        itemModel.UnitPrice = goodsBean.UnitPrice;
        itemModel.Discount = 1.0f;
        itemModel.SalesType = 1;
        itemModel.TaxRate = 1.0;
        itemModel.UnitId = goodsBean.UnitId;
        itemModel.Quantity = count;
        itemModel.WarehouseId = goodsBean.WarehouseId;
        itemModel.ProductId = goodsBean.ProductId;
        itemModel.LocationId = goodsBean.LocationId;
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
        int traderId = PreferencesUtil.getInt("TraderId", 0);
        //获取当前商品的详细信息
        RetrofitHelper.getGoodsListAPI()
                .getGoodsList("ProductList", "", "Id =" + goodsBean.ProductId, "[" + traderId + "]", AppInfoUtil.getToken())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GoodsListEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull GoodsListEntity goodsListEntity) {
                        GoodsListEntity.ValueEntity goodsEntity = goodsListEntity.Value.get(0);
                        if (!goodsListEntity.HasError) {
                            Flowable.fromIterable(goodsEntity.ProductUnitList)
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
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void delete(int productId) {
        RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class)
                .where(SaleGoodsItemModel_Table.ProductId.eq(productId)))
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
                .where(GoodsUnitModel_Table.ProductId.eq(productId)))
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
        return itemData.size();
    }

    public class ItemViewHolder extends ClickableViewHolder {
        ImageView itemImg;
        LinearLayout llDelete;
        TextView tvName;
        TextView tvPackSpec;
        TextView tvBasicPrice;
        TextView tvCode;
        AnimShopButton btnEle;
        ImageView addShopCart;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemImg = $(R.id.item_img);
            llDelete = $(R.id.ll_delete);
            tvName = $(R.id.tv_name);
            tvBasicPrice = $(R.id.tv_price);
            tvPackSpec = $(R.id.tv_PackSpec);
            tvCode = $(R.id.tv_Code);
            btnEle = $(R.id.btnEle);
            addShopCart = $(R.id.addShopCart);
        }
    }

}
