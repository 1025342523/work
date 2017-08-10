package com.yifarj.yifadinghuobao.ui.activity.productCategory;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.GoodsListAdapter;
import com.yifarj.yifadinghuobao.adapter.GoodsListViewAdapter;
import com.yifarj.yifadinghuobao.adapter.helper.HeaderViewRecyclerAdapter;
import com.yifarj.yifadinghuobao.database.model.ReturnListItemModel;
import com.yifarj.yifadinghuobao.database.model.ReturnListItemModel_Table;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel_Table;
import com.yifarj.yifadinghuobao.model.entity.GoodsListEntity;
import com.yifarj.yifadinghuobao.network.PageInfo;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.network.utils.JsonUtils;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.ui.activity.shoppingcart.ShopDetailActivity;
import com.yifarj.yifadinghuobao.ui.activity.shoppingcart.ShoppingCartActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.PreferencesUtil;
import com.yifarj.yifadinghuobao.view.CustomEmptyView;
import com.yifarj.yifadinghuobao.view.SearchView;
import com.yifarj.yifadinghuobao.view.TitleView;

import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 货品分类对应的货品
 *
 * @auther Czech.Yuan
 * @date 2017/7/25 14:30
 */

public class ProductListActivity extends BaseActivity {
    private static final int REQUEST_REFRESH = 10;
    private static final int REQUEST_ITEM = 11;

    @BindView(R.id.lvContent)
    ListView lvContent;
    @BindView(R.id.empty_view)
    CustomEmptyView mCustomEmptyView;
    @BindView(R.id.titleView)
    TitleView titleView;
    @BindView(R.id.searchView)
    SearchView searchView;

    private boolean mIsRefreshing = false;

    private View loadMoreView;

    private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;

    private GoodsListAdapter mGoodsListAdapter;


    private int totalCount, orderCount;
    private int categoryId, saleType = 0;
    private int shopQuantity = 0, itemPosition, itemType, shopId;

    private PageInfo pageInfo = new PageInfo();
    private boolean requesting;
    private boolean morePage = true;
    private GoodsListEntity goodsList;
    private GoodsListViewAdapter goodsListAdapter;

    private PageInfo searchPageInfo = new PageInfo();
    private boolean searchRequesting;
    private boolean searchMorePage = true;
    private GoodsListEntity searchGoodsList;
    private GoodsListViewAdapter searchGoodsListAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_product_list;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        pageInfo = new PageInfo();
        goodsList = null;
        saleType = getIntent().getIntExtra("saleType", 0);
        String CategoryName = getIntent().getStringExtra("CategoryName");
        categoryId = getIntent().getIntExtra("CategoryId", 0);
        lazyLoad();

        titleView.setTitle(StringUtils.isEmpty(CategoryName) ? "商品列表" : CategoryName);

        titleView.setLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titleView.setRightIconClickListener(view -> {
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            intent.putExtra("saleType", saleType);
            startActivityForResult(intent, REQUEST_REFRESH);
        });
        titleView.setRightLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.show();
                titleView.setVisibility(View.GONE);
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
                searchView.getListView().setAdapter(null);
                searchView.getEditText().setText("");
                searchGoodsList = null;
                searchPageInfo.PageIndex = -1;
                searchRequesting = false;
                searchMorePage = true;
            }
        });
        searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String result = s.toString();
                if (StringUtils.isEmpty(result)) {
                    searchGoodsList = null;
                    searchPageInfo.PageIndex = -1;
                    searchRequesting = false;
                    searchMorePage = true;
                }
                if (!StringUtils.isEmpty(result)) {
                    if (result.length() == 13 || result.length() == 12 || result.length() == 8) {
                        doSearch(result);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setRightIcon(int visibility, int title) {
        titleView.setRightIconText(visibility, title);
    }

    private void doSearch(String keyword) {
        if (searchRequesting) {
            return;
        }
        searchRequesting = true;
        ++searchPageInfo.PageIndex;
        String body;
        if (categoryId == 0) {
            body = "(name like '%" + keyword + "%' or right(Code,4) like '%" + keyword + "%'" + "or Mnemonic like '%" + keyword + "%' or id in (select productid from TB_ProductBarcode where Barcode like '%" + keyword + "%' and len('" + keyword + "')>=8) and  status not in(4,8))";
        } else {
            body = "((name like '%" + keyword + "%' or right(Code,4) like '%" + keyword + "%'" + "or Mnemonic like '%" + keyword + "%' or id in (select productid from TB_ProductBarcode where Barcode like '%" + keyword + "%' and len('" + keyword + "')>=8)) and CategoryId = " + categoryId + ")";
        }
        int traderId = PreferencesUtil.getInt("TraderId", 0);
        RetrofitHelper.getGoodsListAPI()
                .getGoodsList("ProductList", JsonUtils.serialize(searchPageInfo), body, "[" + traderId + "]", AppInfoUtil.getToken())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GoodsListEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull GoodsListEntity entity) {
                        if (searchGoodsList == null) {
                            searchGoodsList = entity;
                            if (!entity.HasError) {
                                if (entity.Value != null && entity.Value.size() > 0) {
                                    searchGoodsListAdapter = new GoodsListViewAdapter(searchGoodsList.Value, null, 0, ProductListActivity.this, true, saleType);
                                    searchView.getListView().setAdapter(searchGoodsListAdapter);
                                    searchView.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            itemPosition = position;
                                            itemType = 0;
                                            shopId = searchGoodsList.Value.get(position).Id;
                                            Intent intent = new Intent(ProductListActivity.this, ShopDetailActivity.class);
                                            intent.putExtra("shoppingId", searchGoodsList.Value.get(position).Id);
                                            intent.putExtra("saleType", saleType);
                                            startActivityForResult(intent, REQUEST_REFRESH);
                                            //                                            searchView.clearText();
                                            //                                            searchGoodsList = null;
                                            //                                            searchPageInfo.PageIndex = -1;
                                            //                                            searchRequesting = false;
                                            //                                            searchMorePage = true;
                                        }
                                    });
                                    if (entity.Value.size() == 1) {
                                        itemPosition = 0;
                                        itemType = 0;
                                        shopId = searchGoodsList.Value.get(0).Id;
                                        Intent intent = new Intent(ProductListActivity.this, ShopDetailActivity.class);
                                        intent.putExtra("shoppingId", searchGoodsList.Value.get(0).Id);
                                        intent.putExtra("saleType", saleType);
                                        startActivityForResult(intent, REQUEST_REFRESH);
                                        //                                        searchView.clearText();
                                        //                                        searchGoodsList = null;
                                        //                                        searchPageInfo.PageIndex = -1;
                                        //                                        searchRequesting = false;
                                        //                                        searchMorePage = true;
                                    }
                                    searchView.getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
                                        @Override
                                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                                        }

                                        @Override
                                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                            if ((visibleItemCount + firstVisibleItem == totalItemCount)
                                                    && !searchRequesting && searchMorePage && searchGoodsList != null) {
                                                doSearch(keyword);
                                            }
                                        }
                                    });
                                } else {
                                    ToastUtils.showShortSafe("无结果");
                                }
                            } else {
                                ToastUtils.showShortSafe(entity.Information == null ? "无结果" : entity.Information.toString());
                            }
                        } else if (entity != null && entity.Value.size() > 0) {
                            if (searchGoodsList != null && searchGoodsListAdapter != null) {
                                searchGoodsList.Value.addAll(entity.Value);
                                if (!searchGoodsListAdapter.onbind) {
                                    searchGoodsListAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            searchMorePage = false;
                            ToastUtils.showShortSafe("已全部加载");
                        }
                    }


                    @Override
                    public void onError(@NonNull Throwable e) {
                        ToastUtils.showShortSafe("当前网络不可用,请检查网络设置");
                        searchRequesting = false;
                        --searchPageInfo.PageIndex;
                    }

                    @Override
                    public void onComplete() {
                        searchRequesting = false;
                    }
                });
    }

    public void lazyLoad() {
        goodsList = null;
        pageInfo.PageIndex = -1;
        pageInfo.SortOrder = 1;
        morePage = true;
        loadData();
    }

    @Override
    public void loadData() {
        if (saleType == 1) {
            // 查询退货清单
            RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class).where())
                    .queryList()
                    .subscribe(new Consumer<List<ReturnListItemModel>>() {
                        @Override
                        public void accept(@NonNull List<ReturnListItemModel> returnListItemModel) throws Exception {
                            orderCount = returnListItemModel.size();
                            if (orderCount > 0) {
                                titleView.setRightIconText(View.VISIBLE, orderCount);
                                LogUtils.e("orderCount：" + orderCount);
                            } else if (orderCount == 0) {
                                titleView.setRightIconText(View.GONE, 0);
                                LogUtils.e("orderCount：" + orderCount);
                            }
                            LogUtils.e("returnListItemModel：" + returnListItemModel.size());
                        }
                    });
        } else {
            // 查询购物车商品
            RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class).where())
                    .queryList()
                    .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                        @Override
                        public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModels) throws Exception {
                            orderCount = saleGoodsItemModels.size();
                            if (orderCount > 0) {
                                titleView.setRightIconText(View.VISIBLE, orderCount);
                                LogUtils.e("orderCount：" + orderCount);
                            } else if (orderCount == 0) {
                                titleView.setRightIconText(View.GONE, 0);
                                LogUtils.e("orderCount：" + orderCount);
                            }
                            LogUtils.e("saleGoodsItemModels：" + saleGoodsItemModels.size());
                        }
                    });
        }
        getGoodsList();
    }

    public void getGoodsList() {
        if (requesting) {
            return;
        }
        requesting = true;
        ++pageInfo.PageIndex;
        LogUtils.e("loadData", "获取商品列表数据");
        String body;
        if (categoryId == 0) {
            body = "status  not in (4,8)";
        } else {
            body = "CategoryId = " + categoryId;
        }
        int traderId = PreferencesUtil.getInt("TraderId", 0);
        RetrofitHelper.getGoodsListAPI()
                .getGoodsList("ProductList", JsonUtils.serialize(pageInfo), body, "[" + traderId + "]", AppInfoUtil.getToken())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GoodsListEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull GoodsListEntity goodsListEntity) {
                        if (goodsList == null) {
                            goodsList = goodsListEntity;
                            if (!goodsList.HasError) {
                                if (goodsList.Value != null && goodsList.Value.size() > 0) {
                                    goodsListAdapter = new GoodsListViewAdapter(goodsList.Value, null, 0, ProductListActivity.this, true, saleType);
                                    lvContent.setAdapter(goodsListAdapter);
                                    lvContent.setOnItemClickListener((parent, view, position, id) -> {
                                        if (goodsList != null && goodsList.Value != null && goodsList.Value.size() > 0 && goodsList.Value.get(position) != null) {
                                            itemPosition = position;
                                            itemType = 1;
                                            shopId = goodsList.Value.get(position).Id;

                                            Intent intent = new Intent(ProductListActivity.this, ShopDetailActivity.class);
                                            intent.putExtra("shoppingId", goodsList.Value.get(position).Id);
                                            intent.putExtra("saleType", saleType);
                                            startActivityForResult(intent, REQUEST_ITEM);

                                        }
                                    });
                                    lvContent.setOnScrollListener(new AbsListView.OnScrollListener() {
                                        @Override
                                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                                        }

                                        @Override
                                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                            if ((visibleItemCount + firstVisibleItem == totalItemCount)
                                                    && !requesting && morePage && goodsList != null) {
                                                getGoodsList();
                                            }
                                        }
                                    });
                                } else {
                                    showEmptyView();
                                }
                            } else {
                                ToastUtils.showShortSafe("请求超时");
                            }
                        } else if (goodsListEntity != null && goodsListEntity.Value.size() > 0) {
                            goodsList.Value.addAll(goodsListEntity.Value);
                            if (!goodsListAdapter.onbind) {
                                goodsListAdapter.notifyDataSetChanged();
                            }
                        } else {
                            morePage = false;
                            ToastUtils.showShortSafe("已全部加载");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.e("获取商品列表数据失败", e.getMessage());
                        requesting = false;
                        showEmptyView();
                        --pageInfo.PageIndex;
                    }

                    @Override
                    public void onComplete() {
                        requesting = false;
                    }
                });
    }

    public void showEmptyView() {
        if (mCustomEmptyView == null || lvContent == null) {
            return;
        }
        mCustomEmptyView.setVisibility(View.VISIBLE);
        lvContent.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.drawable.ic_data_empty);
        mCustomEmptyView.setEmptyText("暂无数据");
    }


    public void hideEmptyView() {
        if (mCustomEmptyView == null || lvContent == null) {
            return;
        }
        mCustomEmptyView.setVisibility(View.GONE);
        lvContent.setVisibility(View.VISIBLE);
    }

    public void searchSQlite(int productId, int saleType) {
        if (saleType == 1) {
            // 查询退货清单中是否有当前商品
            RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class).where(ReturnListItemModel_Table.ProductId.eq(productId)))
                    .queryList()
                    .subscribe(new Consumer<List<ReturnListItemModel>>() {
                        @Override
                        public void accept(@NonNull List<ReturnListItemModel> returnListItemModel) throws Exception {
                            if (returnListItemModel != null && returnListItemModel.size() > 0) {
                                LogUtils.e("退货清单中有此商品：" + returnListItemModel.get(0).ProductName);
                                shopQuantity = returnListItemModel.get(0).Quantity;
                            } else {
                                shopQuantity = 0;
                                LogUtils.e("退货清单中没有此商品");
                            }
                        }
                    });
        } else {
            // 查询购物车中是否有当前商品
            RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class).where(SaleGoodsItemModel_Table.ProductId.eq(productId)))
                    .queryList()
                    .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                        @Override
                        public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModel) throws Exception {
                            if (saleGoodsItemModel != null && saleGoodsItemModel.size() > 0) {
                                LogUtils.e("购物车有此商品：" + saleGoodsItemModel.get(0).ProductName);
                                shopQuantity = saleGoodsItemModel.get(0).Quantity;
                            } else {
                                shopQuantity = 0;
                                LogUtils.e("购物车没有此商品");
                            }
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REFRESH) {
            lazyLoad();
        } else if (requestCode == REQUEST_ITEM) {
            searchSQlite(shopId, saleType);
            if (itemType == 0) {
                searchGoodsListAdapter.updataView(itemPosition, shopQuantity, searchView.getListView());
            } else {
                goodsListAdapter.updataView(itemPosition, shopQuantity, lvContent);
            }
        }
    }

    //    private void doSearch(String keyword) {
    //        String body;
    //        if (categoryId == 0) {
    //            body = "(name like '%" + keyword + "%' or right(Code,4) like '%" + keyword + "%'" + "or Mnemonic like '%" + keyword + "%' or id in (select productid from TB_ProductBarcode where Barcode like '%" + keyword + "%' and len('" + keyword + "')>=8) and  status not in(4,8))";
    //        } else {
    //            body = "((name like '%" + keyword + "%' or right(Code,4) like '%" + keyword + "%'" + "or Mnemonic like '%" + keyword + "%' or id in (select productid from TB_ProductBarcode where Barcode like '%" + keyword + "%' and len('" + keyword + "')>=8)) and CategoryId = " + categoryId + ")";
    //        }
    //        RetrofitHelper.getGoodsListAPI()
    //                .getGoodsList("ProductList", "", body, "[" + DataSaver.getMettingCustomerInfo().TraderId + "]", AppInfoUtil.getToken())
    //                .compose(bindToLifecycle())
    //                .subscribeOn(Schedulers.newThread())
    //                .observeOn(AndroidSchedulers.mainThread())
    //                .subscribe(new Observer<GoodsListEntity>() {
    //                    @Override
    //                    public void onSubscribe(@NonNull Disposable d) {
    //
    //                    }
    //
    //                    @Override
    //                    public void onNext(@NonNull GoodsListEntity goodsListEntity) {
    //                        if (!goodsListEntity.HasError) {
    //                            if (goodsListEntity.Value != null && goodsListEntity.Value.size() > 0) {
    //                                searchView.getListView().setAdapter(new GoodsListAdapter(searchView.getListView(), goodsListEntity.Value, true, null, ProductListActivity.this,0));
    //                            } else {
    //                                ToastUtils.showShortSafe("无结果");
    //                            }
    //                        } else {
    //                            ToastUtils.showShortSafe("无结果");
    //                        }
    //                    }
    //
    //                    @Override
    //                    public void onError(@NonNull Throwable e) {
    //                        ToastUtils.showShortSafe("当前网络不可用,请检查网络设置");
    //                    }
    //
    //                    @Override
    //                    public void onComplete() {
    //
    //                    }
    //                });
    //    }

    /*public void lazyLoad() {
        pageInfo.PageIndex = 0;
        pageInfo.SortOrder = 1;
        mIsRefreshing = false;
        goodsList.clear();
        loadData();
        initRecyclerView();
    }

    public void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mGoodsListAdapter = new GoodsListAdapter(mRecyclerView, goodsList, true, null, this,0,saleType);
        mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(mGoodsListAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, R.drawable.recyclerview_divider_goods));
        mRecyclerView.setAdapter(mHeaderViewRecyclerAdapter);
        setRecycleNoScroll();
        createLoadMoreView();

        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {

            @Override
            public void onLoadMore(int i) {
                mIsRefreshing = true;
                pageInfo.PageIndex++;
                loadData();
                loadMoreView.setVisibility(View.VISIBLE);
            }
        });

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mIsRefreshing;
            }
        });

        mGoodsListAdapter.setOnItemClickListener(new AbsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, AbsRecyclerViewAdapter.ClickableViewHolder holder) {
                if (holder != null && position < goodsList.size()) {
                    Intent intent = new Intent(ProductListActivity.this, ShopDetailActivity.class);
                    intent.putExtra("shoppingId", goodsList.get(position).Id);
                    intent.putExtra("saleType", saleType);
                    startActivityForResult(intent, REQUEST_REFRESH);
                }
            }
        });
    }

    @Override
    public void loadData() {
        if (saleType == 1) {
            // 查询退货清单
            RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class).where())
                    .queryList()
                    .subscribe(new Consumer<List<ReturnListItemModel>>() {
                        @Override
                        public void accept(@NonNull List<ReturnListItemModel> returnListItemModel) throws Exception {
                            orderCount = returnListItemModel.size();
                            if (orderCount > 0) {
                                titleView.setRightIconText(View.VISIBLE, orderCount);
                                LogUtils.e("orderCount：" + orderCount);
                            } else if (orderCount == 0) {
                                titleView.setRightIconText(View.GONE, 0);
                                LogUtils.e("orderCount：" + orderCount);
                            }
                            LogUtils.e("returnListItemModel：" + returnListItemModel.size());
                        }
                    });
        } else {
            // 查询购物车商品
            RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class).where())
                    .queryList()
                    .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                        @Override
                        public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModels) throws Exception {
                            orderCount = saleGoodsItemModels.size();
                            if (orderCount > 0) {
                                titleView.setRightIconText(View.VISIBLE, orderCount);
                                LogUtils.e("orderCount：" + orderCount);
                            } else if (orderCount == 0) {
                                titleView.setRightIconText(View.GONE, 0);
                                LogUtils.e("orderCount：" + orderCount);
                            }
                            LogUtils.e("saleGoodsItemModels：" + saleGoodsItemModels.size());
                        }
                    });
        }
        LogUtils.e("loadData", "获取商品列表数据");
        String body;
        if (categoryId == 0) {
            body = "status  not in (4,8)";
        } else {
            body = "CategoryId = " + categoryId;
        }
        RetrofitHelper.getGoodsListAPI()
                .getGoodsList("ProductList", JsonUtils.serialize(pageInfo), body, "[" + DataSaver.getMettingCustomerInfo().TraderId + "]", AppInfoUtil.getToken())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(goodsListEntity -> {
                    if (goodsListEntity.PageInfo.PageIndex * goodsListEntity.PageInfo.PageLength >= goodsListEntity.PageInfo.TotalCount) {
                        loadMoreView.setVisibility(View.GONE);
                        mHeaderViewRecyclerAdapter.removeFootView();
                    }
                })
                .subscribe(new Observer<GoodsListEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull GoodsListEntity goodsListEntity) {
                        if (!goodsListEntity.HasError) {
                            totalCount = goodsListEntity.PageInfo.TotalCount;
                            goodsList.addAll(goodsListEntity.Value);
                            finishTask();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.e("获取商品列表数据失败", e.getMessage());
                        showEmptyView();
                        loadMoreView.setVisibility(View.GONE);
                        --pageInfo.PageIndex;
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void finishTask() {
        //        if (mSwipeRefreshLayout.isRefreshing()) {
        //            mSwipeRefreshLayout.setRefreshing(false);
        //        }
        mIsRefreshing = false;
        if (goodsList != null) {
            if (goodsList.size() == 0) {
                showEmptyView();
            } else {
                hideEmptyView();
            }
        }
        loadMoreView.setVisibility(View.GONE);
        LogUtils.e("Page：" + pageInfo.PageIndex);
        LogUtils.e("ListSize" + goodsList.size());
        if (!mGoodsListAdapter.onbind) {
            if (mRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE || !mRecyclerView.isComputingLayout()) { // RecyclerView滑动过程中刷新数据导致的Crash(Android官方的一个Bug)
                mGoodsListAdapter.notifyDataSetChanged();
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

    private void createLoadMoreView() {
        loadMoreView = LayoutInflater.from(this)
                .inflate(R.layout.layout_load_more, mRecyclerView, false);
        mHeaderViewRecyclerAdapter.addFooterView(loadMoreView);
        loadMoreView.setVisibility(View.GONE);
    }


    private void setRecycleNoScroll() {
        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pageInfo.PageIndex = 0;
        goodsList.clear();
        loadData();
    }*/
}
