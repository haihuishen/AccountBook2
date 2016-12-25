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
import android.os.Handler;
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
import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.GetWindowParaUtils;
import com.shen.accountbook2.Utils.ImageFactory;
import com.shen.accountbook2.Utils.LogUtils;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TableEx;
import com.shen.accountbook2.global.AccountBookApplication;
import com.shen.accountbook2.ui.view.MyMenuRecyclerView.AccounBookProvider;
import com.shen.accountbook2.ui.view.MyMenuRecyclerView.RecyclerViewCursorAdapter;
import com.shen.accountbook2.ui.view.MyMenuRecyclerView.SlidingButtonView;

import java.io.File;
import java.util.HashMap;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;


//public class ReportForD_Activity extends AppCompatActivity implements Adapter.IonSlidingViewClickListener{
public class ConsumerDetails_Activity extends Activity implements OnClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    public static final int OK = 1;

    private Context mContext;

    /******************************标题***********************************/
    private TextView tvTitle;
    private ImageButton btnMenu;
    private ImageButton btnBack;
    private ImageButton btnShared;

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

    private Handler handler = AccountBookApplication.getHandler();

    // 传递过来的内容
    private static int mCurrentState;
    public static String mCurrentTime;
    public static String mCurrentContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_details);

        mContext = this;

        Intent intent = getIntent();
        mCurrentState = intent.getExtras().getInt("type");
        mCurrentTime = intent.getExtras().getString("time");
        mCurrentContent = intent.getExtras().getString("content");

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
        btnShared = (ImageButton) findViewById(R.id.btn_shared);

        mTvAllPriceOfDay = (TextView) findViewById(R.id.tv_all_price);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        mParent = findViewById(R.id.parent);
        mBg = findViewById(R.id.bg);
        mPhotoView = (PhotoView) findViewById(R.id.img);
    }

    private void initListener(){
        // 标题
        btnBack.setOnClickListener(this);
        btnShared.setOnClickListener(this);


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

    }

    private void initDate(){
        /******************************标题***********************************/
        btnMenu.setVisibility(GONE);
        btnBack.setVisibility(VISIBLE);
        btnShared.setVisibility(GONE);
        tvTitle.setText("");

        mTableEx = new TableEx(AccountBookApplication.getContext());

        //queryPriceOfDay();
    }

    private void setAdapter(){

        String time = mCurrentTime;
        String content = mCurrentContent;
        switch (mCurrentState){
            case ReportForMixture_Activity.DATE_YEAR:
                time = time.replace("%","")  + content.replace("月","") + "-%";
                LogUtils.i("time:" + time);
                mCursor = getContentResolver().query(AccounBookProvider.URI_ACCOUNTBOOK2_ALL, null, "date like ? and user=?",
                        new String[]{time, AccountBookApplication.getUserInfo().getUserName()}, "date");
                break;
            case ReportForMixture_Activity.DATE_YEARMONTH:
                time = time.replace("%","")  + content.replace("日","");
                LogUtils.i("time:" + time);
                mCursor = getContentResolver().query(AccounBookProvider.URI_ACCOUNTBOOK2_ALL, null, "date like ? and user=?",
                        new String[]{time, AccountBookApplication.getUserInfo().getUserName()}, "date");
                break;
            case ReportForMixture_Activity.DATE_MAINTYPE:
                LogUtils.i("time:" + time);
                LogUtils.i("content:" + content);
                mCursor = getContentResolver().query(AccounBookProvider.URI_ACCOUNTBOOK2_ALL, null, "date like ? and user=? and maintype=?",
                        new String[]{time, AccountBookApplication.getUserInfo().getUserName(), content}, "date");
                break;
            case ReportForMixture_Activity.DATE_TYPE1:
                LogUtils.i("time:" + time);
                LogUtils.i("content:" + content);
                mCursor = getContentResolver().query(AccounBookProvider.URI_ACCOUNTBOOK2_ALL, null, "date like ? and user=? and maintype=? and type1=?",
                        new String[]{time, AccountBookApplication.getUserInfo().getUserName(), content.split("-")[0], content.split("-")[1]}, "date");
                break;
        }

        MyRecyclerViewCursorAdapter.IonSlidingViewClickListener ionSlidingViewClickListener = new MyRecyclerViewCursorAdapter.IonSlidingViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LogUtils.i("点击项："+position);
            }

            @Override
            public void onDeleteBtnCilck(View view, int position,String id, String image) {
                LogUtils.i("删除项："+position);
                LogUtils.i("删除项___________________id："+id);

                int i = getContentResolver().delete(AccounBookProvider.URI_ACCOUNTBOOK2_ALL,"_id=? and user=?",
                        new String[]{id, AccountBookApplication.getUserInfo().getUserName()});
                // 删除对应的图片!
                if(i > 0 && !TextUtils.isEmpty(image)){
                    File f = new File(Constant.IMAGE_PATH+AccountBookApplication.getUserInfo().getUserName(), image);
                    if(f.exists())
                        f.delete();
                }

                LogUtils.i("删除项___________________i："+i);
            }

            @Override
            public void onUpdateBtnCilck(HashMap hashMapItem, int position) {
                LogUtils.i("更新项position："+position);
                LogUtils.i("更新项___________________id："+ hashMapItem.get(Constant.TABLE_CONSUMPTION__id_STRING));
                LogUtils.i("更新项___________________user："+ hashMapItem.get(Constant.TABLE_CONSUMPTION_user_STRING));
                LogUtils.i("更新项___________________mainType："+ hashMapItem.get(Constant.TABLE_CONSUMPTION_maintype_STRING));
                LogUtils.i("更新项___________________type1："+ hashMapItem.get(Constant.TABLE_CONSUMPTION_type1_STRING));
                LogUtils.i("更新项___________________concreteness："+ hashMapItem.get(Constant.TABLE_CONSUMPTION_concreteness_STRING));
                LogUtils.i("更新项___________________unitPrice："+ hashMapItem.get(Constant.TABLE_CONSUMPTION_unitprice_STRING));
                LogUtils.i("更新项___________________number："+ hashMapItem.get(Constant.TABLE_CONSUMPTION_number_STRING));
                LogUtils.i("更新项___________________price："+ hashMapItem.get(Constant.TABLE_CONSUMPTION_price_STRING));
                LogUtils.i("更新项___________________image："+ hashMapItem.get(Constant.TABLE_CONSUMPTION_image_STRING));
                LogUtils.i("更新项___________________date："+ hashMapItem.get(Constant.TABLE_CONSUMPTION_date_STRING));

                Intent intent = new Intent(ConsumerDetails_Activity.this, ChangeConsumptionInfoActivity.class);
                Bundle bundle = new Bundle();

                bundle.putString(Constant.TABLE_CONSUMPTION__id_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION__id_STRING).toString());
                bundle.putString(Constant.TABLE_CONSUMPTION_user_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION_user_STRING).toString());
                bundle.putString(Constant.TABLE_CONSUMPTION_maintype_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION_maintype_STRING).toString());
                bundle.putString(Constant.TABLE_CONSUMPTION_type1_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION_type1_STRING).toString());
                bundle.putString(Constant.TABLE_CONSUMPTION_concreteness_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION_concreteness_STRING).toString());
                bundle.putString(Constant.TABLE_CONSUMPTION_unitprice_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION_unitprice_STRING).toString());
                bundle.putString(Constant.TABLE_CONSUMPTION_number_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION_number_STRING).toString());
                bundle.putString(Constant.TABLE_CONSUMPTION_price_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION_price_STRING).toString());
                bundle.putString(Constant.TABLE_CONSUMPTION_image_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION_image_STRING).toString());
                bundle.putString(Constant.TABLE_CONSUMPTION_date_STRING, hashMapItem.get(Constant.TABLE_CONSUMPTION_date_STRING).toString());

                intent.putExtras(bundle);

                startActivity(intent);

            }

        };

        myRecyclerViewCursorAdapter = new MyRecyclerViewCursorAdapter(this, mCursor,1,ionSlidingViewClickListener);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));      // 不改，默认是"纵向"
        mRecyclerView.setAdapter(myRecyclerViewCursorAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());           // 默认的"项"动作的"动画"

    }

    /**
     * 在我们使用CurSorLoader时大家都会考虑一种情况的处理—–当数据库发生变化时如何自动刷新当前UI，
     * 数据库在数据改变时通过ContentPorvider和ContentResolver发出通知，
     * 接着ContentProvider通知Cursor的观察者数据发生了变化，
     * 然后Cursor通知CursorLoader的观察者数据发生了变化，
     * 然后CursorLoader通过ContentProvider加载新数据，
     * 完事调用CursorAdapter的changeCursor()用新数据替换旧数据显示。
     */
    private void initLoader() {
        getLoaderManager().initLoader(1, null,this);
    }

    /**
     * 查询"当日消费"
     */
    private void queryPriceOfDay(){

        String time = mCurrentTime;
        String content = mCurrentContent;
        String mycontent = "";

        switch (mCurrentState){
            case ReportForMixture_Activity.DATE_YEAR:
                time = time.replace("%","")  + content.replace("月","") + "-%";
                // cast(sum(asset) as TEXT)--> 这样就不会变成"科学计数法"
                // sum(asset) -->asset 就算是 varchar(20),不是decimal(18,2)，使用sum(asset)后还是"会使用科学计数法"
                mAllCursor = mTableEx.Query(Constant.TABLE_CONSUMPTION, new String[]{"cast(sum(price) as TEXT)"},
                        "date like ? and user=?", new String[]{time, AccountBookApplication.getUserInfo().getUserName()},
                        null, null, "date");

                mycontent = time.replace("-%","")  + "月";
                tvTitle.setText(mycontent);
                try {
                    if (mAllCursor.getCount() != 0) {        // 查询：带"函数"字段，就算"没记录"，返回的也是"1"
                        mAllCursor.moveToFirst();
                        if(mAllCursor.getString(0) != null){        // 没记录，返回1，这里返回的是 "null"
                            mTvAllPriceOfDay.setTextColor(this.getResources().getColor(R.color.red));
                            mTvAllPriceOfDay.setText("消费: " + mAllCursor.getString(0));
                        }else{
                            mTvAllPriceOfDay.setTextColor(this.getResources().getColor(R.color.forestgreen));
                            mTvAllPriceOfDay.setText("消费: 0");
                        }
                    }else{
                        mTvAllPriceOfDay.setTextColor(this.getResources().getColor(R.color.forestgreen));
                        mTvAllPriceOfDay.setText("消费: 0");
                    }
                }catch (Exception e){
                    System.out.println("消费error:"+e.getMessage());
                }
                break;
            case ReportForMixture_Activity.DATE_YEARMONTH:
                time = time.replace("%","")  + content.replace("日","");
                mAllCursor = mTableEx.Query(Constant.TABLE_CONSUMPTION, new String[]{"cast(sum(price) as TEXT)"},
                        "date like ? and user=?", new String[]{time, AccountBookApplication.getUserInfo().getUserName()},
                        null, null, "date");

                mycontent = time.replace("-%","")  + "日";
                tvTitle.setText(mycontent);
                try {
                    if (mAllCursor.getCount() != 0) {        // 查询：带"函数"字段，就算"没记录"，返回的也是"1"
                        mAllCursor.moveToFirst();
                        if(mAllCursor.getString(0) != null){        // 没记录，返回1，这里返回的是 "null"
                            mTvAllPriceOfDay.setTextColor(this.getResources().getColor(R.color.red));
                            mTvAllPriceOfDay.setText("消费: " + mAllCursor.getString(0));
                        }else{
                            mTvAllPriceOfDay.setTextColor(this.getResources().getColor(R.color.forestgreen));
                            mTvAllPriceOfDay.setText("消费: 0");
                        }
                    }else{
                        mTvAllPriceOfDay.setTextColor(this.getResources().getColor(R.color.forestgreen));
                        mTvAllPriceOfDay.setText("消费: 0");
                    }
                }catch (Exception e){
                    System.out.println("消费error:"+e.getMessage());
                }
                break;
            case ReportForMixture_Activity.DATE_MAINTYPE:
                mAllCursor = mTableEx.Query(Constant.TABLE_CONSUMPTION, new String[]{"cast(sum(price) as TEXT)"},
                        "date like ? and user=? and maintype=?", new String[]{time, AccountBookApplication.getUserInfo().getUserName(), content},
                        null, null, "date");

                mycontent = time.replace("%","") + content;
                tvTitle.setText(mycontent);
                try {
                    if (mAllCursor.getCount() != 0) {        // 查询：带"函数"字段，就算"没记录"，返回的也是"1"
                        mAllCursor.moveToFirst();
                        if(mAllCursor.getString(0) != null){        // 没记录，返回1，这里返回的是 "null"
                            mTvAllPriceOfDay.setTextColor(this.getResources().getColor(R.color.red));
                            mTvAllPriceOfDay.setText("消费: " + mAllCursor.getString(0));
                        }else{
                            mTvAllPriceOfDay.setTextColor(this.getResources().getColor(R.color.forestgreen));
                            mTvAllPriceOfDay.setText("消费: 0");
                        }
                    }else{
                        mTvAllPriceOfDay.setTextColor(this.getResources().getColor(R.color.forestgreen));
                        mTvAllPriceOfDay.setText("消费: 0");
                    }
                }catch (Exception e){
                    System.out.println("消费error:"+e.getMessage());
                }
                break;
            case ReportForMixture_Activity.DATE_TYPE1:
                mAllCursor = mTableEx.Query(Constant.TABLE_CONSUMPTION, new String[]{"cast(sum(price) as TEXT)"},
                        "date like ? and user=? and maintype=? and type1=?", new String[]{time, AccountBookApplication.getUserInfo().getUserName(),
                                content.split("-")[0], content.split("-")[1]}, null, null, "date");

                mycontent = time.replace("%","")  + content;
                tvTitle.setText(mycontent);
                try {
                    if (mAllCursor.getCount() != 0) {        // 查询：带"函数"字段，就算"没记录"，返回的也是"1"
                        mAllCursor.moveToFirst();
                        if(mAllCursor.getString(0) != null){        // 没记录，返回1，这里返回的是 "null"
                            mTvAllPriceOfDay.setTextColor(this.getResources().getColor(R.color.red));
                            mTvAllPriceOfDay.setText("消费: " + mAllCursor.getString(0));
                        }else{
                            mTvAllPriceOfDay.setTextColor(this.getResources().getColor(R.color.forestgreen));
                            mTvAllPriceOfDay.setText("消费: 0");
                        }
                    }else{
                        mTvAllPriceOfDay.setTextColor(this.getResources().getColor(R.color.forestgreen));
                        mTvAllPriceOfDay.setText("消费: 0");
                    }
                }catch (Exception e){
                    System.out.println("消费error:"+e.getMessage());
                }
                break;
        }
    }


    /*************************************   Loader      ***********************************************/
    // LoaderManager.LoaderCallbacks<Cursor>  接口要实现的
    // 是 getLoaderManager().initLoader(1, null,this); 的第三参数
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        LogUtils.i("调用___________________Loader");

        CursorLoader loader = null;

        String time = mCurrentTime;
        String content = mCurrentContent;
        switch (mCurrentState){
            case ReportForMixture_Activity.DATE_YEAR:
                time = time.replace("%","")  + content.replace("月","") + "-%";
                loader = new CursorLoader(ConsumerDetails_Activity.this, AccounBookProvider.URI_ACCOUNTBOOK2_ALL, null, "date like ? and user=?",
                        new String[]{time, AccountBookApplication.getUserInfo().getUserName()}, "date");
                break;
            case ReportForMixture_Activity.DATE_YEARMONTH:
                time = time.replace("%","")  + content.replace("日","");
                loader = new CursorLoader(ConsumerDetails_Activity.this, AccounBookProvider.URI_ACCOUNTBOOK2_ALL, null, "date like ? and user=?",
                        new String[]{time, AccountBookApplication.getUserInfo().getUserName()}, "date");
                break;
            case ReportForMixture_Activity.DATE_MAINTYPE:
                loader = new CursorLoader(ConsumerDetails_Activity.this, AccounBookProvider.URI_ACCOUNTBOOK2_ALL, null, "date like ? and user=? and maintype=?",
                        new String[]{time, AccountBookApplication.getUserInfo().getUserName(), content}, "date");
                break;
            case ReportForMixture_Activity.DATE_TYPE1:
                loader = new CursorLoader(ConsumerDetails_Activity.this, AccounBookProvider.URI_ACCOUNTBOOK2_ALL, null, "date like ? and user=? and maintype=? and type1=?",
                        new String[]{mCurrentTime, AccountBookApplication.getUserInfo().getUserName(), content.split("-")[0], content.split("-")[1]}, "date");
                break;
            default:
                loader = new CursorLoader(ConsumerDetails_Activity.this, AccounBookProvider.URI_ACCOUNTBOOK2_ALL, null, "date like ? and user=?",
                        new String[]{mCurrentTime, AccountBookApplication.getUserInfo().getUserName()}, "date");
                break;
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        LogUtils.i("调用___________________onLoadFinished");
        myRecyclerViewCursorAdapter.swapCursor(data);
        queryPriceOfDay();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        LogUtils.i("调用___________________onLoaderReset");
        myRecyclerViewCursorAdapter.swapCursor(null);
    }



    /************************************************************************************/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:                                         // 退出本Activity

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("type", mCurrentState);
                bundle.putString("time", mCurrentTime);
                intent.putExtras(bundle);
                setResult(OK, intent);
                finish();
                break;
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

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("type", mCurrentState);
            bundle.putString("time", mCurrentTime);
            intent.putExtras(bundle);
            setResult(OK, intent);
            finish();

        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

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
            final String image = cursor.getString(Constant.TABLE_CONSUMPTION_image);
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

//            LogUtils.i("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//            LogUtils.i("cursor.getCount():" + cursor.getCount());
//            LogUtils.i(
//                                "_id:" + _id +"\n"+
//                                "maintype:" + mainType+"\n"+
//                                "type1:" + type1+"\n"+
//                                "concreteness:" + concreteness+"\n"+
//                                "price:" + price+"\n"+
//                                "number:" + number+"\n"+
//                                "unitPrice:" + unitPrice+"\n"+
//                                "date:" + date+"\n"+
//                                "imageName:" + image
//                        );
//            Log.i("shenshenshenshen","这张图片："+ Constant.IMAGE_PATH+"/"+image);

            final Bitmap bitmap;
            if(!TextUtils.isEmpty(image)) {
                if (new File(Constant.IMAGE_PATH + AccountBookApplication.getUserInfo().getUserName(), image).exists())     // 有这个文件，才生成位图
                    bitmap = ImageFactory.getBitmap(Constant.IMAGE_PATH + AccountBookApplication.getUserInfo().getUserName() + File.separator + image);
                else
                    bitmap = ImageFactory.getBitmap(Constant.CACHE_IMAGE_PATH + "no_preview_picture.png");
            }else{
                bitmap = ImageFactory.getBitmap(Constant.CACHE_IMAGE_PATH + "no_preview_picture.png");
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

            /*******************************************************************************************/
            if(holder.getLayoutPosition()>0){
                //获取上一个item的 时间
                Cursor myCursor = cursor;
                String previousDate = "";
                if(myCursor.moveToPosition(holder.getLayoutPosition() - 1)){
                    previousDate = myCursor.getString(Constant.TABLE_CONSUMPTION_date);
                }

//                LogUtils.i("holder.getLayoutPosition() - 1:" + (holder.getLayoutPosition() - 1));
//                LogUtils.i("newDate:" + date);
//                LogUtils.i("previousDate:" + previousDate);

                //拿当前的item的时间和上一个item的时间比较
                if(date.equals(previousDate)){
                    //说明item的时间相同，需要隐藏当前item的时间
                    holder.tvTopDay.setVisibility(View.GONE);
                    LogUtils.i("if:");
                }else {
                    // 不一样，需要显示当前的item的时间
                    // 由于布局是复用的，
                    // ***所以在需要显示的时候，再次将item的时间设置为可见
                    holder.tvTopDay.setVisibility(View.VISIBLE);
                    holder.tvTopDay.setText(date);
                    LogUtils.i("else:");
                }
            }else {			// 第1个必须显示
                holder.tvTopDay.setVisibility(View.VISIBLE);
                holder.tvTopDay.setText(date);
            }
            /*******************************************************************************************/

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
            // 每隔item之间颜色不同
            holder.layoutContent.setBackgroundResource((holder.getLayoutPosition()) % 2 == 0 ? R.drawable.bg_pink : R.drawable.bg_bule);

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
                    mIonSlidingViewClickListener.onDeleteBtnCilck(v, position, _id, image);
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
            View v=inflater.inflate(R.layout.item_list,parent,false);         // "项布局"
            return new MyViewHolder(v);
        }

        /**
         * 项布局里面的控件
         */
        class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView tvTopDay;

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

                tvTopDay = (TextView) itemView.findViewById(R.id.tv_top_day);
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
            notifyItemRemoved(position);                    // 项
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
             * @param image             表的image字段
             */
            void onDeleteBtnCilck(View view, int position, String id, String image);

            /**
             *  控件(项被点击)点击事件(菜单中：更新)，子类实现
             * @param hashMapItem             项中的所有内容
             * @param position          项的索引
             */
            void onUpdateBtnCilck(HashMap hashMapItem, int position);
        }
    }

}
