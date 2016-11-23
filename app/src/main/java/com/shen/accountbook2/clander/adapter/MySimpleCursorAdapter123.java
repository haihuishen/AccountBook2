package com.shen.accountbook2.clander.adapter;

/**
 * Created by shen on 9/19 0019.
 */
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.SimpleCursorAdapter;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.shen.accountbook2.R;

public class MySimpleCursorAdapter123 extends SimpleCursorAdapter {

    private Context mContext;

    View mParent;
    View mBg;
    PhotoView mPhotoView;
    Info mInfo;

    AlphaAnimation in = new AlphaAnimation(0, 1);
    AlphaAnimation out = new AlphaAnimation(1, 0);




    public MySimpleCursorAdapter123(Context context, int layout, Cursor c,
                                    String[] from, int[] to) {

        super(context, layout, c, from, to);
        // TODO Auto-generated constructor stub

        mContext = context;


        in.setDuration(300);
        out.setDuration(300);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                mBg.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        // listview每次得到一个item，都要view去绘制，通过getView方法得到view
        // position为item的序号
        View view = null;
        if (convertView != null) {
            view = convertView;
            // 使用缓存的view,节约内存
            // 当listview的item过多时，拖动会遮住一部分item，被遮住的item的view就是convertView保存着。
            // 当滚动条回到之前被遮住的item时，直接使用convertView，而不必再去new view()

        } else {
            view = super.getView(position, convertView, parent);

        }

        // int数组，两种颜色
        int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色

        view.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同

//        PhotoView p = new PhotoView(mContext);
        PhotoView p = (PhotoView) view.findViewById(R.id.tableItem_pv_image);
//        p.setLayoutParams(new AbsListView.LayoutParams((int) (mContext.getResources().getDisplayMetrics().density * 100), (int) (mContext.getResources().getDisplayMetrics().density * 100)));
//        p.setScaleType(ImageView.ScaleType.CENTER_CROP);
        p.setImageResource(R.drawable.test);
        // 把PhotoView当普通的控件把触摸功能关掉
        p.disenable();



        return super.getView(position, view, parent);
    }

}