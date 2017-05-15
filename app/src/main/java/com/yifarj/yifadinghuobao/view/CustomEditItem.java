package com.yifarj.yifadinghuobao.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.yifarj.yifadinghuobao.R;

/**
 * 自定义编辑条
 */
public class CustomEditItem extends RelativeLayout {
    private EditText etContent;
    private TextView tvName;
    private ImageView ivMoreInfoIcon;
    private ImageView ivMoreArrow;
    private View contentView;
    private View coverView;
    private boolean mEditable = true;
    private boolean mMore;

    public CustomEditItem(Context context) {
        super(context);
        initContent(context);
    }

    public CustomEditItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomEditItem);
        initContent(context, typedArray);
    }

    public CustomEditItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomEditItem, defStyleAttr, 0);
        initContent(context, typedArray);
    }

    private void initContent(Context context) {
        initContent(context, null);
    }

    private void initContent(Context context, TypedArray array) {
        contentView = LayoutInflater.from(context).inflate(R.layout.part_custom_edit_item, this, false);
        etContent = (EditText) contentView.findViewById(R.id.etContent);
        etContent.setHintTextColor(getResources().getColor(R.color.text_desc));
        etContent.setInputType(InputType.TYPE_CLASS_TEXT);
        etContent.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        tvName = (TextView) contentView.findViewById(R.id.tvName);
        ivMoreArrow = (ImageView) contentView.findViewById(R.id.ivMore);
        ivMoreInfoIcon = (ImageView) contentView.findViewById(R.id.ivMoreInfoIcon);
        coverView = contentView.findViewById(R.id.coverView);
        if (array != null) {
            for (int i = 0; i < array.getIndexCount(); i++) {
                int attr = array.getIndex(i);
                switch (attr) {
                    case R.styleable.CustomEditItem_name:
                        int resId = array.getResourceId(R.styleable.CustomEditItem_name, 0);
                        String name = array.getString(R.styleable.CustomEditItem_name);
                        if (!isInEditMode()) {
                            if (resId > 0) {
                                tvName.setText(resId);
                            } else {
                                tvName.setText(name);
                            }
                        } else if (!StringUtils.isEmpty(name)) {
                            tvName.setText(name);
                        } else {
                            tvName.setText("名称");
                        }
                        break;
                    case R.styleable.CustomEditItem_default_content:
                        int contentId = array.getResourceId(R.styleable.CustomEditItem_default_content, 0);
                        String content = array.getString(R.styleable.CustomEditItem_default_content);
                        if (!isInEditMode()) {
                            if (contentId > 0) {
                                etContent.setText(contentId);
                            } else {
                                etContent.setText(content);
                            }
                        } else if (!StringUtils.isEmpty(content)) {
                            etContent.setText(content);
                        }
                        break;
                    case R.styleable.CustomEditItem_editable:
                        boolean editable = array.getBoolean(R.styleable.CustomEditItem_editable, true);
                        setEditable(editable);
                        break;
                    case R.styleable.CustomEditItem_moreMode:
                        boolean moreMode = array.getBoolean(R.styleable.CustomEditItem_moreMode, false);
                        setMoreMode(moreMode);
                        break;
                    case R.styleable.CustomEditItem_hint:
                        int hintId = array.getResourceId(R.styleable.CustomEditItem_hint, 0);
                        String hint = array.getString(R.styleable.CustomEditItem_hint);
                        if (!isInEditMode()) {
                            if (hintId > 0) {
                                etContent.setHint(hintId);
                            } else {
                                etContent.setHint(hint);
                            }
                        }
                        break;
                }
            }
        }
        addView(contentView);
    }

    public void setMoreMode(boolean more) {
        mMore = more;
        if (ivMoreArrow != null && ivMoreInfoIcon != null) {
            processMoreArrowIcon(more);
            setEditable(!more && mEditable);
        }
    }

    private void processMoreArrowIcon(boolean visible) {
        ivMoreArrow.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setEditable(boolean editable) {
        mEditable = editable;
        etContent.setEnabled(editable);
        processMoreInfoIcon(editable);
        processMoreArrowIcon(!editable && mMore);
    }

    private void processMoreInfoIcon(boolean visible) {
        ivMoreInfoIcon.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setOnItemClickListener(OnClickListener l) {
        if (coverView != null) {
            coverView.setOnClickListener(l);
        }
    }

    public void setItemName(String name) {
        if (name != null && tvName != null) {
            tvName.setText(name);
        }
    }

    public void setItemName(int resId) {
        if (resId != 0 && tvName != null) {
            tvName.setText(resId);
        }
    }

    public void setMoreInfoIconVisibility(boolean visible) {
        if (ivMoreInfoIcon != null) {
            ivMoreInfoIcon.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public TextView getItemNameTextView() {
        return tvName;
    }

    public EditText getEditText() {
        return etContent;
    }

    public void setOnEditTextActionListener(TextView.OnEditorActionListener l) {
        if (etContent != null) {
            etContent.setOnEditorActionListener(l);
        }
    }

}
