package com.shen.accountbook2.ui.view.MyMenuRecyclerView;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shen.accountbook2.R;

/**
 * 项<p>
 * 自定义控件<p>
 *
 * Android 的界面绘制流程
 * 测量			 摆放		绘制
 * measure	->	layout	->	draw
 * 	  | 		  |			 |
 * onMeasure -> onLayout -> onDraw 重写这些方法, 实现自定义控件
 *
 * onResume()之后执行 下面的方法--(activity可见可交互)
 *
 * 继承View
 * onMeasure() (在这个方法里指定自己的宽高) -> onDraw() (绘制自己的内容)
 *
 * 继承ViewGroup
 * onMeasure() (指定自己的宽高, 所有子View的宽高)-> onLayout() (摆放所有子View) -> onDraw() (绘制内容)
 *
 * extends HorizontalScrollView--横向滑动控件
 */
public class SlidingButtonView extends HorizontalScrollView {

    private TextView mTvTopDate;
    private LinearLayout mLinearLayoutMenu;

    private int mScrollWidth;
    private int mScrollWidthTv;

    private IonSlidingButtonListener mIonSlidingButtonListener;

    private Boolean isOpen = false;
    private Boolean once = false;           // 用于防止重复绑定控件


    public SlidingButtonView(Context context) {
        this(context, null);
    }

    public SlidingButtonView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SlidingButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // listview下拉或上拉时，顶部的或底部的渐变色怎么去除   // 水平的也一样，左右!
        this.setOverScrollMode(OVER_SCROLL_NEVER);
    }

    /**
     * 测量
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(!once){
            mTvTopDate = (TextView) findViewById(R.id.tv_top_day);
            mLinearLayoutMenu = (LinearLayout) findViewById(R.id.layout_RecyclerViewItem_menu);
            once = true;
        }

    }

    /**
     * 继承ViewGroup<br>
     * onMeasure() (指定自己的宽高, <br>
     * 所有子View的宽高)-> onLayout() (摆放所有子View) -> onDraw() (绘制内容)<p>
     *
     * 在自定义ViewGroup的时候，<br>
     * 如果对子View的测量没有特殊的需求，那么可以继承系统已有的布局(比如FrameLayout).<p>
     *
     * 目的是为了让已有的布局帮我们实行onMeasure;<br>
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed){
            this.scrollTo(0,0);
            //获取水平滚动条可以滑动的范围，即右侧菜单的宽度
            mScrollWidth = mLinearLayoutMenu.getWidth();
            //mScrollWidthTv = mTvTopDate.getWidth();

            Log.i("asd", "mScrollWidth:" + mScrollWidth);
        }

    }


    // 触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:           // 手指按下去
            case MotionEvent.ACTION_MOVE:           // 拖动
                mIonSlidingButtonListener.onDownOrMove(this);
                break;
            case MotionEvent.ACTION_UP:             // 手指抬起来
            case MotionEvent.ACTION_CANCEL:        // 动作取消
                changeScrollx();                     // 按滚动条被拖动距离判断关闭或打开菜单
                return true;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 该方法在onMeasure() 测量————执行完之后执行<p>
     * 那么可以在该方法中————初始化自己和子View的宽高
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mLinearLayoutMenu.setTranslationX(l - mScrollWidth);     // 删除控件右边和屏幕右边对齐
        mTvTopDate.setTranslationX(l);     // 删除控件右边和屏幕右边对齐
    }

    /**
     * 拖动结束调用这个<p>
     * 按滚动条被拖动距离判断关闭或打开菜单
     */
    public void changeScrollx(){
        if(getScrollX() >= (mScrollWidth/2)){
            this.smoothScrollTo(mScrollWidth, 0);           // 整个"项"x轴移动mScrollWidth个单位
            isOpen = true;                                  // 打开
            mIonSlidingButtonListener.onMenuIsOpen(this);
        }else{
            this.smoothScrollTo(0, 0);                          // 整个"项" x轴为原点
            isOpen = false;                                 // 关闭
        }
    }

    /**
     * 打开菜单
     */
    public void openMenu()
    {
        if (isOpen){
            return;
        }
        this.smoothScrollTo(mScrollWidth, 0);
        isOpen = true;
        mIonSlidingButtonListener.onMenuIsOpen(this);
    }

    /**
     * 关闭菜单
     */
    public void closeMenu()
    {
        if (!isOpen){
            return;
        }
        this.smoothScrollTo(0, 0);                      // 整个"项" x轴为原点
        isOpen = false;
    }


    // 获取"子类"实现好的 "接口"
    public void setSlidingButtonListener(IonSlidingButtonListener listener){
        mIonSlidingButtonListener = listener;
    }

    /**
     * 滑动监听(如菜单是否打开)，接口
     */
    public interface IonSlidingButtonListener{
        /**
         * 菜单是否打开(如：删除，修改)
         * @param view
         */
        void onMenuIsOpen(View view);

        /**
         * 滑动或者点击了Item监听
         * @param slidingButtonView     项控件
         */
        void onDownOrMove(SlidingButtonView slidingButtonView);
    }


}
