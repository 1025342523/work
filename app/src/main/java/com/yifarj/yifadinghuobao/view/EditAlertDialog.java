/*
 * 文 件 名:  CommentBottomDialog.java
 * 版    权:  China Academy of Telecommunication Research of HUNANTV Holdings Limited 2006-2014,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  penglei
 * 修改时间:  2014年8月12日 上午9:32:17
 */
package com.yifarj.yifadinghuobao.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yifarj.yifadinghuobao.R;


/**
 * 对话框
 */
public class EditAlertDialog extends Dialog {

    private Context mContext;
    private View viewBg;
    private LinearLayout llDialogBg;
    private Handler mHandler;
    private int whatMsg;
    private TextView tvDone;
    private TextView tvTitle;
    private TextView tvContent;
    private EditText etContent;
    private TextView tvCancel;

    public EditAlertDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
        init();
        show();
    }

    public EditAlertDialog(Context context) {
        super(context, R.style.dialog);
        this.mContext = context;
        init();
        show();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_alert);
        tvCancel = (TextView) findViewById(R.id.tvCancel);
        tvDone = (TextView) findViewById(R.id.tvDone);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvContent = (TextView) findViewById(R.id.tvContent);
        etContent = (EditText) findViewById(R.id.etContent);
        viewBg = findViewById(R.id.viewBg);
        llDialogBg = (LinearLayout) findViewById(R.id.llDialogBg);
        tvCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
//		viewBg.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				dismiss();
//			}
//		});
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

    public void setContent(int resId) {
        if (tvContent != null) {
            tvContent.setText(resId);
        }
    }

    public void setContent(String content) {
        if (tvContent != null) {
            tvContent.setText(content);
        }
    }

    public EditText getEditText() {
        return etContent;
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

    public void setOkText(int text) {
        if (tvDone != null) {
            tvDone.setText(text);
        }
    }

    public void setCancelText(String text) {
        if (tvCancel != null) {
            tvCancel.setText(text);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        viewBg.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.fade_in));
        llDialogBg.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.fade_in));
    }

    /**
     * @param mHandler
     */
    public void dismissDialog(int whatMsg, Handler mHandler) {
        this.mHandler = mHandler;
        this.whatMsg = whatMsg;
        dismiss();
    }

    public void setStyle() {
        tvTitle.setVisibility(View.INVISIBLE);
        etContent.setVisibility(View.GONE);
    }

    public void setContentTextSize(int size) {
        tvContent.setText(size);
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
                llDialogBg.setVisibility(View.INVISIBLE);
                if (null != mHandler && 0 != whatMsg) {
                    Message msg = new Message();
                    msg.what = whatMsg;
                    mHandler.sendMessage(msg);
                }
                EditAlertDialog.super.dismiss();
            }
        });
        viewBg.startAnimation(anim);
        llDialogBg.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.fade_out));
    }
}
