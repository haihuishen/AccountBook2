package com.shen.accountbook2.Utils.BitmapUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.shen.accountbook2.Utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 网络缓存(工具)
 * 
 * 
 * 
 */
public class NetCacheUtils {

	/**本地缓存*/
	private LocalCacheUtils mLocalCacheUtils;
	/**内存缓存*/
	private MemoryCacheUtils mMemoryCacheUtils;

	
	/**
	 * 构造函数
	 * 
	 * @param localCacheUtils
	 * @param memoryCacheUtils
	 */
	public NetCacheUtils(LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils) {
		mLocalCacheUtils = localCacheUtils;
		mMemoryCacheUtils = memoryCacheUtils;
	}

	/**
	 * 从网络中得到图片<p>
	 * 
	 * AsyncTask 异步封装的工具, 可以实现异步请求及主界面更新(对线程池+handler的封装)<br>
	 * new BitmapTask().execute(imageView, url);// 启动AsyncTask(我们只传递俩个参数)<p>
	 * 
	 * class BitmapTask extends AsyncTask<Object, Integer, Bitmap><br>
	 * 
	 * @param imageView				"显示在网络加载的图片"的控件
	 * @param url
	 */
	public void getBitmapFromNet(ImageView imageView, String url) {
		
		// AsyncTask 异步封装的工具, 可以实现异步请求及主界面更新(对线程池+handler的封装)
		new BitmapTask().execute(imageView, url);// 启动AsyncTask
	}

	/**
	 * 异步请求网络AsyncTask<p>
	 * 
	 * 使用new BitmapTask().execute(imageView, url);// 启动AsyncTask(这里只传递俩个参数)<p>
	 * 
	 * 
	 * 三个泛型意义: <br>
	 * 第一个泛型: doInBackground() 		里的参数类型 <br>
	 * 第二个泛型: onProgressUpdate()		里的参数类型 <br>
	 * 第三个泛型: onPostExecute()		里的参数类型 <br>
	 * 			及  doInBackground()		的返回类型<P>
	 * 
	 * 就是 AsyncTask<Object, Integer, Bitmap> 参数是什么   其对应的回调函数也要写对应类型的!<p>
	 * 
	 * 顺序<br>
	 * onPreExecute()		1.预加载<br>
	 * doInBackground()		2.正在加载<br>
	 * onProgressUpdate()	3.更新进度的方法<br>
	 * onPostExecute()		4.加载结束<br>
	 * 
	 */
	class BitmapTask extends AsyncTask<Object, Integer, Bitmap> {

		private ImageView imageView;
		private String url;

		/**
		 *  1.预加载
		 *  ***(运行在主线程)
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// System.out.println("onPreExecute");
		}

		/**
		 * 在后台进行
		 * ***(运行在子线程)
		 * ***(核心方法)
		 * 
		 *  2.正在加载, 运行在子线程(核心方法), 可以直接异步请求
		 *  
		 */
		@Override
		protected Bitmap doInBackground(Object... params) {
			// System.out.println("doInBackground");
			
			imageView = (ImageView) params[0];
			url = (String) params[1];
			
			// 打标记
			// ***将当前imageview和url绑定在了一起
			imageView.setTag(url);

			// 开始下载图片
			Bitmap bitmap = download(url);
			// publishProgress(values) 调用此方法实现进度更新(会回调onProgressUpdate)

			return bitmap;
		}

		/**
		 *  3.更新进度的方法
		 *  ***(运行在主线程)
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度条
			super.onProgressUpdate(values);
		}

		/**
		 *  4.加载结束
		 *  ***运行在主线程, 可以直接更新UI
		 *  ***(核心方法)
		 */
		@Override
		protected void onPostExecute(Bitmap result) {
			// System.out.println("onPostExecute");

			// 如果位图不为空
			if (result != null) {
				
				// 给imageView设置图片
				// ***由于listview的重用机制导致"imageview对象"可能被多个item共用,
				// ***从而可能将错误的图片设置给了imageView对象
				// ***所以需要在此处校验, 判断是否是正确的图片
				String url = (String) imageView.getTag();

				// 判断图片绑定的url是否就是当前bitmap的url,
				// ***如果是,说明图片正确(没有错乱)
				if (url.equals(this.url)) {
					// 给控件设置"图片"
					imageView.setImageBitmap(result);
                    LogUtils.i("NetCacheUtils:从网络加载图片啦!!!");

					// 写本地缓存
					mLocalCacheUtils.setLocalCache(url, result);
					// 写内存缓存
					mMemoryCacheUtils.setMemoryCache(url, result);
				}
			}

			super.onPostExecute(result);
		}

	}

	/**
	 *  下载图片<p>
	 *  
	 *  使用HttpURLConnection<br>
	 *  
	 *  
	 * @param url		下载的网址
	 * @return
	 */
	public Bitmap download(String url) {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection(); 	// 打开一个链接

			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);								// 连接超时
			conn.setReadTimeout(5000);									// 读取超时

			conn.connect();												// 连接

			int responseCode = conn.getResponseCode();					// 得到"响应码"

			if (responseCode == 200) {
				InputStream inputStream = conn.getInputStream();

				// 根据输入流生成bitmap对象
				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

				return bitmap;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();			// 断开连接
			}
		}

		return null;
	}

}
