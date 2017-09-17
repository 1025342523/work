package com.yifarj.yifadinghuobao.view;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.utils.ScreenUtil;

/**
 * Created by Administrator on 2017-09-16.
 */

public abstract class BaseDialog extends DialogFragment {
    private static final String MARGIN = "margin";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String DIM = "dim_amount";
    private static final String BOTTOM = "show_bottom";
    private static final String CANCLE = "out_cancel";
    private static final String ANIM = "anim_style";
    private static final String LAYOUT = "layout_id";

    private int margin;//左右边距
    private int width;//宽度
    private int height;//高度
    private float dimAmount = 0.5f;//灰度深浅
    private boolean showBottom;//是否底部显示
    private boolean outCancel = true;//是否点击外部取消

    @StyleRes
    private int animStyle;
    @LayoutRes
    protected int layoutId;

    public abstract int intLayoutId();

    public abstract void convertView(ViewHolder holder,BaseDialog dialog);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.NiceDialog);
        layoutId = intLayoutId();
        ScreenUtil.init(getContext());
      /*  //恢复保存的数据
        if(savedInstanceState != null){
            margin = savedInstanceState.getInt(MARGIN);
            width = savedInstanceState.getInt(WIDTH);
            height = savedInstanceState.getInt(HEIGHT);
            dimAmount = savedInstanceState.getFloat(DIM);
            showBottom = savedInstanceState.getBoolean(BOTTOM);
            outCancel = savedInstanceState.getBoolean(CANCLE);
            animStyle = savedInstanceState.getInt(ANIM);
            layoutId = savedInstanceState.getInt(LAYOUT);
        }*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layoutId, container, false);
        convertView(ViewHolder.create(view),this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initParams();
    }

    private void initParams() {
        Window window = getDialog().getWindow();
        if(window != null){
            WindowManager.LayoutParams lp = window.getAttributes();
            //调节灰色背景透明度[0-1],默认0.5f
            lp.dimAmount = dimAmount;
            //是否在底部显示
            if(showBottom){
                lp.gravity = Gravity.BOTTOM;
                if(animStyle == 0){
                    animStyle = R.style.DefaultAnimation;
                }
            }
            //设置dialog宽度
            if(width == 0){
                lp.width = ScreenUtil.getScreenWidth(getContext()) - 2*ScreenUtil.dip2px(margin);
            }else if(width == -1){
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            }else{
                lp.width = ScreenUtil.dip2px(width);
            }
            //设置dialog高度
            if(height == 0){
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            }else{
                lp.height = ScreenUtil.dip2px(height);
            }
            //设置dialog进入、退出动画
            window.setWindowAnimations(animStyle);
            window.setAttributes(lp);
        }
        setCancelable(outCancel);
    }


    public BaseDialog setMargin(int margin){
        this.margin = margin;
        return this;
    }

    public BaseDialog setWidth(int width) {
        this.width = width;
        return this;
    }

    public BaseDialog setHeight(int height) {
        this.height = height;
        return this;
    }

    public BaseDialog setDimAmount(float dimAmount) {
        this.dimAmount = dimAmount;
        return this;
    }

    public BaseDialog setShowBottom(boolean showBottom) {
        this.showBottom = showBottom;
        return this;
    }

    public BaseDialog setOutCancel(boolean outCancel) {
        this.outCancel = outCancel;
        return this;
    }

    public BaseDialog setAnimStyle(int animStyle) {
        this.animStyle = animStyle;
        return this;
    }

    public BaseDialog show(FragmentManager manager){
        super.show(manager,String.valueOf(System.currentTimeMillis()));
        return this;
    }
}
