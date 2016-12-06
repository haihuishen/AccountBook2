package com.shen.accountbook2.domain;

import java.util.ArrayList;

/**
 * 组图对象<p>
 * 
 * 使用Gson解析时,对象书写技巧: <br>
 * 1. 逢{}创建"对象", 逢[]创建"集合(ArrayList)" <br>
 * 
 * 2. 所有"字段名称"要和"json返回字段"高度一致 (要一样!!!)<br>
 * Javabean
 */
public class PhotosBean {

    public ArrayList<Shens> getShen() {
        return shen;
    }

    public void setShen(ArrayList<Shens> shen) {
        this.shen = shen;
    }

    public ArrayList<Shens> shen;

    public class Shens {
        public int id;
        public String listimage;
        public String title;
    }
}
