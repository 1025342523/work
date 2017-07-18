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

import com.blankj.utilcode.util.StringUtils;
import com.yifarj.yifadinghuobao.R;

/**
 * 带搜索框的标题栏
 */
public class CzechYuanTitleView extends RelativeLayout {

    private TextView tvTitle;
    private ImageView ivRight;
    private ImageView ivRightLeft;
    private ImageView ivLeft;
    private TextView tvRight;

    public CzechYuanTitleView(Context context) {
        super(context);
        initContent(context);
    }

    public CzechYuanTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleView);
        initContent(context, typedArray);
    }

    public CzechYuanTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TitleView, defStyleAttr, 0);
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
        tvTitle = (TextView) contentView.findViewById(R.id.tvTitle);
        ivRight = (ImageView) contentView.findViewById(R.id.ivRight);
        ivRightLeft = (ImageView) contentView.findViewById(R.id.ivRightLeft);
        ivLeft = (ImageView) contentView.findViewById(R.id.ivLeft);
        tvRight = (TextView) contentView.findViewById(R.id.tvRight);
        if (array != null) {
            for (int i = 0; i < array.getIndexCount(); i++) {
                int attr = array.getIndex(i);
                Drawable drawable;
                int resId;
                String name;
                switch (attr) {
                    case R.styleable.TitleView_title_name:
                        resId = array.getResourceId(R.styleable.TitleView_title_name, 0);
                        name = array.getString(R.styleable.TitleView_title_name);
                        if (!isInEditMode()) {
                            if (resId > 0) {
                                tvTitle.setText(resId);
                            } else {
                                tvTitle.setText(name);
                            }
                        } else if (!StringUtils.isEmpty(name)) {
                            tvTitle.setText(name);
                        }
                        break;
                    case R.styleable.TitleView_text_right:
                        resId = array.getResourceId(R.styleable.TitleView_text_right, 0);
                        name = array.getString(R.styleable.TitleView_text_right);
                        if (!isInEditMode()) {
                            if (resId > 0) {
                                tvRight.setText(resId);
                            } else {
                                tvRight.setText(name);
                            }
                        } else if (!StringUtils.isEmpty(name)) {
                            tvRight.setText(name);
                        }
                        break;
                    case R.styleable.TitleView_icon_left:

                        drawable = array.getDrawable(R.styleable.TitleView_icon_left);
                        if (drawable != null) {
                            ivLeft.setImageDrawable(drawable);
                        }
                        break;
                    case R.styleable.TitleView_icon_right:
                        drawable = array.getDrawable(R.styleable.TitleView_icon_right);
                        if (drawable != null) {
                            ivRight.setImageDrawable(drawable);
                        }
                        break;
                    case R.styleable.TitleView_icon_right_left:
                        drawable = array.getDrawable(R.styleable.TitleView_icon_right_left);
                        if (drawable != null) {
                            ivRightLeft.setImageDrawable(drawable);
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

    public void setRightLeftIconClickListener(OnClickListener l) {
        if (ivRightLeft != null) {
            ivRightLeft.setOnClickListener(l);
        }
    }

    public void setRightTextClickListener(OnClickListener l) {
        if (tvRight != null) {
            tvRight.setVisibility(View.VISIBLE);
            tvRight.setOnClickListener(l);
        }
    }

    public void setTitle(String title) {
        if (tvTitle != null && title != null) {
            tvTitle.setText(title);
        }
    }

    public void setTitle(int resId) {
        if (resId != 0 && tvTitle != null) {
            tvTitle.setText(resId);
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

    public void setImageRightLeft(int resId) {
        if (ivRightLeft != null) {
            ivRightLeft.setImageResource(resId);
        }
    }

    public void setRightTextVisibility(int visibility) {
        if (tvRight != null) {
            tvRight.setVisibility(visibility);
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

    public void setTvRightEnabled(boolean enabled) {
        if (tvRight != null) {
            tvRight.setEnabled(enabled);
        }
    }


    public void setTvRightTextColor(int color) {
        if (tvRight != null) {
            tvRight.setTextColor(color);
        }
    }

    public ImageView getImageViewContent() {
        return ivRight;
    }
}
