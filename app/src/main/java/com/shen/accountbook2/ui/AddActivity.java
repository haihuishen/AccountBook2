package com.shen.accountbook2.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.ImageFactory;
import com.shen.accountbook2.Utils.SharePrefUtil;
import com.shen.accountbook2.Utils.ToFormatUtil;
import com.shen.accountbook2.Utils.ToastUtil;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TableEx;
import com.shen.accountbook2.domain.ConsumptionMainType;
import com.shen.accountbook2.domain.ConsumptionType1;
import com.shen.accountbook2.domain.TypeManage;
import com.shen.accountbook2.domain.TypeModelManage;
import com.shen.accountbook2.global.AccountBookApplication;
import com.shen.accountbook2.widget.OnWheelChangedListener;
import com.shen.accountbook2.widget.WheelView;
import com.shen.accountbook2.widget.adapters.ArrayWheelAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by shen on 10/20 0020.
 */
public class AddActivity extends Activity implements OnWheelChangedListener, View.OnClickListener{

    private Context mContext;


    TextView tvTitle;
    ImageButton btnMenu;
    ImageButton btnBack;

    /** 时间选择控件*/
    private DatePicker datePicker;

    /** 主类型滑动选择控件*/
    private WheelView wv_maintype;
    /** 次类型滑动选择控件*/
    private WheelView wv_type1;

    /** 具体类型编辑框*/
    private EditText et_concreteness;
    /** 总价编辑框*/
    private EditText et_price;
    /** 数量编辑框*/
    private EditText et_number;
    /** 单价编辑框*/
    private EditText et_unitPrice;

    /** 包裹预览图片的控件*/
    private LinearLayout linearLayout_pv;
    /** 预览图片控件*/
    private PhotoView pv_camaraPhoto;
    /** 拍照按钮*/
    private Button btnCamera;
    /** 清除按钮*/
    private Button btnClear;

    /** 添加按钮*/
    private Button btn_add;


    private Bitmap bitmap;

    View mParent;
    View mBg;
    /** 放大后存放图片的控件*/
    PhotoView mPhotoView;
    Info mInfo;

    AlphaAnimation in;
    AlphaAnimation out;


    private TypeManage typeManage;

    private String[] s_mainType;

    private List<String> list_mainType;
    private Map<Integer,ArrayList<ConsumptionType1>> map_list_Type1;
    private TypeModelManage m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mContext = getBaseContext();

        initUI();
        initListener();
        initData();
    }


    private void initUI(){

        tvTitle = (TextView) findViewById(R.id.tv_title);
        btnMenu = (ImageButton) findViewById(R.id.btn_menu);
        btnBack = (ImageButton) findViewById(R.id.btn_back);

        datePicker = (DatePicker) findViewById(R.id.datePicker);

        wv_maintype = (WheelView) findViewById(R.id.wheel_maintype);
        wv_type1 = (WheelView) findViewById(R.id.wheel_type1);

        et_concreteness = (EditText) findViewById(R.id.et_concreteness);
        et_price = (EditText) findViewById(R.id.et_price);
        et_number = (EditText) findViewById(R.id.et_number);
        et_unitPrice = (EditText) findViewById(R.id.et_unitPrice);

        linearLayout_pv = (LinearLayout) findViewById(R.id.linearLayout_pv);
        pv_camaraPhoto = (PhotoView) findViewById(R.id.pv_image);
        btnCamera = (Button) findViewById(R.id.btn_camera);
        btnClear = (Button) findViewById(R.id.btn_clear);

        btn_add = (Button) findViewById(R.id.btn_add);


        mParent = findViewById(R.id.parent);
        mBg = findViewById(R.id.bg);
        mPhotoView = (PhotoView) findViewById(R.id.img);
    }


    private void initListener(){

        btnBack.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btn_add.setOnClickListener(this);

        mPhotoView.setOnClickListener(this);
        linearLayout_pv.setOnClickListener(this);

        // 添加change事件
        wv_maintype.addChangingListener(this);
        // 添加change事件
        wv_type1.addChangingListener(this);
    }

    private void initData(){
        btnMenu.setVisibility(View.GONE);
        btnBack.setVisibility(View.VISIBLE);

        typeManage = new TypeManage(mContext);
        list_mainType = new ArrayList<String>();

        if (!typeManage.list_MainType.isEmpty()) {
            for (ConsumptionMainType mt : typeManage.list_MainType) {
                list_mainType.add(mt.getType());
            }
            s_mainType = new String[list_mainType.size()];
            list_mainType.toArray(s_mainType);              // 将 List<String> 转换成 String[]
        } else
            ToastUtil.show("为空");

        m = new TypeModelManage(mContext);
        wv_maintype.setVisibleItems(3);
        wv_type1.setVisibleItems(3);

        wv_maintype.setViewAdapter(new ArrayWheelAdapter<String>(mContext, m.mainType()));
        wv_type1.setViewAdapter(new ArrayWheelAdapter<String>(mContext, m.type1(wv_maintype.getCurrentItem())));

        SharePrefUtil.saveBoolean(mContext, SharePrefUtil.IMAGE_KEY.IS_ADD_IMAGE, false);

        // 预览图片的动画
        in = new AlphaAnimation(0, 1);
        out = new AlphaAnimation(1, 0);

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

        bitmap = ImageFactory.getBitmap(Constant.CACHE_IMAGE_PATH + "/no_preview_picture.png");

        pv_camaraPhoto.disenable();// 把PhotoView当普通的控件，把触摸功能关掉
        pv_camaraPhoto.setImageBitmap(bitmap);

        mPhotoView.setImageBitmap(bitmap);
        mPhotoView.enable();

    }

    /**
     * 点击"添加按钮"<p>
     * 将这次消费添加到数据库
     */
    private void add(){

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
        String currentTime = sDateFormat.format(new java.util.Date());
        String imageName = currentTime+".jpg";

        Boolean saveImage = SharePrefUtil.getBoolean(mContext, SharePrefUtil.IMAGE_KEY.IS_ADD_IMAGE, false);

        System.out.println("SharePrefUtil.IMAGE_KEY.IS_ADD_IMAGE："+saveImage);
        if(saveImage) {
            try {
                ImageFactory.ratioAndGenThumb(Constant.CACHE_IMAGE_PATH + "/CacheImage.jpg", Constant.IMAGE_PATH + "/" + imageName, 200, 200, false);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            imageName = null;   // 空不要写成 ""  ，不要写成 "null" 要写成String imageName = null
        }
        TableEx consumptionEx = new TableEx(mContext);

        String id = null;   // 空不要写成 ""  ，不要写成 "null" 要写成String id = null;  主键可以不写
        String maintype = null;
        String type1 = null;
        String concreteness = null;
        float price = 0;
        int number = 0;
        float unitPrice = 0;
        String image = null;    // 空不要写成 ""  ，不要写成 "null" 要写成String image = null;
        String date = null;

        maintype = m.mainType()[wv_maintype.getCurrentItem()];
        type1 = m.type1(wv_maintype.getCurrentItem())[wv_type1.getCurrentItem()];

        concreteness = et_concreteness.getText().toString();
        date = getDatePicker();


        if(!et_unitPrice.getText().toString().isEmpty()){
            unitPrice = Float.valueOf(et_unitPrice.getText().toString());
        }else{
            unitPrice = 0;
        }
        if(!et_number.getText().toString().isEmpty()){
            number = Integer.valueOf(et_number.getText().toString());
        }else{
            number = 0;
        }
        if(!et_price.getText().toString().isEmpty()){
            price = Float.valueOf(et_price.getText().toString());
            if(!et_unitPrice.getText().toString().isEmpty() && !et_number.getText().toString().isEmpty()){
                price = unitPrice * number;
            }
        }else{
            price = unitPrice * number;
        }

        System.out.println("maintype:" + maintype+
                "type1:" + type1+
                "concreteness:" + concreteness+
                "price:" + price+
                "number:" + number+
                "unitPrice:" + unitPrice+
                "date:" + date +
                "ImageName:"+imageName
        );

        ContentValues values = new ContentValues();
        //   values.put("_id", id);                   // 主键可以不写
        values.put("user", AccountBookApplication.getUserInfo().getUserName());
        values.put("maintype", maintype);                        // 字段  ： 值
        values.put("type1", type1);
        values.put("concreteness", concreteness);
        values.put("price", ToFormatUtil.toDecimalFormat(price, 2));
        values.put("number", number);
        values.put("unitPrice", ToFormatUtil.toDecimalFormat(unitPrice, 2));
        values.put("image", imageName);
        values.put("date", date);   // 这里只要填写 YYYY-MM-DD  ，不用填date(2016-09-12 00:00:00) 这么麻烦
        consumptionEx.Add(Constant.TABLE_CONSUMPTION, values);

    }

    /**
     * 将 拿到"年月日==>""YYYY-MM-DD"<p>
     *
     * @return String YYYY-MM-DD
     */
    private String getDatePicker(){

        NumberFormat nf = NumberFormat.getInstance();   // 得到一个NumberFormat的实例
        nf.setGroupingUsed(false);                      // 设置是否使用分组
        nf.setMaximumIntegerDigits(2);                  // 设置最大整数位数
        nf.setMinimumIntegerDigits(2);                  // 设置最小整数位数

        int y = datePicker.getYear();
        int m = datePicker.getMonth()+1;
        int d = datePicker.getDayOfMonth();

        String s_date = y + "-" + nf.format(m) + "-" + nf.format(d);        // "补零"后 // YYYY-MM-DD

        return s_date;
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == wv_maintype) {
            wv_type1.setViewAdapter(new ArrayWheelAdapter<String>(mContext,m.type1(wv_maintype.getCurrentItem())));
            wv_type1.setCurrentItem(0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            SharePrefUtil.saveBoolean(mContext,SharePrefUtil.IMAGE_KEY.IS_ADD_IMAGE,true);
            bitmap = ImageFactory.ratio(Constant.CACHE_IMAGE_PATH +"/CacheImage.jpg", 200, 200);
            pv_camaraPhoto.setImageBitmap(bitmap);// 将图片显示在ImageView里
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:                                         // 退出本Activity
                finish();
                break;

            case R.id.btn_camera:                                       // 点击"拍照按钮"，跳到"拍照界面"
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Constant.CACHE_IMAGE_PATH ,"CacheImage.jpg");  // 携带图片存放路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, 1);
                break;

            case R.id.btn_clear:                                       // 清除预览控件的图片;为默认图片
                SharePrefUtil.saveBoolean(mContext,SharePrefUtil.IMAGE_KEY.IS_ADD_IMAGE,false);
                bitmap = ImageFactory.getBitmap(Constant.CACHE_IMAGE_PATH +"/no_preview_picture.png");
                pv_camaraPhoto.setImageBitmap(bitmap);              // 将图片显示在ImageView里
                mPhotoView.setImageBitmap(bitmap);
                break;

            case R.id.img:                                          // 点击"放大后的预览图片的控件"，缩小、隐藏那个预览布局
                mBg.startAnimation(out);
                mPhotoView.animaTo(mInfo, new Runnable() {
                    @Override
                    public void run() {
                        mParent.setVisibility(View.GONE);
                    }
                });
                break;
            case R.id.linearLayout_pv:                              // 点击"包裹预览图片控件的布局"，放大、那个预览布局设为可见.
                mInfo = pv_camaraPhoto.getInfo();                   // 拿到pv_camaraPhoto的信息(如：位置)，用于动画

                mPhotoView.setImageBitmap(bitmap);
                mBg.startAnimation(in);             // 执行动画
                mBg.setVisibility(View.VISIBLE);
                mParent.setVisibility(View.VISIBLE);;
                mPhotoView.animaFrom(mInfo);
                ToastUtil.show("点击了预览图片");
                break;
            case R.id.btn_add:                                              // 点击"添加"
                if(AccountBookApplication.isLogin()){
                    if(AccountBookApplication.getUserInfo() != null){
                        if(!et_price.getText().toString().isEmpty() ||
                                ((!et_unitPrice.getText().toString().isEmpty())&&(!et_number.getText().toString().isEmpty()))){
                            add();
                        }else{
                            ToastUtil.show("＇总价＇或＇单价、数量＇不能为空");
                        }
                    }else{
                        AccountBookApplication.setIsLogin(false);
                        AccountBookApplication.setUserInfo(null);
                    }
                }else
                    ToastUtil.show("请登陆后再添加!!!");
                break;

        }
    }
}
