package com.yifarj.yifadinghuobao.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.yifarj.yifadinghuobao.R;


public class LoadingDialog extends Dialog implements Loader {
	private TextView msgView;
	private String msg;
	private Context context;

	public LoadingDialog(Context context) {
		this(context, R.string.loading_please_wait);
	}

	public LoadingDialog(Context context, int resId) {
		this(context, context.getString(resId));
	}

	public LoadingDialog(Context context, int resId, boolean cannotBack) {
		this(context, context.getString(resId));
		if (cannotBack)
			backTime = -1000;
	}

	public LoadingDialog(Context context, String msg) {
		super(context, R.style.loading_dialog);
		this.context = context;
		this.msg = msg;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_loading);
		msgView = (TextView)findViewById(R.id.txtDialogMsg);
		msgView.setText(msg);
		setCanceledOnTouchOutside(false);
	}
	
	
	public void setMessage(String msg) {
		this.msg = msg; 
		if(msgView != null)
			msgView.setText(msg);
	}

	private static final int MAX_BACK_TIME = 2;
	private int backTime = -1000;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			backTime++;
			if (backTime >= MAX_BACK_TIME) {
				return super.onKeyDown(keyCode, event);
			}
			return true;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void loadFailure() {
		dismiss();
	}

	@Override
	public void loadSuccess() {
		dismiss();
	}

	@Override
	public void loadFinished() {
		dismiss();
	}

	@Override
	public void loadStart() {
		if (context != null && context instanceof Activity) {
			if (((Activity) context).isFinishing()) {
				return;
			}
		}
		if(!isShowing())
			show();
	}
}
