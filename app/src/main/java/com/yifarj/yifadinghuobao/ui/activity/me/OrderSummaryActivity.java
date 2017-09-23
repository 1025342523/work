package com.yifarj.yifadinghuobao.ui.activity.me;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.model.entity.OrderSummaryEntity;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.ZipUtil;
import com.yifarj.yifadinghuobao.view.TitleView;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017-09-21.
 */

public class OrderSummaryActivity extends BaseActivity{

    @BindView(R.id.titleView)
    TitleView titleView;

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
        init();
    }

    private void init() {

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
