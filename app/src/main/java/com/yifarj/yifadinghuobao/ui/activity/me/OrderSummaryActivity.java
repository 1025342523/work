package com.yifarj.yifadinghuobao.ui.activity.me;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.OrderSummaryAdapter;
import com.yifarj.yifadinghuobao.adapter.OrderSummaryHeadAdapter;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel;
import com.yifarj.yifadinghuobao.model.entity.OrderSummaryEntity;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.ZipUtil;
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
 * Created by ZhangZeZhi on 2017-09-21.
 *
 */

public class OrderSummaryActivity extends BaseActivity {

    @BindView(R.id.titleView)
    TitleView titleView;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;

    @BindView(R.id.empty_view)
    CustomEmptyView emptyView;

    private List<OrderSummaryEntity.ValueEntity.Product> mList;

    private LinearLayoutManager mManager;
    private OrderSummaryAdapter mAdapter;

    private List<OrderSummaryEntity.ValueEntity.Product> mProductList;
    private OrderSummaryHeadAdapter mHeadAdapter;


    @Override
    public int getLayoutId() {
        return R.layout.activity_order_summary;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        titleView.setLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter = null;
                finish();
            }
        });
        loadHeadData();
    }

    private void init() {
        mManager = new LinearLayoutManager(this);
        recycleView.setHasFixedSize(true);
        recycleView.setNestedScrollingEnabled(true);
        recycleView.setLayoutManager(mManager);
        //添加分割线
        recycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        Log.e("init", String.valueOf(mList.size()));
        //
        if (mProductList != null&&mProductList.size() > 0&&mList != null && mList.size() > 0) {

            mHeadAdapter = new OrderSummaryHeadAdapter(mList, this, mProductList);
            recycleView.setAdapter(mHeadAdapter);

        } else if(mList != null && mList.size() > 0){

            mAdapter = new OrderSummaryAdapter(mList, this);
            recycleView.setAdapter(mAdapter);

        }else{

            emptyView.setVisibility(View.VISIBLE);
            recycleView.setVisibility(View.GONE);
            emptyView.setEmptyImage(R.drawable.ic_data_empty);
            emptyView.setEmptyText("暂无数据");

        }

    }

    private void loadHeadData() {
        // 查询购物车商品
        RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class).where())
                .queryList()
                .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                    @Override
                    public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModels) throws Exception {
                        if (!saleGoodsItemModels.isEmpty()) {
                            mProductList = new ArrayList<OrderSummaryEntity.ValueEntity.Product>();
                            for (int i = 0; i < saleGoodsItemModels.size(); i++) {
                                OrderSummaryEntity.ValueEntity.Product product = new OrderSummaryEntity.ValueEntity.Product();
                                SaleGoodsItemModel model = saleGoodsItemModels.get(i);
                                if (TextUtils.isEmpty(model.Supplier)) {
                                    model.Supplier = "无供应商";
                                }
                                product.Name = model.Supplier;
                                product.TotalPrice = model.CurrentPrice;
                                product.ProductName = model.ProductName;
                                product.Code = model.Code;
                                product.Quantity = model.Quantity;
                                product.UnitName = model.ProductUnitName;
                                mProductList.add(product);
                            }
                        }
                        LogUtils.e("saleGoodsItemModels：" + saleGoodsItemModels.size());

                        loadData();

                        Log.e("mHeadList", String.valueOf(saleGoodsItemModels.size()));
                    }
                });
    }

    @Override
    public void loadData() {
        Log.e("loadData:", "loadData");
        String body = "{\"SqlStr\":\"select vsd.ProductName,vsd.Quantity,vsd.UnitName,vsd.TotalPrice,vsd.SalesTypeName,vsd.Code,tt.Name from VS_SalesOutBillDetails vsd " +
                "left join TB_Product tp on vsd.ProductId = tp.Id " +
                "left join TB_Trader tt on tt.Id = tp.DefaultTraderId " +
                "where vsd.SalesTypeName = '售' and tt.Name != '' and vsd.Code != ''\",\"SummaryResult\":\"\"}";
        body = body.replace("\\", "");
        body = ZipUtil.gzip(body);
        RetrofitHelper.getOrderSummaryAPI()
                .getOrderSummary(AppInfoUtil.getToken(), "SummaryView", body, "")
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderSummaryEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.e("onSubscribe", "onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull OrderSummaryEntity entity) {

                        Log.e("onNext", String.valueOf(entity.HasError));
                        if (!entity.HasError) {
                            if (entity.Value != null) {
                                mList = entity.Value.SummaryResult;
                                init();
                                Log.e("list", String.valueOf(mList.size()));
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        mAdapter = null;
        super.onDestroy();

    }
}
