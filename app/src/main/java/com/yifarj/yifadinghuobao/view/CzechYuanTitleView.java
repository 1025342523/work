package com.yifarj.yifadinghuobao.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yifarj.yifadinghuobao.R;

/**
 * 带搜索框的标题栏
 */
public class CzechYuanTitleView extends RelativeLayout {

    private ImageView ivRight;
    private ImageView ivLeft;
    private RelativeLayout rlSearch;
    private TextView tvRightIcon;

    public CzechYuanTitleView(Context context) {
        super(context);
        initContent(context);
    }

    public CzechYuanTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CzechYuanTitleView);
        initContent(context, typedArray);
    }

    public CzechYuanTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CzechYuanTitleView, defStyleAttr, 0);
        initContent(context, typedArray);
    }

    private void initContent(Context context) {
        initContent(context, null);
    }

    private void initContent(Context context, TypedArray array) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.part_czech_yuan_title_view, this, false);
        if (Build.VERSION.SDK_INT >= 21) {//5.0以上使用状态栏一体,需要减小TitleBar的高度
            contentView.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.size_84);
        }
        ivRight = (ImageView) contentView.findViewById(R.id.ivRight);
        ivLeft = (ImageView) contentView.findViewById(R.id.ivLeft);
        rlSearch = (RelativeLayout) contentView.findViewById(R.id.rlSearch);
        tvRightIcon = (TextView) contentView.findViewById(R.id.tvRight_icon);
        if (array != null) {
            for (int i = 0; i < array.getIndexCount(); i++) {
                int attr = array.getIndex(i);
                Drawable drawable;
                switch (attr) {
                    case R.styleable.CzechYuanTitleView_icon_left_czech_yuan:

                        drawable = array.getDrawable(R.styleable.CzechYuanTitleView_icon_left_czech_yuan);
                        if (drawable != null) {
                            ivLeft.setImageDrawable(drawable);
                        }
                        break;
                    case R.styleable.CzechYuanTitleView_icon_right_czech_yuan:
                        drawable = array.getDrawable(R.styleable.CzechYuanTitleView_icon_right_czech_yuan);
                        if (drawable != null) {
                            ivRight.setImageDrawable(drawable);
                        }
                        break;
                }
            }
        }
        addView(contentView);
    }

    public void setLeftIconClickListener(OnClickListener l) {
        if (ivLeft != null) {
            ivLeft.setOnClickListener(l);
        }
    }

    public void setRightIconClickListener(OnClickListener l) {
        if (ivRight != null) {
            ivRight.setOnClickListener(l);
        }
    }

    public void setRlSearchClickListener(OnClickListener l) {
        if (rlSearch != null) {
            rlSearch.setOnClickListener(l);
        }
    }


    public void setImageLeft(int resId) {
        if (ivLeft != null) {
            ivLeft.setImageResource(resId);
        }
    }

    public void setImageRight(int resId) {
        if (ivRight != null) {
            ivRight.setImageResource(resId);
        }
    }

    public void setRightImageVisibility(int visibility) {
        if (ivRight != null) {
            ivRight.setVisibility(visibility);
        }
    }

    public void setLeftImageVisibility(int visibility) {
        if (ivLeft != null) {
            ivLeft.setVisibility(visibility);
        }
    }


    public ImageView getImageViewContent() {
        return ivRight;
    }

    public void setRightIconText(int visibility, int title) {
        if (tvRightIcon != null && title > 0) {
            tvRightIcon.setVisibility(visibility);
            tvRightIcon.setText(String.valueOf(title));
        } else if (title == 0) {
            tvRightIcon.setVisibility(visibility);
        }
    }
}