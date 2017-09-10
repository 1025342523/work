package com.yifarj.yifadinghuobao.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yifarj.yifadinghuobao.R;


/**
 * 搜索界面
 */
public class SearchView extends LinearLayout {


    private Context mContext;
    private EditText etSearch;
    private ListView lvSearch;
    private TextView tvCancel;
    private LinearLayout llContent;
    private boolean showing;
    private ImageView ivLeft, ivSearch, ivRight;
    private OnClickListener mOnCancelListener;
    private OnClickListener mScanQrCodeListener;
    private OnSearchClickListener mOnSearchClickListener;

    public SearchView(Context context) {
        super(context);
        init(context);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchView);
        init(context, typedArray);
    }

    private void init(Context context) {
        init(context, null);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SearchView, defStyleAttr, 0);
        init(context, typedArray);
    }

    private void init(Context context, TypedArray array) {
        View content = LayoutInflater.from(context).inflate(R.layout.part_search_view_perfect, this, false);
        llContent = (LinearLayout) content.findViewById(R.id.llContent);
        etSearch = (EditText) content.findViewById(R.id.etSearch);
        lvSearch = (ListView) content.findViewById(R.id.searchContent);
        ivLeft = (ImageView) content.findViewById(R.id.ivLeft);
        ivRight = (ImageView) content.findViewById(R.id.ivRight);
        ivSearch = (ImageView) content.findViewById(R.id.ivSearch);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(context);
        if (array != null) {
            for (int i = 0; i < array.getIndexCount(); i++) {
                int attr = array.getIndex(i);
                switch (attr) {
                    case R.styleable.SearchView_text_hint:
                        int resId = array.getResourceId(R.styleable.SearchView_text_hint, 0);
                        String name = array.getString(R.styleable.SearchView_text_hint);
                        if (!isInEditMode()) {
                            if (resId > 0) {
                                etSearch.setHint(resId);
                            } else {
                                etSearch.setHint(name);
                            }
                            break;
                        }
                }
            }

            ivLeft.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnCancelListener != null) {
                        mOnCancelListener.onClick(v);
                    }
                    cancelSearchView();
                }
            });
            ivRight.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mScanQrCodeListener != null) {
                        mScanQrCodeListener.onClick(v);
                    }
                }
            });
            ivSearch.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnSearchClickListener != null) {
                        mOnSearchClickListener.onSearch(etSearch.getText().toString());
                    }
                    if (mContext instanceof Activity) {
                        hideInputMethod((Activity) mContext);
                    }
                }
            });
            etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (mOnSearchClickListener != null) {
                            mOnSearchClickListener.onSearch(etSearch.getText().toString());
                        }
                        if (mContext instanceof Activity) {
                            hideInputMethod((Activity) mContext);
                        }
                        return true;
                    }
                    return false;
                }
            });
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            addView(content, params);
        }
    }

    public EditText getEditText() {
        return etSearch;
    }

    public ListView getListView() {
        return lvSearch;
    }

    public void hideCancelView() {
        if (tvCancel != null) {
            tvCancel.setVisibility(View.GONE);
        }
    }

    public void setOnSearchClickListener(OnSearchClickListener l) {
        mOnSearchClickListener = l;
    }

    public void setOnCancelListener(final OnClickListener l) {
        mOnCancelListener = l;
    }

    public void setOnScanQrCodeListener(final OnClickListener l) {
        mScanQrCodeListener = l;
    }

    public void show() {
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                llContent.setVisibility(View.VISIBLE);
                showing = true;
                etSearch.requestFocus();
                if (mContext instanceof Activity) {
                    showKeyboard((Activity) mContext);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        llContent.startAnimation(anim);
    }

    public void hideSearchView() {
        hideInputMethod((Activity) mContext);
        llContent.setVisibility(View.INVISIBLE);
        showing = false;
    }

    public void clearText() {
        etSearch.setText("");
    }

    public void cancelSearchView() {
        lvSearch.setAdapter(null);
        etSearch.setText("");
        etSearch.clearFocus();
        hideSearchView();
    }

    public void hideInputMethod(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(
                            activity.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private void showKeyboard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(etSearch, 0);
        }
    }

    public void hide() {
        if (mContext instanceof Activity) {
            hideInputMethod((Activity) mContext);
        }
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                llContent.setVisibility(View.INVISIBLE);
                showing = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        llContent.startAnimation(anim);
    }

    public boolean isShowing() {
        return showing;
    }

    public interface OnSearchClickListener {
        void onSearch(String keyword);
    }

    public void setSearchImageOnclickListener(OnClickListener l) {
        if (ivSearch != null) {
            ivSearch.setOnClickListener(l);
        }
    }
}
