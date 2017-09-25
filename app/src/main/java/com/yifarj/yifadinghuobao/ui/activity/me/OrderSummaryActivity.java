package com.yifarj.yifadinghuobao.ui.activity.me;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.OrderSummaryAdapter;
import com.yifarj.yifadinghuobao.adapter.OrderSummaryHeadAdapter;
import com.yifarj.yifadinghuobao.adapter.helper.HeaderViewRecyclerAdapter;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel;
import com.yifarj.yifadinghuobao.model.entity.OrderSummaryEntity;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.ZipUtil;
import com.yifarj.yifadinghuobao.view.CustomEmptyView;
import com.yifarj.yifadinghuobao.view.TitleView;
import com.yifarj.yifadinghuobao.view.utils.DividerItemDecoration;

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
 */

public class OrderSummaryActivity extends BaseActivity{

    @BindView(R.id.titleView)
    TitleView titleView;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;

    @BindView(R.id.empty_view)
    CustomEmptyView emptyView;

    private List<OrderSummaryEntity.ValueEntity.Product> mList;
    private LinearLayoutManager mManager;
    private OrderSummaryAdapter mAdapter;
    private HeaderViewRecyclerAdapter mHeaderAdapter;
    private TextView mTvNoOrder;
    private RecyclerView mRecyclerHeadView;
    private TextView mTvOrder;
    private List<SaleGoodsItemModel> mHeadList;
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
                finish();
            }
        });
        loadData();
    }

    private void init() {
        mManager = new LinearLayoutManager(this);
        recycleView.setHasFixedSize(true);
        recycleView.setNestedScrollingEnabled(true);
        recycleView.setLayoutManager(mManager);
        //添加分割线
        recycleView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        Log.e("init", String.valueOf(mList.size()));
        mAdapter = new OrderSummaryAdapter(mList,this);

        mHeaderAdapter = new HeaderViewRecyclerAdapter(mAdapter);
        recycleView.setAdapter(mHeaderAdapter);
        createHeaderView();

    }

    private void createHeaderView() {
        View headerView = LayoutInflater.from(this).inflate(R.layout.item_order_summary_head, recycleView, false);
        mTvNoOrder = headerView.findViewById(R.id.tv_no_order);
        mRecyclerHeadView = headerView.findViewById(R.id.recycle_head);
        mTvOrder = headerView.findViewById(R.id.tv_order);
        loadHeadData();
        mHeadAdapter = new OrderSummaryHeadAdapter(mHeadList, this);
        //添加分割线
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerHeadView.addItemDecoration(decoration);
        mRecyclerHeadView.setAdapter(mHeadAdapter);

        mHeaderAdapter.addHeaderView(headerView);

    }

    private void loadHeadData() {
        // 查询购物车商品
        RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class).where())
                .queryList()
                .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                    @Override
                    public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModels) throws Exception {
                        LogUtils.e("saleGoodsItemModels：" + saleGoodsItemModels.size());
                        mHeadList = saleGoodsItemModels;
                        Log.e("mHeadList", String.valueOf(mHeadList.size()));
                        /*if (orderCount > 0) {
                            titleView.setRightIconText(View.VISIBLE, orderCount);
                            LogUtils.e("orderCount：" + orderCount);
                        } else if (orderCount == 0) {
                            titleView.setRightIconText(View.GONE, 0);
                            LogUtils.e("orderCount：" + orderCount);
                        }*/
                    }
                });
    }

    @Override
    public void loadData() {
        Log.e("loadData:","loadData");
        String body = "{\"SqlStr\":\"select vsd.ProductName,vsd.Quantity,vsd.UnitName,vsd.TotalPrice,vsd.SalesTypeName,vsd.Code,tt.Name from VS_SalesOutBillDetails vsd " +
                "left join TB_Product tp on vsd.ProductId = tp.Id " +
                "left join TB_Trader tt on tt.Id = tp.DefaultTraderId " +
                "where vsd.SalesTypeName = '售' and tt.Name != '' and vsd.Code != ''\",\"SummaryResult\":\"\"}";
        body = body.replace("\\","");
        body = ZipUtil.gzip(body);
        RetrofitHelper.getOrderSummaryAPI()
                .getOrderSummary(AppInfoUtil.getToken(),"SummaryView",body,"")
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderSummaryEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.e("onSubscribe","onSubscribe");
                    }
                    @Override
                    public void onNext(@NonNull OrderSummaryEntity entity) {

                        Log.e("onNext", String.valueOf(entity.HasError));
                        if(!entity.HasError){
                            if(entity.Value != null){
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

}
