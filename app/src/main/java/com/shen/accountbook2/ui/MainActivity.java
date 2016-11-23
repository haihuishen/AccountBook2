package com.shen.accountbook2.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import com.shen.accountbook2.R;
import com.shen.accountbook2.ui.fragment.HomeFragment;
import com.shen.accountbook2.ui.fragment.MineFragment;
import com.shen.accountbook2.ui.fragment.OtherFragment;


/**
 * Created by shen on 8/15 0015.
 */
public class MainActivity extends FragmentActivity{

//    private Handler handler = AccountBookApplication.getHandler();

    // 底部标签切换的Fragment
    private Fragment mHomeFragment,mMineFragment,mOtherFragment,mCurrentFragment;

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
        initTab();
    }

    private void initView(){
        rgGroup = (RadioGroup) findViewById(R.id.rg_group);
    }


    private void initData(){

        // "底栏标签" 切换监听
        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home:						     // 首页
                            if (mHomeFragment == null)
                                mHomeFragment = new HomeFragment();
                            addOrShowFragment(getSupportFragmentManager().beginTransaction(), mHomeFragment);
                        break;

                    case R.id.rb_mine:						     // 添加
                        if (mMineFragment == null)
                            mMineFragment = new MineFragment();
                        addOrShowFragment(getSupportFragmentManager().beginTransaction(), mMineFragment);
                        break;

                    case R.id.rb_other:						// 其他
                        if (mOtherFragment == null)
                            mOtherFragment = new OtherFragment();
                        addOrShowFragment(getSupportFragmentManager().beginTransaction(), mOtherFragment);
                        break;

                    default:
                        break;
                }
            }
        });

//        rgGroup.check(R.id.rb_home);   // 默认选中"首页"

    }

    /**
     * 初始化底部标签
     */
    private void initTab() {
        if (mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
        }

        if (!mHomeFragment.isAdded()) {
            // 提交事务
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_content, mHomeFragment).commit();

            // 记录当前Fragment
            mCurrentFragment = mHomeFragment;
        }
    }

    /**
     * 添加或者显示碎片
     *
     * @param transaction
     * @param fragment
     */
    private void addOrShowFragment(FragmentTransaction transaction,
                                   Fragment fragment) {
        if (mCurrentFragment == fragment)
            return;


        if (!fragment.isAdded()) {      // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.hide(mCurrentFragment)
                    .add(R.id.frame_content, fragment).commit();
        } else {                       // 如果存在，直接隐藏之前的，显示现在这个
            transaction.hide(mCurrentFragment).show(fragment).commit();
        }

        mCurrentFragment = fragment;
    }

}