package com.shen.accountbook2.ui.fragment.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.DateTimeFormat;
import com.shen.accountbook2.Utils.SharePrefUtil;
import com.shen.accountbook2.Utils.ToFormatUtil;
import com.shen.accountbook2.Utils.ToastUtil;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TableEx;
import com.shen.accountbook2.global.AccountBookApplication;

import java.util.Date;


/**
 * Created by shen on 10/14 0014.
 */
public class ReportForMixture_Activity extends BaseReportActivity implements PopupWindow.OnDismissListener {

    public static final int DATE_YEAR = 1;
    public static final int DATE_YEARMONTH = 2;
    public static final int DATE_MAINTYPE = 3;
    public static final int DATE_TYPE1 = 4;

    public static final int REQ_CODE = 1;               // 请求码 到ConsumerDetalis_Activity

    // 传递过去的内容
    private int mCurrentState;
    private String mCurrentTime;
    private String mCurrentContent;

    private Context mContext;
    private TextView mTvChoiceYM;

    //popwindow
    RelativeLayout mRlPopNull;
    TextView mTvPopCancel;
    TextView mTvPopChoiceYear;
    TextView mTvPopChoiceYearMonth;

    PopupWindow pop;                // 选择"年份"还是"年月"

    /** "年份"选择器*/
    TimePickerView pvTimeYear;
    /** "年月"选择器*/
    TimePickerView pvTimeYearMonth;

    private ListView lvDay;

    public CheckBox mCbType;           // 主类型/次类型
    public TextView mTvType;

    private Button btnQuery;            // 查询

    private int type;                   // 选择了 "类型"，或"日期"
    private TextView tvAllPriceName;    // 本月/今年
    private TextView tvAllPrice;        // 本月/今年总消费

    Cursor cursor;

    private static float progressMax = 1;            // 最大值

    public ReportForMixture_Activity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_report_mixture);
        super.initView();

        mTvChoiceYM = (TextView) findViewById(R.id.tv_choice_m_y);

        lvDay = (ListView) findViewById(R.id.lv);

        mCbType = (CheckBox) findViewById(R.id.cb_type);
        mTvType = (TextView) findViewById(R.id.tv_type);

        btnQuery = (Button) findViewById(R.id.btn_query);

        tvAllPrice = (TextView) findViewById(R.id.tv_AllPrice);
        tvAllPriceName = (TextView) findViewById(R.id.tv_AllPriceName);
    }

    @Override
    public void initListener() {
        super.initListener();

        mTvChoiceYM.setOnClickListener(this);

        mTvType.setOnClickListener(this);
        btnQuery.setOnClickListener(this);


        // 主类型/次类型
        mCbType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharePrefUtil.saveBoolean(AccountBookApplication.getContext(),SharePrefUtil.REPORT_KEY_3.MAINTYPE_OR_TYPE1,mCbType.isChecked());
            }
        });
    }

    @Override
    public void initData() {
        super.initData();

        mTitle.setText("混合报表");
        mMeun.setVisibility(View.GONE);
        mBack.setVisibility(View.VISIBLE);

        // 初始化，选中的;默认选中
        mCbType.setChecked(SharePrefUtil.getBoolean(AccountBookApplication.getContext(),SharePrefUtil.REPORT_KEY_3.MAINTYPE_OR_TYPE1,true));

        initialPopup();

        /******************************************************************************/
        //"年份"选择器
        pvTimeYear = new TimePickerView(this, TimePickerView.Type.YEAR);
        //控制时间范围
        //Calendar calendar = Calendar.getInstance();
        //pvTimeYear.setRange(calendar.get(Calendar.YEAR) - 116, calendar.get(Calendar.YEAR) + 50);//要在setTime 之前才有效果哦
        pvTimeYear.setTitle("年份");         // 设置"标题"
        pvTimeYear.setTime(new Date());         // 设置当前的时间，到时间选择器
        pvTimeYear.setCyclic(false);             // 是否循环滚动
        pvTimeYear.setCancelable(false);         // true:点击弹出"布局"外部，收回"布局";false:没反应
        //时间选择后回调
        pvTimeYear.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                mTvChoiceYM.setText(DateTimeFormat.getTime(date,"yyyy"));
            }
        });

        /******************************************************************************/
        //"年月"选择器
        pvTimeYearMonth = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH);
        //控制时间范围
        //Calendar calendar = Calendar.getInstance();
        //pvTimeYearMonth.setRange(calendar.get(Calendar.YEAR) - 116, calendar.get(Calendar.YEAR) + 50);//要在setTime 之前才有效果哦
        pvTimeYearMonth.setTitle("年月份");         // 设置"标题"
        pvTimeYearMonth.setTime(new Date());         // 设置当前的时间，到时间选择器
        pvTimeYearMonth.setCyclic(false);             // 是否循环滚动
        pvTimeYearMonth.setCancelable(false);         // true:点击弹出"布局"外部，收回"布局";false:没反应
        //时间选择后回调
        pvTimeYearMonth.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                mTvChoiceYM.setText(DateTimeFormat.getTime(date,  "yyyy-MM"));
            }
        });

    }

    /**
     * 初始化popupwindow
     */
    private void initialPopup() {

        LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.popupwindow_choice_year_month, null);

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 创建PopupWindow对象
        pop = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        pop.setOnDismissListener(this);

        mRlPopNull = (RelativeLayout) view.findViewById(R.id.layout_null);
        mTvPopCancel = (TextView) view.findViewById(R.id.tv_choiceYM_cancel);
        mTvPopChoiceYear = (TextView) view.findViewById(R.id.tv_year);
        mTvPopChoiceYearMonth = (TextView) view.findViewById(R.id.tv_year_month);

        mRlPopNull.setOnClickListener(this);
        mTvPopCancel.setOnClickListener(this);
        mTvPopChoiceYear.setOnClickListener(this);
        mTvPopChoiceYearMonth.setOnClickListener(this);

        // 需要顺利让PopUpWindow dimiss（即点击PopuWindow之外的地方此或者back键PopuWindow会消失）；
        // PopUpWindow的背景不能为空。必须在popuWindow.showAsDropDown(v);
        // 或者其它的显示PopuWindow方法之前设置它的背景不为空：

        // 需要设置一下此参数，点击外边可消失
        pop.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        pop.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        pop.setFocusable(true);
    }

    /**
     * 显示popupwindow
     */
    private void showPopupWindow() {

        if (pop.isShowing()) {
            // 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
            pop.dismiss();
        } else {
            // 显示窗口
            // pop.showAsDropDown(v);
            // 获取屏幕和PopupWindow的width和height
            pop.setAnimationStyle(R.style.MenuAnimationFade);           // 动画怎么设置，怎会动!
            pop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            pop.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
            // 设置显示PopupWindow的位置位于View的左下方，x,y表示坐标偏移量
            pop.showAsDropDown(mTvChoiceYM, 0, 0);                  // 绑定哪个控件来"控制"控件弹出  ???

            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.alpha = 0.7f;
            getWindow().setAttributes(params);
        }
    }

    /**
     * 查询
     */
    private void query() {

        TableEx tableEx = new TableEx(AccountBookApplication.getContext());
        String ChoiceYM_Y = mTvChoiceYM.getText().toString();               // 选择的时间——"年月"或"年"
        String y_ym;
        if(!TextUtils.isEmpty(ChoiceYM_Y)) {
            y_ym = ChoiceYM_Y + "-%";
            mCurrentTime = y_ym;

            if (mCbType.isChecked()) {                                          // 选择了"按类型查询"
                if(!TextUtils.isEmpty(mTvType.getText().toString())) {
                    if(mTvType.getText().toString().equals("主类型")) {
                        cursor = tableEx.Query(Constant.TABLE_CONSUMPTION, new String[]{"sum(price) as _id", "maintype"},
                                "date like ? and user=?", new String[]{y_ym, AccountBookApplication.getUserInfo().getUserName()},
                                "maintype", null, "maintype");

                        mCurrentState = DATE_MAINTYPE;
                    }else{
                        // 不要使用 "+" 连接字段; 会当作"数值"的， 使用 "||" 代替 "+"
                        cursor = tableEx.Query(Constant.TABLE_CONSUMPTION, new String[]{"sum(price) as _id", "(maintype||'-'||type1)"},
                                "date like ? and user=?", new String[]{y_ym, AccountBookApplication.getUserInfo().getUserName()},
                                "maintype,type1", null, "maintype");

                        mCurrentState = DATE_TYPE1;
                    }
                }else{
                    ToastUtil.show("请选择\"主类型\"或\"次类型\"");
                    return;
                }
            } else  {
                if(ChoiceYM_Y.split("-").length == 1) {                         // 2016 ==> length==1
                    cursor = tableEx.Query(Constant.TABLE_CONSUMPTION, new String[]{"sum(price) as _id", "strftime('%m月',date)"},
                            "date like ? and user=?", new String[]{y_ym, AccountBookApplication.getUserInfo().getUserName()},
                            "strftime('%Y年%m月',date)", null, null);         // 以"年月"分组，合计

                    mCurrentState = DATE_YEAR;

                }else if (ChoiceYM_Y.split("-").length == 2) {                  // 2016-11 ==> length==2
                    cursor = tableEx.Query(Constant.TABLE_CONSUMPTION, new String[]{"sum(price) as _id", "strftime('%d日',date)"},
                            "date like ? and user=?", new String[]{y_ym, AccountBookApplication.getUserInfo().getUserName()},
                            "strftime('%Y年%m月%d日',date)", null, null);   // 以"年月日"分组，合计

                    mCurrentState = DATE_YEARMONTH;
                }
            }

            // 获取本月总支出 或 获取本年总支出
            Cursor c = tableEx.Query(Constant.TABLE_CONSUMPTION, new String[]{"sum(price)"},
                    "date like ? and user=?", new String[]{y_ym, AccountBookApplication.getUserInfo().getUserName()},
                    null, null, null);
            try {
                if (c.getCount() != 0) {        // 查询：带"函数"字段，就算"没记录"，返回的也是"1"
                    c.moveToFirst();
                    if (c.getString(0) != null) {        // 没记录，返回1，这里返回的是 "null"
                        if (Float.valueOf(c.getString(0)) >= 0) {
                            progressMax = Float.valueOf(c.getString(0));
                            tvAllPriceName.setText(ChoiceYM_Y + "消费了: ");
                            tvAllPrice.setText(" ￥" + ToFormatUtil.toDecimalFormat(progressMax, 2));
                        } else {
                            tvAllPriceName.setText(ChoiceYM_Y + "消费了: ");
                            tvAllPrice.setText(" ￥ 0");
                        }
                    } else {
                        tvAllPriceName.setText(ChoiceYM_Y + "消费了: ");
                        tvAllPrice.setText(" ￥ 0");
                    }
                } else {
                    tvAllPriceName.setText(ChoiceYM_Y + "消费了: ");
                    tvAllPrice.setText(" ￥ 0");
                }
            } catch (Exception e) {
                System.out.println("总资产error:" + e.getMessage());
            }

            lvDay.setAdapter(new MyCursorAdapter(getApplicationContext(), cursor));
        }else{
            ToastUtil.show("请选择时间");
        }


    }


    //    (1)newView：并不是每次都被调用的，它只在实例化的时候调用,数据增加的时候也会调用,
    //        但是在重绘(比如修改条目里的TextView的内容)的时候不会被调用
    //    (2)bindView：从代码中可以看出在绘制Item之前一定会调用bindView方法它在重绘的时候也同样被调用
    //          CursorAdapter还有一个重要的方法 public void changeCursor (Cursor cursor)：
    public class MyCursorAdapter extends CursorAdapter {

        //超重点，cursor 中查询出来的字段 必须有一个是"_id"， 没有的话，可以将"列"重命名
        // 如：——"sum(price) as _id"
        public MyCursorAdapter(Context context, Cursor c) {
            super(context, c);
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
            View view = inflater.inflate(R.layout.report_day_item,parent,false); // listview中每一项的布局

            viewHolder.tvDay = (TextView) view.findViewById(R.id.tv_day);
            viewHolder.pbPrice = (ProgressBar) view.findViewById(R.id.pb_price);
            viewHolder.tvProgressbar = (TextView) view.findViewById(R.id.tv_progressbar);
            viewHolder.tvPrice = (TextView) view.findViewById(R.id.tv_Price);

            view.setTag(viewHolder); // 设置进去

            return view;
        }

        /**在绘制Item之前一定会调用bindView方法它在重绘的时候也同样被调用*/
        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            final ViewHolder viewHolder=(ViewHolder) view.getTag();   // 拿出来

            float sumPrice = Float.valueOf(cursor.getString(0));
            final String dateOrType = cursor.getString(1);

            viewHolder.pbPrice.setMax(100);
            viewHolder.pbPrice.setProgress((int)((sumPrice/progressMax)*100));
            viewHolder.tvDay.setText(dateOrType);
            // sumPrice+"/"+progressMax+"="+
            viewHolder.tvProgressbar.setText(ToFormatUtil.toDecimalFormat((sumPrice/progressMax)*100,2)+"%");
            viewHolder.tvPrice.setText("-"+ToFormatUtil.toDecimalFormat(sumPrice,2));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ReportForMixture_Activity.this, ConsumerDetails_Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", mCurrentState);
                    bundle.putString("time", mCurrentTime);
                    bundle.putString("content", dateOrType);

                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQ_CODE);
                }
            });

        }

        class ViewHolder{
            TextView tvDay;
            ProgressBar pbPrice;
            TextView tvProgressbar;
            TextView tvPrice;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:                 // 返回
                finish();
                break;

            case R.id.tv_choice_m_y:            // 弹出pop窗口，选择"年份"或"年月"
                showPopupWindow();
                break;

            case R.id.layout_null:              // popupwindow上的"空白布局"
                if (pop != null) {
                    pop.dismiss();
                }
                break;

            case R.id.tv_year:                  // popupwindow上的"选择年份"
                if (pop != null) {
                    pop.dismiss();
                }
                pvTimeYear.show();
                break;

            case R.id.tv_year_month:            // popupwindow上的"选择年月"
                if (pop != null) {
                    pop.dismiss();
                }
                pvTimeYearMonth.show();
                break;

            case R.id.tv_choiceYM_cancel:                 // popupwindow上的"取消"
                if (pop != null) {
                    pop.dismiss();
                }
                break;

            case R.id.tv_type:                  // 弹出菜单，选择"主类型/次类型"
                PopupMenu popup = new PopupMenu(mContext, mTvType);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu_type, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        mTvType.setText(item.getTitle().toString());
                        return true;
                    }
                });
                popup.show(); //showing popup menu
                break;

            case R.id.btn_query:
                query();
                break;
        }
    }

    @Override
    public void onDismiss() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 1f;
        getWindow().setAttributes(params);
    }


    // 按钮监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {                 // 如果点击的是"返回按钮"

            if (pvTimeYear.isShowing()) {
                pvTimeYear.dismiss();
                return true;
            }

            if (pvTimeYearMonth.isShowing()) {
                pvTimeYearMonth.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE){                       // 拍照获取图片
            if (resultCode == ConsumerDetails_Activity.OK) {
                Bundle bundle = data.getExtras();
                int type = bundle.getInt("type");
                String time = bundle.getString("time");
                switch (type){
                    case DATE_YEAR:
                        mCbType.setChecked(false);
                        mTvType.setText("");
                        break;
                    case DATE_YEARMONTH:
                        mCbType.setChecked(false);
                        mTvType.setText("");
                        break;
                    case DATE_MAINTYPE:
                        mCbType.setChecked(true);
                        mTvType.setText("主类型");
                        break;
                    case DATE_TYPE1:
                        mCbType.setChecked(true);
                        mTvType.setText("次类型");
                        break;
                }

                mTvChoiceYM.setText(time.replace("-%", ""));
                query();

            }
        }
    }
}
