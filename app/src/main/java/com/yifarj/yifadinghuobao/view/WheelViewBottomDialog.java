package com.yifarj.yifadinghuobao.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;
import com.yifarj.yifadinghuobao.R;

import java.util.List;

/**
 * 数据选择器
 */
public class WheelViewBottomDialog extends Dialog {
    private Context mContext;
    private View viewBg;
    private RelativeLayout rlDialogBg;
    private Handler mHandler;
    private int whatMsg;
    private TextView tvCancel;
    private TextView tvDone;
    private TextView tvTitle;
    private FrameLayout flContent;
    private WheelView wheelView;

    public WheelViewBottomDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
        init();
        show();
    }

    public WheelViewBottomDialog(Context context) {
        super(context, R.style.dialog);
        this.mContext = context;
        init();
        show();
    }

    /**
     * 初始化控件 penglei
     */
    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bottom_dialog_wheel_view);
        tvCancel = (TextView) findViewById(R.id.tvCancel);
        tvDone = (TextView) findViewById(R.id.tvDone);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        viewBg = findViewById(R.id.viewBg);
        rlDialogBg = (RelativeLayout) findViewById(R.id.rlDialogBg);
        wheelView = (WheelView) findViewById(R.id.wheelView);
        tvCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        viewBg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        wheelView.setSkin(WheelView.Skin.Holo);
        wheelView.setWheelAdapter(new ArrayWheelAdapter(mContext));
    }

    public void setWheelData(List<String> data) {
        wheelView.setWheelData(data);
    }

    public void setIndex(int index) {
        wheelView.setSelection(index);
    }

    public void setOnWheelItemSelectedListener(WheelView.OnWheelItemSelectedListener l) {
        wheelView.setOnWheelItemSelectedListener(l);
    }

    public void setOkBtnClickListener(final View.OnClickListener l) {
        if (tvDone != null) {
            tvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (l != null) {
                        l.onClick(v);
                    }
                    dismiss();
                }
            });
        }
    }

    public void setCancelBtnClickListennal(final View.OnClickListener l) {
        if (tvCancel != null) {
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (l != null) {
                        l.onClick(v);
                    }
                    dismiss();
                }
            });
        }
    }

    public void setTitle(String title) {
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    public void setTitle(int resId) {
        if (tvTitle != null) {
            tvTitle.setText(resId);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        viewBg.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.fade_in));
        rlDialogBg.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.slide_in_up));
    }

    /**
     * @param mHandler penglei
     */
    public void dismissDialog(int whatMsg, Handler mHandler) {
        this.mHandler = mHandler;
        this.whatMsg = whatMsg;
        dismiss();
    }

    @Override
    public void dismiss() {
        Animation anim = AnimationUtils
                .loadAnimation(mContext, R.anim.fade_out);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewBg.setVisibility(View.INVISIBLE);
                rlDialogBg.setVisibility(View.INVISIBLE);
                if (null != mHandler && 0 != whatMsg) {
                    Message msg = new Message();
                    msg.what = whatMsg;
                    mHandler.sendMessage(msg);
                }
                WheelViewBottomDialog.super.dismiss();
            }
        });
        viewBg.startAnimation(anim);
        rlDialogBg.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.slide_out_down));
    }
}
