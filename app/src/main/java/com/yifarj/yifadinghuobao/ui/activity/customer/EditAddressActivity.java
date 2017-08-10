package com.yifarj.yifadinghuobao.ui.activity.customer;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.model.entity.TraderEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.network.utils.JsonUtils;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.ZipUtil;
import com.yifarj.yifadinghuobao.view.TitleView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * EditAddressActivity
 *
 * @auther Czech.Yuan
 * @date 2017/7/31 17:31
 */
public class EditAddressActivity extends BaseActivity {

    @BindView(R.id.btnSave)
    Button btnSave;

    @BindView(R.id.titleView)
    TitleView titleView;

    @BindView(R.id.etAddress)
    EditText etAddress;

    @BindView(R.id.ivDefault)
    ImageView ivDefault;

    @BindView(R.id.llSetDefault)
    LinearLayout llSetDefault;

    @BindView(R.id.tvSetDefault)
    TextView tvSetDefault;

    private String address;

    //    private boolean isSelected = false;
    private int operation, position;

    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_delivery_adress;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        init();
        loadData();
    }

    public void hideInputMethod() {
        if (getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private void init() {
        etAddress.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etAddress.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        titleView.setLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideInputMethod();
                finish();
            }
        });
        etAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doSave();
                }
                return false;
            }
        });
        RxView.clicks(btnSave)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        doSave();
                    }
                });

        llSetDefault.setVisibility(View.GONE);
//        RxView.clicks(llSetDefault)
//                .compose(bindToLifecycle())
//                .subscribe(new Consumer<Object>() {
//                    @Override
//                    public void accept(@NonNull Object o) throws Exception {
//                        if (isSelected) {
//                            isSelected = false;
//                            ivDefault.setImageResource(R.drawable.ic_check_default_address);
//                        } else {
//                            isSelected = true;
//                            ivDefault.setImageResource(R.drawable.ic_check_default_address_selected);
//                        }
//                    }
//                });
    }

    private void doSave() {
        String address = etAddress.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            ToastUtils.showShortSafe("详细地址为必填项");
        } else {
            switch (operation) {
                case 0:
                    TraderEntity.ValueEntity.TraderDeliveryAddressListEntity traderDeliveryAddress = new TraderEntity.ValueEntity.TraderDeliveryAddressListEntity();
                    traderDeliveryAddress.Address = address;
                    traderDeliveryAddress.Id = 0;
                    traderDeliveryAddress.TraderId = DataSaver.getMettingCustomerInfo().TraderId;
//                    if (isSelected) {
//                        List<Integer> idList = new ArrayList<>();
//                        for (TraderEntity.ValueEntity.TraderDeliveryAddressListEntity item : DataSaver.getTraderInfo().TraderDeliveryAddressList) {
//                            idList.add(item.Id);
//                        }
//                        int minId = Collections.min(idList);
//                        LogUtils.e("最小ID为 " + minId);
//                        traderDeliveryAddress.Id = minId;
//                        DataSaver.getTraderInfo().TraderDeliveryAddressList.add(0, traderDeliveryAddress);
//                    } else {
//                        DataSaver.getTraderInfo().TraderDeliveryAddressList.add(traderDeliveryAddress);
//                    }
                    DataSaver.getTraderInfo().TraderDeliveryAddressList.add(traderDeliveryAddress);
                    saveAddress();
                    break;
                case 1:
                    if (position != -1) {
//                        if (isSelected) {
//                            if (position == 0) {
//                                TraderEntity.ValueEntity.TraderDeliveryAddressListEntity editAddress = DataSaver.getTraderInfo().TraderDeliveryAddressList.get(position);
//                                editAddress.Address = address;
//                                saveAddress();
//                            } else {
//                                TraderEntity.ValueEntity.TraderDeliveryAddressListEntity newAddress = new TraderEntity.ValueEntity.TraderDeliveryAddressListEntity();
//                                TraderEntity.ValueEntity.TraderDeliveryAddressListEntity oldAddress = DataSaver.getTraderInfo().TraderDeliveryAddressList.get(position);
//                                newAddress.Address = address;
//                                newAddress.Id = oldAddress.Id;
//                                newAddress.TraderId = oldAddress.TraderId;
//                                DataSaver.getTraderInfo().TraderDeliveryAddressList.remove(position);
//                                DataSaver.getTraderInfo().TraderDeliveryAddressList.add(0, newAddress);
//                                saveAddress();
//                            }
//                        } else {
                        TraderEntity.ValueEntity.TraderDeliveryAddressListEntity editAddress = DataSaver.getTraderInfo().TraderDeliveryAddressList.get(position);
                        editAddress.Address = address;
                        saveAddress();
//                        }
                    }
                    break;
            }
        }
    }

    private void saveAddress() {
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
                            ToastUtils.showShortSafe("保存成功！");
                            DataSaver.setTraderInfo(traderEntity.Value);
                            hideInputMethod();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            ToastUtils.showShortSafe("保存失败");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ToastUtils.showShortSafe("保存失败，请检查网络是否畅通");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void loadData() {
        address = getIntent().getStringExtra("TraderDeliveryAddress");
        operation = getIntent().getIntExtra("operation", -1);
        position = getIntent().getIntExtra("position", -1);
        if (!TextUtils.isEmpty(address)) {
            etAddress.setText(address);
            etAddress.setSelection(address.length());
        }
        if (operation == 0) {
            titleView.setTitle("新增收货地址");
        } else {
            titleView.setTitle("编辑收货地址");
        }

//        if (position == 0) {
//            isSelected = true;
//        }
//        if (isSelected) {
//            ivDefault.setImageResource(R.drawable.ic_check_default_address_selected);
//        } else {
//            ivDefault.setImageResource(R.drawable.ic_check_default_address);
//        }
    }


}
