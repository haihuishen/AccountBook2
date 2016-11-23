package com.shen.accountbook2.ui.fragment.activity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.ImageFactory;
import com.shen.accountbook2.Utils.ToastUtil;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TableEx;
import com.shen.accountbook2.global.AccountBookApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by shen on 2016/11/18.
 */
public class ReportForD4_Activity extends Activity implements View.OnClickListener{

    private Context mContext;
    private Handler mHandler;

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

    private TextView mTvDate;

    private ListView mListView;
    private MyCursorAdapter myCursorAdapter;

    /******************************全屏图片***********************************/
    static View mParent;
    static View mBg;
    static PhotoView mPhotoView;
    static Info mInfo;

    static AlphaAnimation in = new AlphaAnimation(0, 1);
    static AlphaAnimation out = new AlphaAnimation(1, 0);


    private TableEx tableEx;
    private Cursor cursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_day4);

        mContext = this;

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
//                super.handleMessage(msg);


                switch (msg.what){
                    case 0:
                        ToastUtil.show("0");
                        myCursorAdapter = new MyCursorAdapter(mContext, cursor);
                        mListView.setAdapter(myCursorAdapter);
                        break;
                    case 1:
                        ToastUtil.show("1");
                        myCursorAdapter.changeCursor(cursor);
                        break;
                }

            }
        };


        initView();
        initListener();
        initDate();
    }

    private void initView(){
        /******************************标题***********************************/
        tvTitle = (TextView) findViewById(R.id.tv_title);
        btnMenu = (ImageButton) findViewById(R.id.btn_menu);
        btnBack = (ImageButton) findViewById(R.id.btn_back);

        mTvDate = (TextView) findViewById(R.id.tv_date);

        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);

        mListView = (ListView) findViewById(R.id.lv_content);

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
                mBg.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mPhotoView.enable();
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBg.startAnimation(out);
                mPhotoView.animaTo(mInfo, new Runnable() {
                    @Override
                    public void run() {
                        mParent.setVisibility(View.GONE);
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

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cursor = tableEx.Query(Constant.TABLE_CONSUMPTION, null, "date=? and user=?",
                                new String[]{mTvDate.getText().toString(), AccountBookApplication.getUserInfo().getUserName()},
                                null, null, null);
                         Message msg = mHandler.obtainMessage();
                         msg.what=1;
                         //msg.obj=0;
                        mHandler.sendMessage(msg);
                    }
                }).start();

            }

            // 月份滑动监听
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                ToastUtil.show(dateFormatForYMD.format(firstDayOfNewMonth));    // 得到这个月的第一天
                mTvDate.setText(dateFormatForYMD.format(firstDayOfNewMonth));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cursor = tableEx.Query(Constant.TABLE_CONSUMPTION, null, "date=? and user=?",
                                new String[]{mTvDate.getText().toString(), AccountBookApplication.getUserInfo().getUserName()},
                                null, null, null);
                        Message msg = mHandler.obtainMessage();
                        msg.what=1;
                        //msg.obj=0;
                        mHandler.sendMessage(msg);
                    }
                }).start();

            }
        });
    }

    private void initDate(){
        /******************************标题***********************************/
        btnMenu.setVisibility(View.GONE);
        btnBack.setVisibility(View.VISIBLE);
        tvTitle.setText("日报表4");

        mTvDate.setText(dateFormatForYMD.format(new Date()));

        compactCalendarView.setDayColumnNames(dayNames);            // 周一到周末
        // currentCalender.setTime(new Date());                         // 将当前日期，设为日历当前

        tableEx = new TableEx(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                cursor = tableEx.Query(Constant.TABLE_CONSUMPTION, null, "date=? and user=?",
                        new String[]{mTvDate.getText().toString(), AccountBookApplication.getUserInfo().getUserName()},
                        null, null, null);
                Message msg = mHandler.obtainMessage();
                msg.what=0;
                //msg.obj=0;
                mHandler.sendMessage(msg);
            }
        }).start();

    }


    /************************* CursorAdapter适配器  ****************************************/
    //    (1)newView：并不是每次都被调用的，它只在实例化的时候调用,数据增加的时候也会调用,
    //        但是在重绘(比如修改条目里的TextView的内容)的时候不会被调用
    //    (2)bindView：从代码中可以看出在绘制Item之前一定会调用bindView方法它在重绘的时候也同样被调用
    //  CursorAdapter还有一个重要的方法 public void changeCursor (Cursor cursor)：
    public static class MyCursorAdapter extends CursorAdapter {

        public MyCursorAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            view = super.getView(position, convertView, parent);
            view.setBackgroundResource(position % 2  == 0 ? R.drawable.bg_pink : R.drawable.bg_bule);// 每隔item之间颜色不同

            return view;
        }


        /**并不是每次都被调用的，它只在实例化的时候调用,数据增加的时候也会调用,
         但是在重绘(比如修改条目里的TextView的内容)的时候不会被调用*/
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // 获得 LayoutInflater 实例的三种方式:
            // LayoutInflater inflater = getLayoutInflater();  //调用Activity的getLayoutInflater()
            // LayoutInflater localinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // LayoutInflater inflater = LayoutInflater.from(context);

            ViewHolder viewHolder= new ViewHolder();
            // 将 layout 填充成"View"
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
            View view = inflater.inflate(R.layout.table_item,parent,false); // listview中每一项的布局

            viewHolder.tvMainType = (TextView) view.findViewById(R.id.tableItem_tv_ProductName_maintype);
            viewHolder.tvType1 = (TextView) view.findViewById(R.id.tableItem_tv_ProductName_type1);
            viewHolder.tvConcreteness = (TextView) view.findViewById(R.id.tableItem_tv_ProductName_concreteness);
            viewHolder.tvUnitPrice = (TextView) view.findViewById(R.id.tableItem_tv_UnitPrice);
            viewHolder.tvNumber = (TextView) view.findViewById(R.id.tableItem_tv_Number);
            viewHolder.tvPrice = (TextView) view.findViewById(R.id.tableItem_tv_Price);
            viewHolder.pvImage = (PhotoView) view.findViewById(R.id.tableItem_pv_image);

            view.setTag(viewHolder); // 设置进去

            return view;
        }

        /**在绘制Item之前一定会调用bindView方法它在重绘的时候也同样被调用*/
        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            final ViewHolder viewHolder=(ViewHolder) view.getTag();   // 拿出来

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

            viewHolder.tvMainType.setText(mainType);
            viewHolder.tvType1.setText(type1);
            viewHolder.tvConcreteness.setText(concreteness+"-"+cursor.getString(Constant.TABLE_CONSUMPTION__id));
            viewHolder.tvUnitPrice.setText(unitPrice);
            viewHolder.tvNumber.setText(number);
            viewHolder.tvPrice.setText(price);
            viewHolder.pvImage.setImageBitmap(bitmap);


            // 把PhotoView当普通的控件把触摸功能关掉
            viewHolder.pvImage.disenable();
            viewHolder.pvImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mInfo =  viewHolder.pvImage.getInfo();
                    mPhotoView.setImageBitmap(bitmap);
                    mBg.startAnimation(in);
                    mBg.setVisibility(View.VISIBLE);
                    mParent.setVisibility(View.VISIBLE);;
                    mPhotoView.animaFrom(mInfo);
                }
            });

        }

        static class ViewHolder{
            TextView tvMainType;
            TextView tvType1;
            TextView tvConcreteness;
            TextView tvUnitPrice;
            TextView tvNumber;
            TextView tvPrice;
            PhotoView pvImage;
        }
    }



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

            if(mParent.getVisibility() == View.VISIBLE && mBg.getVisibility() == View.VISIBLE){   // 缩小、隐藏那个预览布局
                mBg.startAnimation(out);
                mPhotoView.animaTo(mInfo, new Runnable() {
                    @Override
                    public void run() {
                        mParent.setVisibility(View.GONE);
                    }
                });
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
