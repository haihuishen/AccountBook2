/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2015 cpoopc
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package com.shen.accountbook2.clander.view;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ScrollView;

/**
 * 自定义"滚动帮助类"
 */
public class ScrollableHelper {

    private ScrollableContainer mCurrentScrollableCainer;

    private View mCurrentView;

    /**
     * a viewgroup whitch contains ScrollView/ListView/RecycelerView..
     */
    public interface ScrollableContainer{
        /**
         * @return ScrollView/ListView/RecycelerView..'s instance
         */
        View getScrollableView();
    }

    /**
     * 当需要 用fragment 时，用这个方法
     * @param scrollableContainer
     */
    public void setCurrentScrollableContainer(ScrollableContainer scrollableContainer) {
        this.mCurrentScrollableCainer = scrollableContainer;
    }


    /**
     * 直接设置一个 View 进来
     *
     * Current Container：目前的容器
     * @param scrollableContainer   可滚动的容器(布局)(比如：listview)
     */
    public void setCurrentContainer(View scrollableContainer) {
        this.mCurrentView = scrollableContainer;
    }

    /** 得到可滚动的控件：在之前已经设置进来了(比如：listview)*/
    private View getScrollableView() {
        /*if (mCurrentScrollableCainer == null) {
            return null;
        }
        return mCurrentScrollableCainer.getScrollableView();*/
        return mCurrentView;
    }

    /**
     *
     * 判断是否滑动到顶部方法,ScrollAbleLayout根据此方法来做一些逻辑判断
     * 目前只实现了AdapterView,ScrollView,RecyclerView
     * 需要支持其他view可以自行补充实现
     * @return
     */
    public boolean isTop() {
        View scrollableView = getScrollableView();
        if (scrollableView == null) {
//            throw new NullPointerException("You should call ScrollableHelper.setCurrentScrollableContainer() to set ScrollableContainer.");
            return true;
        }
        if (scrollableView instanceof AdapterView) {                        //   根据父类的方法来判断是否(到达了顶部)
            return isAdapterViewTop((AdapterView) scrollableView);
        }
        if (scrollableView instanceof ScrollView) {
            return isScrollViewTop((ScrollView) scrollableView);
        }
        if (scrollableView instanceof RecyclerView) {
            return isRecyclerViewTop((RecyclerView) scrollableView);
        }
        if (scrollableView instanceof WebView) {
            return isWebViewTop((WebView) scrollableView);
        }
        // 都不是，就抛出这个 错误： scrollableView 必须继承于这些父类其中的一个
        throw new IllegalStateException("scrollableView must be a instance of AdapterView|ScrollView|RecyclerView");
    }

//    　　Android L SDK发布的，新API中最有意思的就是RecyclerView （后面为RV） 和 CardView了，
// 按照官方的说法， RV 是一个ListView 的一个更高级更灵活的一个版本， 可以自定义的东西太多了。
// 以前会不会觉得写一个Horizontal ListView 都觉得挺吃力的，
// 但是如果你看过RV的话，你就会觉得这也太简单了吧。 废话不多，下面转入正题。
//        今天这里主要讲述的是 RV 简单用法:
//        1.跟ListView 一样 需要一个 Adapter
//    　　2.跟ListView 一样 需要一个 ViewHolder
//    　　3.有点不同了, 需要一个LayoutManager
    private static boolean isRecyclerViewTop(RecyclerView recyclerView) {
        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                // 第一个"可见"子控件 在所有的 子控件的索引
                int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                View childAt = recyclerView.getChildAt(0);   // 第一个子控件
                // 如果没有"子控件"  或者  第一个"可见"子控件==第一个子控件  第一个子控件和顶部距离为0
                if (childAt == null || (firstVisibleItemPosition == 0 && childAt.getTop() == 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isAdapterViewTop(AdapterView adapterView){
        if(adapterView != null){
            int firstVisiblePosition = adapterView.getFirstVisiblePosition();
            View childAt = adapterView.getChildAt(0);
            if(childAt == null || (firstVisiblePosition == 0 && childAt.getTop() == 0)){
                return true;
            }
        }
        return false;
    }

    private static boolean isScrollViewTop(ScrollView scrollView){
        if(scrollView != null) {
            int scrollViewY = scrollView.getScrollY();
            return scrollViewY <= 0;
        }
        return false;
    }

    private static boolean isWebViewTop(WebView scrollView){
        if(scrollView != null) {
            int scrollViewY = scrollView.getScrollY();
            return scrollViewY <= 0;
        }
        return false;
    }


//    @SuppressLint标注忽略指定的警告
//    这个是android带的lint工具提示的，
// lint官方的说法是 Improving Your Code with lint，
// 应该是帮助提升代码的 ，如果不想用的话，
// 可以右键点工程，然后在android tools 中，选择 clear lint marker 就没有这个错误了

    /**
     * 根据，设进来(可滚动控件的父类), 设置其的 滑动情况(在duration内 滑动distance距离)
     *
     * @param velocityY
     * @param distance
     * @param duration
     */
    @SuppressLint("NewApi")
    public void smoothScrollBy(int velocityY, int distance, int duration) {
        View scrollableView = getScrollableView();              // 得到可滚动的控件：在之前已经设置进来了

        if (scrollableView instanceof AbsListView) {
            AbsListView absListView = (AbsListView) scrollableView;
            if (Build.VERSION.SDK_INT >= 21) {
                //absListView.fling(velocityY);
            } else {
                absListView.smoothScrollBy(distance, duration); // 平滑滚动的像素距离,在时间毫秒内。(在duration内 滑动distance距离)
            }
        } else if (scrollableView instanceof ScrollView) {
            ((ScrollView) scrollableView).fling(velocityY);     //
        } else if (scrollableView instanceof RecyclerView) {
            ((RecyclerView) scrollableView).fling(0, velocityY);
        } else if (scrollableView instanceof WebView) {
            ((WebView) scrollableView).flingScroll(0, velocityY);
        }
    }

}
