package com.shen.accountbook2.ui.fragment.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.GetWindowParaUtils;
import com.shen.accountbook2.Utils.ImageFactory;
import com.shen.accountbook2.Utils.ToastUtil;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TableEx;
import com.shen.accountbook2.global.AccountBookApplication;
import com.shen.accountbook2.widget.MyMenuRecyclerView.AccounBookProvider;
import com.shen.accountbook2.widget.MyMenuRecyclerView.RecyclerViewCursorAdapter;
import com.shen.accountbook2.widget.MyMenuRecyclerView.SlidingButtonView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;


//public class ReportForD5_Activity extends AppCompatActivity implements Adapter.IonSlidingViewClickListener{
public class ReportForD5_Activity extends Activity implements OnClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private final String TAG = "ReportForD5_Activity";

    private Context mContext;

    // String[] dayNames = {"Mon", "Tue", "Wed", "Thur", "Fri", "Sat", "Sun"};
    String[] dayNames = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    // 日历实例
    // private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    // Locale.getDefault()获取当前的语言环境，把返回值放进SimpleDateFormat的构造里，就能实现通用化，
    // 因此format.format(date)方法返回的值也会根据当前语言来返回对应的值
    // private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());
    private SimpleDateFormat dateFormatForYMD = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private boolean shouldShow = false;                             // false:关闭 true:打开
    private CompactCalendarView compactCalendarView;

    /******************************标题***********************************/
    private TextView tvTitle;
    private ImageButton btnMenu;
    private ImageButton btnBack;

    private TextView mTvDate;              // 日期;可弹出收回"日历"

    private TextView mTvAllPriceOfDay;    // 日总消费
    /*****************************列表************************************/
    private RecyclerView mRecyclerView;
    private MyRecyclerViewCursorAdapter myRecyclerViewCursorAdapter;


    /******************************全屏图片***********************************/
    static View mParent;
    static View mBg;
    static PhotoView mPhotoView;
    static Info mInfo;

    static AlphaAnimation in = new AlphaAnimation(0, 1);
    static AlphaAnimation out = new AlphaAnimation(1, 0);


    private TableEx mTableEx;
    private Cursor mCursor = null;
    private Cursor mAllCursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_day5_text);

        mContext = this;

        initView();
        initListener();
        initDate();
        setAdapter();
        initLoader();

    }

    private void initView(){

        /******************************标题***********************************/
        tvTitle = (TextView) findViewById(R.id.tv_title);
        btnMenu = (ImageButton) findViewById(R.id.btn_menu);
        btnBack = (ImageButton) findViewById(R.id.btn_back);

        mTvDate = (TextView) findViewById(R.id.tv_date);
        mTvAllPriceOfDay = (TextView) findViewById(R.id.tv_all_price);

        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        mParent = findViewById(R.id.parent);
        mBg = findViewById(R.id.bg);
        mPhotoView = (PhotoView) findViewById(R.id.img);
    }

    private void initListener(){
        // 标题
        btnBack.setOnClickListener(this);

        // 日历的开关，同时显示当前的时间
        mTvDate.setOnClickListener(this);

        // 全屏图片
        in.setDuration(300);
        out.setDuration(300);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBg.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mPhotoView.enable();
        mPhotoView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBg.startAnimation(out);
                mPhotoView.animaTo(mInfo, new Runnable() {
                    @Override
                    public void run() {
                        mParent.setVisibility(GONE);
                    }
                });
            }
        });

        // 日历的监听
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            // 点击"日"
            @Override
            public void onDayClick(Date dateClicked) {                                          // 点击"日"

                ToastUtil.show(dateFormatForYMD.format(dateClicked));
                mTvDate.setText(dateFormatForYMD.format(dateClicked));

//                mCursor = getContentResolver().query(AccounBookProvider.URI_ACCOUNTBOOK2_ALL, null, "date=? and user=?",
//                        new String[]{mTvDate.getText().toString(), AccountBookApplication.getUserInfo().getUserName()}, null);

                getLoaderManager().restartLoader(1, null, ReportForD5_Activity.this);       // 重启 Loader
            }

            // 月份滑动监听
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                ToastUtil.show(dateFormatForYMD.format(firstDayOfNewMonth));                // 得到这个月的第一天
                mTvDate.setText(dateFormatForYMD.format(firstDayOfNewMonth));

//                mCursor = getContentResolver().query(AccounBookProvider.URI_ACCOUNTBOOK2_ALL, null, "date=? and user=?",
//                        new String[]{mTvDate.getText().toString(), AccountBookApplication.getUserInfo().getUserName()}, null);

                getLoaderManager().restartLoader(1, null, ReportForD5_Activity.this);       // 重启 Loader

            }
        });
    }

    private void initDate(){
        /******************************标题***********************************/
        btnMenu.setVisibility(GONE);
        btnBack.setVisibility(VISIBLE);
        tvTitle.setText("日报表5");

        mTvDate.setText(dateFormatForYMD.format(new Date()));

        compactCalendarView.setDayColumnNames(dayNames);            // 周一到周末

        mTableEx = new TableEx(AccountBookApplication.getContext());

        //queryPriceOfDay();
    }

    private void setAdapter(){

        mCursor = getContentResolver().query(AccounBookProvider.URI_ACCOUNTBOOK2_ALL, null, "date=? and user=?",
                new String[]{mTvDate.getText().toString(), AccountBookApplication.getUserInfo().getUserName()}, null);


        MyRecyclerViewCursorAdapter.IonSlidingViewClickListener ionSlidingViewClickListener = new MyRecyclerViewCursorAdapter.IonSlidingViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i(TAG,"点击项："+position);
            }

            @Override
            public void onDeleteBtnCilck(View view, int position,String id) {
                Log.i(TAG,"删除项："+position);
                Log.i(TAG,"删除项___________________id："+id);

                int i = getContentResolver().delete(AccounBookProvider.URI_ACCOUNTBOOK2_ALL,"_id=? and user=?",
                        new String[]{id, AccountBookApplication.getUserInfo().getUserName()});
                Log.i(TAG,"删除项___________________i："+i);
            }

            @Override
            public void onUpdateBtnCilck(HashMap hashMapItem, int position) {
                Log.i(TAG,"更新项position："+position);
                Log.i(TAG,"更新项___________________id："+ hashMapItem.get(Constant.TABLE_CONSUMPTION__id_STRING));
                Log.i(TAG,"更新项___________________user："+ hashMapItem.get(Constant.TABLE_CONSUMPTION_user_STRING));
                Log.i(TAG,"更新项___________________mainType："+ hashMapItem.get(Constant.TABLE_CONSUMPTION_maintype_STRING));
                Log.i(TAG,"更新项___________________type1："+ hashMapItem.get(Constant.TABLE_CONSUMPTION_type1_STRING));
                Log.i(TAG,"更新项___________________concreteness："+ hashMapItem.get(Constant.TABLE_CONSUMPTION_concreteness_STRING));
                Log.i(TAG,"更新项___________________unitPrice："+ hashMapItem.get(Constant.TABLE_CONSUMPTION_unitprice_STRING));
                Log.i(TAG,"更新项___________________number："+ hashMapItem.get(Constant.TABLE_CONSUMPTION_number_STRING));
                Log.i(TAG,"更新项___________________price："+ hashMapItem.get(Constant.TABLE_CONSUMPTION_price_STRING));
                Log.i(TAG,"更新项___________________image："+ hashMapItem.get(Constant.TABLE_CONSUMPTION_image_STRING));
                Log.i(TAG,"更新项___________________date："+ hashMapItem.get(Constant.TABLE_CONSUMPTION_date_STRING));

                Intent intent = new Intent(ReportForD5_Activity.this, ChangeConsumptionInfoActivity.class);
                Bundle bundle = new Bundle();
                Log.i(TAG,"add项___________________date1：");
                bundle.putString(Constant.TABLE_CONSUMPTION__id_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION__id_STRING).toString());
                Log.i(TAG,"add项___________________date2：");
                bundle.putString(Constant.TABLE_CONSUMPTION_user_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION_user_STRING).toString());
                Log.i(TAG,"add项___________________date3：");
                bundle.putString(Constant.TABLE_CONSUMPTION_maintype_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION_maintype_STRING).toString());
                Log.i(TAG,"add项___________________date4：");
                bundle.putString(Constant.TABLE_CONSUMPTION_type1_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION_type1_STRING).toString());
                Log.i(TAG,"add项___________________date5：");
                bundle.putString(Constant.TABLE_CONSUMPTION_concreteness_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION_concreteness_STRING).toString());
                Log.i(TAG,"add项___________________date6：");
                bundle.putString(Constant.TABLE_CONSUMPTION_unitprice_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION_unitprice_STRING).toString());
                Log.i(TAG,"add项___________________date7：");
                bundle.putString(Constant.TABLE_CONSUMPTION_number_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION_number_STRING).toString());
                Log.i(TAG,"add项___________________date8：");
                bundle.putString(Constant.TABLE_CONSUMPTION_price_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION_price_STRING).toString());
                Log.i(TAG,"add项___________________date9：");

                bundle.putString(Constant.TABLE_CONSUMPTION_image_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION_image_STRING).toString());
                Log.i(TAG,"add项___________________date10：");
                bundle.putString(Constant.TABLE_CONSUMPTION_date_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION_date_STRING).toString());
                Log.i(TAG,"add项___________________date20：");

                intent.putExtras(bundle);

                startActivityForResult(intent,1);

            }

        };

        myRecyclerViewCursorAdapter = new MyRecyclerViewCursorAdapter(this, mCursor,1,ionSlidingViewClickListener);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));      // 不改，默认是"纵向"
        mRecyclerView.setAdapter(myRecyclerViewCursorAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());           // 默认的"项"动作的"动画"

    }

    private void initLoader() {
        getLoaderManager().initLoader(1, null,this);
    }

    /**
     * 查询"当日消费"
     */
    private void queryPriceOfDay(){
        // cast(sum(asset) as TEXT)--> 这样就不会变成"科学计数法"
        // sum(asset) -->asset 就算是 varchar(20),不是decimal(18,2)，使用sum(asset)后还是"会使用科学计数法"
        mAllCursor = mTableEx.Query(Constant.TABLE_CONSUMPTION, new String[]{"cast(sum(price) as TEXT)"},
                "date=? and user=?", new String[]{mTvDate.getText().toString(), AccountBookApplication.getUserInfo().getUserName()},
                null, null, null);
        try {
            if (mAllCursor.getCount() != 0) {        // 查询：带"函数"字段，就算"没记录"，返回的也是"1"
                mAllCursor.moveToFirst();
                if(mAllCursor.getString(0) != null){        // 没记录，返回1，这里返回的是 "null"
                        mTvAllPriceOfDay.setTextColor(this.getResources().getColor(R.color.red));
                        mTvAllPriceOfDay.setText("当日消费: " + mAllCursor.getString(0));
                }else{
                    mTvAllPriceOfDay.setTextColor(this.getResources().getColor(R.color.forestgreen));
                    mTvAllPriceOfDay.setText("当日消费: 0");
                }
            }else{
                mTvAllPriceOfDay.setTextColor(this.getResources().getColor(R.color.forestgreen));
                mTvAllPriceOfDay.setText("当日消费: 0");
            }
        }catch (Exception e){
            System.out.println("当日消费error:"+e.getMessage());
        }
    }


    /*************************************   Loader      ***********************************************/
    // LoaderManager.LoaderCallbacks<Cursor>  接口要实现的
    // 是 getLoaderManager().initLoader(1, null,this); 的第三参数
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(TAG,"调用___________________Loader");
        CursorLoader loader = new CursorLoader(ReportForD5_Activity.this, AccounBookProvider.URI_ACCOUNTBOOK2_ALL, null, "date=? and user=?",
                new String[]{mTvDate.getText().toString(), AccountBookApplication.getUserInfo().getUserName()}, null);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(TAG,"调用___________________onLoadFinished");
        myRecyclerViewCursorAdapter.swapCursor(data);
        queryPriceOfDay();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(TAG,"调用___________________onLoaderReset");
        myRecyclerViewCursorAdapter.swapCursor(null);
    }



    /************************************************************************************/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:                                         // 退出本Activity
                finish();
                break;

            case R.id.tv_date:
                if (shouldShow) {
                    compactCalendarView.showCalendarWithAnimation();                            // 打开日历
                } else {
                    compactCalendarView.hideCalendarWithAnimation();                            // 隐藏日历
                }
                shouldShow = !shouldShow;                                           // 更改当前状态
        }
    }

    /************************************************************************************/
    // 按钮监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {                 // 如果点击的是"返回按钮"

            if(mParent.getVisibility() == VISIBLE && mBg.getVisibility() == VISIBLE){   // 缩小、隐藏那个预览布局
                mBg.startAnimation(out);
                mPhotoView.animaTo(mInfo, new Runnable() {
                    @Override
                    public void run() {
                        mParent.setVisibility(GONE);
                    }
                });
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ChangeConsumptionInfoActivity.OK) {
            if (resultCode == Activity.RESULT_OK) {
                if(data.getStringExtra(ChangeConsumptionInfoActivity.CHANGE) == ChangeConsumptionInfoActivity.CHANGE){
                    //queryPriceOfDay();
                }
            }
        }
    }



    /******************************************************************************************************************************/
    /***********************************            适配器：内部类                *************************************************/
    /******************************************************************************************************************************/
    // 适配器
    /**
     * Created by shen on 11/20 0020.
     */
    public static class MyRecyclerViewCursorAdapter extends RecyclerViewCursorAdapter<MyRecyclerViewCursorAdapter.MyViewHolder> implements SlidingButtonView.IonSlidingButtonListener{

        private LayoutInflater inflater;

        private Context mContext;

        private Cursor mCursor;

        // 监听，基本是给子类实现的接口
        private IonSlidingViewClickListener mIonSlidingViewClickListener;

        private SlidingButtonView mMenu = null;

        public MyRecyclerViewCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);

            mCursor = c;
            mContext = context;
            inflater=LayoutInflater.from(context);
        }

        public MyRecyclerViewCursorAdapter(Context context, Cursor c, int flags, IonSlidingViewClickListener ionSlidingViewClickListener) {
            super(context, c, flags);

            mCursor = c;
            mContext = context;
            mIonSlidingViewClickListener = ionSlidingViewClickListener;   // 子类实现的接口(菜单控件点击事件)
            inflater=LayoutInflater.from(context);
        }


        @Override
        public Cursor getCursor() {
            return mCursor;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, Cursor cursor) {

            final String _id = cursor.getString(Constant.TABLE_CONSUMPTION__id);
            String user = cursor.getString(Constant.TABLE_CONSUMPTION_user);
            String mainType = cursor.getString(Constant.TABLE_CONSUMPTION_maintype);
            String type1 = cursor.getString(Constant.TABLE_CONSUMPTION_type1);
            String concreteness = cursor.getString(Constant.TABLE_CONSUMPTION_concreteness);
            String unitPrice = cursor.getString(Constant.TABLE_CONSUMPTION_unitprice);
            String number = cursor.getString(Constant.TABLE_CONSUMPTION_number);
            String price = cursor.getString(Constant.TABLE_CONSUMPTION_price);
            String image = cursor.getString(Constant.TABLE_CONSUMPTION_image);
            String date = cursor.getString(Constant.TABLE_CONSUMPTION_date);

            final HashMap<String,String> hashMapItem = new HashMap<String,String>();
            hashMapItem.put(Constant.TABLE_CONSUMPTION__id_STRING, _id);
            hashMapItem.put(Constant.TABLE_CONSUMPTION_user_STRING, user);
            hashMapItem.put(Constant.TABLE_CONSUMPTION_maintype_STRING, mainType);
            hashMapItem.put(Constant.TABLE_CONSUMPTION_type1_STRING, type1);
            hashMapItem.put(Constant.TABLE_CONSUMPTION_concreteness_STRING, concreteness);
            hashMapItem.put(Constant.TABLE_CONSUMPTION_unitprice_STRING, unitPrice);
            hashMapItem.put(Constant.TABLE_CONSUMPTION_number_STRING, number);
            hashMapItem.put(Constant.TABLE_CONSUMPTION_price_STRING, price);
            hashMapItem.put(Constant.TABLE_CONSUMPTION_image_STRING, image);
            hashMapItem.put(Constant.TABLE_CONSUMPTION_date_STRING, date);

            Log.i("shenshenshenshen","调用123456___________________image:"+image);
            //
            //            System.out.println(
            //                    "_id" + mCursor.getString(Constant.TABLE_CONSUMPTION__id)+
            //                    "maintype:" + mainType+
            //                    "type1:" + type1+
            //                    "concreteness:" + concreteness+
            //                    "price:" + price+
            //                    "number:" + number+
            //                    "unitPrice:" + unitPrice+
            //
            //                    "date:" + mCursor.getString(Constant.TABLE_CONSUMPTION_date)+"\n"+
            //                    "imageName:" + imageName
            //            );
            //            System.out.println("这张图片："+ Constant.IMAGE_PATH+"/"+imageName);
            final Bitmap bitmap;
            if(!TextUtils.isEmpty(image)) {
                if (new File(Constant.IMAGE_PATH, image).exists())
                    bitmap = ImageFactory.getBitmap(Constant.IMAGE_PATH + "/" + image);
                else
                    bitmap = ImageFactory.getBitmap(Constant.CACHE_IMAGE_PATH + "/" + "no_preview_picture.png");
            }else{
                bitmap = ImageFactory.getBitmap(Constant.CACHE_IMAGE_PATH + "/" + "no_preview_picture.png");
            }

            holder.tvMainType.setText(mainType);
            holder.tvType1.setText(type1);
            if(AccountBookApplication.getUserInfo().getUserName().equals("test"))
                holder.tvConcreteness.setText(concreteness+"-"+cursor.getString(Constant.TABLE_CONSUMPTION__id));
            else
                holder.tvConcreteness.setText(concreteness);
            holder.tvUnitPrice.setText(unitPrice);
            holder.tvNumber.setText(number);
            holder.tvPrice.setText(price);
            holder.pvImage.setImageBitmap(bitmap);


            // 把PhotoView当普通的控件把触摸功能关掉
            holder.pvImage.disenable();
            holder.pvImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mInfo =  holder.pvImage.getInfo();
                    mPhotoView.setImageBitmap(bitmap);
                    mBg.startAnimation(in);
                    mBg.setVisibility(VISIBLE);
                    mParent.setVisibility(VISIBLE);
                    mPhotoView.animaFrom(mInfo);
                }
            });


            //设置内容布局的宽为屏幕宽度
            holder.layoutContent.getLayoutParams().width = GetWindowParaUtils.getScreenWidth(mContext);

            holder.layoutContent.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //判断是否有删除菜单打开
                    if (menuIsOpen()) {
                        closeMenu();//关闭菜单
                    } else {
                        int position = holder.getLayoutPosition();                     // 获得当前"项"的"索引"
                        mIonSlidingViewClickListener.onItemClick(v, position);
                    }
                }
            });


            // 滑出菜单里面的控件：删除
            holder.tvDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();                     // Recycler中拿到当前项的"索引"
                    mIonSlidingViewClickListener.onDeleteBtnCilck(v, position, _id);
                }
            });

            // 滑出菜单里面的控件：更新
            holder.tvUpdate.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();                     // Recycler中拿到当前项的"索引"
                    mIonSlidingViewClickListener.onUpdateBtnCilck(hashMapItem, position);
                    closeMenu();
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
            public TextView tvUpdate;

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
                tvUpdate = (TextView) itemView.findViewById(R.id.tv_RecyclerViewItem_update);

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
                ((SlidingButtonView) itemView).setSlidingButtonListener(MyRecyclerViewCursorAdapter.this);
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
         * 滑出的菜单的控件监听，基本是给子类实现的接口
         */
        public interface IonSlidingViewClickListener {
            /**
             * @param view              项中被点击的控件
             * @param position          项的索引
             */
            void onItemClick(View view, int position);

            /**
             *  控件(项被点击)点击事件(菜单中：删除)，子类实现
             * @param view              项中的删除菜单
             * @param position          项的索引
             * @param id                表的_id字段
             */
            void onDeleteBtnCilck(View view, int position, String id);

            /**
             *  控件(项被点击)点击事件(菜单中：更新)，子类实现
             * @param hashMapItem             项中的所有内容
             * @param position          项的索引
             */
            void onUpdateBtnCilck(HashMap hashMapItem, int position);
        }
    }

}
