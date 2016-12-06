package com.shen.accountbook2.ui.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shen.accountbook2.R;

import java.text.NumberFormat;

/**
 * 继承 AlertDialog 的"自定义进度条窗口"
 */
public abstract class CommonProgressDialog extends AlertDialog {

    /** 进度条*/
    private ProgressBar mProgress;
    /** 比例 ==> 完成数量/总数 */
    private TextView mProgressNumber;
    /** 进度百分比*/
    private TextView mProgressPercent;
    /** 进度框的"标题"*/
    private TextView mProgressMessage;
    private Handler mViewUpdateHandler;
    /** 进度条最大值*/
    private int mMax;
    private CharSequence mMessage;
    private boolean mHasStarted;
    /** 进度条当前值*/
    private int mProgressVal;
    private String TAG="CommonProgressDialog";
    /** 比例字符串的格式*/
    private String mProgressNumberFormat;
    /** 进度百分比字符串的格式的类*/
    private NumberFormat mProgressPercentFormat;

    /** 取消按钮*/
    private Button mBtnCancel;

    public CommonProgressDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        initFormats();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_common_progress);

        setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 安卓弹出ProgressDialog进度框之后触摸屏幕就消失了的解决方法
        setCanceledOnTouchOutside(false);

        mProgress=(ProgressBar) findViewById(R.id.progress);
        mProgressNumber=(TextView) findViewById(R.id.progress_number);
        mProgressPercent=(TextView) findViewById(R.id.progress_percent);
        mProgressMessage=(TextView) findViewById(R.id.progress_message);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        // LayoutInflater inflater = LayoutInflater.from(getContext());
        mViewUpdateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);

                int progress = mProgress.getProgress();
                int max = mProgress.getMax();
                // double dProgress = (double)progress/(double)(1024 * 1024);
                // double dMax = (double)max/(double)(1024 * 1024);

                // 比例字符串格式
                if (mProgressNumberFormat != null) {
                    String format = mProgressNumberFormat;
                    // mProgressNumber.setText(String.format(format, dProgress, dMax));
                    mProgressNumber.setText(String.format(format, progress, max));
                } else {
                    mProgressNumber.setText("");
                }
                // 进度百分比字符串格式的类
                if (mProgressPercentFormat != null) {
                    double percent = (double) progress / (double) max;
                    SpannableString tmp = new SpannableString(mProgressPercentFormat.format(percent));
                    tmp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                            0, tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mProgressPercent.setText(tmp);
                } else {
                    mProgressPercent.setText("");
                }
            }
        };
        // View view = inflater.inflate(R.layout.common_progress_dialog, null);
        // mProgress = (ProgressBar) view.findViewById(R.id.progress);
        // mProgressNumber = (TextView) view.findViewById(R.id.progress_number);
        // mProgressPercent = (TextView) view.findViewById(R.id.progress_percent);
        // setView(view);
        //mProgress.setMax(100);

        onProgressChanged();

        if (mMessage != null) {
            setMessage(mMessage);
        }
        if (mMax > 0) {
            setMax(mMax);
        }
        if (mProgressVal > 0) {
            setProgress(mProgressVal);
        }

        /***点击返回的事件***/
        //点击取消事件监听（没有点击"更新"或"稍后更新"而是直接点击"返回按钮"）
        setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //即使用户点击取消
                dialog.dismiss();	// 关闭对话框
            }
        });

    }


    /**
     * 点击"取消按钮"干的事情
     * @return View
     */
    public abstract void cancel();

    /**
     * 初始比例==> 完成数量/总数"的"格式"<br>
     * 和百分比的"整数部分允许的最大位数为0"
     */
    private void initFormats() {
        mProgressNumberFormat = "%d条/%d条";
        mProgressPercentFormat = NumberFormat.getPercentInstance(); // 拿到"格式"实例
        mProgressPercentFormat.setMaximumFractionDigits(0); // 设置数值的整数部分允许的最大位数。
    }

    /**
     * 进度条改变了，就发个"空消息"给"handler"
     */
    private void onProgressChanged() {
        mViewUpdateHandler.sendEmptyMessage(0);
    }

    /**
     * 设置是进度条的类型
     * @param style
     */
    public void setProgressStyle(int style) {
    //mProgressStyle = style;
    }

    /**
     * 获得"进度条"的最大值
     * @return
     */
    public int getMax() {
        if (mProgress != null) {
            return mProgress.getMax();
        }
        return mMax;
    }

    /**
     * 设置"进度条"的最大值
     * @param max
     */
    public void setMax(int max) {
        if (mProgress != null) {
            mProgress.setMax(max);
            onProgressChanged();
        } else {
            mMax = max;
        }
    }

    /**
     * indeterminate -- 不明确<p>
     *不明确(false)就是滚动条的当前值自动在最小到最大值之间来回移动，<br>
     * 形成这样一个动画效果，这个只是告诉别人“我正在工作”，但不能提示工作进度到哪个阶段。<br>
     * 主要是在进行一些无法确定操作时间的任务时作为提示。<p>
     * 而“明确”(true)就是根据你的进度可以设置现在的进度值。
     * @param indeterminate
     */
    public void setIndeterminate(boolean indeterminate) {
        if (mProgress != null) {
            mProgress.setIndeterminate(indeterminate);
        }
        // else {
        // mIndeterminate = indeterminate;
        // }
    }

    /**
     * 设置"进度条"的"当前值"
     * @param value
     */
    public void setProgress(int value) {
        if (mHasStarted) {
            mProgress.setProgress(value);
            onProgressChanged();
        } else {
            mProgressVal = value;
        }
    }

    /** 设置"对话框"的"标题"*/
    @Override
    public void setMessage(CharSequence message) {
        // TODO Auto-generated method stub
        //super.setMessage(message);
        if(mProgressMessage!=null){
            mProgressMessage.setText(message);
        }
        else{
            mMessage = message;
        }
    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        mHasStarted = true;
    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        mHasStarted = false;
    }
}