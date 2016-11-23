package com.shen.accountbook2.xml;

import android.util.Xml;

import com.shen.accountbook2.global.AccountBookApplication;
import com.shen.accountbook2.xml.model.MainTypeBean;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shen on 10/26 0026.
 */
public class PullTypeParser implements TypeParser {

    @Override
    public void parser() throws Exception {
        Map<String,MainTypeBean> mainTypeBeanMap = null;
        ArrayList<String> mainTypeList = null;
        MainTypeBean mainType = null;

        // for each map.keySet()，再调用get获取
        // for(Integer key : hashMap.keySet()){
        //      System.out.println(key);
        //      System.out.println(hashMap.get(key));
        //  }

        // a.在res/xml目录下（推荐使用）：
        // XmlResourceParser parser = AccountBookApplication.getContext().getResources().getXml(R.xml.type);
        // b.在res/xml、res/raw目录下：
        // InputStream inputStream = this.getResources().openRawResource(R.xml.XXX);
        // XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        // XmlPullParser parser = factory.newPullParser();
        // 或
        // XmlPullParser parser = Xml.newPullParser();                             //由android.util.Xml创建一个XmlPullParser实例
        // parser.setInput(is, "UTF-8");                                           //设置输入流 并指明编码方式

        File files = AccountBookApplication.getContext().getFilesDir();
        File file = new File(files, "Type.xml");
        InputStream is;
        if(file.exists()){
            is = new FileInputStream(file);
        }else
            return ;

         XmlPullParser parser = Xml.newPullParser();                             //由android.util.Xml创建一个XmlPullParser实例
         parser.setInput(is, "UTF-8");                                           //设置输入流 并指明编码方式

        int eventType = parser.getEventType();                                      //获取第一个事件

        while (eventType != XmlPullParser.END_DOCUMENT) {                           //如果还不是结束文档事件,迭代每一个元素
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:                                  //开始文档事件
                    mainTypeBeanMap = new HashMap<String, MainTypeBean>();
                    mainTypeList = new ArrayList<String>();
                    break;
                case XmlPullParser.START_TAG:                                       //开始元素事件
                    if (parser.getName().equals("MainType")) {                      //parser.getName()得到当前指针所指向的节点的名称
                        mainType = new MainTypeBean();
                        mainType.setType1List(new ArrayList<String>());             // new个type1列表
                        mainType.setMainType(parser.getAttributeValue(0));          // 拿到"主类型"
                        mainTypeList.add(parser.getAttributeValue(0));
                    } else if (parser.getName().equals("type1")) {
                        mainType.getType1List().add(parser.nextText());//得到当前节点下一个文本节点的内容------>  <type1>shen</type1> 得到shen
                    }
                    break;
                case XmlPullParser.END_TAG:                                         //结束元素事件
                    if (parser.getName().equals("MainType")) {
                        mainTypeBeanMap.put(mainType.getMainType(),mainType);
                        mainType = null;
                    }
                    break;
            }
            eventType = parser.next();                                              //进入下一个元素并触发相应事件
        }

        AccountBookApplication.setMainTypeList(mainTypeList);           // 将其放到"全局变量"--必须先放这个
        AccountBookApplication.setMainTypeBeanMap(mainTypeBeanMap);     // 将其放到"全局变量"--不然，这里根据mainType获取type1列表会出问题
    }

    @Override
    public String serialize(Map<String,MainTypeBean> mainTypeMap) throws Exception {

        // for each map.keySet()，再调用get获取
        // for(Integer key : hashMap.keySet()){
        //      System.out.println(key);
        //      System.out.println(hashMap.get(key));
        //  }

        // XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        // XmlSerializer serializer = factory.newSerializer();

        MainTypeBean mainType = null;
        XmlSerializer serializer = Xml.newSerializer(); //由android.util.Xml创建一个XmlSerializer实例
        StringWriter writer = new StringWriter();
        serializer.setOutput(writer);                   //设置输出方向为writer
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Type");

        for(String key : mainTypeMap.keySet()){
            // System.out.println(key);
            // System.out.println(mainTypeMap.get(key));
            mainType = mainTypeMap.get(key);

            serializer.startTag("", "MainType");
            serializer.attribute("", "name", mainType.getMainType());
            for (String type1 : mainType.getType1List()){
                serializer.startTag("", "type1");
                serializer.text(type1);
                serializer.endTag("", "type1");
            }
            serializer.endTag("", "MainType");
        }

        serializer.endTag("", "Type");
        serializer.endDocument();

        return writer.toString();
    }
}

//readBtn.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        try {
//        InputStream is = getAssets().open("books.xml");
////          parser = new SaxBookParser();
////          parser = new DomBookParser();
//        parser = new PullBookParser();
//        books = parser.parse(is);
//        for (Book book : books) {
//        Log.i(TAG, book.toString());
//        }
//        } catch (Exception e) {
//        Log.e(TAG, e.getMessage());
//        }
//        }
//        });