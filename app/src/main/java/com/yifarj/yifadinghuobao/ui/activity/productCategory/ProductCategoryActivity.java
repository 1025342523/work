package com.yifarj.yifadinghuobao.ui.activity.productCategory;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.ProductCategoryAdapter;
import com.yifarj.yifadinghuobao.adapter.helper.AbsRecyclerViewAdapter;
import com.yifarj.yifadinghuobao.model.entity.ProductCategoryListEntity;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.view.CustomEmptyView;
import com.yifarj.yifadinghuobao.view.TitleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * ProductCategoryActivity
 *
 * @auther Czech.Yuan
 * @date 2017/7/17 15:23
 */
public class ProductCategoryActivity extends BaseActivity {

    @BindView(R.id.recycleView)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_view)
    CustomEmptyView mCustomEmptyView;

    @BindView(R.id.titleView)
    TitleView titleView;

    private ProductCategoryAdapter mProductCategoryAdapter;

    private List<ProductCategoryListEntity.ValueEntity> mItemData = new ArrayList<>();


    @Override
    public int getLayoutId() {
        return R.layout.activity_product_category;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        loadData();
    }

    @Override
    public void loadData() {
        titleView.setLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initRecyclerView();
        RetrofitHelper.getProductCategoryListApi()
                .getProductCategoryList("ProductCategoryList", "", "", "", AppInfoUtil.getToken())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ProductCategoryListEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ProductCategoryListEntity productCategoryListEntity) {
                        if (!productCategoryListEntity.HasError) {
                            mItemData.addAll(productCategoryListEntity.Value);
                            finishTask();
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

    @Override
    public void finishTask() {
        if (mItemData != null) {
            if (mItemData.size() == 0) {
                showEmptyView();
            } else {
                hideEmptyView();
            }
        }
        if (mRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE || !mRecyclerView.isComputingLayout()) { // RecyclerView滑动过程中刷新数据导致的Crash(Android官方的一个Bug)
            mProductCategoryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mProductCategoryAdapter = new ProductCategoryAdapter(mRecyclerView, mItemData);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, R.drawable.recyclerview_divider_goods));
        mRecyclerView.setAdapter(mProductCategoryAdapter);
        mProductCategoryAdapter.setOnItemClickListener(new AbsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, AbsRecyclerViewAdapter.ClickableViewHolder holder) {
                if (holder != null) {
                }
            }
        });
    }


    public void showEmptyView() {
//        if (mSwipeRefreshLayout != null) {
//            mSwipeRefreshLayout.setRefreshing(false);
//        }
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.drawable.ic_data_empty);
        mCustomEmptyView.setEmptyText("暂无订货单");
    }

    public void hideEmptyView() {
        if (mCustomEmptyView == null && mRecyclerView == null) {
            return;
        }
        mCustomEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


}
