package com.yifarj.yifadinghuobao.ui.fragment.order;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.OrderListAdapter;
import com.yifarj.yifadinghuobao.adapter.helper.AbsRecyclerViewAdapter;
import com.yifarj.yifadinghuobao.adapter.helper.EndlessRecyclerOnScrollListener;
import com.yifarj.yifadinghuobao.adapter.helper.HeaderViewRecyclerAdapter;
import com.yifarj.yifadinghuobao.model.entity.SaleOrderListEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.network.PageInfo;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.network.utils.JsonUtils;
import com.yifarj.yifadinghuobao.ui.activity.order.MettingOrderActivity;
import com.yifarj.yifadinghuobao.ui.activity.shoppingcart.ShoppingCartActivity;
import com.yifarj.yifadinghuobao.ui.fragment.base.BaseFragment;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.view.CustomEmptyView;
import com.yifarj.yifadinghuobao.view.TitleView;
import com.yifarj.yifadinghuobao.view.utils.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * TabOrderFragment
 *
 * @auther Czech.Yuan
 * @date 2017/5/22 9:02
 */
public class TabOrderFragment extends BaseFragment {
    private static final int REQUEST_REFRESH = 10;

    @BindView(R.id.titleView)
    TitleView titleView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    CustomEmptyView mCustomEmptyView;
//    @BindView(R.id.rlTab1)
//    RelativeLayout rlTab1;
//    @BindView(R.id.rlTab2)
//    RelativeLayout rlTab2;
//    @BindView(R.id.rlTab3)
//    RelativeLayout rlTab3;
//    @BindView(R.id.rlTab4)
//    RelativeLayout rlTab4;


    private boolean mIsRefreshing = false;


    private View loadMoreView;

    private PageInfo pageInfo = new PageInfo();
    private List<SaleOrderListEntity.ValueEntity> mSaleOrderList = new ArrayList<>();

    private OrderListAdapter mOrderListAdapter;
    private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_order;
    }

    @Override
    protected void finishCreateView(Bundle savedInstanceState) {
        pageInfo.SortedColumn = "BillDate";
        pageInfo.SortOrder = 2;
        isPrepared = true;
        lazyLoad();
        titleView.setRightIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShoppingCartActivity.class);
                startActivityForResult(intent, REQUEST_REFRESH);
            }
        });
    }

    @Override
    protected void finishTask() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mIsRefreshing = false;
        if (mSaleOrderList != null) {
            if (mSaleOrderList.size() == 0) {
                showEmptyView();
            } else {
                hideEmptyView();
            }
        }
        loadMoreView.setVisibility(View.GONE);
        mOrderListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mOrderListAdapter = new OrderListAdapter(mRecyclerView, mSaleOrderList);
        mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(mOrderListAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST, R.drawable.recyclerview_divider_goods));
        mRecyclerView.setAdapter(mHeaderViewRecyclerAdapter);
        setRecycleNoScroll();
        createLoadMoreView();
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {

            @Override
            public void onLoadMore(int i) {
                pageInfo.PageIndex++;
                loadData();
                loadMoreView.setVisibility(View.VISIBLE);
            }
        });
        mOrderListAdapter.setOnItemClickListener(new AbsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, AbsRecyclerViewAdapter.ClickableViewHolder holder) {
                Intent intent = new Intent(getActivity(), MettingOrderActivity.class);
                intent.putExtra("orderId", mSaleOrderList.get(position).Id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
//        mSwipeRefreshLayout.setColorSchemeResources(R.color.light_blue);
        mSwipeRefreshLayout.post(() -> {

            mSwipeRefreshLayout.setRefreshing(true);
            mIsRefreshing = true;
            loadData();
        });

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            pageInfo.PageIndex = 0;
            mIsRefreshing = false;
            mSaleOrderList.clear();
            loadData();
        });
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared && !isVisible) {
            LogUtils.e("TabOrderFragment", "lazyLoad（） false");
            return;
        }
        initRefreshLayout();
        initRecyclerView();
        isPrepared = false;
    }

    @Override
    protected void loadData() {
        int contactId = DataSaver.getMettingCustomerInfo().Id;
        RetrofitHelper
                .getOrderListApi()
                .getOrderList("SalesOutBillInfoList", JsonUtils.serialize(pageInfo), "ContactId = " + contactId, "", AppInfoUtil.getToken())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<SaleOrderListEntity>() {
                    @Override
                    public void accept(@NonNull SaleOrderListEntity saleOrderListEntity) throws Exception {
                        if (saleOrderListEntity.PageInfo.PageIndex * saleOrderListEntity.PageInfo.PageLength >= saleOrderListEntity.PageInfo.TotalCount) {
                            loadMoreView.setVisibility(View.GONE);
                            mHeaderViewRecyclerAdapter.removeFootView();
                        }
                    }
                })
                .subscribe(new Observer<SaleOrderListEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull SaleOrderListEntity saleOrderListEntity) {
                        if (!saleOrderListEntity.HasError) {
                            mSaleOrderList.addAll(saleOrderListEntity.Value);
                            finishTask();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        showEmptyView();
                        loadMoreView.setVisibility(View.GONE);
                        --pageInfo.PageIndex;
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void showErrorView() {
        mSwipeRefreshLayout.setRefreshing(false);
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.drawable.img_tips_error_load_error);
        mCustomEmptyView.setEmptyText("加载失败~(≧▽≦)~啦啦啦.");
    }

    public void showEmptyView() {
        mSwipeRefreshLayout.setRefreshing(false);
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.drawable.ic_data_empty);
        mCustomEmptyView.setEmptyText("暂无订货单");
    }


    public void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    private void setRecycleNoScroll() {

        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }

    private void createLoadMoreView() {
        loadMoreView = LayoutInflater.from(getActivity())
                .inflate(R.layout.layout_load_more, mRecyclerView, false);
        mHeaderViewRecyclerAdapter.addFooterView(loadMoreView);
        loadMoreView.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pageInfo.PageIndex = 0;
        mSaleOrderList.clear();
        loadData();
    }
}
