package com.yifarj.yifadinghuobao.utils;

import android.content.Context;

/**
 * px to dip/sp or dip/sp to px. get screen height/width, show/hide system UI
 */
public class ScreenUtil {
	private static Context context;

	/**
	 * Initializes context
	 * You must call this method first before you call others methods.
	 *
	 * @param ctx
	 */
	public static void init(Context ctx) {
		context = ctx;
	}

	/**
	 * convert dip value to px value.
	 * 
	 * @param dpValue
	 *            dip value.
	 * @return px value.
	 */
	public static int dip2px(float dpValue) {
		final float scale = context.getResources()
				.getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * convert px value to dip value.
	 * 
	 * @param pxValue
	 *            px value.
	 * @return dip value.
	 */
	public static int px2dip(float pxValue) {
		final float scale = context.getResources()
				.getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * convert sp value to px value.
	 * 
	 * @param spValue
	 *            sp value.
	 * @return px value.
	 */
	public static int sp2px(float spValue) {
		final float scale = context.getResources()
				.getDisplayMetrics().scaledDensity;
		return (int) (spValue * scale + 0.5f);
	}

	/**
	 * convert px value to sp value.
	 * 
	 * @param pxValue
	 *            px value.
	 * @return sp value.
	 */
	public static int px2sp(float pxValue) {
		final float scale = context.getResources()
				.getDisplayMetrics().scaledDensity;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * get screen width pixels
	 * 
	 * @return
	 */

	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

}