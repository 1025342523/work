package com.yifarj.yifadinghuobao.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yifarj.yifadinghuobao.R;

/**
* ViewPagerIndicator
* @auther  Czech.Yuan
* @date 2017/5/23 9:08
*/
public class ViewPagerIndicator extends LinearLayout {
	private Context mContext;
	private int mCount;
	private int mCurrentItem;
    private OnItemClickedListener itemClickedListener;

    public ViewPagerIndicator(Context context) {
		super(context);
		mContext = context;
		setOrientation(HORIZONTAL);
	}
	
	public ViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		setOrientation(HORIZONTAL);
	}
	
	public void setCount(int count) {
		if(count < 0)
			throw new IllegalArgumentException("The count of tips must grater than 0.");
		mCount = count;
		initUI();
	}
	
	public int getCount() {
		return mCount;
	}
	
	public void setCurrentItem(int item) {
		if(item < 0) {
			item += mCount;
		}
		item = item % mCount;
		int childCount = getChildCount();
		if(childCount != mCount) {
			return;
		}
		
		ImageView child = (ImageView) getChildAt(mCurrentItem);
		child.setSelected(false);
		child = (ImageView) getChildAt(item);
		child.setSelected(true);
		mCurrentItem = item;
	}

    public void setOnItemClickedListener(OnItemClickedListener l) {
        this.itemClickedListener = l;
    }

    public interface OnItemClickedListener {
        void onItemClicked(int position);
    }
	
	public int getCurrentItem() {
		return mCurrentItem;
	}
	
	private void initUI() {
		removeAllViews();
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.height = lp.width = getResources().getDimensionPixelSize(R.dimen.size_24);
		for(int i=0; i<mCount; i++) {
            final int position = i;
			ImageView tip = new ImageView(mContext);
            int p = getResources().getDimensionPixelSize(R.dimen.size_4);
            tip.setPadding(p,p,p,p);
            tip.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemClickedListener != null) {
                        itemClickedListener.onItemClicked(position);
                    }
                }
            });
			tip.setImageResource(R.drawable.selector_pager_dot);
			addView(tip, lp);
		}
	}

}
