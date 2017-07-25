package com.yifarj.yifadinghuobao.ui.activity.productCategory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.ProductCategoryAdapter;
import com.yifarj.yifadinghuobao.adapter.ProductCategoryChildAdapter;
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

    @BindView(R.id.recycleViewChild)
    RecyclerView recycleViewChild;

    @BindView(R.id.llAll)
    LinearLayout llAll;

    @BindView(R.id.empty_view)
    CustomEmptyView mCustomEmptyView;

    @BindView(R.id.titleView)
    TitleView titleView;

    @BindView(R.id.tvParentName)
    TextView parentName;

    private ProductCategoryAdapter mProductCategoryAdapter;

    private List<ProductCategoryListEntity.ValueEntity> mItemData = new ArrayList<>();
    private List<ProductCategoryListEntity.ValueEntity> parentData = new ArrayList<>();
    private List<ProductCategoryListEntity.ValueEntity> childData = new ArrayList<>();
    private ProductCategoryChildAdapter mProductCategoryChildAdapter;


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
                            parentData.clear();
                            ProductCategoryListEntity.ValueEntity allProduct = new ProductCategoryListEntity.ValueEntity();
                            allProduct.Level = 1;
                            allProduct.ParentId = 0;
                            allProduct.Path = "0";
                            allProduct.Name = "全部货品";
                            allProduct.Id = 0;
                            parentData.add(allProduct);
                            for (ProductCategoryListEntity.ValueEntity item : mItemData) {
                                if (item.Level == 1) {
                                    parentData.add(item);
                                }
                            }
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
        mProductCategoryAdapter = new ProductCategoryAdapter(mRecyclerView, parentData);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, R.drawable.recyclerview_divider_goods));
        mRecyclerView.setAdapter(mProductCategoryAdapter);

        recycleViewChild.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycleViewChild.setLayoutManager(linearLayoutManager);

        mProductCategoryAdapter.setOnItemClickListener(new AbsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, AbsRecyclerViewAdapter.ClickableViewHolder holder) {
                if (holder != null) {
                    if (position == 0) {
                        parentName.setText(parentData.get(position).Name);
                        llAll.setVisibility(View.VISIBLE);
                        recycleViewChild.setVisibility(View.GONE);
                    } else {
                        llAll.setVisibility(View.GONE);
                        recycleViewChild.setVisibility(View.VISIBLE);
                        boolean flag = false;
                        for (ProductCategoryListEntity.ValueEntity item : mItemData) {
                            if (item.ParentId == parentData.get(position).Id) {
                                flag = true;
                                break;
                            }
                        }
                        if (flag) {
                            childData.clear();
                            parentName.setText(parentData.get(position).Name);
                            for (ProductCategoryListEntity.ValueEntity item : mItemData) {
                                if (item.Path.contains(String.valueOf(parentData.get(position).Id)) && item.Id != mItemData.get(position).Id) {
                                    childData.add(item);
                                }
                            }
                            mProductCategoryChildAdapter = new ProductCategoryChildAdapter(recycleViewChild, childData);
                            recycleViewChild.setAdapter(mProductCategoryChildAdapter);
                            mProductCategoryChildAdapter.setOnChildClickListener(new ProductCategoryChildAdapter.OnChildClickListener() {
                                @Override
                                public void onChildClick(int position) {
                                    Intent intent = new Intent(ProductCategoryActivity.this, ProductListActivity.class);
                                    intent.putExtra("CategoryId", childData.get(position).Id);
                                    intent.putExtra("CategoryName", childData.get(position).Name);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Intent intent = new Intent(ProductCategoryActivity.this, ProductListActivity.class);
                            intent.putExtra("CategoryId", parentData.get(position).Id);
                            intent.putExtra("CategoryName", parentData.get(position).Name);
                            startActivity(intent);
                        }
                    }
                }
            }
        });

        mRecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView, null, 0);

    }


    public void showEmptyView() {
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.drawable.ic_data_empty);
        mCustomEmptyView.setEmptyText("暂无数据");
    }

    public void hideEmptyView() {
        if (mCustomEmptyView == null && mRecyclerView == null) {
            return;
        }
        mCustomEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


}
