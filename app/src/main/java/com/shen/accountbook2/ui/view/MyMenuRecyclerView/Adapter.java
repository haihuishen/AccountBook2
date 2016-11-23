package com.shen.accountbook2.ui.view.MyMenuRecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.GetWindowParaUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView适配器<p>
 *
 *     适合——数据类型是：数组
 *
 * Adapter.MyViewHolder   Adapter的内部类
 */
public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> implements SlidingButtonView.IonSlidingButtonListener {

    private Context mContext;

    // 监听，基本是给子类实现的接口
    private IonSlidingViewClickListener mIDeleteBtnClickListener;

    private List<String> mDatas = new ArrayList<String>();

    private SlidingButtonView mMenu = null;

    public Adapter(Context context) {

        mContext = context;
        mIDeleteBtnClickListener = (IonSlidingViewClickListener) context;   // 这个不懂

        for (int i = 0; i < 10; i++) {
            mDatas.add(i+"");
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    // 控件绑定
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        //设置内容布局的宽为屏幕宽度
        holder.layoutContent.getLayoutParams().width = GetWindowParaUtils.getScreenWidth(mContext);

        holder.layoutContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否有删除菜单打开
                if (menuIsOpen()) {
                    closeMenu();//关闭菜单
                } else {
                    int n = holder.getLayoutPosition();
                    mIDeleteBtnClickListener.onItemClick(v, n);
                }
            }
        });

        holder.tvDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int n = holder.getLayoutPosition();                     // Recycler中拿到当前项的"索引"
                mIDeleteBtnClickListener.onDeleteBtnCilck(v, n);
            }
        });
    }

    /**
     * 控件的"项"布局
     * @param arg0
     * @param arg1
     * @return
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item, arg0,false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }



    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDelete;
        public ViewGroup layoutContent;       // 包裹textView的控件

        /**
         *
         * @param itemView 控件中的"项布局"，从中可以拿到里面的控件(如删除)
         */
        public MyViewHolder(View itemView) {
            super(itemView);
            tvDelete = (TextView) itemView.findViewById(R.id.tv_RecyclerViewItem_delete);
            layoutContent = (ViewGroup) itemView.findViewById(R.id.layout_RecyclerViewItem_content);

            ((SlidingButtonView) itemView).setSlidingButtonListener(Adapter.this);
        }
    }

    /**
     * 添加项
     * @param position 添加项下标
     */
    public void addData(int position) {
        mDatas.add(position, "添加项");
        notifyItemInserted(position);
    }

    /**
     * 删除项
     * @param position
     */
    public void removeData(int position){
        mDatas.remove(position);                // 项数据删除
        notifyItemRemoved(position);            // 项

    }

    /**
     * 删除菜单打开信息接收
     * @param view      slidingButtonView  项控件;拿到这个参数就可以知道"项"是否被打开;
     */
    @Override
    public void onMenuIsOpen(View view) {
        mMenu = (SlidingButtonView) view;
    }

    /**
     * 滑动或者点击了Item监听
     * @param slidingButtonView  项控件
     */
    @Override
    public void onDownOrMove(SlidingButtonView slidingButtonView) {
        if(menuIsOpen()){                       // true:打开
            if(mMenu != slidingButtonView){     // 如果不是 项，就关闭
                closeMenu();
            }
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        mMenu.closeMenu();
        mMenu = null;

    }
    /**
     * 判断是否有菜单打开
     */
    public Boolean menuIsOpen() {
        if(mMenu != null){
            return true;
        }
        Log.i("asd","mMenu为null");
        return false;
    }


    /**
     * 监听，基本是给子类实现的接口
     */
    public interface IonSlidingViewClickListener {
        /**
         * 控件(项被点击)点击事件，子类实现
         * @param view              项中被点击的控件
         * @param position          项的索引
         */
        void onItemClick(View view, int position);

        /**
         *  控件(项被点击)点击事件(删除菜单)，子类实现
         * @param view              项中的删除菜单
         * @param position          项的索引
         */
        void onDeleteBtnCilck(View view, int position);
    }
}

