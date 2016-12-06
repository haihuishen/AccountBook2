package com.shen.accountbook2.Utils;

import java.io.File;

/**
 * Created by shen on 11/26 0026.
 */
public class CreateFilesUtils {

    private CreateFilesUtils()
    {
		/* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    /**
     * 不存在就创建"文件夹"<p>
     * 拿到后使用： files.exists() 再判断一次，比如文件创建失败
     *
     * @param path 文件夹路径
     */
    public static File create(String path){
        File files = new File(path);
        if (!files.exists()) {                              // 不存在，创建
            try {
                //按照指定的路径创建文件夹
                files.mkdirs();
            } catch (Exception e) {
                // TODO: handle exception
                LogUtils.i("创建文件夹失败:"+path);
            }
        }

        return files;           // 拿到后使用： files.exists() 再判断一次，比如文件创建失败
    }
}
