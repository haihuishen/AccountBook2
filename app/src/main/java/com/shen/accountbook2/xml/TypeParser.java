package com.shen.accountbook2.xml;

import com.shen.accountbook2.xml.model.MainTypeBean;

import java.util.Map;

/**
 * Created by shen on 10/26 0026.
 */
public interface TypeParser {

    /**
     * 解析，xml
     * @return
     * @throws Exception
     */
    public void parser() throws Exception;

    /**
     * 将List<MainTypeBean> 中的数据，序列化成 xml样式的流
     * @param mainTypeMap     要被序列化的数据
     * @return               序列化成一条字符串，将其保存成文件，就是xml文件
     * @throws Exception
     */
    public String serialize(Map<String,MainTypeBean> mainTypeMap) throws Exception;
}
