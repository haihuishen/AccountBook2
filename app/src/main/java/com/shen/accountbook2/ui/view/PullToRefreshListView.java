package com.shen.accountbook2.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 下拉刷新的listview
 *
 *
 *
 */
public class PullToRefreshListView extends ListView implements OnScrollListener {

    /** 下拉刷新"没松手"（刷新状态）*/
    private static final int STATE_PULL_TO_REFRESH = 1;
    /** 刷新中"松手了"（刷新状态）*/
    private static final int STATE_RELEASE_TO_REFRESH = 2;
    /** 当前刷新模式（刷新状态）*/
    private static final int STATE_REFRESHING = 3;
    /** 当前刷新状态 */
    private int mCurrentState = STATE_PULL_TO_REFRESH;

    /** 头布局*/
    private View mHeaderView;
    /** 头布局高度*/
    private int mHeaderViewHeight;

    private int startY = -1;

    /** 头布局 标题*/
    private TextView tvTitle;
    /** 头布局时间文本*/
    private TextView tvTime;
    /** 箭头布局（图片）*/
    private ImageView ivArrow;

    /** 箭头向上动画*/
    private RotateAnimation animUp;
    /** 箭头向下动画*/
    private RotateAnimation animDown;
    /** 进度指示器（下拉的进度条）*/
    private ProgressBar pbProgress;


    /** 脚布局*/
    private View mFooterView;
    /** 脚布局高度*/
    private int mFooterViewHeight;
    /** 标记是否正在"加载更多(脚布局)" */
    private boolean isLoadMore;


    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initHeaderView();
        initFooterView();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
        initFooterView();
    }

    public PullToRefreshListView(Context context) {
        super(context);
        initHeaderView();
        initFooterView();
    }

    /**
     * 初始化"头布局"（下拉的头布局）
     */
    private void initHeaderView() {
        mHeaderView = View.inflate(getContext(), R.layout.listview_refresh_header, null);

        // 在listview"最前面"添加"头布局"
        this.addHeaderView(mHeaderView);

        tvTitle = (TextView) mHeaderView.findViewById(R.id.tv_title);
        tvTime = (TextView) mHeaderView.findViewById(R.id.tv_time);
        ivArrow = (ImageView) mHeaderView.findViewById(R.id.iv_arrow);
        pbProgress = (ProgressBar) mHeaderView.findViewById(R.id.pb_loading);

        // 在正常显示下,拿到"头布局"的高
        mHeaderView.measure(0, 0);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        // 隐藏头布局 （相当于,上移动自身高度）
        mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

        initAnim();
        setCurrentTime();
    }

    /**
     * 初始化"脚布局"
     */
    private void initFooterView() {
        mFooterView = View.inflate(getContext(),R.layout.listview_refresh_footer, null);

        // 在listview最后添加"脚布局"
        this.addFooterView(mFooterView);

        // 在正常显示下,拿到"脚布局"的高
        mFooterView.measure(0, 0);
        mFooterViewHeight = mFooterView.getMeasuredHeight();
        // 隐藏脚布局 （相当于,上移动自身高度）
        mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);

        // "脚布局"滑动   监听
        this.setOnScrollListener(this);
    }

    /**
     *  设置刷新时间
     */
    private void setCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");      // 格式
        String time = format.format(new Date());                                        // new Date() 得到当前的日期
        tvTime.setText(time);
    }

    /**
     * listview控件的点击事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:			 // 按下
                startY = (int) ev.getY();	         // y轴
                break;

            case MotionEvent.ACTION_MOVE:			// 拖动

                // 当用户按住"头条新闻的viewpager"进行下拉时,ACTION_DOWN会被viewpager消费掉
                // ***(我们现在是在listview中)
                // ***导致startY没有赋值,此处需要重新获取一下
                if (startY == -1) {
                    startY = (int) ev.getY();
                }

                if (mCurrentState == STATE_REFRESHING) {
                    // 如果是正在刷新, 跳出循环
                    break;
                }

                int endY = (int) ev.getY();
                int dy = endY - startY;

                int firstVisiblePosition = getFirstVisiblePosition();   // 当前显示的"第一个item"的位置

                if (dy > 0 && firstVisiblePosition == 0) {              // 必须下拉,并且当前显示的是第一个item
                    int padding = dy - mHeaderViewHeight;             // 计算当前下拉控件的padding值（偏移量）
                    mHeaderView.setPadding(0, padding, 0, 0);          // 设置当前"头布局"的位置

                    // 当拉下的"偏移量"大于   "头布局"自生高度时
                    if (padding > 0 && mCurrentState != STATE_RELEASE_TO_REFRESH) {
                        mCurrentState = STATE_RELEASE_TO_REFRESH;   // 改为松开刷新
                        refreshState();
                    } else if (padding < 0 && mCurrentState != STATE_PULL_TO_REFRESH) {
                        mCurrentState = STATE_PULL_TO_REFRESH;      // 改为下拉刷新
                        refreshState();
                    }
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:				// 松手
                startY = -1;

                if (mCurrentState == STATE_RELEASE_TO_REFRESH) {
                    mCurrentState = STATE_REFRESHING;
                    refreshState();

                    mHeaderView.setPadding(0, 0, 0, 0);                             // 完整展示头布局

                    // 4. 进行回调
                    if (mListener != null) {
                        mListener.onRefresh();                                      // 子类实现的
                    }
                } else if (mCurrentState == STATE_PULL_TO_REFRESH) {
                    mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);          // 隐藏头布局
                }
                break;

            default:
                break;
        }

        return super.onTouchEvent(ev);
    }

    /**
     * 初始化箭头动画
     */
    private void initAnim() {


        // 向上转, 围绕着自己的中心, 逆时针旋转0 -> -180.
		/*
		 *第一个参数 fromDegrees：旋转的起始角度
		 *第二个参数 toDegrees:旋转的结束角度
		 *第三个参数 pivotXType:X轴原点的类型(相对于自身还是相对于父容器)
		 *第四个参数 pivotXValue:原点的X轴坐标   （0~1f; 0.5f 就是X轴的一半）
		 *第五个参数 pivotYType： Y轴原点的类型
		 *第六个参数pivotYValue:原点的Y轴坐标
		 ************************
		 *RotateAnimation.RELATIVE_TO_SELF 相对于自己
		 */
        animUp = new RotateAnimation(0, -180,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animUp.setDuration(200);					// 播放一次动画的时间
        animUp.setFillAfter(true);					// 动画停留在结束位置


        // 向下转, 围绕着自己的中心, 逆时针旋转 -180 -> -360
        animDown = new RotateAnimation(-180, 0,
                Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animDown.setDuration(200);
        animDown.setFillAfter(true);
    }

    /**
     * 根据当前状态刷新界面
     */
    private void refreshState() {
        switch (mCurrentState) {
            case STATE_PULL_TO_REFRESH:
                tvTitle.setText("下拉刷新");
                pbProgress.setVisibility(View.INVISIBLE);
                ivArrow.setVisibility(View.VISIBLE);
                ivArrow.startAnimation(animDown);
                break;

            case STATE_RELEASE_TO_REFRESH:
                tvTitle.setText("松开刷新");
                pbProgress.setVisibility(View.INVISIBLE);
                ivArrow.setVisibility(View.VISIBLE);
                ivArrow.startAnimation(animUp);
                break;

            case STATE_REFRESHING:
                tvTitle.setText("正在刷新...");
                ivArrow.clearAnimation();			// 清除箭头动画,否则无法隐藏
                pbProgress.setVisibility(View.VISIBLE);
                ivArrow.setVisibility(View.INVISIBLE);
                break;

            default:
                break;
        }
    }

    /**
     * 刷新结束,收起控件
     *
     * @param   success			boolean(只有刷新成功"true"之后才更新时间)
     */
    public void onRefreshComplete(boolean success) {
        if(!isLoadMore) {
            mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);            // 隐藏"头部局"

            mCurrentState = STATE_PULL_TO_REFRESH;	                        //下拉刷新"没松手"（刷新状态）

            tvTitle.setText("下拉刷新");
            pbProgress.setVisibility(View.INVISIBLE);
            ivArrow.setVisibility(View.VISIBLE);	                            // 箭头图片,设为可见

            if (success) {                                                      // 只有刷新成功之后才更新时间
                setCurrentTime();
            }
        }else {
            mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);            //隐藏布局，加载更多
            isLoadMore = false;
        }
    }


    /************************   子类实现的接口(传进来接收后，调用)    ****************************/
    /*********************************************************************************************/
    /**
     * 3. 定义成员变量,接收监听对象<p>
     *  下拉刷新的————回调接口
     */
    private OnRefreshListener mListener;


    /**
     * 2. 暴露接口,设置监听
     *
     * @param	listener	OnRefreshListener对象（调用时会  new出一个;这里就拿到了"实例对象"）
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }


    /***********************************   子类实现的接口    *************************************/
    /*********************************************************************************************/

    /**
     * 1. 下拉刷新的————回调接口<br>
     * 2. 上拉加载更多————回调接口
     */
    public interface OnRefreshListener {

        /**
         * 下拉刷新数据(头布局),子类来写这个（干什么）<p>
         * 这里应是"刷新数据"
         */
        public void onRefresh();

        /**
         * 上拉加载更多(脚布局),子类来写这个（干什么）<p>
         * 这里应是"加载数据"
         */
        public void onLoadMore();
    }


    /*************************   implements OnScrollListener    **********************************/
    /*********************************************************************************************/

    /**
     *  滑动状态发生变化（监听的是"脚布局"）
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {


        // . 加载更多
        // ***1、最新状态是空闲状态(SCROLL_STATE_IDLE)
        // ***2、并且"当前界面"显示了"所有数据的最后一条"
        if (scrollState == SCROLL_STATE_IDLE) {				// 空闲状态

            int lastVisiblePosition = getLastVisiblePosition();              // 拿到"当前界面"显示了"所有数据的最后一条"

            if (lastVisiblePosition == getCount() - 1 && !isLoadMore) {     // 当前显示的是最后一个item并且没有正在加载更多
                LogUtils.i("加载更多...");
                isLoadMore = true;                                          // 到底了
                mFooterView.setPadding(0, 0, 0, 0);                         // 显示加载更多的布局（显示脚布局）

                // 跳转到"最后一条", 使其显示出加载更多.
                // ***将listview显示在最后一个item上,
                // ***从而加载更多会直接展示出来, 无需手动滑动
                setSelection(getCount() - 1);

                if(mListener != null) {                                       // 通知主界面加载下一页数据
                    mListener.onLoadMore();                                 // 子类实现的
                }
            }
        }
    }

    /**
     *  滑动过程回调（监听的是"脚布局"）
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

    }
}
