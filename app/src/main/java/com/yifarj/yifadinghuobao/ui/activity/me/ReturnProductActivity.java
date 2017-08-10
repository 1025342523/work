package com.yifarj.yifadinghuobao.ui.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.facebook.stetho.common.LogUtil;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.GoodsListViewAdapter;
import com.yifarj.yifadinghuobao.database.model.ReturnListItemModel;
import com.yifarj.yifadinghuobao.database.model.ReturnListItemModel_Table;
import com.yifarj.yifadinghuobao.model.entity.GoodsListEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.network.PageInfo;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.network.utils.JsonUtils;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.ui.activity.productCategory.ProductCategoryActivity;
import com.yifarj.yifadinghuobao.ui.activity.shoppingcart.ShopDetailActivity;
import com.yifarj.yifadinghuobao.ui.activity.shoppingcart.ShoppingCartActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.view.CustomEmptyView;
import com.yifarj.yifadinghuobao.view.CzechYuanTitleView;
import com.yifarj.yifadinghuobao.view.SearchView;

import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zydx-pc on 2017/8/4.
 */

public class ReturnProductActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_REFRESH = 10;
    private static final int REQUEST_ITEM = 11;

    @BindView(R.id.lvContent)
    ListView lvContent;

    @BindView(R.id.empty_view)
    CustomEmptyView mCustomEmptyView;

    @BindView(R.id.titleView)
    CzechYuanTitleView titleView;

    @BindView(R.id.searchView)
    SearchView searchView;

    @BindView(R.id.search_all_layout)
    LinearLayout rlTab1;

    @BindView(R.id.search_name_layout)
    LinearLayout rlTab2;

    @BindView(R.id.search_code_layout)
    LinearLayout rlTab3;

    @BindView(R.id.search_price_layout)
    LinearLayout rlTab4;

    private int orderCount;

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

    private int shopQuantity = 0, itemPosition, itemType, shopId;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_goods;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        loadData();
        init();
    }

    private void init() {
        rlTab1.setOnClickListener(this);
        rlTab2.setOnClickListener(this);
        rlTab3.setOnClickListener(this);
        rlTab4.setOnClickListener(this);

        titleView.setLeftBackVisibility(View.VISIBLE);
        titleView.setLeftBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titleView.setRightIconClickListener(view -> {
            Intent intent = new Intent(ReturnProductActivity.this, ShoppingCartActivity.class);
            intent.putExtra("saleType", 1);
            startActivityForResult(intent, REQUEST_REFRESH);
        });
        titleView.setLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReturnProductActivity.this, ProductCategoryActivity.class);
                intent.putExtra("saleType", 1);
                startActivityForResult(intent, REQUEST_REFRESH);
            }
        });
        titleView.setRlSearchClickListener(view -> {
            titleView.setVisibility(View.GONE);
            searchView.show();
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
        RetrofitHelper.getGoodsListAPI()
                .getGoodsList("ProductList", JsonUtils.serialize(searchPageInfo), "(name like '%" + keyword + "%' or right(Code,4) like '%" + keyword + "%'" + "or Mnemonic like '%" + keyword + "%' or id in (select productid from TB_ProductBarcode where Barcode like '%" + keyword + "%' and len('" + keyword + "')>=8) and  status not in(4,8))", "[" + DataSaver.getMettingCustomerInfo().TraderId + "]", AppInfoUtil.getToken())
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
                                    searchGoodsListAdapter = new GoodsListViewAdapter(searchGoodsList.Value, null, 0, ReturnProductActivity.this, true, 1);
                                    searchView.getListView().setAdapter(searchGoodsListAdapter);
                                    searchView.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            itemPosition = position;
                                            itemType = 0;
                                            shopId = searchGoodsList.Value.get(position).Id;
                                            Intent intent = new Intent(ReturnProductActivity.this, ShopDetailActivity.class);
                                            intent.putExtra("shoppingId", searchGoodsList.Value.get(position).Id);
                                            intent.putExtra("saleType", 1);
                                            startActivityForResult(intent, REQUEST_ITEM);
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
                                        Intent intent = new Intent(ReturnProductActivity.this, ShopDetailActivity.class);
                                        intent.putExtra("shoppingId", searchGoodsList.Value.get(0).Id);
                                        intent.putExtra("saleType", 1);
                                        startActivityForResult(intent, REQUEST_ITEM);
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


    //    @Override
    //    protected void initRefreshLayout() {
    //        if (mSwipeRefreshLayout == null) {
    //            return;
    //        }
    //        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
    ////        mSwipeRefreshLayout.setColorSchemeResources(R.color.light_blue);
    //        mSwipeRefreshLayout.post(() -> {
    //
    //            mSwipeRefreshLayout.setRefreshing(true);
    //            mIsRefreshing = true;
    //            loadData();
    //        });

    //
    //        mSwipeRefreshLayout.setOnRefreshListener(() -> {
    //            try {
    //                pageInfo.PageIndex = 0;
    //                mIsRefreshing = false;
    //                goodsList.clear();
    //                loadData();
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //        });
    //    }

    @Override
    public void loadData() {
        // 查询退货清单商品
        RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class).where())
                .queryList()
                .subscribe(new Consumer<List<ReturnListItemModel>>() {
                    @Override
                    public void accept(@NonNull List<ReturnListItemModel> returnListItemModels) throws Exception {
                        orderCount = returnListItemModels.size();
                        if (orderCount > 0) {
                            titleView.setRightIconText(View.VISIBLE, orderCount);
                            LogUtils.e("orderCount：" + orderCount);
                        } else if (orderCount == 0) {
                            titleView.setRightIconText(View.GONE, 0);
                            LogUtils.e("orderCount：" + orderCount);
                        }
                        LogUtils.e("returnListItemModels：" + returnListItemModels.size());
                    }
                });
        LogUtils.e("loadData", "获取商品列表数据");
        onTab1Click();
        rlTab1.setSelected(true);
    }

    private void getGoodsList() {
        if (requesting) {
            return;
        }
        requesting = true;
        ++pageInfo.PageIndex;
        RetrofitHelper.getGoodsListAPI()
                .getGoodsList("ProductList", JsonUtils.serialize(pageInfo), "status  not in (4,8)", "[" + DataSaver.getMettingCustomerInfo().TraderId + "]", AppInfoUtil.getToken())
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
                                    goodsListAdapter = new GoodsListViewAdapter(goodsList.Value, null, 0, ReturnProductActivity.this, true, 1);
                                    lvContent.setAdapter(goodsListAdapter);
                                    lvContent.setOnItemClickListener((parent, view, position, id) -> {
                                        if (goodsList != null && goodsList.Value != null && goodsList.Value.size() > 0 && goodsList.Value.get(position) != null) {
                                            itemPosition = position;
                                            itemType = 1;
                                            shopId = goodsList.Value.get(position).Id;
                                            Intent intent = new Intent(ReturnProductActivity.this, ShopDetailActivity.class);
                                            intent.putExtra("shoppingId", goodsList.Value.get(position).Id);
                                            intent.putExtra("saleType", 1);
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

    public void searchSQlite(int productId) {
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REFRESH) {
            onTab1Click();
        } else if (requestCode == REQUEST_ITEM) {
            searchSQlite(shopId);
            if (itemType == 0) {
                searchGoodsListAdapter.updataView(itemPosition, shopQuantity, searchView.getListView());
            } else {
                goodsListAdapter.updataView(itemPosition, shopQuantity, lvContent);
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_all_layout:
                LogUtil.e("全部");
                onTab1Click();
                break;
            case R.id.search_name_layout:
                onTab2Click();
                LogUtil.e("品名");
                break;
            case R.id.search_code_layout:
                LogUtil.e("货号");
                onTab3Click();
                break;
            case R.id.search_price_layout:
                LogUtil.e("价格");
                onTab4Click();
                break;
        }
    }

    private void onTab1Click() {
        setCheckedItem(0);
        goodsList = null;
        pageInfo.PageIndex = -1;
        pageInfo.SortOrder = 2;
        pageInfo.SortedColumn = "CreatedTime";
        morePage = true;
        getGoodsList();
    }

    private void onTab2Click() {
        setCheckedItem(1);
        goodsList = null;
        pageInfo.PageIndex = 0;
        pageInfo.SortOrder = 2;
        pageInfo.SortedColumn = "Name";
        morePage = true;
        getGoodsList();
    }

    private void onTab3Click() {
        setCheckedItem(2);
        goodsList = null;
        pageInfo.PageIndex = 0;
        pageInfo.SortOrder = 2;
        pageInfo.SortedColumn = "Code";
        morePage = true;
        getGoodsList();
    }

    private void onTab4Click() {
        setCheckedItem(3);
        goodsList = null;
        pageInfo.PageIndex = 0;
        pageInfo.SortOrder = 2;
        pageInfo.SortedColumn = "Price0";
        morePage = true;
        getGoodsList();
    }

    public void setCheckedItem(int position) {
        rlTab1.setSelected(false);
        rlTab2.setSelected(false);
        rlTab3.setSelected(false);
        rlTab4.setSelected(false);
        switch (position) {
            case 0:
                rlTab1.setSelected(true);
                break;
            case 1:
                rlTab2.setSelected(true);
                break;
            case 2:
                rlTab3.setSelected(true);
                break;
            case 3:
                rlTab4.setSelected(true);
                break;
        }
    }

}

