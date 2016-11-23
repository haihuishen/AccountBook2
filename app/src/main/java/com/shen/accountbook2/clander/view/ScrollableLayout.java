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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.shen.accountbook2.clander.Constants;

/**
 * Created by cpoopc(303727604@qq.com) on 2015-02-10.
 */
public class ScrollableLayout extends LinearLayout {

    private final String tag = "cp:scrollableLayout";
    private float mDownX;
    private float mDownY;
    private float mLastY;

    private int minY = 0;
    private int maxY = 0;
    private int mHeadHeight;
    private int mExpandHeight;
    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    // 方向
    private DIRECTION mDirection;
    private int mCurY;
    private int mLastScrollerY;
    private boolean needCheckUpdown;
    private boolean updown;
    /** true : 父不能再调用 onInterceptTouchEvent  也无法截获以后的action。*/
    private boolean mDisallowIntercept;
    private boolean isClickHead;
    /**  true:点击的位置是在"第一个控件"的"内部"<br>
     *  false:点击的位置是在"第一个控件"的"外部"<br>*/
    private boolean isClickHeadExpand;

    private View mHeadView;
    private ViewPager childViewPager;

    private Scroller mScroller;                 // 滑动类
    private VelocityTracker mVelocityTracker;

    /**
     * 滑动方向 <p>
     * enum DIRECTION {<br>
     * UP,// 向上划 ==>0<br>
     * DOWN// 向下划 ==>1<br>
     * }
     */
    enum DIRECTION {
        UP,// 向上划
        DOWN// 向下划
    }

    /** 自定义的"接口"*/
    public interface OnScrollListener {
        /**
         * 自定义接口中的 方法(子类实现)
         * @param currentY
         * @param maxY
         */
        void onScroll(int currentY, int maxY);
    }

    private OnScrollListener onScrollListener;

    /**
     *  将 拿到"传进来"的"自定义的接口"
     * @param onScrollListener
     */
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    /** 自定义的"滚动帮助类"*/
    private com.shen.accountbook2.clander.view.ScrollableHelper mHelper;

    /** 拿到"全局变量"中的 —— 自定义的"滚动帮助类"*/
    public com.shen.accountbook2.clander.view.ScrollableHelper getHelper() {
        return mHelper;
    }

    public ScrollableLayout(Context context) {
        super(context);
        init(context);
    }

    public ScrollableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    //指使用该注解的方法适用于  系统版本  为3.0及以上系统的手机
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ScrollableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /*@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScrollableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }*/

    private void init(Context context) {
        mHelper = new ScrollableHelper();
        mScroller = new Scroller(context);
        final ViewConfiguration configuration = ViewConfiguration.get(context); // 控件配置   ;Configuration 配置;外形
        mTouchSlop = configuration.getScaledTouchSlop();                        // 获取控件的缩放比例
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity(); // 比例 最低 扔(急冲) 速度
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity(); // 比例 最高 扔(急冲) 速度
    }

    /** 是否超过顶部<p>   sticked==>刺破(超过边界)  当前的Y值 和 最大的Y值 比较*/
    public boolean isSticked() {
        return mCurY == maxY;
    }

    /**
     * 扩大头部点击滑动范围
     * expand ==> 扩大
     * @param expandHeight
     */
    public void setClickHeadExpand(int expandHeight) {
        mExpandHeight = expandHeight;
    }

    public int getMaxY() {
        return maxY;
    }

    /** 判断是否是 控件头部==> 当前的Y值 和 最小的Y值 比较*/
    public boolean isHeadTop() {
        return mCurY == minY;
    }

    public boolean canPtr() {
        return updown && mCurY == minY && mHelper.isTop();
    }


    /**
     * 对于底层的View来说，有一种方法可以阻止父层的View截获touch事件，
     * 就是调用getParent().requestDisallowInterceptTouchEvent(true);方法。
     * 一旦底层View收到touch的action后调用这个方法那么父层View就不会再调用onInterceptTouchEvent了，
     * 也无法截获以后的action。
     *
     * true : 父不能再调用 onInterceptTouchEvent  也无法截获以后的action。
     */
    public void requestScrollableLayoutDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
        mDisallowIntercept = disallowIntercept;
    }

    // 当"控件"被点击时调用!
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {  // dispatch 调度
        float currentX = ev.getX();
        float currentY = ev.getY();
        float deltaY;
        int shiftX = (int) Math.abs(currentX - mDownX);
        int shiftY = (int) Math.abs(currentY - mDownY);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:                       // 按下去的时候
                mDisallowIntercept = false;
                needCheckUpdown = true;
                updown = true;
                mDownX = currentX;
                mDownY = currentY;
                mLastY = currentY;
                // getScrollY() 得到当前的 y轴的偏移量(没改变之前的)
                checkIsClickHead((int) currentY, mHeadHeight, getScrollY());        // mHeadView = getChildAt(0);--> mHeadHeight = mHeadView.getMeasuredHeight();
                checkIsClickHeadExpand((int) currentY, mHeadHeight, getScrollY());

                initOrResetVelocityTracker();
                mVelocityTracker.addMovement(ev); //将事件加入到VelocityTracker类实例中
                mScroller.forceFinished(true);
                break;
            case MotionEvent.ACTION_MOVE:                   // 拖动的时候
                if (mDisallowIntercept) {
                    break;
                }
                initVelocityTrackerIfNotExists();
                mVelocityTracker.addMovement(ev);   //将事件加入到VelocityTracker类实例中
                deltaY = mLastY - currentY;
                if (needCheckUpdown) {
                    if (shiftX > mTouchSlop && shiftX > shiftY) {
                        needCheckUpdown = false;
                        updown = false;
                    } else if (shiftY > mTouchSlop && shiftY > shiftX) {
                        needCheckUpdown = false;
                        updown = true;
                    }
                }

                if (updown && shiftY > mTouchSlop && shiftY > shiftX &&
                        (!isSticked() || mHelper.isTop() || isClickHeadExpand)) {

                    if (childViewPager != null) {
                        childViewPager.requestDisallowInterceptTouchEvent(true);
                    }
                    scrollBy(0, (int) (deltaY + 0.5));
                }
                mLastY = currentY;
                break;
            case MotionEvent.ACTION_UP:
                if (updown && shiftY > shiftX && shiftY > mTouchSlop) {
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    float yVelocity = -mVelocityTracker.getYVelocity();
                    boolean dislowChild = false;
                    if (Math.abs(yVelocity) > mMinimumVelocity) {
                        mDirection = yVelocity > 0 ? DIRECTION.UP : DIRECTION.DOWN;
                        if ((mDirection == DIRECTION.UP && isSticked()) || (!isSticked() && getScrollY() == 0 && mDirection == DIRECTION.DOWN)) {
                            dislowChild = true;
                        } else {
                            mScroller.fling(0, getScrollY(), 0, (int) yVelocity, 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                            mScroller.computeScrollOffset();
                            mLastScrollerY = getScrollY();
                            invalidate();
                        }
                    }
                    if (!dislowChild && (isClickHead || !isSticked())) {
                        int action = ev.getAction();
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        boolean dispathResult = super.dispatchTouchEvent(ev);
                        ev.setAction(action);
                        return dispathResult;
                    }
                }
                break;
            default:
                break;
        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    /**
     * 兼容版本之类
     * @param distance
     * @param duration
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private int getScrollerVelocity(int distance, int duration) {
        if (mScroller == null) {
            return 0;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return (int) mScroller.getCurrVelocity();
        } else {
            return distance / duration;
        }
    }

    //会在draw()过程调用该方法
    // 由父视图调用用来请求子视图根据偏移值 mScrollX,mScrollY重新绘制 
    // 内容自定义
    @Override
    public void computeScroll() {

        // 在滑动中，拿到当前的位置，从而更改控件位置
        if (mScroller.computeScrollOffset()) {   // 调用这个当你想知道新位置。如果动画还没有完成,它返回true。

            final int currY = mScroller.getCurrY();

            if (mDirection == DIRECTION.UP) {
                // 手势向上划
                if (isSticked()) {                  // 是否超过顶部
                    // distance:距离
                    int distance = mScroller.getFinalY() - currY;     // getFinalY():返回滚动将结束的地方。有效的只是“扔”卷轴。
                    // duration:持续时间
                    // getDuration():返回滚动事件需要多长时间,以毫秒为单位。
                    // timePassed():返回时间以来的滚动。
                    int duration = calcDuration(mScroller.getDuration(), mScroller.timePassed());
                    mHelper.smoothScrollBy(getScrollerVelocity(distance, duration), distance, duration);
                    mScroller.forceFinished(true);  //将完成的字段强制为特定的值。
                    return;
                } else {
                    scrollTo(0, currY);
                }
            } else {
                // 手势向下划
                if (mHelper.isTop() || isClickHeadExpand) {
                    int deltaY = (currY - mLastScrollerY);
                    int toY = getScrollY() + deltaY;
                    scrollTo(0, toY);
                    if (mCurY <= minY) {
                        mScroller.forceFinished(true);
                        return;
                    }
                }
                invalidate();       //invalidate()是用来刷新View的，必须是在UI线程中进行工作。
                                    // 比如在修改某个view的显示时，调用invalidate()才能看到重新绘制的界面。
                                    // invalidate()的调用是把之前的旧的view从主UI线程队列中pop掉。
            }
            mLastScrollerY = currY;
        }
    }
//    scrollBy（int x,int y）：
//    从源码中看出，它实际上是调用了scrollTo(mScrollX + x, mScrollY + y);
//    mScrollX + x和mScrollY + y，即表示在原先偏移的基础上在发生偏移，通俗的说就是相对我们当前位置偏移。
    //根据父类VIEW里面移动，如果移动到了超出的地方，就不会显示。
    @Override
    public void scrollBy(int x, int y) {
        int scrollY = getScrollY();
        int toY = scrollY + y;
        if (toY >= maxY) {
            toY = maxY;
        } else if (toY <= minY) {
            toY = minY;
        }
        y = toY - scrollY;
        super.scrollBy(x, y);
    }

//    scrollTo（int x,int y）：
//    如果偏移位置发生了改变，就会给mScrollX和mScrollY赋新值，改变当前位置。
//    注意：x,y代表的不是坐标点，而是偏移量。
//    例如：
//    我要移动view到坐标点（100，100），那么我的偏移量就是(0,，0)  - （100，100） = （-100 ，-100）  ，我就要执行view.scrollTo(-100,-100),达到这个效果。
    //根据父类VIEW里面移动，如果移动到了超出的地方，就不会显示。

    /**
     * 这里，判断修正Y不超过边界，执行子类实现的onScrollListener接口的onScroll(y, maxY);方法<br>
     * 并且还执行---super.scrollTo(x, y);
     * @param x
     * @param y
     */
    @Override
    public void scrollTo(int x, int y) {
        if (y >= maxY) {
            y = maxY;
        } else if (y <= minY) {
            y = minY;
        }
        mCurY = y;
        if (onScrollListener != null) {
            onScrollListener.onScroll(y, maxY);
        }
        super.scrollTo(x, y);
    }

    /**
     * 获得 VelocityTracker 的实例,(没有就获得，有就清空)<p>
     *
     *     当你需要跟踪触摸屏事件的速度的时候,使用obtain()方法来获得VelocityTracker类的一个实例对象<br>
     **/
    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain(); //当你需要跟踪触摸屏事件的速度的时候,使用obtain()方法来获得VelocityTracker类的一个实例对象
        } else {
            mVelocityTracker.clear();
        }
    }

    /**
     * 获得 VelocityTracker 的实例,(没有就获得，有不出来)<p>
     *
     *     当你需要跟踪触摸屏事件的速度的时候,使用obtain()方法来获得VelocityTracker类的一个实例对象<br>
     **/
    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();//当你需要跟踪触摸屏事件的速度的时候,使用obtain()方法来获得VelocityTracker类的一个实例对象
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }


    /**
     *  检查：是否点击了"头部"<p>
     *
     *  isClickHead = downY + scrollY <= headHeight<br>
     *  true:点击的位置是在"第一个控件"的"内部"<br>
     *  false:点击的位置是在"第一个控件"的"外部"<br>
     *
     * @param downY                  是"当前屏幕的点击"y坐标
     * @param headHeight            第一个子控件测量的高： mHeadView = getChildAt(0);--> mHeadHeight = mHeadView.getMeasuredHeight();
     * @param scrollY               未移动前的 Y轴偏移量：当 拉倒最大时(scrollY == 0 说明，这个才是原始状态);缩小时(scrollY变大)
     */
    private void checkIsClickHead(int downY, int headHeight, int scrollY) {
        isClickHead = downY + scrollY <= headHeight;
//        System.out.println("downY:"+downY);
//        System.out.println("scrollY:"+scrollY);
//        System.out.println("headHeight:"+headHeight);
//        System.out.println("isClickHead:"+isClickHead);
    }

    /**
     * 检查：是否点击了"头部"(如果mExpandHeight没有赋值则和"checkIsClickHead")<p>
     *
     *  isClickHeadExpand = downY + scrollY <=  headHeight + mExpandHeight<br>
     *  true:点击的位置是在"第一个控件"的"内部"<br>
     *  false:点击的位置是在"第一个控件"的"外部"<br>
     *
     * @param downY             是"当前屏幕的点击"y坐标
     * @param headHeight        测量"第一个控件的高度"
     * @param scrollY           当 拉倒最大时(scrollY == 0 说明，这个才是原始状态);缩小时(scrollY变大)
     */
    private void checkIsClickHeadExpand(int downY, int headHeight, int scrollY) {
        if (mExpandHeight <= 0) {
            isClickHeadExpand = false;
//            System.out.println(" if (mExpandHeight <= 0): isClickHeadExpand = false;");
        }
        isClickHeadExpand = downY + scrollY <= headHeight + mExpandHeight;
//        System.out.println("downY:"+downY);            // 是"当前屏幕的点击"y坐标
//        System.out.println("scrollY:"+scrollY);        // 当 拉倒最大时(scrollY == 0 说明，这个才是原始状态);缩小时(scrollY变大)
//        System.out.println("headHeight:"+headHeight);   // 测量"第一个控件的高度"
//        System.out.println("isClickHeadExpand:"+isClickHeadExpand);
    }

    /** calcDuration :calc持续时间    duration:持续时间   timepass:休闲(空闲？) -----return duration - timepass*/
    private int calcDuration(int duration, int timepass) {
        return duration - timepass;
    }


//    MeasureSpec参数的值为int型，分为高32位和低16为，高32位保存的是specMode，
// 低16位表示specSize，specMode分三种：
//      1、MeasureSpec.UNSPECIFIED,父视图不对子视图施加任何限制，子视图可以得到任意想要的大小；
//      2、MeasureSpec.EXACTLY，父视图希望子视图的大小是specSize中指定的大小；
//      3、MeasureSpec.AT_MOST，子视图的大小最多是specSize中的大小。
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mHeadView = getChildAt(0);      // 得到第一个子控件 (这里是;头控件(把手？))
        //某一个子view，多宽，多高, 内部加上了viewGroup的padding值、margin值和传入的宽高wUsed、hUsed
        measureChildWithMargins(mHeadView, widthMeasureSpec, 0, MeasureSpec.UNSPECIFIED, 0);
        // TODO 此处修改 最后显示日历的高度
        maxY = mHeadView.getMeasuredHeight() - Constants.dip2px(getContext(), 40f);
        mHeadHeight = mHeadView.getMeasuredHeight();
        super.onMeasure(widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) + maxY,//      2、MeasureSpec.EXACTLY，父视图希望子视图的大小是specSize中指定的大小；
                        MeasureSpec.EXACTLY));
    }

    // onFinishInflate 当View中所有的子控件均被映射成xml后触发
    @Override
    protected void onFinishInflate() {
        if (mHeadView != null && !mHeadView.isClickable()) {
            mHeadView.setClickable(true);   // 将标志设为 true
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null && childAt instanceof ViewPager) {
                childViewPager = (ViewPager) childAt;
            }
        }
        super.onFinishInflate();
    }

    /**
     * 　　public final void forceFinished (boolean finished)
     *   强制终止的字段到特定值。（译者注：立即停止滚动？）
     *   参数
     *   finished    新的结束值
     */
    public void stopSliding(){
        mScroller.forceFinished(true);
    }
}
