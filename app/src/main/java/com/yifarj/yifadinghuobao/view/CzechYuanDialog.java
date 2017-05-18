package com.yifarj.yifadinghuobao.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yifarj.yifadinghuobao.R;


/**
 * CzechYuanDialog
 *
 * @auther Czech.Yuan
 * @date 2017/5/18 17:17
 */
public class CzechYuanDialog extends Dialog {
    private static int default_width = 300; //默认宽度

    private static int default_height = 200;//默认高度

    private TextView tvContent;
    private TextView cancel;
    private TextView confirm;

    public CzechYuanDialog(Context context, int style) {

        this(context, default_width, default_height, style);
        init();
        show();
    }


    public CzechYuanDialog(Context context, int width, int height, int style) {

        super(context, style);
        init();

        Window window = getWindow();

        WindowManager.LayoutParams params = window.getAttributes();

        params.gravity = Gravity.CENTER;

        window.setAttributes(params);
        show();
    }

    private void init() {
        setContentView(R.layout.czech_yuan_dialog);
        tvContent = (TextView) findViewById(R.id.tvContentText);
        cancel = (TextView) findViewById(R.id.cancel);
        confirm = (TextView) findViewById(R.id.confirm);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setConfirmClickListener(final View.OnClickListener l) {
        if (confirm != null) {
            confirm.setOnClickListener(new View.OnClickListener() {
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
}
