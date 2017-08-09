package com.yifarj.yifadinghuobao.ui.fragment.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.GoodsListAdapter;
import com.yifarj.yifadinghuobao.adapter.helper.AbsRecyclerViewAdapter;
import com.yifarj.yifadinghuobao.adapter.helper.EndlessRecyclerOnScrollListener;
import com.yifarj.yifadinghuobao.adapter.helper.HeaderViewRecyclerAdapter;
import com.yifarj.yifadinghuobao.model.entity.GoodsListEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.network.PageInfo;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.network.utils.JsonUtils;
import com.yifarj.yifadinghuobao.ui.activity.main.CollectionActivity;
import com.yifarj.yifadinghuobao.ui.activity.main.NewProductActivity;
import com.yifarj.yifadinghuobao.ui.activity.main.PromotionActivity;
import com.yifarj.yifadinghuobao.ui.activity.main.RecommendActivity;
import com.yifarj.yifadinghuobao.ui.activity.shoppingcart.ShopDetailActivity;
import com.yifarj.yifadinghuobao.ui.activity.web.WebActivity;
import com.yifarj.yifadinghuobao.ui.fragment.base.BaseFragment;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.ScreenUtil;
import com.yifarj.yifadinghuobao.view.AutoScrollViewPager;
import com.yifarj.yifadinghuobao.view.CustomEmptyView;
import com.yifarj.yifadinghuobao.view.ViewPagerIndicator;
import com.yifarj.yifadinghuobao.view.utils.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * TabMainFragment
 *
 * @auther Czech.Yuan
 * @date 2017/5/12 15:07
 */
public class TabMainFragment extends BaseFragment {

    //    @BindView(R.id.viewpager)
    AutoScrollViewPager viewPager;

    //    @BindView(R.id.indicator)
    ViewPagerIndicator indicator;

    //    @BindView(R.id.rlPagerContainer)
    RelativeLayout rlPagerContainer;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_view)
    CustomEmptyView mCustomEmptyView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    //    @BindView(R.id.tvPromotion)
    TextView tvPromotion;
    //    @BindView(R.id.tvCollection)
    TextView tvCollection;
    //    @BindView(R.id.tvNewProduct)
    TextView tvNewProduct;
    //    @BindView(R.id.tvRecommend)
    TextView tvRecommend;

    private boolean mIsRefreshing = false;

    private View loadMoreView;

    private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;

    private List<GoodsListEntity.ValueEntity> goodsList = new ArrayList<>();

    private PageInfo pageInfo = new PageInfo();

    private GoodsListAdapter mGoodsListAdapter;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void finishCreateView(Bundle savedInstanceState) {
        isPrepared = true;
        lazyLoad();
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
        if (mSwipeRefreshLayout == null) {
            return;
        }
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mGoodsListAdapter = new GoodsListAdapter(mRecyclerView, goodsList, true, null, null, 0, 0);
        mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(mGoodsListAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST, R.drawable.recyclerview_divider_goods));
        mRecyclerView.setAdapter(mHeaderViewRecyclerAdapter);
        setRecycleNoScroll();
        createHeadView();
        createLoadMoreView();
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {

            @Override
            public void onLoadMore(int i) {
                pageInfo.PageIndex++;
                loadData();
                loadMoreView.setVisibility(View.VISIBLE);
            }
        });
        mGoodsListAdapter.setOnItemClickListener(new AbsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, AbsRecyclerViewAdapter.ClickableViewHolder holder) {
                if (holder != null && position < goodsList.size()) {
                    Intent intent = new Intent(getActivity(), ShopDetailActivity.class);
                    intent.putExtra("shoppingId", goodsList.get(position).Id);
                    intent.putExtra("saleType", 0);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void loadData() {
        LogUtils.e("loadData", "获取商品列表数据");
        if (pageInfo == null) {
            pageInfo = new PageInfo();
        }
        RetrofitHelper.getGoodsListAPI()
                .getGoodsList("ProductList", JsonUtils.serialize(pageInfo), "status  not in (4,8)", "[" + DataSaver.getMettingCustomerInfo().TraderId + "]", AppInfoUtil.getToken())
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
        if (mSwipeRefreshLayout == null) {
            return;
        }
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

        if (mRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE || !mRecyclerView.isComputingLayout()) { // RecyclerView滑动过程中刷新数据导致的Crash(Android官方的一个Bug)
            mGoodsListAdapter.notifyDataSetChanged();
        }

    }

    public void showEmptyView() {
        mSwipeRefreshLayout.setRefreshing(false);
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.drawable.ic_data_empty);
        mCustomEmptyView.setEmptyText("暂无数据");
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


    private void createHeadView() {
        View headView = LayoutInflater.from(getActivity())
                .inflate(R.layout.tab_main_head_view, mRecyclerView, false);

        viewPager = (AutoScrollViewPager) headView.findViewById(R.id.viewpager);
        indicator = (ViewPagerIndicator) headView.findViewById(R.id.indicator);
        rlPagerContainer = (RelativeLayout) headView.findViewById(R.id.rlPagerContainer);
        tvPromotion = (TextView) headView.findViewById(R.id.tvPromotion);
        tvCollection = (TextView) headView.findViewById(R.id.tvCollection);
        tvNewProduct = (TextView) headView.findViewById(R.id.tvNewProduct);
        tvRecommend = (TextView) headView.findViewById(R.id.tvRecommend);
        rlPagerContainer.getLayoutParams().height = ScreenUtil.getScreenWidth(getContext()) * 200 / 750;
        viewPager.setCurrentItem(0);
        indicator.setCount(4);
        indicator.setCurrentItem(0);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {
                ImageView view = new ImageView(getContext());
                view.setScaleType(ImageView.ScaleType.FIT_XY);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                view.setLayoutParams(params);
                switch (position) {
                    case 0:
                        view.setImageResource(R.drawable.banner_1);
                        break;
                    case 1:
                        view.setImageResource(R.drawable.banner_2);
                        break;
                    case 2:
                        view.setImageResource(R.drawable.banner_3);
                        break;
                    case 3:
                        view.setImageResource(R.drawable.banner_4);
                        break;
                }
                container.addView(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), WebActivity.class);
                        String url = "";
                        switch (position) {
                            case 0:
                                url = "http://m.yifarj.com/index.php?m=wap";
                                break;
                            case 1:
                                url = "http://m.yifarj.com/index.php?a=lists&typeid=29";
                                break;
                            case 2:
                                url = "http://m.yifarj.com/index.php?a=lists&typeid=19";
                                break;
                            case 3:
                                url = "http://m.yifarj.com/index.php?a=lists&typeid=7";
                                break;
                        }
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                });
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (indicator != null) {
                    indicator.setCurrentItem(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.startAutoScroll();

        tvPromotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PromotionActivity.class);
                startActivity(intent);
            }
        });
        tvCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CollectionActivity.class);
                startActivity(intent);
            }
        });
        tvNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewProductActivity.class);
                startActivity(intent);
            }
        });
        tvRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RecommendActivity.class);
                startActivity(intent);
            }
        });
        mHeaderViewRecyclerAdapter.addHeaderView(headView);
    }

    private void setRecycleNoScroll() {

        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }
}
