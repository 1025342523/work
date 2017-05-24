package com.yifarj.yifadinghuobao.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.yifarj.yifadinghuobao.R;


/**
 * 通用菜单条目
 */
public class CustomItem extends RelativeLayout {
    private ImageView ivIcon;
    private TextView tvName;
    private ImageView ivMore;

    public CustomItem(Context context) {
        super(context);
        initContent(context);
    }

    public CustomItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomItem);
        initContent(context, typedArray);
    }

    public CustomItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomItem, defStyleAttr, 0);
        initContent(context, typedArray);
    }

    private void initContent(Context context) {
        initContent(context, null);
    }

    private void initContent(Context context, TypedArray array) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.part_custom_item, this, false);
        ivIcon = (ImageView) contentView.findViewById(R.id.ivIcon);
        tvName = (TextView) contentView.findViewById(R.id.tvName);
        ivMore = (ImageView) contentView.findViewById(R.id.ivMore);
        if(array != null) {
            for (int i = 0; i < array.getIndexCount(); i++) {
                int attr = array.getIndex(i);
                Drawable drawable;
                switch (attr) {
                    case R.styleable.CustomItem_cus_name:
                        int resId = array.getResourceId(R.styleable.CustomItem_cus_name, 0);
                        String name = array.getString(R.styleable.CustomItem_cus_name);
                        if(!isInEditMode()) {
                            if (resId > 0) {
                                tvName.setText(resId);
                            } else {
                                tvName.setText(name);
                            }
                        }else if(!StringUtils.isEmpty(name)) {
                            tvName.setText(name);
                        }else {
                            tvName.setText("名称");
                        }
                        break;
                    case R.styleable.CustomItem_cus_icon_left:
                        drawable = array.getDrawable(R.styleable.CustomItem_cus_icon_left);
                        if(drawable != null) {
                            ivIcon.setImageDrawable(drawable);
                        }
                        break;
                    case R.styleable.CustomItem_cus_icon_right:
                        drawable = array.getDrawable(R.styleable.CustomItem_cus_icon_right);
                        if(drawable != null) {
                            ivMore.setImageDrawable(drawable);
                        }
                        break;
                }
            }
        }
        addView(contentView);
    }

    public void setTitle(String title) {
       if(!StringUtils.isEmpty(title) && tvName != null) {
           tvName.setText(title);
       }
    }

    public void setTextColor(int color) {
        if(tvName != null && color!=0) {
            tvName.setTextColor(color);
        }
    }

    public void setTitle(int resId) {
        if(resId != 0 && tvName != null) {
            tvName.setText(resId);
        }
    }

    public void setIcon(int resId) {
        if(ivIcon != null) {
            ivIcon.setImageResource(resId);
        }
    }

    public void setIconRight(int resId) {
        if(ivMore != null) {
            ivMore.setImageResource(resId);
        }
    }
}
