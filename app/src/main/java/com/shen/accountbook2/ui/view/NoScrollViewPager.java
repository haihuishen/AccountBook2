package com.shen.accountbook2.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 不允许滑动的viewpager
 * 
 * 
 */
public class NoScrollViewPager extends ViewPager {

	public NoScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NoScrollViewPager(Context context) {
		super(context);
	}

	/**
	 *  事件拦截
	 *  
	 *  如viewpage1 嵌套一个  viewpage2, viewpage2想要拿到事件!!!（不拦截子控件的事件）
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		return false;// 不拦截子控件的事件
	}

	
	/**
	 *  重写此方法, 触摸时什么都不做, 从而实现对滑动事件的禁用
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// 重写此方法, 触摸时什么都不做, 从而实现对滑动事件的禁用
		return true;
	}

}
