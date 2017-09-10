package com.yifarj.yifadinghuobao.ui.activity.common;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.czechyuan.imagepicker.ImagePicker;
import com.czechyuan.imagepicker.bean.ImageItem;
import com.czechyuan.imagepicker.ui.ImageGridActivity;
import com.jakewharton.rxbinding2.view.RxView;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.database.AppDatabase;
import com.yifarj.yifadinghuobao.model.entity.AccountListEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.utils.CommonUtil;
import com.yifarj.yifadinghuobao.utils.PreferencesUtil;
import com.yifarj.yifadinghuobao.view.CzechYuanDialog;
import com.yifarj.yifadinghuobao.view.TitleView;
import com.yifarj.yifadinghuobao.view.utils.GlideImageLoader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.yifarj.yifadinghuobao.utils.PreferencesUtil.putString;

public class ConnectServerActivity extends BaseActivity {

    private static final int REQUEST_BARCODE = 10;
    public static final int REQUEST_IMAGE_SELECTOR = 52;

    @BindView(R.id.titleView)
    TitleView titleView;
    @BindView(R.id.llGetAccountIds)
    LinearLayout llGetAccountIds;
    @BindView(R.id.llScan)
    LinearLayout llScan;
    @BindView(R.id.llScan2)
    LinearLayout llScan2;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.llTest)
    LinearLayout llTest;
    @BindView(R.id.tvTestName)
    TextView tvTestName;

    private String mAccountId;
    private String mIp, mPort, mUrl, mKeyCode;
    private AsyncTask<Void, Void, String> task;
    private boolean isPressed = false;
    private static Handler mHandler;

    @Override
    public int getLayoutId() {
        return R.layout.activity_connect_server;
    }


    @Override
    protected void onDestroy() {
        if (task != null) {
            task.cancel(true);
        }
        if (mHandler != null) {
            mHandler.removeMessages(6);
            mHandler = null;
            System.gc();
        }
        super.onDestroy();
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 6) {
                    String serverInfo = (String) msg.obj;
                    decodeServerInfo(serverInfo);
                }
            }
        };
        titleView.setLeftIconClickListener(view -> finish());
        RxView.clicks(llGetAccountIds)
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        if (!isPressed) {
                            llScan.setVisibility(View.VISIBLE);
                            llScan2.setVisibility(View.VISIBLE);
                            isPressed = true;
                        } else {
                            llScan.setVisibility(View.GONE);
                            llScan2.setVisibility(View.GONE);
                            isPressed = false;
                        }
                    }
                });

        RxView.clicks(llScan)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        Intent intent = new Intent(ConnectServerActivity.this, ScanQrcodeActivity.class);
                        startActivityForResult(intent, REQUEST_BARCODE);
                        overridePendingTransition(R.anim.slide_in_up, R.anim.fake_fade_out);
                    }
                });

        RxView.clicks(llScan2)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        ImagePicker imagePicker = ImagePicker.getInstance();
                        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
                        imagePicker.setMultiMode(false);
                        imagePicker.setShowCamera(true);  //显示拍照按钮
                        imagePicker.setCrop(false);        //允许裁剪（单选才有效）
                        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
                        imagePicker.setStyle(com.czechyuan.imagepicker.view.CropImageView.Style.RECTANGLE);  //裁剪框的形状
                        imagePicker.setFocusWidth(900);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
                        imagePicker.setFocusHeight(900);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
                        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
                        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
                        Intent intent = new Intent(ConnectServerActivity.this, ImageGridActivity.class);
                        startActivityForResult(intent, REQUEST_IMAGE_SELECTOR);
                    }
                });

        mIp = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_IP);
        mPort = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_PORT);
        mKeyCode = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_KEY_CODE);
        mUrl = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_DOMAIN);
        mAccountId = PreferencesUtil.getString(ApiConstants.CPreference.ACCOUNT_ID);
        RxView.clicks(llTest)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        getAccountIds(mIp, mPort, mKeyCode);
                    }
                });
    }


    private void getAccountIds(String ip, String port, String keycode) {
        if (!CommonUtil.isNetworkAvailable(ConnectServerActivity.this)) {
            ToastUtils.showShortSafe("当前网络不可用,请检查网络设置");
            return;
        }
        if (ip == null || port == null || mUrl == null) {
            ToastUtils.showShortSafe("请先获取服务器信息");
            return;
        }
        RetrofitHelper.getAccountAPI()
                .getAccountList(ip, port, keycode)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AccountListEntity>() {


                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        LogUtils.e("onSubscribe", "订阅");
                    }

                    @Override
                    public void onNext(AccountListEntity valueEntity) {
                        LogUtils.e("onNext", valueEntity.toString());
                        if (!valueEntity.HasError) {
                            //                            ToastUtils.showShortSafe("测试连接成功");
                            CzechYuanDialog mDialog = new CzechYuanDialog(ConnectServerActivity.this, R.style.CzechYuanDialog);
                            mDialog.setContent("恭喜您，测试连接成功，去登录吧");
                            mDialog.setConfirmClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String oldAccountID = PreferencesUtil.getString(ApiConstants.CPreference.ACCOUNT_ID);
                                    String oldUrl = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_DOMAIN);
                                    if (!StringUtils.isEmpty(oldAccountID)) {
                                        if (!oldAccountID.equals(mAccountId)) {
                                            FlowManager.getDatabase(AppDatabase.class).reset(ConnectServerActivity.this);
                                        }
                                    }
                                    if (!StringUtils.isEmpty(oldUrl)) {
                                        if (!oldUrl.equals(mUrl)) {
                                            FlowManager.getDatabase(AppDatabase.class).reset(ConnectServerActivity.this);
                                        }
                                    }
                                    onSave();
                                    finish();
                                }
                            });
                        } else {
                            ToastUtils.showShortSafe("测试连接失败，服务器异常");
                            LogUtils.e(valueEntity.Information);
                        }

                    }

                    @Override
                    public void onError(Throwable t) {
                        ToastUtils.showShortSafe("测试连接失败，请检查网络是否畅通");
                        LogUtils.e(t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.e("onComplete", "完成");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_BARCODE) {

                if (data != null) {
                    String serverInfo = data.getStringExtra("barcode");
                    decodeServerInfo(serverInfo);
                } else {
                    ToastUtils.showShortSafe("未获取到服务器信息，请重新获取");
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == REQUEST_IMAGE_SELECTOR) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                final String picturePath = images.get(0).path;
                if (picturePath != null) {
                    task = new BackgroundTask(ConnectServerActivity.this, picturePath).execute();
                } else {
                    Toast.makeText(this, "获取图片路径失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "未选择任何图片", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class BackgroundTask extends AsyncTask<Void, Void, String> {
        private WeakReference<Context> mContext;
        private String path;

        BackgroundTask(Context context, String path) {
            this.mContext = new WeakReference<>(context);
            this.path = path;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return QRCodeDecoder.syncDecodeQRCode(path);
        }

        @Override
        protected void onPostExecute(String result) {
            ConnectServerActivity activity = (ConnectServerActivity) mContext.get();
            if (activity != null) {
                if (TextUtils.isEmpty(result)) {
                    Toast.makeText(activity, "未发现二维码", Toast.LENGTH_SHORT).show();
                } else {
                    Message msg = new Message();
                    msg.what = 6;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            }

        }
    }

    private void decodeServerInfo(String serverInfo) {
        try {
            String tempServerInfo = serverInfo.substring(7, serverInfo.length());
            String[] connectInfoArray = tempServerInfo.split("\\?");
            String server1 = connectInfoArray[0];
            String server2;
            if (server1.contains("yidinghuo")) {
                server2 = server1.substring(0, server1.length() - 13);
            } else {
                server2 = server1.substring(0, server1.length() - 8);
            }
            mUrl = "http://" + server2 + "yifa.asmx";
            String connectInfo2 = connectInfoArray[1];
            String[] connectInfo3 = connectInfo2.split("&");
            mIp = connectInfo3[0].substring(5, connectInfo3[0].length());
            mPort = connectInfo3[1].substring(5, connectInfo3[1].length());
            mAccountId = connectInfo3[2].substring(9, connectInfo3[2].length());
            if (connectInfo3.length > 3) {
                mKeyCode = connectInfo3[3].substring(8, connectInfo3[3].length());
            } else {
                mKeyCode = "";
            }
            Log.e("Url", mUrl);
            Log.e("mIp", mIp);
            Log.e("mPort", mPort);
            Log.e("mAccountId", mAccountId);
            Log.e("mKeyCode", mKeyCode);

            ApiConstants.CUrl.BASE_URL = mUrl;
            CzechYuanDialog mDialog = new CzechYuanDialog(ConnectServerActivity.this, R.style.CzechYuanDialog);
            mDialog.setContent("获取服务器信息成功，是否测试连接？");
            mDialog.setConfirmClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getAccountIds(mIp, mPort, mKeyCode);
                }
            });
        } catch (Exception e) {
            ToastUtils.showShortSafe("获取服务器信息失败，请重新获取");
        }
    }

    public void onSave() {
        putString(ApiConstants.CPreference.ACCOUNT_ID, mAccountId);
        putString(ApiConstants.CPreference.LOGIN_IP, mIp);
        putString(ApiConstants.CPreference.LOGIN_PORT, mPort);
        putString(ApiConstants.CPreference.LOGIN_DOMAIN, mUrl);
        putString(ApiConstants.CPreference.LOGIN_KEY_CODE, mKeyCode);
    }
}
