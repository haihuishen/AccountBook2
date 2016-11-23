package com.shen.accountbook2.ui.view.MyMenuRecyclerView;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.GetWindowParaUtils;
import com.shen.accountbook2.Utils.ImageFactory;
import com.shen.accountbook2.config.Constant;

import java.io.File;

/**
 * * RecyclerView适配器<p>
 *
 *     适合——数据类型是：Cursor
 *
 *  使用的
 */
public class MyRecyclerViewCursorAdapter2 extends RecyclerViewCursorAdapter<MyRecyclerViewCursorAdapter2.MyViewHolder> implements SlidingButtonView.IonSlidingButtonListener{

    private LayoutInflater inflater;

    private Context mContext;

    private Cursor mCursor;

    // 监听，基本是给子类实现的接口
    private IonSlidingViewClickListener mIDeleteBtnClickListener;

    private SlidingButtonView mMenu = null;


    public MyRecyclerViewCursorAdapter2(Context context, Cursor c, int flags) {
        super(context, c, flags);

        mCursor = c;
        mContext = context;
        mIDeleteBtnClickListener = (IonSlidingViewClickListener) context;   // 这个不懂
        inflater=LayoutInflater.from(context);
    }

    public MyRecyclerViewCursorAdapter2(Context context, Cursor c, int flags,
                                        View mParent, View mBg, PhotoView mPhotoView, AlphaAnimation in, AlphaAnimation out) {
        super(context, c, flags);

        mCursor = c;
        mContext = context;
        mIDeleteBtnClickListener = (IonSlidingViewClickListener) context;   // 这个不懂
        inflater=LayoutInflater.from(context);


    }

    @Override
    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, Cursor cursor) {

        final String _id = cursor.getString(Constant.TABLE_CONSUMPTION__id);
        String mainType = cursor.getString(Constant.TABLE_CONSUMPTION_maintype);
        String type1 = cursor.getString(Constant.TABLE_CONSUMPTION_type1);
        String concreteness = cursor.getString(Constant.TABLE_CONSUMPTION_concreteness);
        String unitPrice = cursor.getString(Constant.TABLE_CONSUMPTION_unitprice);
        String number = cursor.getString(Constant.TABLE_CONSUMPTION_number);
        String price = cursor.getString(Constant.TABLE_CONSUMPTION_price);
        String imageName = cursor.getString(Constant.TABLE_CONSUMPTION_image);
        //
        //            System.out.println(
        //                    "_id" + cursor.getString(Constant.TABLE_CONSUMPTION__id)+
        //                    "maintype:" + mainType+
        //                    "type1:" + type1+
        //                    "concreteness:" + concreteness+
        //                    "price:" + price+
        //                    "number:" + number+
        //                    "unitPrice:" + unitPrice+
        //
        //                    "date:" + cursor.getString(Constant.TABLE_CONSUMPTION_date)+"\n"+
        //                    "imageName:" + imageName
        //            );
        //            System.out.println("这张图片："+ Constant.IMAGE_PATH+"/"+imageName);
        final Bitmap bitmap;
        if(!TextUtils.isEmpty(imageName)) {
            if (new File(Constant.IMAGE_PATH, imageName).exists())
                bitmap = ImageFactory.getBitmap(Constant.IMAGE_PATH + "/" + imageName);
            else
                bitmap = ImageFactory.getBitmap(Constant.CACHE_IMAGE_PATH + "/" + "no_preview_picture.png");
        }else{
            bitmap = ImageFactory.getBitmap(Constant.CACHE_IMAGE_PATH + "/" + "no_preview_picture.png");
        }

        holder.tvMainType.setText(mainType);
        holder.tvType1.setText(type1);
        holder.tvConcreteness.setText(concreteness+"-"+cursor.getString(Constant.TABLE_CONSUMPTION__id));
        holder.tvUnitPrice.setText(unitPrice);
        holder.tvNumber.setText(number);
        holder.tvPrice.setText(price);
        holder.pvImage.setImageBitmap(bitmap);


//        // 把PhotoView当普通的控件把触摸功能关掉
//        holder.pvImage.disenable();
//        holder.pvImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Info mInfo =  holder.pvImage.getInfo();
//                mPhotoView.setImageBitmap(bitmap);
//                mBg.startAnimation(in);
//                mBg.setVisibility(View.VISIBLE);
//                mParent.setVisibility(View.VISIBLE);
//                mPhotoView.animaFrom(mInfo);
//            }
//        });


        //设置内容布局的宽为屏幕宽度
        holder.layoutContent.getLayoutParams().width = GetWindowParaUtils.getScreenWidth(mContext);

        holder.layoutContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否有删除菜单打开
                if (menuIsOpen()) {
                    closeMenu();//关闭菜单
                } else {
                    int n = holder.getLayoutPosition();                     // 获得当前"项"的"索引"
                    mIDeleteBtnClickListener.onItemClick(v, n);
                }
            }
        });

        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n = holder.getLayoutPosition();                     // Recycler中拿到当前项的"索引"
                mIDeleteBtnClickListener.onDeleteBtnCilck(v, n, _id);
            }
        });

    }

    @Override
    protected void onContentChanged() {}

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=inflater.inflate(R.layout.recyclerview_item,parent,false);         // "项布局"
        return new MyViewHolder(v);
    }

    /**
     * 项布局里面的控件
     */
    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDelete;

        public ViewGroup layoutContent;       // 包裹textView的控件

        TextView tvMainType;
        TextView tvType1;
        TextView tvConcreteness;
        TextView tvUnitPrice;
        TextView tvNumber;
        TextView tvPrice;
        PhotoView pvImage;

        /**
         *
         * @param itemView 控件中的"项布局"，从中可以拿到里面的控件(如删除)
         */
        public MyViewHolder(View itemView) {
            super(itemView);
            tvDelete = (TextView) itemView.findViewById(R.id.tv_RecyclerViewItem_delete);

            layoutContent = (ViewGroup) itemView.findViewById(R.id.layout_RecyclerViewItem_content);

            tvMainType = (TextView) itemView.findViewById(R.id.tableItem_tv_ProductName_maintype);
            tvType1 = (TextView) itemView.findViewById(R.id.tableItem_tv_ProductName_type1);
            tvConcreteness = (TextView) itemView.findViewById(R.id.tableItem_tv_ProductName_concreteness);
            tvUnitPrice = (TextView) itemView.findViewById(R.id.tableItem_tv_UnitPrice);
            tvNumber = (TextView) itemView.findViewById(R.id.tableItem_tv_Number);
            tvPrice = (TextView) itemView.findViewById(R.id.tableItem_tv_Price);
            pvImage = (PhotoView) itemView.findViewById(R.id.tableItem_pv_image);

            // 将设配器(其实因为  implements SlidingButtonView.IonSlidingButtonListener)，
            // 所以设置的应该是：接口;在本类中，实现了IonSlidingButtonListener的接口中的方法
            ((SlidingButtonView) itemView).setSlidingButtonListener(MyRecyclerViewCursorAdapter2.this);
        }
    }

    /*********************************************************************************************************/

    /**
     * 添加项 (数据库方式用不到这个)
     * @param position 添加项下标
     */
    public void addData(int position) {
        //mDatas.add(position, "添加项");
        notifyItemInserted(position);
    }

    /**
     * 删除项(数据库方式用不到这个)
     * @param position
     */
    public void removeData(int position){
        //mDatas.remove(position);                // 项数据删除
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
         * @param id                表的_id字段
         */
        void onDeleteBtnCilck(View view, int position, String id);
    }
}
