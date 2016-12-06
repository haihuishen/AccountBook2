package com.shen.accountbook2.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.widget.RadioGroup;

import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.ToastUtil;
import com.shen.accountbook2.ui.fragment.HomeFragment;
import com.shen.accountbook2.ui.fragment.MineFragment;
import com.shen.accountbook2.ui.fragment.OtherFragment;
import com.shen.accountbook2.ui.fragment.ShoppingFragment;


/**
 * Created by shen on 8/15 0015.
 */
public class MainActivity extends FragmentActivity{

    /** 第一次点击的时间(拿到当前系统的时间); 第二次点击时(拿到那时系统的时间)，相减不超过一定的值，就结束程序*/
    private long mExitTime = 0;

    // 底部标签切换的Fragment
    private Fragment mHomeFragment,mMineFragment,mOtherFragment,mShoppingFragment;

    private ViewPager mViewPager;
    /** 下面的"底栏标签"(RadioButton)的 组
     * <p>private RadioGroup rgGroup;
     */
    private RadioGroup rgGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        initView();
        initData();
    }

    private void initView(){

        mViewPager = (ViewPager) findViewById(R.id.vp_content);
        rgGroup = (RadioGroup) findViewById(R.id.rg_group);
    }


    private void initData(){
        mViewPager.setAdapter(new MyFragmentStatePageAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(3);    // 设置当前page左右两侧应该被保持的page数量，超过这个限制，page会被销毁重建


        // "底栏标签" 切换监听
        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home:						     // 首页
                        mViewPager.setCurrentItem(0);
                        break;

                    case R.id.rb_mine:						     // 我的
                        mViewPager.setCurrentItem(1);
                        break;

                    case R.id.rb_shopping:						 // 商城
                        mViewPager.setCurrentItem(2);
                        break;

                    case R.id.rb_other:						// 其他
                        mViewPager.setCurrentItem(3);
                        break;

                    default:
                        break;
                }
            }
        });

//    switch (mViewPager.getCurrentItem()){
//        case 0:
//            rgGroup.check(R.id.rb_home);
//        case 1:
//            rgGroup.check(R.id.rb_mine);
//        case 2:
//            rgGroup.check(R.id.rb_other);
//    }

    }



    /**
     * 自定义fragment适配器
     * @author ZHF
     *
     */
    public class MyFragmentStatePageAdapter extends FragmentStatePagerAdapter {
        public MyFragmentStatePageAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (mHomeFragment == null)
                        mHomeFragment = new HomeFragment();
                    return  mHomeFragment;
                case 1:
                    if (mMineFragment == null)
                        mMineFragment = new MineFragment();
                    return mMineFragment;
                case 2:
                    if (mShoppingFragment == null)
                        mShoppingFragment = new ShoppingFragment();
                    return mShoppingFragment;
                case 3:
                    if (mOtherFragment == null)
                        mOtherFragment = new OtherFragment();
                    return mOtherFragment;
                default:
                    return null;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()- mExitTime) > 2000){           // 2秒内
                ToastUtil.show("再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
