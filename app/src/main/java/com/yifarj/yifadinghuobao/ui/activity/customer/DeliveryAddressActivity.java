package com.yifarj.yifadinghuobao.ui.activity.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.AddressListAdapter;
import com.yifarj.yifadinghuobao.model.entity.TraderEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.network.utils.JsonUtils;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.ZipUtil;
import com.yifarj.yifadinghuobao.view.CustomEmptyView;
import com.yifarj.yifadinghuobao.view.CzechYuanDialog;
import com.yifarj.yifadinghuobao.view.TitleView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * DeliveryAddressActivity
 *
 * @auther Czech.Yuan
 * @date 2017/7/31 15:16
 */
public class DeliveryAddressActivity extends BaseActivity {

    @BindView(R.id.btnAdd)
    Button btnAdd;

    @BindView(R.id.titleView)
    TitleView titleView;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_view)
    CustomEmptyView emptyView;

    private AddressListAdapter mAddressListAdapter;
    private List<TraderEntity.ValueEntity.TraderDeliveryAddressListEntity> mItemData = new ArrayList<>();
    private TraderEntity.ValueEntity traderInfo;

    @Override
    public int getLayoutId() {
        return R.layout.activity_delivery_adress;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        init();
        loadData();
        initRecyclerView();
    }

    @Override
    public void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAddressListAdapter = new AddressListAdapter(mRecyclerView, mItemData);
        mRecyclerView.setAdapter(mAddressListAdapter);
        mAddressListAdapter.setDeleteClickListener(new AddressListAdapter.DeleteClickListener() {
            @Override
            public void deleteClick(int position) {
                TraderEntity.ValueEntity.TraderDeliveryAddressListEntity item = mItemData.get(position);
                int index = mItemData.indexOf(item);
                if (index == 0) {
                    ToastUtils.showShortSafe("默认收货地址不允许删除！");
                } else {
                    CzechYuanDialog mDialog = new CzechYuanDialog(DeliveryAddressActivity.this, R.style.CzechYuanDialog);
                    mDialog.setContent("确定删除？");
                    mDialog.setConfirmClickListener(view1 -> {
                        DataSaver.getTraderInfo().TraderDeliveryAddressList.remove(position);
                        saveAddress(position);
                    });
                }
            }
        });
        mAddressListAdapter.setEditClickListener(new AddressListAdapter.EditClickListener() {
            @Override
            public void editClick(int position) {
                Intent intent = new Intent(DeliveryAddressActivity.this, EditAddressActivity.class);
                intent.putExtra("TraderDeliveryAddress", mItemData.get(position).Address);
                intent.putExtra("position", position);
                intent.putExtra("operation", 1);
                startActivityForResult(intent, 9);
            }
        });
    }

    private void saveAddress(int position) {
        RetrofitHelper
                .saveTraderApi()
                .saveTrader("Trader", ZipUtil.gzip(JsonUtils.serialize(DataSaver.getTraderInfo())), "", AppInfoUtil.getToken())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TraderEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TraderEntity traderEntity) {
                        if (!traderEntity.HasError) {
                            mAddressListAdapter.notifyItemRemoved(position);
                            DataSaver.setTraderInfo(traderEntity.Value);
                            traderInfo = traderEntity.Value;
                            mItemData = traderInfo.TraderDeliveryAddressList;
                            mAddressListAdapter.setData(mItemData);
                            ToastUtils.showShortSafe("删除成功！");
                        } else {
                            ToastUtils.showShortSafe("删除失败！");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ToastUtils.showShortSafe("删除失败，请检查网络是否畅通");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void init() {
        titleView.setLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        RxView.clicks(btnAdd)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        Intent intent = new Intent(DeliveryAddressActivity.this, EditAddressActivity.class);
                        intent.putExtra("operation", 0);
                        startActivityForResult(intent, 9);
                    }
                });
    }

    @Override
    public void loadData() {
        traderInfo = DataSaver.getTraderInfo();
        if (traderInfo == null) {
            return;
        }
        mItemData = traderInfo.TraderDeliveryAddressList;
    }

    public void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setEmptyImage(R.drawable.ic_data_empty);
        emptyView.setEmptyText("暂无数据");
    }

    public void hideEmptyView() {
        emptyView.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9 && resultCode == RESULT_OK) {
            traderInfo = DataSaver.getTraderInfo();
            mItemData = traderInfo.TraderDeliveryAddressList;
            if (mAddressListAdapter != null) {
                mAddressListAdapter.setData(mItemData);
                mAddressListAdapter.notifyDataSetChanged();
            }
        }
    }
}
