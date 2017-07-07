package com.yifarj.yifadinghuobao.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.TintTypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yifarj.yifadinghuobao.R;

import static com.yifarj.yifadinghuobao.R.id.et_num;


/**
 * NumberAddSubView
 *
 * @auther Czech.Yuan
 * @date 2017/5/18 11:52
 */
public class NumberAddSubView extends LinearLayout implements View.OnClickListener {


    public static final String TAG = "NumberAddSubView";
    public static final int DEFUALT_MAX = 10000;

    private TextView mEtxtNum;
    private ImageView mBtnAdd;
    private ImageView mBtnSub;

    private OnButtonClickListener onButtonClickListener;


    private LayoutInflater mInflater;


    private int value;
    private int minValue;
    private int maxValue = DEFUALT_MAX;


    public NumberAddSubView(Context context) {
        this(context, null);
    }

    public NumberAddSubView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberAddSubView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mInflater = LayoutInflater.from(context);
        initView();

        if (attrs != null) {

            final TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs,
                    R.styleable.NumberAddSubView, defStyleAttr, 0);


            int val = a.getInt(R.styleable.NumberAddSubView_value, 0);
            setValue(val);

            int maxVal = a.getInt(R.styleable.NumberAddSubView_maxValue, 0);
            if (maxVal != 0)
                setMaxValue(maxVal);

            int minVal = a.getInt(R.styleable.NumberAddSubView_minValue, 0);
            setMinValue(minVal);

            Drawable etBackground = a.getDrawable(R.styleable.NumberAddSubView_editBackground);
            if (etBackground != null)
                setEditTextBackground(etBackground);


            Drawable buttonAddBackground = a.getDrawable(R.styleable.NumberAddSubView_buttonAddBackgroud);
            if (buttonAddBackground != null)
                setButtonAddBackgroud(buttonAddBackground);

            Drawable buttonSubBackground = a.getDrawable(R.styleable.NumberAddSubView_buttonSubBackgroud);
            if (buttonSubBackground != null)
                setButtonSubBackgroud(buttonSubBackground);


            a.recycle();
        }
    }


    private void initView() {


        View view = mInflater.inflate(R.layout.widet_num_add_sub, this, true);

        mEtxtNum = (TextView) view.findViewById(et_num);
        mEtxtNum.setInputType(InputType.TYPE_NULL);
        mEtxtNum.setKeyListener(null);


        mBtnAdd = (ImageView) view.findViewById(R.id.iv_add);
        mBtnSub = (ImageView) view.findViewById(R.id.iv_minus);

        mBtnAdd.setOnClickListener(this);
        mBtnSub.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_add) {

            numAdd();

            if (onButtonClickListener != null) {
                onButtonClickListener.onButtonAddClick(v, this.value);
            }
        } else if (v.getId() == R.id.iv_minus) {
            numSub();
            if (onButtonClickListener != null) {
                onButtonClickListener.onButtonSubClick(v, this.value);
            }

        }
    }


    private void numAdd() {


        getValue();

        if (this.value <= maxValue)
            this.value = +this.value + 1;

        mEtxtNum.setText(value + "");
    }


    private void numSub() {


        getValue();

        if (this.value > minValue)
            this.value = this.value - 1;

        mEtxtNum.setText(value + "");
    }


    public int getValue() {

        String value = mEtxtNum.getText().toString();

        if (value != null && !"".equals(value))
            this.value = Integer.parseInt(value);

        return this.value;
    }

    public void setValue(int value) {
        mEtxtNum.setText(value + "");
        this.value = value;
    }


    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }


    public void setEditTextBackground(Drawable drawable) {

        mEtxtNum.setBackgroundDrawable(drawable);

    }


    public void setEditTextBackground(int drawableId) {

        setEditTextBackground(getResources().getDrawable(drawableId));

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setButtonAddBackgroud(Drawable backgroud) {
        this.mBtnAdd.setBackground(backgroud);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setButtonSubBackgroud(Drawable backgroud) {
        this.mBtnSub.setBackground(backgroud);
    }


    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }

    public void setOnNumberEditClickListener(OnClickListener l) {
        mEtxtNum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (l != null) {
                    l.onClick(view);
                }
            }
        });
    }

    public interface OnButtonClickListener {

        public void onButtonAddClick(View view, int value);

        public void onButtonSubClick(View view, int value);

    }


}
