package com.yifarj.yifadinghuobao.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
* AutoScrollViewPager
* @auther  Czech.Yuan
* @date 2017/5/23 9:08
*/
public class AutoScrollViewPager extends ViewPager {
    /**默认延时*/
	public static final int DEFAULT_INTERVAL = 6000;
    /**用户操作后的延时*/
	public static final int USER_INTERVAL = 10000;

	public static final int SCROLL_WHAT = 0;
	private long interval = DEFAULT_INTERVAL;
	private boolean stopScrollWhenTouch = true;
	private Handler handler;

	public AutoScrollViewPager(Context paramContext) {
		super(paramContext);
		init();
	}

	public AutoScrollViewPager(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		setMyScroller();
		init();
	}

	private void init() {
		handler = new InternalHandler(this);
	}

	private static class InternalHandler extends Handler {
		public WeakReference<AutoScrollViewPager> wPager;
		
		public InternalHandler(AutoScrollViewPager view) {
			wPager = new WeakReference<AutoScrollViewPager>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			AutoScrollViewPager pager = wPager.get();
			if(pager != null) {
				switch (msg.what) {
					case SCROLL_WHAT:
						pager.scrollOnce();
                        pager.interval = DEFAULT_INTERVAL;
						pager.sendScrollMessage(pager.interval);
					default:
						break;
				}
			}
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (stopScrollWhenTouch) {
			if (ev.getAction() == MotionEvent.ACTION_DOWN) {
				stopAutoScroll();
			} else if (ev.getAction() == MotionEvent.ACTION_UP ) {
                interval = USER_INTERVAL;//用户操作后延时调整
				startAutoScroll();
			}
		}
		
		if(ev.getAction() == MotionEvent.ACTION_MOVE){
			stopAutoScroll();
		}
		
		getParent().requestDisallowInterceptTouchEvent(true);
		return super.onTouchEvent(ev);

	}

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
        resetAutoScroll();
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
        resetAutoScroll();
    }

    public void stopAutoScroll() {
		handler.removeMessages(SCROLL_WHAT);
	}

	public void startAutoScroll() {
		sendScrollMessage(interval);
	}

	public void resetAutoScroll() {
		startAutoScroll();
	}

	private void sendScrollMessage(long delayTimeInMills) {
		/** remove messages before, keeps one message is running at most **/
		handler.removeMessages(SCROLL_WHAT);
		handler.sendEmptyMessageDelayed(SCROLL_WHAT, delayTimeInMills);
	}

	public void scrollOnce() {
		int currentItem = getCurrentItem();
		int nextItem = 0;
		if(getAdapter() != null && getAdapter().getCount() > 0) {
			nextItem = (currentItem + 1) % getAdapter().getCount();
		}
		setCurrentItem(nextItem, true);
	}

	public void enableAutoScroll(boolean auto) {
		stopScrollWhenTouch = auto;
	}
	
	// ===========解决 滑动动画的问题 =======
	private void setMyScroller() {
		try {
			Class<?> viewpager = ViewPager.class;
			Field scroller = viewpager.getDeclaredField("mScroller");
			scroller.setAccessible(true);
			scroller.set(this, new MyScroller(getContext()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class MyScroller extends Scroller {
		public MyScroller(Context context) {
			super(context, new DecelerateInterpolator());
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy, int duration) {
			super.startScroll(startX, startY, dx, dy, 800 );
		}
	}

}
