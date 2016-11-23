package com.shen.accountbook2.clander;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shen.accountbook2.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日历gridview中的每一个item显示的textview
 * @author lmw
 *
 */
public class CalendarAdapter extends BaseAdapter {

    /** 是否为闰年*/
	private boolean isLeapyear = false;         //是否为闰年
    /** 某月的天数*/
    private int daysOfMonth = 0;                //某月的天数
    /** 某月第一天为星期几*/
    private int dayOfWeek = 0;                  //某月第一天为星期几
    /** 上一个月的总天数*/
    private int lastDaysOfMonth = 0;            //上一个月的总天数

	private Context context;
	private int items = 42;
    /** 一个gridview中的日期存入此数组中*/
	private String[] dayNumber = new String[42];        //一个gridview中的日期存入此数组中
//	private static String week[] = {"周日","周一","周二","周三","周四","周五","周六"};
	private SpecialCalendar sc = null;
	private LunarCalendar lc = null;

	private String currentYear = "";
	private String currentMonth = "";
	private String currentDay = "";

	private String mYear;
	private String mMonth;
	public String mDay;
    /** 每次滑动，增加或减去一个月,默认为0（即显示当前月）*/
	private int mJumpMonth;                          // 每次滑动，增加或减去一个月,默认为0（即显示当前月）

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    /** 用于标记当天*/
	private int currentFlag = -1;                      //用于标记当天
	private int[] schDateTagFlag = null;            //存储当月所有的日程日期

    /** 用于在头部显示的年份*/
	private String showYear = "";                   //用于在头部显示的年份
    /** 用于在头部显示的月份*/
	private String showMonth = "";                   //用于在头部显示的月份
	private String animalsYear = "";
    /** 闰哪一个月*/
	private String leapMonth = "";                  //闰哪一个月
    /** 天干地支*/
	private String cyclical = "";                   //天干地支

    /** 系统当前时间*/
	private String sysDate = "";
    /** 系统当前时间：分割出的"年"*/
	private String sys_year = "";
    /** 系统当前时间：分割出的"月"*/
	private String sys_month = "";
    /** 系统当前时间：分割出的"日"*/
	private String sys_day = "";

    /**
     * 获得系统当前的日期：并分割使用全局变量保存!
     */
	public CalendarAdapter(){
		Date date = new Date();
		sysDate = sdf.format(date);  //当期日期
		sys_year = sysDate.split("-")[0];
		sys_month = sysDate.split("-")[1];
		sys_day = sysDate.split("-")[2];
		
	}

    /**
     *
     * @param context
     * @param rs         如： getResources()是获取项目中的资源文件可以用来获取你说的string,xml还可以获取图片，音乐，视频等资源文件。
     * @param jumpMonth     每次滑动，增加或减去一个月,默认为0（即显示当前月）
     * @param jumpYear      滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
     * @param year_c        系统当前的年
     * @param month_c       系统当前的月
     * @param day_c         系统当前的日
     */
	public CalendarAdapter(Context context, Resources rs, int jumpMonth, int jumpYear, int year_c, int month_c, int day_c){
		this();   //  无参数的构造函数
		this.context= context;
		sc = new SpecialCalendar();
		lc = new LunarCalendar();
		this.mJumpMonth = jumpMonth;

		int stepYear = year_c+jumpYear;
		int stepMonth = month_c+jumpMonth ;
		if(stepMonth > 0){                          //
			//往下一个月滑动
            // 情况一：刚好滑动到12月份(之前是11月份的)
			if(stepMonth%12 == 0){                  // 0%12 (这个除外)、12%12 、24%12、36%12...==> 0
				stepYear = year_c + stepMonth/12 -1;   // stepMonth/12 ==>  1、2、3、4...
				stepMonth = 12;
			}
            // 情况二： 反正是 stepMonth ==> 1(n*12) ~ 11(n*12)月
            else{                                  // stepMonth ==> 1(n*12) ~ 11(n*12)月
				stepYear = year_c + stepMonth/12;       // stepMonth/12 ==>  1、2、3、4...
				stepMonth = stepMonth%12;
			}
		}else{                                      // stepMonth <= 0
			//往上一个月滑动
			stepYear = year_c - 1 + stepMonth/12;   // stepMonth/12 ==>  0、-1、-2、-3、-4...
			stepMonth = stepMonth%12 + 12;         // stepMonth%12 ==> 0、-1、-2、-3、-4...-11
			if(stepMonth%12 == 0){
				//???
			}
		}
	
		currentYear = String.valueOf(stepYear);;    //得到当前的年份
		currentMonth = String.valueOf(stepMonth);  //得到本月 （jumpMonth为滑动的次数，每滑动一次就增加一月或减一月）
		currentDay = String.valueOf(day_c);         //得到当前日期是哪天

		getCalendar(Integer.parseInt(currentYear), Integer.parseInt(currentMonth));
		
	}

    /**
     *
     * @param context
     * @param rs
     * @param year
     * @param month
     * @param day
     */
	public CalendarAdapter(Context context, Resources rs, int year, int month, int day){
		this();
		this.context= context;
		sc = new SpecialCalendar();
		lc = new LunarCalendar();
		currentYear = String.valueOf(year);;    //得到跳转到的年份
		currentMonth = String.valueOf(month);  //得到跳转到的月份
		currentDay = String.valueOf(day);       //得到跳转到的天
		
		getCalendar(Integer.parseInt(currentYear), Integer.parseInt(currentMonth));
		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items;                                // 宫格有数据有多少？
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;                            // 点中的 宫格的哪一项
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}


    @Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if(convertView == null){        // groupview 每一个项
			convertView = LayoutInflater.from(context).inflate(R.layout.calendar_item, null);
		 }
		TextView textView = (TextView) convertView.findViewById(R.id.tvtext); // 项中的 那个"文本"

//        在java中 \代表转义字符 \n \t 等，而 \\ 代表一个反斜杠 而.代表一个元字符
//        要表示一个.就需要用 要用\.
//        所以"\\." 在实际编译中 就代表 .
		mDay = dayNumber[position].split("\\.")[0];
		textView.setText(mDay + "");

		if (position < daysOfMonth + dayOfWeek && position >= dayOfWeek) {              // "本月的项"
			// 当前月信息显示
			textView.setTextColor(Color.BLACK);// 当月字体设黑
			// 设置 "选中"的背景
			if (Constants.zYear.equals(currentYear) && Constants.zMonth.equals(currentMonth) && Constants.zDay.equals(mDay)){
				textView.setBackgroundResource(R.drawable.select_day_bg);
				textView.setTextColor(Color.WHITE);
			}else if(currentFlag == position){
				//设置 "当天"的背景
				textView.setBackgroundResource(R.drawable.today_bg);
				textView.setTextColor(Color.WHITE);
			} else if (!Constants.zMonth.equals(currentMonth) && mJumpMonth != 0 && "1".equals(mDay)){
				// TODO 切换月份时，如果"该月"不是"当前月"——默认选第一天
				textView.setBackgroundResource(R.drawable.select_day_bg);
				textView.setTextColor(Color.WHITE);
			}else{
				textView.setBackgroundResource(R.color.white);  // 其他的"背景"都设为"白色"
			}
		}else{
			textView.setTextColor(Color.WHITE);                                             // "不是本月"字体都设为"白色"
		}
		return convertView;
	}


	/** 得到某年的某月的天数且这月的第一天是星期几(根据这些来确定groupview设置为 5行/6行)*/
	public void getCalendar(int year, int month){
		isLeapyear = sc.isLeapYear(year);                           //是否为闰年
		daysOfMonth = sc.getDaysOfMonth(isLeapyear, month);         //某月的总天数
		dayOfWeek = sc.getWeekdayOfMonth(year, month);              //某月第一天为星期几
		lastDaysOfMonth = sc.getDaysOfMonth(isLeapyear, month-1);  //上一个月的总天数

		int days = daysOfMonth;
		if (dayOfWeek != 7){                    // 某月第一天不是"星期日"
			days = days + dayOfWeek;            // 比如 某月第一天是：星期六
                                                // (说明 groupview 第一行有六个格子是上个月的;所以要"六行")
		}
		if (days <= 35){                        // 说明  第一行上个月占的少：这也要看月份的(30/31天)!
			items = 35;
			Constants.scale = 0.25f;            // 4行
		}else{                                  // 说明  第一行上个月占的少：这也要看月份的(30/31天)!
			items = 42;
			Constants.scale = 0.2f;             // 5行
		}

		Log.d("DAY", isLeapyear+" ======  "+daysOfMonth+"  ============  "+dayOfWeek+"  =========   "+lastDaysOfMonth);
		getweek(year,month);
	}
	
	/** 将一个月中的每一天的值添加入数组dayNuber中(前面几个格子有可能是上个月的)*/
	private void getweek(int year, int month) {
		int j = 1;
		int flag = 0;
		String lunarDay = "";
		
		//得到当前月的所有日程日期(这些日期需要标记)

		for (int i = 0; i < dayNumber.length; i++) {
			// 周一
			 if(i < dayOfWeek){  //前一个月
				int temp = lastDaysOfMonth - dayOfWeek+1;  // 31 - 3 + 1 ==> 29号(上个月)
//				lunarDay = lc.getLunarDate(year, month-1, temp+i,false);
				dayNumber[i] = (temp + i)+"."+lunarDay;  // "."后面试 "阴历" 不过被注释掉了
//				 setShowMonth(String.valueOf(""));
				
			}else if(i < daysOfMonth + dayOfWeek){   //本月
				String day = String.valueOf(i-dayOfWeek+1);   //得到的日期
//				lunarDay = lc.getLunarDate(year, month, i-dayOfWeek+1,false);
				dayNumber[i] = i-dayOfWeek+1+"."+lunarDay;  // "."后面试 "阴历" 不过被注释掉了

                 //对于"当前月"才去标记"当前日期"
				if(sys_year.equals(String.valueOf(year)) && sys_month.equals(String.valueOf(month)) && sys_day.equals(day)){
					//标记当前日期
					currentFlag = i;
				}
				setShowYear(String.valueOf(year));
				setShowMonth(String.valueOf(month));

				setAnimalsYear(lc.animalsYear(year));       // 下面这几句使用了"阴历"才有用
				setLeapMonth(lc.leapMonth == 0?"": String.valueOf(lc.leapMonth));
				setCyclical(lc.cyclical(year));

			}else{   //下一个月         i >= daysOfMonth + dayOfWeek
//				lunarDay = lc.getLunarDate(year, month+1, j,false);
				dayNumber[i] = j+"."+lunarDay;          // 下个月  从1号开始(在本月中)
//				 setShowMonth(String.valueOf(""));
				j++;
			}
		}
        
        String abc = "";
        for(int i = 0; i < dayNumber.length; i++){
        	 abc = abc+dayNumber[i]+":";
        }
        Log.d("DAYNUMBER",abc);    // 调试，本页的 groupview 的 "部分上月"+"本月(全)"+"部分下月"


	}
	
	
	public void matchScheduleDate(int year, int month, int day){
		
	}
	
	/**
	 * 点击每一个item时返回item中的日期
	 * @param position
	 * @return
	 */
	public String getDateByClickItem(int position){
		return dayNumber[position];
	}
	
	/**
	 * 在点击gridView时，得到这个月中第一天的位置
	 * @return
	 */
	public int getStartPositon(){
		return dayOfWeek + 7;
	}
	
	/**
	 * 在点击gridView时，得到这个月中最后一天的位置
	 * @return
	 */
	public int getEndPosition(){
		return  (dayOfWeek + daysOfMonth + 7) - 1;
	}
	
	public String getShowYear() {
		return showYear;
	}

	public void setShowYear(String showYear) {
		this.showYear = showYear;
	}

	public String getShowMonth() {
		return showMonth;
	}

	public void setShowMonth(String showMonth) {
		this.showMonth = showMonth;
	}
	
	public String getAnimalsYear() {
		return animalsYear;
	}

	public void setAnimalsYear(String animalsYear) {
		this.animalsYear = animalsYear;
	}
	
	public String getLeapMonth() {
		return leapMonth;
	}

	public void setLeapMonth(String leapMonth) {
		this.leapMonth = leapMonth;
	}
	
	public String getCyclical() {
		return cyclical;
	}

	public void setCyclical(String cyclical) {
		this.cyclical = cyclical;
	}
}
