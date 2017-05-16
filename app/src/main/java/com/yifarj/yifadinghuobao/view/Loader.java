package com.yifarj.yifadinghuobao.view;

public interface Loader {
	
	/**
     * Fired when a request started
     *
     */
	public void loadStart();
	
	/**
	 * Fired when a request from the server: 
	 * <li>failured
	 * <li>returns successfully but the response code isn't 0
	 */
	public void loadFailure();
	
	/**
	 * Fired when a request from the server returns successfully and the response code is '0'
	 */
	public void loadSuccess();

	/**
	 * Fired when a request from the server finished
	 */
	public void loadFinished();
}
