package com.yifarj.yifadinghuobao.ui.fragment.goods;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.LogUtils;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.GoodsListAdapter;
import com.yifarj.yifadinghuobao.adapter.helper.EndlessRecyclerOnScrollListener;
import com.yifarj.yifadinghuobao.adapter.helper.HeaderViewRecyclerAdapter;
import com.yifarj.yifadinghuobao.model.entity.GoodsListEntity;
import com.yifarj.yifadinghuobao.network.PageInfo;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
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
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * TabGoodsFragment
 *
 * @auther Czech.Yuan
 * @date 2017/5/12 15:07
 */
public class TabGoodsFragment extends BaseFragment {
    private static final int REQUEST_REFRESH = 10;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_view)
    CustomEmptyView mCustomEmptyView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.titleView)
    TitleView titleView;

    private boolean mIsRefreshing = false;


    private View loadMoreView;

    private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;

    private GoodsListAdapter mGoodsListAdapter;

    private List<GoodsListEntity.ValueEntity> goodsList = new ArrayList<>();

    private PageInfo pageInfo = new PageInfo();


    @Override
    public int getLayoutResId() {
        return R.layout.fragment_goods;
    }

    @Override
    protected void finishCreateView(Bundle savedInstanceState) {
        LogUtils.e("TabGoodsFragment", "finishCreateView");
        isPrepared = true;
        lazyLoad();
        titleView.setRightIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), ShoppingCartActivity.class), REQUEST_REFRESH);
            }
        });
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared && !isVisible) {
            LogUtils.e("TabGoodsFragment", "lazyLoad（） false");
            return;
        }
        initRefreshLayout();
        initRecyclerView();
        isPrepared = false;
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
            goodsList.clear();
            loadData();
        });
    }

    @Override
    protected void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mGoodsListAdapter = new GoodsListAdapter(mRecyclerView, goodsList);
        mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(mGoodsListAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST, R.drawable.recyclerview_divider_goods));
        mRecyclerView.setAdapter(mHeaderViewRecyclerAdapter);
        setRecycleNoScroll();
        createHeadView();
        createLoadMoreView();
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {

            @Override
            public void onLoadMore(int i) {
                pageInfo.PageIndex++;
                loadData();
                loadMoreView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void loadData() {

        LogUtils.e("loadData", "获取商品列表数据");
        RetrofitHelper.getGoodsListAPI()
                .getGoodsList("ProductList", JSON.toJSONString(pageInfo), "status  not in (4,8)", "", AppInfoUtil.getToken())
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
                            goodsList.addAll(goodsListEntity.Value);
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

    @Override
    protected void finishTask() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mIsRefreshing = false;
        if (goodsList != null) {
            if (goodsList.size() == 0) {
                showEmptyView();
            } else {
                hideEmptyView();
            }
        }
        loadMoreView.setVisibility(View.GONE);
        mGoodsListAdapter.notifyDataSetChanged();
    }

    private void createHeadView() {

        View headView = LayoutInflater.from(getActivity())
                .inflate(R.layout.layout_search_archive_head_view, mRecyclerView, false);
//        RecyclerView mHeadRecycler = (RecyclerView) headView.findViewById(
//                R.id.search_archive_bangumi_head_recycler);
//        mHeadRecycler.setHasFixedSize(false);
//        mHeadRecycler.setNestedScrollingEnabled(false);
//        mHeadRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mGoodsListAdapter = new GoodsListAdapter(mHeadRecycler, goodsList);
//        mHeadRecycler.setAdapter(mGoodsListAdapter);

        mHeaderViewRecyclerAdapter.addHeaderView(headView);
    }

    public void showEmptyView() {
        mSwipeRefreshLayout.setRefreshing(false);
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.drawable.img_tips_error_load_error);
        mCustomEmptyView.setEmptyText("加载失败~(≧▽≦)~啦啦啦.");
    }


    public void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void createLoadMoreView() {
        loadMoreView = LayoutInflater.from(getActivity())
                .inflate(R.layout.layout_load_more, mRecyclerView, false);
        mHeaderViewRecyclerAdapter.addFooterView(loadMoreView);
        loadMoreView.setVisibility(View.GONE);
    }


    private void setRecycleNoScroll() {

        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_REFRESH) {
            pageInfo.PageIndex = 0;
            mIsRefreshing = false;
            goodsList.clear();
            loadData();
        }
    }
}
