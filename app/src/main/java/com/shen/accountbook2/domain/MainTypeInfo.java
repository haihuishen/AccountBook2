package com.shen.accountbook2.domain;

import java.util.List;

/**
 * 主类型的 javabean
 */
public class MainTypeInfo {
        /** "主类型"民称*/
        private String name;
        /** "主类型"对应的"次类型"列表*/
        private List<Type1Info> type1List;

        /** "主类型"的 javabean 构造函数*/
        public MainTypeInfo() {
            super();
        }

        public MainTypeInfo(String name, List<Type1Info> type1List) {
            super();
            this.name = name;
            this.type1List = type1List;
        }

        /** 得到"主类型"(String)*/
        public String getName() {
            return name;
        }

        /** 设置"主类型"*/
        public void setName(String name) {
            this.name = name;
        }

        /** 得到当前"主类型"的"type1"javabean列表*/
        public List<Type1Info> getType1List() {
            return type1List;
        }

        /** 得到当前"主类型"的"type1"javabean列表*/
        public void setType1List(List<Type1Info> type1List) {
            this.type1List = type1List;
        }

        @Override
        public String toString() {
           return "Type [name=" + name + ", type1List=" + type1List + "]";
//            return "Type [name=" + name + "]";
        }
}
