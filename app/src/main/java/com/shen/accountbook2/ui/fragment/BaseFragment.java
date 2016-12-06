package com.shen.accountbook2.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 三个"标签页"的基类<p>
 * viewpage的每一个"标签"
 * 
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener{

	/**
	 * MainActivity对象（主页面）<br>
	 *  BasePager类中public Context mContext; <br>
	 */
    public Context mContext;

    protected boolean isVisible;

	/** 标题栏的标题*/
	public TextView tvTitle;
	/** 标题的导航键（菜单按钮）*/
	public ImageButton btnMenu;


	public BaseFragment() {
	}

    /**
     *  Fragment创建
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = initUI();
        initListener();
        initData();
        return view;
    }


    /**
     * 初始化布局
     * @return View
     */
    public abstract View initUI();

    /**
     * 初始化监听
     */
    public abstract void initListener();

	/**
	 *  初始化"标签"的数据<p>
	 *  
	 *  空的,等我们写
	 */
	public abstract void initData();




    /**
     * 在这里实现Fragment数据的缓加载.
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * onVisiable，即fragment被设置为可见时调用
     */
    protected void onVisible(){
        lazyLoad();
    }

    /**
     * 子类去实现<p>
     *     加载数据，重新加载数据
     */
    protected abstract void lazyLoad();

    /**
     * onInvisible，即fragment被设置为不可见时调用
     */
    protected void onInvisible(){

    }

}
