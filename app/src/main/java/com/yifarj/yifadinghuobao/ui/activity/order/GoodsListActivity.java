package com.yifarj.yifadinghuobao.ui.activity.order;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.ItemGoodsListAdapter;
import com.yifarj.yifadinghuobao.database.model.GoodsPropertyModel;
import com.yifarj.yifadinghuobao.database.model.GoodsPropertyModel_Table;
import com.yifarj.yifadinghuobao.database.model.GoodsUnitModel;
import com.yifarj.yifadinghuobao.database.model.GoodsUnitModel_Table;
import com.yifarj.yifadinghuobao.database.model.ReturnGoodsUnitModel;
import com.yifarj.yifadinghuobao.database.model.ReturnGoodsUnitModel_Table;
import com.yifarj.yifadinghuobao.database.model.ReturnListItemModel;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel;
import com.yifarj.yifadinghuobao.model.entity.CreateOrderEntity;
import com.yifarj.yifadinghuobao.model.entity.ProductPropertyListEntity;
import com.yifarj.yifadinghuobao.model.entity.ProductUnitEntity;
import com.yifarj.yifadinghuobao.model.entity.SaleGoodsItem;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.CommonUtil;
import com.yifarj.yifadinghuobao.view.CustomEmptyView;
import com.yifarj.yifadinghuobao.view.TitleView;
import com.yifarj.yifadinghuobao.view.utils.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 商品清单
 * <p>
 * Created by zydx-pc on 2017/7/28.
 */

public class GoodsListActivity extends BaseActivity {

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    CustomEmptyView mCustomEmptyView;
    @BindView(R.id.titleView)
    TitleView titleView;

    private List<SaleGoodsItem.ValueEntity> mItemData = new ArrayList<>();
    private ItemGoodsListAdapter mItemGoodsListAdapter;
    private CreateOrderEntity.ValueEntity orderInfo;

    private boolean isCreate;
    private int orderId, saleType = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_goods_list;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        loadData();

        titleView.setLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getIntentExtra() {
        isCreate = getIntent().getBooleanExtra("CreateOrder", false);
        orderId = getIntent().getIntExtra("orderId", 0);
        saleType = getIntent().getIntExtra("saleType", 0);
        LogUtils.e("isCreate：" + isCreate + "，orderId：" + orderId + "，saleType：" + saleType);
    }

    @Override
    public void loadData() {
        getIntentExtra();
        if (isCreate) {
            if (saleType == 1) {
                RXSQLite.rx(SQLite.select()
                        .from(ReturnListItemModel.class))
                        .queryList()
                        .subscribe(new Consumer<List<ReturnListItemModel>>() {
                            @Override
                            public void accept(@NonNull List<ReturnListItemModel> ReturnListItemModels) throws Exception {
                                if (ReturnListItemModels != null && ReturnListItemModels.size() > 0) {
                                    Flowable.fromIterable(ReturnListItemModels)
                                            .forEach(new Consumer<ReturnListItemModel>() {
                                                @Override
                                                public void accept(@NonNull ReturnListItemModel returnListItemModel) throws Exception {
                                                    SaleGoodsItem.ValueEntity mItem = new SaleGoodsItem.ValueEntity();
                                                    mItem.ProductProperyId1 = returnListItemModel.ParentProperyId1;
                                                    mItem.ProductProperyId2 = returnListItemModel.ParentProperyId2;
                                                    mItem.ProperyId1 = returnListItemModel.ProperyId1;
                                                    mItem.ProperyId2 = returnListItemModel.ProperyId2;
                                                    mItem.PriceSystemId = returnListItemModel.PriceSystemId;
                                                    mItem.CurrentPrice = returnListItemModel.CurrentPrice;
                                                    mItem.TotalPrice = returnListItemModel.CurrentPrice;
                                                    mItem.ImagePath = returnListItemModel.Path;
                                                    mItem.ProductName = returnListItemModel.ProductName;
                                                    mItem.ActualPrice = returnListItemModel.UnitPrice;
                                                    mItem.ActualUnitPrice = returnListItemModel.BasicUnitPrice;
                                                    mItem.ProductUnitName = returnListItemModel.ProductUnitName;
                                                    mItem.BasicUnitPrice = returnListItemModel.BasicUnitPrice;
                                                    mItem.UnitPrice = returnListItemModel.UnitPrice;
                                                    mItem.Discount = returnListItemModel.Discount;
                                                    mItem.SalesType = returnListItemModel.SalesType;
                                                    mItem.TaxRate = returnListItemModel.TaxRate;
                                                    mItem.UnitId = returnListItemModel.UnitId;
                                                    mItem.Quantity = returnListItemModel.Quantity;
                                                    mItem.WarehouseId = returnListItemModel.WarehouseId;
                                                    mItem.ProductId = returnListItemModel.ProductId;
                                                    mItem.LocationId = returnListItemModel.LocationId;
                                                    mItem.PackSpec = returnListItemModel.PackSpec;
                                                    mItem.Price0 = returnListItemModel.Price0;
                                                    mItem.Price1 = returnListItemModel.Price1;
                                                    mItem.Price2 = returnListItemModel.Price2;
                                                    mItem.Price3 = returnListItemModel.Price3;
                                                    mItem.Price4 = returnListItemModel.Price4;
                                                    mItem.Price5 = returnListItemModel.Price5;
                                                    mItem.Price6 = returnListItemModel.Price6;
                                                    mItem.Price7 = returnListItemModel.Price7;
                                                    mItem.Price8 = returnListItemModel.Price8;
                                                    mItem.Price9 = returnListItemModel.Price9;
                                                    mItem.Price10 = returnListItemModel.Price10;
                                                    mItem.MinSalesQuantity = returnListItemModel.MinSalesQuantity;
                                                    mItem.MaxSalesQuantity = returnListItemModel.MaxSalesQuantity;
                                                    mItem.MinSalesPrice = returnListItemModel.MinSalesPrice;
                                                    mItem.MaxPurchasePrice = returnListItemModel.MaxPurchasePrice;
                                                    mItem.DefaultLocationName = returnListItemModel.DefaultLocationName;
                                                    mItem.OweRemark = returnListItemModel.Remark;
                                                    mItem.BatchId = "";
                                                    mItem.Code = returnListItemModel.Code;
                                                    mItemData.add(mItem);
                                                    LogUtils.e("GoodsItem数量为：" + mItemData.size());
                                                    if (returnListItemModel.ProperyId1 != 0 && returnListItemModel.ProperyId2 != 0) {
                                                        RXSQLite.rx(SQLite.select().from(GoodsPropertyModel.class).where(GoodsPropertyModel_Table.ProductId.eq(mItem.ProductId), GoodsPropertyModel_Table.ParentId.eq(mItem.ProductProperyId1)))
                                                                .queryList()
                                                                .subscribe(new Consumer<List<GoodsPropertyModel>>() {
                                                                    @Override
                                                                    public void accept(@NonNull List<GoodsPropertyModel> goodsPropertyModels) throws Exception {
                                                                        if (goodsPropertyModels != null && goodsPropertyModels.size() > 0) {
                                                                            for (GoodsPropertyModel mModel : goodsPropertyModels) {
                                                                                ProductPropertyListEntity.ValueEntity mProductProperty = new ProductPropertyListEntity.ValueEntity();
                                                                                mProductProperty.Id = mModel.Id;
                                                                                mProductProperty.Name = mModel.Name;
                                                                                mProductProperty.Ordinal = mModel.Ordinal;
                                                                                mProductProperty.ParentId = mModel.ParentId;
                                                                                mProductProperty.Level = mModel.Level;
                                                                                mProductProperty.Path = mModel.Path;
                                                                                mProductProperty.ProductCount = mModel.ProductCount;
                                                                                mItem.ProperyList1.add(mProductProperty);
                                                                            }
                                                                        } else {
                                                                            LogUtils.e("属性1List为空");
                                                                        }
                                                                    }
                                                                });

                                                        RXSQLite.rx(SQLite.select().from(GoodsPropertyModel.class).where(GoodsPropertyModel_Table.ProductId.eq(mItem.ProductId), GoodsPropertyModel_Table.ParentId.eq(mItem.ProductProperyId2)))
                                                                .queryList()
                                                                .subscribe(new Consumer<List<GoodsPropertyModel>>() {
                                                                    @Override
                                                                    public void accept(@NonNull List<GoodsPropertyModel> goodsPropertyModels) throws Exception {
                                                                        if (goodsPropertyModels != null && goodsPropertyModels.size() > 0) {
                                                                            for (GoodsPropertyModel mModel : goodsPropertyModels) {
                                                                                ProductPropertyListEntity.ValueEntity mProductProperty = new ProductPropertyListEntity.ValueEntity();
                                                                                mProductProperty.Id = mModel.Id;
                                                                                mProductProperty.Name = mModel.Name;
                                                                                mProductProperty.Ordinal = mModel.Ordinal;
                                                                                mProductProperty.ParentId = mModel.ParentId;
                                                                                mProductProperty.Level = mModel.Level;
                                                                                mProductProperty.Path = mModel.Path;
                                                                                mProductProperty.ProductCount = mModel.ProductCount;
                                                                                mItem.ProperyList2.add(mProductProperty);
                                                                            }
                                                                        } else {
                                                                            LogUtils.e("属性2List为空");
                                                                        }
                                                                    }
                                                                });

                                                    }
                                                    RXSQLite.rx(SQLite.select().from(ReturnGoodsUnitModel.class).where(ReturnGoodsUnitModel_Table.ProductId.eq(mItem.ProductId)))
                                                            .queryList()
                                                            .subscribe(new Consumer<List<ReturnGoodsUnitModel>>() {
                                                                @Override
                                                                public void accept(@NonNull List<ReturnGoodsUnitModel> goodsUnitModels) throws Exception {
                                                                    if (goodsUnitModels != null && goodsUnitModels.size() > 0) {
                                                                        for (ReturnGoodsUnitModel mModel : goodsUnitModels) {
                                                                            if (mModel.ProductId == mItem.ProductId) {
                                                                                ProductUnitEntity.ValueEntity mUnitData = new ProductUnitEntity.ValueEntity();
                                                                                mUnitData.Id = mModel.Id;
                                                                                mUnitData.ProductId = mModel.ProductId;
                                                                                mUnitData.Name = mModel.Name;
                                                                                mUnitData.Factor = mModel.Factor;
                                                                                mUnitData.BasicFactor = mModel.BasicFactor;
                                                                                mUnitData.IsBasic = mModel.IsBasic;
                                                                                mUnitData.IsDefault = mModel.IsDefault;
                                                                                mUnitData.BreakupNotify = mModel.BreakupNotify;
                                                                                mUnitData.Ordinal = mModel.Ordinal;
                                                                                mItem.ProductUnitList.add(mUnitData);
                                                                            }
                                                                        }
                                                                    } else {
                                                                        LogUtils.e("单位List为空");
                                                                    }
                                                                }
                                                            });

                                                }
                                            });

                                }
                            }
                        });

            } else {
                RXSQLite.rx(SQLite.select()
                        .from(SaleGoodsItemModel.class))
                        .queryList()
                        .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                            @Override
                            public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModels) throws Exception {
                                if (saleGoodsItemModels != null && saleGoodsItemModels.size() > 0) {
                                    Flowable.fromIterable(saleGoodsItemModels)
                                            .forEach(new Consumer<SaleGoodsItemModel>() {
                                                @Override
                                                public void accept(@NonNull SaleGoodsItemModel saleGoodsItemModel) throws Exception {
                                                    SaleGoodsItem.ValueEntity mItem = new SaleGoodsItem.ValueEntity();
                                                    mItem.ProductProperyId1 = saleGoodsItemModel.ParentProperyId1;
                                                    mItem.ProductProperyId2 = saleGoodsItemModel.ParentProperyId2;
                                                    mItem.ProperyId1 = saleGoodsItemModel.ProperyId1;
                                                    mItem.ProperyId2 = saleGoodsItemModel.ProperyId2;
                                                    mItem.PriceSystemId = saleGoodsItemModel.PriceSystemId;
                                                    mItem.CurrentPrice = saleGoodsItemModel.CurrentPrice;
                                                    mItem.TotalPrice = saleGoodsItemModel.CurrentPrice;
                                                    mItem.ImagePath = saleGoodsItemModel.Path;
                                                    mItem.ProductName = saleGoodsItemModel.ProductName;
                                                    mItem.ActualPrice = saleGoodsItemModel.UnitPrice;
                                                    mItem.ActualUnitPrice = saleGoodsItemModel.BasicUnitPrice;
                                                    mItem.ProductUnitName = saleGoodsItemModel.ProductUnitName;
                                                    mItem.BasicUnitPrice = saleGoodsItemModel.BasicUnitPrice;
                                                    mItem.UnitPrice = saleGoodsItemModel.UnitPrice;
                                                    mItem.Discount = saleGoodsItemModel.Discount;
                                                    mItem.SalesType = saleGoodsItemModel.SalesType;
                                                    mItem.TaxRate = saleGoodsItemModel.TaxRate;
                                                    mItem.UnitId = saleGoodsItemModel.UnitId;
                                                    mItem.Quantity = saleGoodsItemModel.Quantity;
                                                    mItem.WarehouseId = saleGoodsItemModel.WarehouseId;
                                                    mItem.ProductId = saleGoodsItemModel.ProductId;
                                                    mItem.LocationId = saleGoodsItemModel.LocationId;
                                                    mItem.PackSpec = saleGoodsItemModel.PackSpec;
                                                    mItem.Price0 = saleGoodsItemModel.Price0;
                                                    mItem.Price1 = saleGoodsItemModel.Price1;
                                                    mItem.Price2 = saleGoodsItemModel.Price2;
                                                    mItem.Price3 = saleGoodsItemModel.Price3;
                                                    mItem.Price4 = saleGoodsItemModel.Price4;
                                                    mItem.Price5 = saleGoodsItemModel.Price5;
                                                    mItem.Price6 = saleGoodsItemModel.Price6;
                                                    mItem.Price7 = saleGoodsItemModel.Price7;
                                                    mItem.Price8 = saleGoodsItemModel.Price8;
                                                    mItem.Price9 = saleGoodsItemModel.Price9;
                                                    mItem.Price10 = saleGoodsItemModel.Price10;
                                                    mItem.MinSalesQuantity = saleGoodsItemModel.MinSalesQuantity;
                                                    mItem.MaxSalesQuantity = saleGoodsItemModel.MaxSalesQuantity;
                                                    mItem.MinSalesPrice = saleGoodsItemModel.MinSalesPrice;
                                                    mItem.MaxPurchasePrice = saleGoodsItemModel.MaxPurchasePrice;
                                                    mItem.DefaultLocationName = saleGoodsItemModel.DefaultLocationName;
                                                    mItem.OweRemark = saleGoodsItemModel.Remark;
                                                    mItem.BatchId = "";
                                                    mItem.Code = saleGoodsItemModel.Code;
                                                    mItemData.add(mItem);
                                                    LogUtils.e("GoodsItem数量为：" + mItemData.size());
                                                    {
                                                        RXSQLite.rx(SQLite.select().from(GoodsPropertyModel.class).where(GoodsPropertyModel_Table.ProductId.eq(mItem.ProductId), GoodsPropertyModel_Table.ParentId.eq(mItem.ProductProperyId1)))
                                                                .queryList()
                                                                .subscribe(new Consumer<List<GoodsPropertyModel>>() {
                                                                    @Override
                                                                    public void accept(@NonNull List<GoodsPropertyModel> goodsPropertyModels) throws Exception {
                                                                        if (goodsPropertyModels != null && goodsPropertyModels.size() > 0) {
                                                                            for (GoodsPropertyModel mModel : goodsPropertyModels) {
                                                                                ProductPropertyListEntity.ValueEntity mProductProperty = new ProductPropertyListEntity.ValueEntity();
                                                                                mProductProperty.Id = mModel.Id;
                                                                                mProductProperty.Name = mModel.Name;
                                                                                mProductProperty.Ordinal = mModel.Ordinal;
                                                                                mProductProperty.ParentId = mModel.ParentId;
                                                                                mProductProperty.Level = mModel.Level;
                                                                                mProductProperty.Path = mModel.Path;
                                                                                mProductProperty.ProductCount = mModel.ProductCount;
                                                                                mItem.ProperyList1.add(mProductProperty);
                                                                            }
                                                                        } else {
                                                                            LogUtils.e("属性1List为空");
                                                                        }
                                                                    }
                                                                });

                                                        RXSQLite.rx(SQLite.select().from(GoodsPropertyModel.class).where(GoodsPropertyModel_Table.ProductId.eq(mItem.ProductId), GoodsPropertyModel_Table.ParentId.eq(mItem.ProductProperyId2)))
                                                                .queryList()
                                                                .subscribe(new Consumer<List<GoodsPropertyModel>>() {
                                                                    @Override
                                                                    public void accept(@NonNull List<GoodsPropertyModel> goodsPropertyModels) throws Exception {
                                                                        if (goodsPropertyModels != null && goodsPropertyModels.size() > 0) {
                                                                            for (GoodsPropertyModel mModel : goodsPropertyModels) {
                                                                                ProductPropertyListEntity.ValueEntity mProductProperty = new ProductPropertyListEntity.ValueEntity();
                                                                                mProductProperty.Id = mModel.Id;
                                                                                mProductProperty.Name = mModel.Name;
                                                                                mProductProperty.Ordinal = mModel.Ordinal;
                                                                                mProductProperty.ParentId = mModel.ParentId;
                                                                                mProductProperty.Level = mModel.Level;
                                                                                mProductProperty.Path = mModel.Path;
                                                                                mProductProperty.ProductCount = mModel.ProductCount;
                                                                                mItem.ProperyList2.add(mProductProperty);
                                                                            }
                                                                        } else {
                                                                            LogUtils.e("属性2List为空");
                                                                        }
                                                                    }
                                                                });

                                                    }
                                                    RXSQLite.rx(SQLite.select().from(GoodsUnitModel.class).where(GoodsUnitModel_Table.ProductId.eq(mItem.ProductId)))
                                                            .queryList()
                                                            .subscribe(new Consumer<List<GoodsUnitModel>>() {
                                                                @Override
                                                                public void accept(@NonNull List<GoodsUnitModel> goodsUnitModels) throws Exception {
                                                                    if (goodsUnitModels != null && goodsUnitModels.size() > 0) {
                                                                        for (GoodsUnitModel mModel : goodsUnitModels) {
                                                                            if (mModel.ProductId == mItem.ProductId) {
                                                                                ProductUnitEntity.ValueEntity mUnitData = new ProductUnitEntity.ValueEntity();
                                                                                mUnitData.Id = mModel.Id;
                                                                                mUnitData.ProductId = mModel.ProductId;
                                                                                mUnitData.Name = mModel.Name;
                                                                                mUnitData.Factor = mModel.Factor;
                                                                                mUnitData.BasicFactor = mModel.BasicFactor;
                                                                                mUnitData.IsBasic = mModel.IsBasic;
                                                                                mUnitData.IsDefault = mModel.IsDefault;
                                                                                mUnitData.BreakupNotify = mModel.BreakupNotify;
                                                                                mUnitData.Ordinal = mModel.Ordinal;
                                                                                mItem.ProductUnitList.add(mUnitData);
                                                                            }
                                                                        }
                                                                    } else {
                                                                        LogUtils.e("单位List为空");
                                                                    }
                                                                }
                                                            });

                                                }
                                            });

                                }
                            }
                        });

            }

            initRecyclerView();
        } else {
            getOrderData();
        }
    }

    private void getOrderData() {
        if (!CommonUtil.isNetworkAvailable(GoodsListActivity.this)) {
            ToastUtils.showShortSafe("当前网络不可用,请检查网络设置");
            return;
        }
        RetrofitHelper
                .getFetchOrderApi()
                .fetchOrderInfo("SalesOutBill", "Id=" + orderId, "", AppInfoUtil.getToken())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CreateOrderEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull CreateOrderEntity createOrderEntity) {
                        if (!createOrderEntity.HasError && createOrderEntity.Value != null) {
                            orderInfo = createOrderEntity.Value;
                            mItemData.addAll(orderInfo.SalesOutBillItemList);
                            LogUtils.e("mItemData数量为：" + mItemData.size());
                            initRecyclerView();
                        } else {
                            showEmptyView();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        showEmptyView();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mItemGoodsListAdapter = new ItemGoodsListAdapter(mRecyclerView, mItemData);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, R.drawable.recyclerview_divider_goods));
        mRecyclerView.setAdapter(mItemGoodsListAdapter);
    }

    public void finishTask() {
        //        if (mSwipeRefreshLayout.isRefreshing()) {
        //            mSwipeRefreshLayout.setRefreshing(false);
        //        }
        if (mItemData != null) {
            if (mItemData.size() == 0) {
                showEmptyView();
            } else {
                hideEmptyView();
            }
        }
    }

    public void showEmptyView() {
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.drawable.ic_data_empty);
        mCustomEmptyView.setEmptyText("暂无数据");
    }


    public void hideEmptyView() {
        if (mCustomEmptyView == null || mRecyclerView == null) {
            return;
        }
        mCustomEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

}
