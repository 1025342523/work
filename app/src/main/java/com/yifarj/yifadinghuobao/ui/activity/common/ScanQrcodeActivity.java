package com.yifarj.yifadinghuobao.ui.activity.common;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.view.TitleView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


/**
 * 扫描二维码
 *
 * @auther Czech.Yuan
 * @date 2017/5/20 10:55
 */
public class ScanQrcodeActivity extends BaseActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_CAMERA = 10;

    @BindView(R.id.zxingview)
    ZXingView zXingView;
    @BindView(R.id.titleView)
    TitleView titleView;
    @BindView(R.id.btnFlashLight)
    Button btnFlashLight;
    private boolean flashOpened, isScanBarcode;


    @Override
    public int getLayoutId() {
        return R.layout.activity_scan_qrcode;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initView();
    }

    private void initView() {
        isScanBarcode = getIntent().getBooleanExtra("ScanBarcode", false);
        if (isScanBarcode) {
            titleView.setTitle("扫描条形码");
        }
        RxView.clicks(btnFlashLight)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        onFlashClick();
                    }
                });


        titleView.setRightTextClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out_down);
            }
        });
        //判断自动获取位置权限是否开启
        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    AlertDialog.Builder b = new AlertDialog.Builder(this);
                    b.setTitle(getString(R.string.becareful));
                    b.setMessage(getString(R.string.photo_permission));
                    b.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)); //跳转到应用管理设置界面
                            dialog.dismiss();
                        }
                    });
                    b.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();

                } else {  //refuse
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_ACCESS_CAMERA);
                }
            } else {
                initQrcodeView();
            }
        } else {
            initQrcodeView();
        }
    }

    private void onFlashClick() {
        if (!flashOpened) {
            zXingView.openFlashlight();
            flashOpened = true;
        } else {
            zXingView.closeFlashlight();
            flashOpened = false;
        }
    }

    public void initQrcodeView() {
        zXingView.setDelegate(new QRCodeView.Delegate() {
            @Override
            public void onScanQRCodeSuccess(String result) {
                Intent intent = new Intent();
                intent.putExtra("barcode", result);
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out_down);
            }

            @Override
            public void onScanQRCodeOpenCameraError() {

            }
        });
        zXingView.startCamera();
        zXingView.startSpotAndShowRect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (zXingView != null) {
            zXingView.stopCamera();
            zXingView.stopSpot();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_down);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initQrcodeView();
            } else {
                ToastUtils.showShortSafe(getString(R.string.refuse_photo_permission));
            }
        }
    }

}
