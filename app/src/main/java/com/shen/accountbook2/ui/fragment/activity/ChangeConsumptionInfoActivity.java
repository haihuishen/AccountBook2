package com.shen.accountbook2.ui.fragment.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.BitmapUtils.CopyFileUtils;
import com.shen.accountbook2.Utils.DateTimeFormat;
import com.shen.accountbook2.Utils.ImageFactory;
import com.shen.accountbook2.Utils.LogUtils;
import com.shen.accountbook2.Utils.SetImageUtil;
import com.shen.accountbook2.Utils.ToFormatUtil;
import com.shen.accountbook2.Utils.ToastUtil;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TableEx;
import com.shen.accountbook2.global.AccountBookApplication;
import com.shen.accountbook2.ui.view.MyMenuRecyclerView.AccounBookProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shen on 10/20 0020.
 */
public class ChangeConsumptionInfoActivity extends Activity implements View.OnClickListener{

    /** 拍照获取图片*/
    public static final int TAKE_PHOTO = 2000;
    /** 从"相册"中获取图片*/
    public static final int PIC_PHOTO = 3000;

    private Context mContext;

    TextView tvTitle;
    ImageButton btnMenu;
    ImageButton btnBack;

    private String mId;
    private String mUser;
    private String mMainType;
    private String mType1;
    private String mConcreteness;
    private String mPrice;
    private String mNumber;
    private String mUnitPrice;
    private String mImage;
    private String mDate;

    private Boolean mImageChange;

    /** 用户民称文本*/
    private TextView tvUser;
    /** 时间文本、条件文本*/
    private TextView tvTime, tvOptions;
    /** 时间选择器*/
    TimePickerView pvTime;
    /** 条件选择器*/
    OptionsPickerView pvOptions;
    View vMasker;

    /** 具体类型编辑框*/
    private EditText etConcreteness;
    /** 总价编辑框*/
    private EditText etPrice;
    /** 数量编辑框*/
    private EditText etNumber;
    /** 单价编辑框*/
    private EditText etUnitPrice;


    /** 包裹预览图片的控件*/
    private LinearLayout linearLayoutPv;
    /** 预览图片控件*/
    private PhotoView pvCamaraPhoto;
    /** 拍照按钮*/
    private Button btnCamera;
    /** 从相册拿图片按钮*/
    private Button btnPhoto;
    /** 清除按钮*/
    private Button btnClear;

    /** 修改按钮*/
    private Button btnUpdate;

    private Bitmap bitmap;

    View mParent;
    View mBg;
    /** 放大后存放图片的控件*/
    PhotoView mPhotoView;
    Info mInfo;

    AlphaAnimation in;
    AlphaAnimation out;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_consumption_info);

        mContext = getBaseContext();

        Intent intent = getIntent();
        //获取数据
        Bundle data = intent.getExtras();

        if(data != null) {
            LogUtils.i("更新项___________________id：" + data.get(Constant.TABLE_CONSUMPTION__id_STRING));
            LogUtils.i("更新项___________________user：" + data.get(Constant.TABLE_CONSUMPTION_user_STRING));
            LogUtils.i("更新项___________________mainType：" + data.get(Constant.TABLE_CONSUMPTION_maintype_STRING));
            LogUtils.i("更新项___________________type1：" + data.get(Constant.TABLE_CONSUMPTION_type1_STRING));
            LogUtils.i("更新项___________________concreteness：" + data.get(Constant.TABLE_CONSUMPTION_concreteness_STRING));
            LogUtils.i("更新项___________________unitPrice：" + data.get(Constant.TABLE_CONSUMPTION_unitprice_STRING));
            LogUtils.i("更新项___________________number：" + data.get(Constant.TABLE_CONSUMPTION_number_STRING));
            LogUtils.i("更新项___________________price：" + data.get(Constant.TABLE_CONSUMPTION_price_STRING));
            LogUtils.i("更新项___________________image：" + data.get(Constant.TABLE_CONSUMPTION_image_STRING));
            LogUtils.i("更新项___________________date：" + data.get(Constant.TABLE_CONSUMPTION_date_STRING));

            mId = data.get(Constant.TABLE_CONSUMPTION__id_STRING).toString();
            mUser = data.get(Constant.TABLE_CONSUMPTION_user_STRING).toString();
            mMainType = data.get(Constant.TABLE_CONSUMPTION_maintype_STRING).toString();
            mType1 = data.get(Constant.TABLE_CONSUMPTION_type1_STRING).toString();
            mConcreteness = data.get(Constant.TABLE_CONSUMPTION_concreteness_STRING).toString();
            mPrice = data.get(Constant.TABLE_CONSUMPTION_price_STRING).toString();
            mNumber = data.get(Constant.TABLE_CONSUMPTION_number_STRING).toString();
            mUnitPrice = data.get(Constant.TABLE_CONSUMPTION_unitprice_STRING).toString();
            mImage = data.get(Constant.TABLE_CONSUMPTION_image_STRING).toString();
            mDate = data.get(Constant.TABLE_CONSUMPTION_date_STRING).toString();
        }


        initUI();
        initListener();
        initData();

    }

    private void initUI(){

        tvTitle = (TextView) findViewById(R.id.tv_title);
        btnMenu = (ImageButton) findViewById(R.id.btn_menu);
        btnBack = (ImageButton) findViewById(R.id.btn_back);

        vMasker=findViewById(R.id.v_Masker);
        tvUser=(TextView) findViewById(R.id.tv_user);
        tvTime=(TextView) findViewById(R.id.tv_Time);
        tvOptions=(TextView) findViewById(R.id.tv_Options);

        etConcreteness = (EditText) findViewById(R.id.et_concreteness);
        etPrice = (EditText) findViewById(R.id.et_price);
        etNumber = (EditText) findViewById(R.id.et_number);
        etUnitPrice = (EditText) findViewById(R.id.et_unitPrice);


        linearLayoutPv = (LinearLayout) findViewById(R.id.linearLayout_pv);
        pvCamaraPhoto = (PhotoView) findViewById(R.id.pv_image);
        btnCamera = (Button) findViewById(R.id.btn_camera);
        btnPhoto = (Button) findViewById(R.id.btn_photo);
        btnClear = (Button) findViewById(R.id.btn_clear);

        btnUpdate = (Button) findViewById(R.id.btn_add);

        mParent = findViewById(R.id.parent);
        mBg = findViewById(R.id.bg);
        mPhotoView = (PhotoView) findViewById(R.id.img);

    }


    private void initListener(){

        btnBack.setOnClickListener(this);

        tvTime.setOnClickListener(this);
        tvOptions.setOnClickListener(this);

        // 编辑框改变时，响应的事件
        etUnitPrice.addTextChangedListener(unitPriceWatcher);
        etNumber.addTextChangedListener(numberWatcher);

        btnCamera.setOnClickListener(this);
        btnPhoto.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

        mPhotoView.setOnClickListener(this);
        linearLayoutPv.setOnClickListener(this);

    }

    private void initData(){

        btnMenu.setVisibility(View.GONE);
        btnBack.setVisibility(View.VISIBLE);

        tvTitle.setText("修改消费信息");
        btnUpdate.setText("修改");
        /**************************   为控件添加，传过来的数据 ***************************************/
        etPrice.setEnabled(false);          // 总价，不可编辑

        tvUser.setText("用户:"+mUser);
        tvTime.setText(mDate);
        tvOptions.setText(mMainType+"-"+mType1);
        etConcreteness.setText(mConcreteness);
        etPrice.setText(mPrice);
        etNumber.setText(mNumber);
        etUnitPrice.setText(mUnitPrice);

        /******************************************************************************/
        //时间选择器
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        //控制时间范围
        // Calendar calendar = Calendar.getInstance();
        // pvTime.setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR));//要在setTime 之前才有效果哦
        pvTime.setTitle("消费时间");         // 设置"标题"
        pvTime.setTime(new Date());         // 设置当前的时间，到时间选择器
        pvTime.setCyclic(false);
        pvTime.setCancelable(false);         // true:点击弹出"布局"外部，收回"布局";false:没反应
        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                tvTime.setText(DateTimeFormat.getTime(date,"yyyy-MM-dd"));
            }
        });

        //选项选择器
        pvOptions = new OptionsPickerView(this);
        pvOptions.setCancelable(false);     // true:点击弹出"布局"外部，收回"布局";false:没反应
        //三级联动效果
        pvOptions.setPicker(AccountBookApplication.getMainTypeList(),
                AccountBookApplication.getListType1List(), null, true);
        //设置选择的三级单位
        // pwOptions.setLabels("主类型", "次类型", "次次类型");
        pvOptions.setTitle("消费类型");
        pvOptions.setCyclic(false, false, true);         // 三级联动，哪个可以循环滚动

        //设置默认选中的三级项目
        //监听确定选择按钮
        pvOptions.setSelectOptions(0, 0, 0);
        //选项选择器后回调
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                String tx = AccountBookApplication.getMainTypeList().get(options1)
                        +"-"+
                        AccountBookApplication.getListType1List().get(options1).get(option2);
                //     + options3Items.get(options1).get(option2).get(options3).getPickerViewText();
                tvOptions.setText(tx);
                vMasker.setVisibility(View.GONE);
            }
        });
        /******************************************************************************/



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

        mImageChange = false;
        if(!TextUtils.isEmpty(mImage)) {
            bitmap = ImageFactory.getBitmap(Constant.IMAGE_PATH + AccountBookApplication.getUserInfo().getUserName() + File.separator + mImage);
        }else {
            bitmap = ImageFactory.getBitmap(Constant.CACHE_IMAGE_PATH + "no_preview_picture.png");
        }

        pvCamaraPhoto.disenable();// 把PhotoView当普通的控件，把触摸功能关掉
        pvCamaraPhoto.setImageBitmap(bitmap);

        mPhotoView.setImageBitmap(bitmap);
        mPhotoView.enable();
    }


    /*********************** 单价编辑框文本改变监听 ***********************/
    private TextWatcher unitPriceWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {

        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(etUnitPrice.isFocused()){

                if (etUnitPrice.getText().toString().equals(".")) {                         // 单价编辑框，首位不能为"."
                    etUnitPrice.setText("");
                    etUnitPrice.setSelection(etUnitPrice.getText().toString().length());//将光标移至文字末尾
                } else {
                    if (!TextUtils.isEmpty(etUnitPrice.getText().toString())) {                // 单价编辑框，不为空 ; 空：不处理
                        if (!TextUtils.isEmpty(etNumber.getText().toString())) {               // 数量编辑框，不为空 ; 空：不处理

                            int number = Integer.valueOf(etNumber.getText().toString());
                            float unitPrice = Float.valueOf(etUnitPrice.getText().toString());

                            // etUnitPrice.setText(ToFormatUtil.toDecimalFormat(unitPrice, 2));
                            etPrice.setText(ToFormatUtil.toDecimalFormat(number * unitPrice, 2));
                        } else
                            etPrice.setText("0");
                    } else
                        etPrice.setText("0");
                }
            }
        }
    };


    /*********************** 数量编辑框文本改变监听 ***********************/
    private TextWatcher numberWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {

        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(etNumber.isFocused()) {
                if (!TextUtils.isEmpty(etNumber.getText().toString())) {                // 单价编辑框，不为空 ; 空：不处理
                    if (!TextUtils.isEmpty(etUnitPrice.getText().toString())) {               // 数量编辑框，不为空 ; 空：不处理

                        int number = Integer.valueOf(etNumber.getText().toString());
                        float unitPrice = Float.valueOf(etUnitPrice.getText().toString());

                        // etNumber.setText(number + "");
                        etPrice.setText(ToFormatUtil.toDecimalFormat(number * unitPrice, 2));
                        LogUtils.i("TAG", "unitPrice: " + unitPrice + "number: " + number);
                    } else
                        etPrice.setText("0");
                } else
                    etPrice.setText("0");
            }
        }
    };


    /**
     * 点击"修改按钮"<p>
     * 将这次消费添加到数据库
     */
    private void update(){
        // 根据当前的时间，组合成"照片名称"
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        String currentTime = sDateFormat.format(new Date());
        String imageName = AccountBookApplication.getUserInfo().getUserName() +"_"+ currentTime+".jpg";

        // 根据全局变量，添加时是否将图片添加到数据库(这个只是"图片名")
        // true:压缩图片保存在指定位置
        if(mImageChange) {  // 换了照片才压缩
            try {
                ImageFactory.ratioAndGenThumb(Constant.CACHE_IMAGE_PATH + "CacheImage.jpg",
                        Constant.IMAGE_PATH + AccountBookApplication.getUserInfo().getUserName() + File.separator + imageName,
                        300, 300, false);

                // 删除对应的图片!
                if(!TextUtils.isEmpty(mImage)){
                    File f = new File(Constant.IMAGE_PATH+AccountBookApplication.getUserInfo().getUserName(), mImage);
                    if(f.exists())
                        f.delete();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            imageName = mImage;   // 空不要写成 ""  ，不要写成 "null" 要写成String imageName = null
        }

        // 数据库
        TableEx consumptionEx = new TableEx(mContext);

        String id = null;   // 空不要写成 ""  ，不要写成 "null" 要写成String id = null;  主键可以不写
        String maintype = null;
        String type1 = null;
        String concreteness = null;
        float price = 0;
        int number = 0;
        float unitPrice = 0;
        String date = null;

        maintype = tvOptions.getText().toString().split("-")[0];
        type1 = tvOptions.getText().toString().split("-")[1];

        concreteness = etConcreteness.getText().toString();             // 得到的是 "" ，不会是 null
        date = tvTime.getText().toString();                                         // 得到的是 "" ，不会是 null


        if(!etUnitPrice.getText().toString().isEmpty()){
            unitPrice = Float.valueOf(etUnitPrice.getText().toString());
        }else{
            unitPrice = 0;
        }
        if(!etNumber.getText().toString().isEmpty()){
            number = Integer.valueOf(etNumber.getText().toString());
        }else{
            number = 0;
        }

        if(!etPrice.getText().toString().isEmpty()){
            price = Float.valueOf(etPrice.getText().toString());
            if(!etUnitPrice.getText().toString().isEmpty() && !etNumber.getText().toString().isEmpty()){
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
        //   values.put("user", AccountBookApplication.getUserInfo().getUserName());
        values.put("maintype", maintype);                        // 字段  ： 值
        values.put("type1", type1);
        values.put("concreteness", concreteness);
        values.put("price", ToFormatUtil.toDecimalFormat(price, 2));
        values.put("number", number);
        values.put("unitPrice", ToFormatUtil.toDecimalFormat(unitPrice, 2));
        values.put("image", imageName);
        values.put("date", date);   // 这里只要填写 YYYY-MM-DD  ，不用填date(2016-09-12 00:00:00) 这么麻烦

        int num = getContentResolver().update(AccounBookProvider.URI_ACCOUNTBOOK2_ALL, values, "_id=? and user=?", new String[]{mId, mUser});
        if(num == 1) {
            ToastUtil.show("修改成功");
            finish();
        }
        else
            ToastUtil.show("修改失败");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            mImageChange = true;        // 拍照返回，设置这个标志为true
            bitmap = ImageFactory.ratio(Constant.CACHE_IMAGE_PATH +"CacheImage.jpg", 300, 300);
            pvCamaraPhoto.setImageBitmap(bitmap);// 将图片显示在ImageView里
        }

        if(requestCode == TAKE_PHOTO){                       // 拍照获取图片
            if (resultCode == Activity.RESULT_OK) {
                mImageChange = true;        // 拍照返回，设置这个标志为true
                bitmap = ImageFactory.ratio(Constant.CACHE_IMAGE_PATH +"CacheImage.jpg", 300, 300);
                pvCamaraPhoto.setImageBitmap(bitmap);// 将图片显示在ImageView里
            }
        }else if(requestCode == PIC_PHOTO){                 // 从相册获取图片
            if (data == null) {
                return;
            } else {
                Uri uri = data.getData();
                if (uri != null) {
                    String path = SetImageUtil.getPath(this, uri);       // 从"相册"中获取"图片"的路径要解析的

                    // 将"相册"的图片，复制到，缓存目录中
                    CopyFileUtils.copyFile(path , Constant.CACHE_IMAGE_PATH_CacheImage);
                }

                mImageChange = true;        // 拍照返回，设置这个标志为true
                bitmap = ImageFactory.ratio(Constant.CACHE_IMAGE_PATH_CacheImage, 300, 300);
                pvCamaraPhoto.setImageBitmap(bitmap);// 将图片显示在ImageView里
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:                                         // 退出本Activity
                finish();
                break;

            case R.id.tv_Time:                                          //弹出时间选择器
                pvTime.show();
                break;

            case R.id.tv_Options:                                       //点击弹出选项选择器
                pvOptions.show();
                break;

            case R.id.btn_camera:                                       // 点击"拍照按钮"，跳到"拍照界面"
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Constant.CACHE_IMAGE_PATH ,"CacheImage.jpg");  // 携带图片存放路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, TAKE_PHOTO);
                break;

            case R.id.btn_photo:                                       // 点击"相册按钮"，跳到"相册界面"
                Intent intentss = new Intent(Intent.ACTION_PICK);
                intentss.setType("image/*");
                startActivityForResult(intentss, PIC_PHOTO);
                break;

            case R.id.btn_clear:                                       // 清除预览控件的图片;为默认图片
                bitmap = ImageFactory.getBitmap(Constant.CACHE_IMAGE_PATH +"no_preview_picture.png");
                mImage = "";                                        // 将传过来的图片名置为空
                pvCamaraPhoto.setImageBitmap(bitmap);              // 将图片显示在ImageView里
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
                mInfo = pvCamaraPhoto.getInfo();                   // 拿到pv_camaraPhoto的信息(如：位置)，用于动画

                mPhotoView.setImageBitmap(bitmap);
                mBg.startAnimation(in);             // 执行动画
                mBg.setVisibility(View.VISIBLE);
                mParent.setVisibility(View.VISIBLE);
                mPhotoView.animaFrom(mInfo);
                ToastUtil.show("点击了预览图片");
                break;

            case R.id.btn_add:                                              // 点击"添加"
                if(AccountBookApplication.isLogin()){
                    if(AccountBookApplication.getUserInfo() != null){
                        if(!TextUtils.isEmpty(tvTime.getText().toString()) || !TextUtils.isEmpty(tvOptions.getText().toString())){
                            if(!TextUtils.isEmpty(etPrice.getText().toString()) &&
                                    ((!TextUtils.isEmpty(etUnitPrice.getText().toString()))&&(!TextUtils.isEmpty(etNumber.getText().toString())))){
                                update();
                            }else{
                                ToastUtil.show("＇单价、数量＇不能为空");
                            }
                        }else{
                            ToastUtil.show("请选择消费时间、类型");
                        }
                    }else{
                        AccountBookApplication.setIsLogin(false);
                        AccountBookApplication.setUserInfo(null);
                    }
                }else
                    ToastUtil.show("请登陆后再修改!!!");
                break;

            default:
                break;
        }
    }

    // 按钮监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {                 // 如果点击的是"返回按钮"
            if(pvOptions.isShowing()||pvTime.isShowing()){
                pvOptions.dismiss();
                pvTime.dismiss();
                return true;
            }
            if(pvTime.isShowing()){
                pvTime.dismiss();
                return true;
            }

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
