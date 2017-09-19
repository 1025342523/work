package com.yifarj.yifadinghuobao.ui.activity.shoppingcart;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.ReturnListAdapter;
import com.yifarj.yifadinghuobao.adapter.ShoppingCartAdapter;
import com.yifarj.yifadinghuobao.adapter.helper.AbsRecyclerViewAdapter;
import com.yifarj.yifadinghuobao.database.model.GoodsUnitModel;
import com.yifarj.yifadinghuobao.database.model.ReturnGoodsUnitModel;
import com.yifarj.yifadinghuobao.database.model.ReturnListItemModel;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.ui.activity.order.MettingOrderActivity;
import com.yifarj.yifadinghuobao.utils.NumberUtil;
import com.yifarj.yifadinghuobao.utils.PreferencesUtil;
import com.yifarj.yifadinghuobao.view.CustomEmptyView;
import com.yifarj.yifadinghuobao.view.CzechYuanDialog;
import com.yifarj.yifadinghuobao.view.TitleView;
import com.yifarj.yifadinghuobao.view.utils.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


/**
 * ShoppingCartActivity
 *
 * @auther Czech.Yuan
 * @date 2017/5/17 14:32
 */
public class ShoppingCartActivity extends BaseActivity {

    @BindView(R.id.empty_view)
    CustomEmptyView mCustomEmptyView;
    @BindView(R.id.titleView)
    TitleView titleView;
    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.tvGoodsTotal)
    TextView tvGoodsTotal;
    @BindView(R.id.btn_submit)
    ButtonBarLayout btnSubmit;
    @BindView(R.id.tvTotalAmount)
    TextView tvTotalAmount;
    @BindView(R.id.rl_bottom)
    RelativeLayout rl_bottom;

    private static final int REQUEST_REFRESH = 10;
    private ShoppingCartAdapter mShoppingCartAdapter;
    private ReturnListAdapter mReturnListAdapter;
    private List<SaleGoodsItemModel> mItemData = new ArrayList<>();
    private List<ReturnListItemModel> returnItemData = new ArrayList<>();
    private List<ReturnGoodsUnitModel> returnUnitData = new ArrayList<>();
    private FlowContentObserver mObserver = new FlowContentObserver();
    private int priceSystemId;

    private int totalCount = 0;
    private double totalPrice = 0;
    private boolean refresh;
    private int saleType = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_shoppingcart;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (saleType == 1) {
            mObserver.registerForContentChanges(this, ReturnListItemModel.class);
        } else {
            mObserver.registerForContentChanges(this, SaleGoodsItemModel.class);
        }
        mObserver.addModelChangeListener(modelChangeListener);
    }


    FlowContentObserver.OnModelStateChangedListener modelChangeListener = new FlowContentObserver.OnModelStateChangedListener() {
        @Override
        public void onModelStateChanged(@Nullable Class<?> table, BaseModel.Action action, @android.support.annotation.NonNull SQLOperator[] primaryKeyValues) {
            refresh = true;
            if (saleType == 1) {
                List<ReturnListItemModel> currentItem = new ArrayList<>();
                RXSQLite.rx(SQLite.select()
                        .from(ReturnListItemModel.class))
                        .queryList()
                        .subscribe(returnListItemModel -> {
                            if (returnListItemModel != null) {
                                currentItem.addAll(returnListItemModel);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showTotalPrice(getReturnTotalPrice(currentItem));
                                        showTotalCount(currentItem.size(), getReturnTotalCount(currentItem));
                                    }
                                });
                            }
                        });
            } else {
                List<SaleGoodsItemModel> currentItem = new ArrayList<>();
                RXSQLite.rx(SQLite.select()
                        .from(SaleGoodsItemModel.class))
                        .queryList()
                        .subscribe(saleGoodsItemModels -> {
                            if (saleGoodsItemModels != null) {
                                currentItem.addAll(saleGoodsItemModels);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showTotalPrice(getTotalPrice(currentItem));
                                        showTotalCount(currentItem.size(), getTotalCount(currentItem));
                                    }
                                });
                            }
                        });
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mObserver.removeModelChangeListener(modelChangeListener);
        mObserver.unregisterForContentChanges(this);
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        saleType = getIntent().getIntExtra("saleType", 0);
        if (saleType == 1) {
            titleView.setTitle("退货清单");
            titleView.setRightTextClickListener(view -> {
                CzechYuanDialog mDialog = new CzechYuanDialog(ShoppingCartActivity.this, R.style.CzechYuanDialog);
                mDialog.setContent("确定清空退货清单？");
                mDialog.setConfirmClickListener(view1 -> {
                    //                FlowManager.getDatabase(AppDatabase.class).reset(ShoppingCartActivity.this);
                    Delete.table(ReturnListItemModel.class);
                    Delete.table(ReturnGoodsUnitModel.class);
                    ToastUtils.showShortSafe("退货清单已清空");
                    showEmptyView();
                    setResult(RESULT_OK);
                    refresh = true;
                });
            });
        } else {
            titleView.setRightTextClickListener(view -> {
                CzechYuanDialog mDialog = new CzechYuanDialog(ShoppingCartActivity.this, R.style.CzechYuanDialog);
                mDialog.setContent("确定清空购物车？");
                mDialog.setConfirmClickListener(view1 -> {
                    //                FlowManager.getDatabase(AppDatabase.class).reset(ShoppingCartActivity.this);
                    Delete.table(SaleGoodsItemModel.class);
                    Delete.table(GoodsUnitModel.class);
                    ToastUtils.showShortSafe("购物车已清空");
                    showEmptyView();
                    setResult(RESULT_OK);
                    refresh = true;
                });
            });
        }

        titleView.setLeftIconClickListener(view -> {
            if (refresh) {
                setResult(RESULT_OK);
                LogUtils.e("返回 true");
            }
            finish();
        });

        loadData();

        RxView.clicks(btnSubmit)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        createOrder();
                    }
                });
    }

    private void createOrder() {
        Intent intent = new Intent(ShoppingCartActivity.this, MettingOrderActivity.class);
        intent.putExtra("CreateOrder", true);
        intent.putExtra("saleType", saleType);
        startActivity(intent);
        setResult(RESULT_OK);
        finish();
    }


    @Override
    public void loadData() {
        priceSystemId = PreferencesUtil.getInt("PriceSystemId", -1);
        LogUtils.e("订货会价格体系Id", priceSystemId + "");
        if (saleType == 1) {
            RXSQLite.rx(SQLite.select()
                    .from(ReturnListItemModel.class))
                    .queryList()
                    .subscribe(new Consumer<List<ReturnListItemModel>>() {
                        @Override
                        public void accept(@NonNull List<ReturnListItemModel> returnListItemModel) throws Exception {
                            if (returnListItemModel != null) {
                                returnItemData.addAll(returnListItemModel);
                            }
                        }
                    });
            RXSQLite.rx(SQLite.select()
                    .from(ReturnGoodsUnitModel.class))
                    .queryList()
                    .subscribe(new Consumer<List<ReturnGoodsUnitModel>>() {
                        @Override
                        public void accept(@NonNull List<ReturnGoodsUnitModel> returnGoodsUnitModel) throws Exception {
                            if (returnGoodsUnitModel != null) {
                                returnUnitData.addAll(returnGoodsUnitModel);
                            }
                        }
                    });
            initRecyclerView();
            finishTask();
            showTotalPrice(getReturnTotalPrice(returnItemData));
            showTotalCount(returnItemData.size(), getReturnTotalCount(returnItemData));
        } else {
            RXSQLite.rx(SQLite.select()
                    .from(SaleGoodsItemModel.class))
                    .queryList()
                    .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                        @Override
                        public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModels) throws Exception {
                            if (saleGoodsItemModels != null) {
                                mItemData.addAll(saleGoodsItemModels);
                            }
                        }
                    });
            initRecyclerView();
            finishTask();
            showTotalPrice(getTotalPrice(mItemData));
            showTotalCount(mItemData.size(), getTotalCount(mItemData));
        }
    }

    @Override
    public void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        if (saleType == 1) {
            mReturnListAdapter = new ReturnListAdapter(mRecyclerView, returnItemData, priceSystemId);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, R.drawable.recyclerview_divider_goods));
            mRecyclerView.setAdapter(mReturnListAdapter);

            mReturnListAdapter.setOnItemClickListener(new AbsRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, AbsRecyclerViewAdapter.ClickableViewHolder holder) {
                    if (holder != null) {
                        if (position < mItemData.size() && position < returnItemData.size()) {
                            Intent intent = new Intent(ShoppingCartActivity.this, ShopDetailActivity.class);
                            intent.putExtra("shoppingId", returnItemData.get(position).ProductId);
                            intent.putExtra("saleType", 1);
                            startActivityForResult(intent, REQUEST_REFRESH);
                        }
                    }
                }
            });
        } else {
            mShoppingCartAdapter = new ShoppingCartAdapter(mRecyclerView, mItemData, priceSystemId);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, R.drawable.recyclerview_divider_goods));
            mRecyclerView.setAdapter(mShoppingCartAdapter);

            /*mShoppingCartAdapter.setOnItemClickListener(new AbsRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, AbsRecyclerViewAdapter.ClickableViewHolder holder) {
                    if (holder != null && position < mItemData.size()) {
                        Intent intent = new Intent(ShoppingCartActivity.this, ShopDetailActivity.class);
                        intent.putExtra("shoppingId", mItemData.get(position).ProductId);
                        intent.putExtra("saleType", 0);
                        startActivityForResult(intent, REQUEST_REFRESH);
                    }
                }
            });*/
        }
    }

    @Override
    public void finishTask() {
        if (saleType == 1) {
            if (returnItemData != null) {
                if (returnItemData.size() == 0) {
                    showEmptyView();
                } else {
                    hideEmptyView();
                }
            }
            mReturnListAdapter.notifyDataSetChanged();
        } else {
            if (mItemData != null) {
                if (mItemData.size() == 0) {
                    showEmptyView();
                } else {
                    hideEmptyView();
                }
            }
            mShoppingCartAdapter.notifyDataSetChanged();
        }
    }

    public void showEmptyView() {
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        rl_bottom.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.drawable.ic_data_empty);
        mCustomEmptyView.setEmptyText("暂无数据");
        titleView.setRightTextVisibility(View.GONE);
    }

    public void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    public void showTotalPrice(double totalPrice) {
        tvTotalAmount.setText(NumberUtil.formatDoubleToString(totalPrice));
    }

    public double getTotalPrice(List<SaleGoodsItemModel> itemData) {
        if (itemData != null) {
            totalPrice = 0;
            Flowable.fromIterable(itemData)
                    .forEach(new Consumer<SaleGoodsItemModel>() {
                        @Override
                        public void accept(@NonNull SaleGoodsItemModel saleGoodsItemModel) throws Exception {
                            //                            totalPrice += saleGoodsItemModel.UnitPrice * saleGoodsItemModel.Quantity;
                            totalPrice += saleGoodsItemModel.CurrentPrice;
                        }
                    });
            return totalPrice;
        }
        return totalPrice;
    }

    public double getReturnTotalPrice(List<ReturnListItemModel> itemData) {
        if (itemData != null) {
            totalPrice = 0;
            Flowable.fromIterable(itemData)
                    .forEach(new Consumer<ReturnListItemModel>() {
                        @Override
                        public void accept(@NonNull ReturnListItemModel returnListItemModel) throws Exception {
                            //                            totalPrice += saleGoodsItemModel.UnitPrice * saleGoodsItemModel.Quantity;
                            totalPrice += returnListItemModel.CurrentPrice;
                        }
                    });
            return totalPrice;
        }
        return totalPrice;
    }

    private void showTotalCount(int goodsCount, int totalCount) {
        tvGoodsTotal.setText("共" + NumberUtil.formatDouble2String(goodsCount) + "款，总数" + NumberUtil.formatDouble2String(totalCount));
    }

    private int getTotalCount(List<SaleGoodsItemModel> itemData) {
        if (itemData != null) {
            totalCount = 0;
            Flowable.fromIterable(itemData)
                    .forEach(new Consumer<SaleGoodsItemModel>() {
                        @Override
                        public void accept(@NonNull SaleGoodsItemModel saleGoodsItemModel) throws Exception {
                            totalCount += saleGoodsItemModel.Quantity;
                        }
                    });
            return totalCount;
        }
        return totalCount;
    }

    private int getReturnTotalCount(List<ReturnListItemModel> itemData) {
        if (itemData != null) {
            totalCount = 0;
            Flowable.fromIterable(itemData)
                    .forEach(new Consumer<ReturnListItemModel>() {
                        @Override
                        public void accept(@NonNull ReturnListItemModel returnListItemModel) throws Exception {
                            totalCount += returnListItemModel.Quantity;
                        }
                    });
            return totalCount;
        }
        return totalCount;
    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mItemData.clear();
        returnItemData.clear();
        loadData();
    }*/
}
