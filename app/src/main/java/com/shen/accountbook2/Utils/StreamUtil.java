package com.shen.accountbook2.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 将流转换成字符串(工具类封装)
 * @author shen
 *
 */
public class StreamUtil {
	/**
	 * 流转换成字符串
	 * @param is	流对象
	 * @return		流转换成的字符串	返回null代表异常
	 */
	public static String streamToString(InputStream is) {
		//1,在读取的过程中,将读取的内容存储值缓存中,然后一次性的转换成字符串返回
				// ByteArrayOutputStream:字节数组输出流
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		//2,读流操作,读到没有为止(循环)
		byte[] buffer = new byte[1024];
		//3,记录读取内容的临时变量 (读了多少个字符)
		int temp = -1;
		try {
			// is -> buffer
			while((temp = is.read(buffer))!=-1){
				// buffer -> bos
				bos.write(buffer, 0, temp);	
			}
			//返回读取数据
			return bos.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				// 关闭流
				is.close();	
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
