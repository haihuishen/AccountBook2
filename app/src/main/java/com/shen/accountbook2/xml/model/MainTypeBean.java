package com.shen.accountbook2.xml.model;

import java.util.ArrayList;

/**
 * Created by shen on 10/26 0026.
 */
public class MainTypeBean {

    String mMainType;
    ArrayList<String> mType1List;

    public String getMainType() {
        return mMainType;
    }

    public void setMainType(String mMainType) {
        this.mMainType = mMainType;
    }

    public ArrayList<String> getType1List() {
        return mType1List;
    }

    public void setType1List(ArrayList<String> mType1List) {
        this.mType1List = mType1List;
    }


    @Override
    public String toString() {
        return mMainType+":"+mType1List;
    }
}
