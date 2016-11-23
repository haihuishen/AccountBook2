package com.shen.accountbook2;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void test(){

        //        a.在res/xml目录下（推荐使用）：
//        XmlResourceParser xmlParser = this.getResources().getXml(R.xml.XXX);
//        b.在res/xml、res/raw目录下：
//        InputStream inputStream = this.getResources().openRawResource(R.xml.XXX);

//        XmlResourceParser xmlParser = getApplication().getResources().getXml(R.xml.type);
//        InputStream inputStream = mContext.getResources().openRawResource(R.xml.type);
//
//        List<MainTypeBean> mL = null;// = new ArrayList<MainTypeBean>();
//
//        PullTypeParser p = new PullTypeParser();
//        try {
//            mL = p.parser(inputStream);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if(mL != null){
//            System.out.print(mL.get(0).getMainType());
//            Log.i("TAG",mL.get(0).getMainType());}
//
//        else{
//            System.out.print("空");
//            Log.i("TAG","空");}
    }
}