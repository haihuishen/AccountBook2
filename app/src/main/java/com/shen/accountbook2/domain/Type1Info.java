package com.shen.accountbook2.domain;

/**
 * 主类型的 javabean
 */
public class Type1Info {

        private String name;

        /** "主类型"的 javabean 构造函数*/
        public Type1Info() {
            super();
        }

        public Type1Info(String name) {
            super();
            this.name = name;
        }

        /** 得到"Type1类型"(String)*/
        public String getName() {
            return name;
        }

        /** 设置"Type1类型"*/
        public void setName(String name) {
            this.name = name;
        }
    
        @Override
        public String toString() {
            return "ProvinceModel [name=" + name  + "]";
        }
}
