package com.shen.accountbook2.Utils;

/**
 * Created by shen on 11/25 0025.
 */

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * SD卡相关的辅助类
 *
 */
public class SDCardUtils
{
    private SDCardUtils()
    {
		/* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断SDCard是否可用<p>
     *
     * 1,判断sd卡是否可用,是否挂在上<br>
     * 外部存储设备的状态;Environment.MEDIA_MOUNTED：挂载了 <br>
     * @return
     */
    public static boolean isSDCardEnable()
    {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径<p>
     *
     * "SD卡"绝对路径+File.separator（"/"调用这个API拿到"正斜杠"还是"反斜杠"）
     * @return
     */
    public static String getSDCardPath()
    {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 获取SD卡的剩余容量 单位byte
     *
     * @return
     */
    public static long getSDCardAllSize()
    {
        if (isSDCardEnable())
        {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long availableBlocks = stat.getAvailableBlocksLong() - 4;
            // 获取单个数据块的大小（byte）
            long freeBlocks = stat.getAvailableBlocksLong();
            return freeBlocks * availableBlocks;
        }
        return 0;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     *
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public static long getFreeBytes(String filePath)
    {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath()))
        {
            filePath = getSDCardPath();
        } else
        {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = stat.getAvailableBlocksLong() - 4;
        return stat.getBlockSizeLong() * availableBlocks;
    }

    /**
     * 获取系统存储路径
     *
     * @return
     */
    public static String getRootDirectoryPath()
    {
        return Environment.getRootDirectory().getAbsolutePath();
    }


}
