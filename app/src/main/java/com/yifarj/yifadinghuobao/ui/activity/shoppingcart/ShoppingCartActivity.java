package com.yifarj.yifadinghuobao.ui.activity.shoppingcart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.ShoppingCartAdapter;
import com.yifarj.yifadinghuobao.database.AppDatabase;
import com.yifarj.yifadinghuobao.database.model.GoodsUnitModel;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
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

    private ShoppingCartAdapter mShoppingCartAdapter;
    private List<SaleGoodsItemModel> mItemData = new ArrayList<>();
    private List<GoodsUnitModel> mUnitData = new ArrayList<>();
    private FlowContentObserver mObserver = new FlowContentObserver();


    private int totalCount = 0;
    private double totalPrice = 0;
    private boolean refresh;

    @Override
    public int getLayoutId() {
        return R.layout.activity_shoppingcart;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mObserver.registerForContentChanges(this, SaleGoodsItemModel.class);
        mObserver.addModelChangeListener(modelChangeListener);
    }


    FlowContentObserver.OnModelStateChangedListener modelChangeListener = new FlowContentObserver.OnModelStateChangedListener() {
        @Override
        public void onModelStateChanged(@Nullable Class<?> table, BaseModel.Action action, @android.support.annotation.NonNull SQLOperator[] primaryKeyValues) {
            refresh = true;
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
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mObserver.removeModelChangeListener(modelChangeListener);
        mObserver.unregisterForContentChanges(this);
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        titleView.setRightTextClickListener(view -> {
            CzechYuanDialog mDialog = new CzechYuanDialog(ShoppingCartActivity.this, R.style.CzechYuanDialog);
            mDialog.setContent("确定清空购物车？");
            mDialog.setConfirmClickListener(view1 -> {
                FlowManager.getDatabase(AppDatabase.class).reset(ShoppingCartActivity.this);
//                        Delete.table(GoodsUnitModel.class);
                ToastUtils.showShortSafe("购物车已清空");
                showEmptyView();
                setResult(RESULT_OK);
                refresh = true;
            });
        });
        titleView.setLeftIconClickListener(view -> {
            if (refresh) {
                setResult(RESULT_OK);
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

                    }
                });
    }

    private void createOrder() {

    }


    @Override
    public void loadData() {
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
        RXSQLite.rx(SQLite.select()
                .from(GoodsUnitModel.class))
                .queryList()
                .subscribe(new Consumer<List<GoodsUnitModel>>() {
                    @Override
                    public void accept(@NonNull List<GoodsUnitModel> goodsUnitModels) throws Exception {
                        if (goodsUnitModels != null) {
                            mUnitData.addAll(goodsUnitModels);
                        }
                    }
                });
        initRecyclerView();
        finishTask();
        showTotalPrice(getTotalPrice(mItemData));
        showTotalCount(mItemData.size(), getTotalCount(mItemData));
    }

    @Override
    public void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mShoppingCartAdapter = new ShoppingCartAdapter(mRecyclerView, mItemData, mUnitData);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, R.drawable.recyclerview_divider_goods));
        mRecyclerView.setAdapter(mShoppingCartAdapter);
    }

    @Override
    public void finishTask() {
        if (mItemData != null) {
            if (mItemData.size() == 0) {
                showEmptyView();
            } else {
                hideEmptyView();
            }
        }
        mShoppingCartAdapter.notifyDataSetChanged();
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
        tvTotalAmount.setText(String.valueOf(totalPrice));
    }

    public double getTotalPrice(List<SaleGoodsItemModel> itemData) {
        if (itemData != null) {
            totalPrice = 0;
            Flowable.fromIterable(itemData)
                    .forEach(new Consumer<SaleGoodsItemModel>() {
                        @Override
                        public void accept(@NonNull SaleGoodsItemModel saleGoodsItemModel) throws Exception {
                            totalPrice += saleGoodsItemModel.CurrentPrice * saleGoodsItemModel.Quantity;
                        }
                    });
            return totalPrice;
        }
        return totalPrice;
    }

    private void showTotalCount(int goodsCount, int totalCount) {
        tvGoodsTotal.setText("共" + String.valueOf(goodsCount) + "款，总数" + String.valueOf(totalCount));
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
}
