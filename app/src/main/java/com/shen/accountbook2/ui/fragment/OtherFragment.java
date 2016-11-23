package com.shen.accountbook2.ui.fragment;

import android.view.View;
import android.widget.TextView;

import com.shen.accountbook2.R;

/**
 * Created by shen on 9/9 0009.
 */
public class OtherFragment extends BaseFragment {

    /******************************标题***********************************/
    private TextView tvTitle;

    public OtherFragment() {
    }

    @Override
    public View initUI() {
        View view = View.inflate(mContext, R.layout.fragment_other, null);

        /******************************标题***********************************/
        tvTitle = (TextView) view.findViewById(R.id.tv_title);

        return view;
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        tvTitle.setText("其他");
    }

    @Override
    public void onClick(View v) {

    }
}
