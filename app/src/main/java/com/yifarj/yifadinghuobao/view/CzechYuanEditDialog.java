package com.yifarj.yifadinghuobao.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.yifarj.yifadinghuobao.R;


/**
 * CzechYuanEditDialog
 *
 * @auther Czech.Yuan
 * @date 2017/5/18 18:18
 */
public class CzechYuanEditDialog extends Dialog {
    private static int default_width = 350; //默认宽度

    private static int default_height = 250;//默认高度

    private TextView tvTitle;
    private TextView cancel;
    private TextView confirm;
    private EditText etInput;

    public CzechYuanEditDialog(Context context, int style) {

        this(context, default_width, default_height, style);
        init();
        show();
    }


    public CzechYuanEditDialog(Context context, int width, int height, int style) {

        super(context, style);
        init();

        Window window = getWindow();

        WindowManager.LayoutParams params = window.getAttributes();

        params.gravity = Gravity.CENTER;

        window.setAttributes(params);
        show();
    }

    private void init() {
        setContentView(R.layout.czech_yuan_edit_dialog);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        cancel = (TextView) findViewById(R.id.cancel);
        confirm = (TextView) findViewById(R.id.confirm);
        etInput = (EditText) findViewById(R.id.etInput);
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

    public void setCancelClickListener(final View.OnClickListener l) {
        if (cancel != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
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

    public EditText getEditText() {
        return etInput;
    }

    public void setTitle(int resId) {
        if (tvTitle != null) {
            tvTitle.setText(resId);
        }
    }

    public void setTvTitle(String content) {
        if (tvTitle != null) {
            tvTitle.setText(content);
        }
    }
}
