package com.yifarj.yifadinghuobao.ui.activity.shoppingcart;

import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
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

import butterknife.BindView;
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

    @Override
    public int getLayoutId() {
        return R.layout.activity_shoppingcart;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        titleView.setRightTextClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CzechYuanDialog mDialog = new CzechYuanDialog(ShoppingCartActivity.this, R.style.CzechYuanDialog);
                mDialog.setContent("确定清空购物车？");
                mDialog.setConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FlowManager.getDatabase(AppDatabase.class).reset(ShoppingCartActivity.this);
//                        Delete.table(GoodsUnitModel.class);
                    }
                });
            }
        });
        titleView.setLeftIconClickListener(view -> finish());
        loadData();
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

}
