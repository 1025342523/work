package com.yifarj.yifadinghuobao.ui.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.CollectionAdapter;
import com.yifarj.yifadinghuobao.adapter.GoodsListAdapter;
import com.yifarj.yifadinghuobao.adapter.helper.AbsRecyclerViewAdapter;
import com.yifarj.yifadinghuobao.database.model.CollectionItemModel;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel;
import com.yifarj.yifadinghuobao.model.entity.GoodsListEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.ui.activity.shoppingcart.ShopDetailActivity;
import com.yifarj.yifadinghuobao.ui.activity.shoppingcart.ShoppingCartActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.view.CustomEmptyView;
import com.yifarj.yifadinghuobao.view.CzechYuanDialog;
import com.yifarj.yifadinghuobao.view.SearchView;
import com.yifarj.yifadinghuobao.view.TitleView;
import com.yifarj.yifadinghuobao.view.utils.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 收藏商品
 *
 * Created by zydx-pc on 2017/7/19.
 */

public class CollectionActivity extends BaseActivity {

    @BindView(R.id.empty_view)
    CustomEmptyView mCustomEmptyView;
    @BindView(R.id.collection_titleView)
    TitleView titleView;
    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.ll_collection)
    LinearLayout ll_collection;
    @BindView(R.id.remove_collection)
    ButtonBarLayout remove_collection;
    @BindView(R.id.searchView)
    SearchView searchView;

    private static final int REQUEST_REFRESH = 10;

    private CollectionAdapter mCollectionAdapter;
    private List<CollectionItemModel> mItemData = new ArrayList<>();
    private FlowContentObserver mObserver = new FlowContentObserver();

    private boolean refresh;
    private int orderCount=0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_collection;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mObserver.registerForContentChanges(this, CollectionItemModel.class);
        mObserver.addModelChangeListener(modelChangeListener);
    }


    FlowContentObserver.OnModelStateChangedListener modelChangeListener = new FlowContentObserver.OnModelStateChangedListener() {
        @Override
        public void onModelStateChanged(@Nullable Class<?> table, BaseModel.Action action, @android.support.annotation.NonNull SQLOperator[] primaryKeyValues) {
            refresh = true;
            List<CollectionItemModel> currentItem = new ArrayList<>();
            RXSQLite.rx(SQLite.select()
                    .from(CollectionItemModel.class))
                    .queryList()
                    .subscribe(collectionItemModels -> {
                        if (collectionItemModels != null) {
                            currentItem.addAll(collectionItemModels);
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

        loadData();
        initRecyclerView();
        finishTask();

        titleView.setLeftIconClickListener(view -> {
            if (refresh) {
                setResult(RESULT_OK);
                LogUtils.e("返回 true");
            }
            finish();
        });
        titleView.setRightIconClickListener(view -> {
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            startActivityForResult(intent, REQUEST_REFRESH);
        });

        RxView.clicks(remove_collection)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        CzechYuanDialog mDialog = new CzechYuanDialog(CollectionActivity.this, R.style.CzechYuanDialog);
                        mDialog.setContent("确定清空收藏夹？");
                        mDialog.setConfirmClickListener(view1 -> {
//                            FlowManager.getDatabase(AppDatabase.class).reset(CollectionActivity.this);
                            Delete.table(CollectionItemModel.class);
                            ToastUtils.showShortSafe("收藏夹已清空");
                            showEmptyView();
                            setResult(RESULT_OK);
                            refresh = true;
                        });
                    }
                });

        titleView.setRightLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.show();
                titleView.setVisibility(View.GONE);
                ll_collection.setVisibility(View.GONE);
            }
        });

        searchView.setOnSearchClickListener(new SearchView.OnSearchClickListener() {
            @Override
            public void onSearch(String keyword) {
                doSearch(keyword);
            }
        });
        searchView.setOnCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleView.setVisibility(View.VISIBLE);
            }
        });
        searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String result = s.toString();
                if (!StringUtils.isEmpty(result) && result.length() == 13) {
                    doSearch(result);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void loadData() {
        RXSQLite.rx(SQLite.select()
                .from(CollectionItemModel.class))
                .queryList()
                .subscribe(new Consumer<List<CollectionItemModel>>() {
                    @Override
                    public void accept(@NonNull List<CollectionItemModel> collectionItemModels) throws Exception {
                        if (collectionItemModels != null) {
                            mItemData.addAll(collectionItemModels);
                        }
                    }
                });
        // 查询购物车商品
        RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class).where())
                .queryList()
                .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                    @Override
                    public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModels) throws Exception {
                        LogUtils.e("saleGoodsItemModels："+saleGoodsItemModels.size());
                        orderCount = saleGoodsItemModels.size();
                        if(orderCount>0){
                            titleView.setRightIconText(View.VISIBLE,orderCount);
                        }else if(orderCount==0){
                            titleView.setRightIconText(View.GONE,0);
                        }
                        LogUtils.e("orderCount："+orderCount);
                    }
                });
    }

    @Override
    public void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mCollectionAdapter = new CollectionAdapter(mRecyclerView, mItemData,this);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, R.drawable.recyclerview_divider_goods));
        mRecyclerView.setAdapter(mCollectionAdapter);

        mCollectionAdapter.setOnItemClickListener(new AbsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, AbsRecyclerViewAdapter.ClickableViewHolder holder) {
                if (holder != null && position < mItemData.size()) {
                    Intent intent = new Intent(CollectionActivity.this, ShopDetailActivity.class);
                    intent.putExtra("shoppingId", mItemData.get(position).ProductId);
                    startActivityForResult(intent, REQUEST_REFRESH);
                }
            }
        });
    }

    private void doSearch(String keyword) {
        RetrofitHelper.getGoodsListAPI()
                .getGoodsList("ProductList", "", "(name like '%" + keyword + "%' or right(Code,4) like '%" + keyword + "%'" + "or Mnemonic like '%" + keyword + "%' or id in (select productid from TB_ProductBarcode where Barcode like '%" + keyword + "%' and len('" + keyword + "')>=8) and  status not in (4,8))", "[" + DataSaver.getMettingCustomerInfo().TraderId + "]", AppInfoUtil.getToken())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GoodsListEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull GoodsListEntity goodsListEntity) {
                        if (!goodsListEntity.HasError) {
                            if (goodsListEntity.Value != null && goodsListEntity.Value.size() > 0) {
                                searchView.getListView().setAdapter(new GoodsListAdapter(searchView.getListView(), goodsListEntity.Value, true, null, CollectionActivity.this));
                            } else {
                                ToastUtils.showShortSafe("无结果");
                            }
                        } else {
                            ToastUtils.showShortSafe("无结果");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ToastUtils.showShortSafe("当前网络不可用,请检查网络设置");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void setRightIcon(int visibility, int title) {
        titleView.setRightIconText(visibility, title);
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
        mCollectionAdapter.notifyDataSetChanged();
    }

    public void showEmptyView() {
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        ll_collection.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.drawable.ic_data_empty);
        mCustomEmptyView.setEmptyText("暂无数据");
    }

    public void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mItemData.clear();
        loadData();
        finishTask();
    }
}
