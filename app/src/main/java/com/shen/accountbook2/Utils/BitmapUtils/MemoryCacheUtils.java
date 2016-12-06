package com.shen.accountbook2.Utils.BitmapUtils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.shen.accountbook2.Utils.LogUtils;

/**
 * 内存缓存<p>
 * 
 * 因为从 Android 2.3 (API Level 9)开始，<br>
 * 垃圾回收器会更倾向于回收持有软引用或弱引用的对象，这让软引用和弱引用变得不再可靠。<br>
 * Google建议使用"LruCache"<p>
 * 
 * 
 * 因为使用HashMap<String, Bitmap> 会使"内存"变的越来越大，最终内存泄漏<br>
 * 因为集合中：拥有了很多Bitmap对象,而且都被引用，所以"垃圾回收器"回收不了<p>
 *
 *方面1：<p>
 * 理解"堆" 和 "栈"<br>
 * 如： Bitmap  b = new Bitmap();<br>
 * 其中： "b变量"			是在    	"栈"<br>
 * 		"Bitmap对象"		是在		"堆"<br>
 * 
 * "b变量"	引用了 	"Bitmap对象"     那么			"垃圾回收器"回收不了<br>
 * 但是如果  "b = null;"  断开了"引用"，"Bitmap对象"就变成了"孤儿对象"可以被"垃圾回收器"回收不了<p>
 * 
 * 
 * 方面2：<p>
 * 	"垃圾回收器"回收不及时<p>
 * 
 * 理解：<br>
 * 强引用：	不可回收(默认)<br>
 * 软引用：	必要时考虑回收		SoftReference<br>
 * 弱引用：	必要时可以回收		WeakReference<br>
 * 虚引用：	最先考虑回收		PhantomReference<p>
 * 
 * 所以我们使用"软引用：	必要时考虑回收		SoftReference"<br>
 * private HashMap<String, Bitmap> mMemoryCache = new HashMap<String,Bitmap>();<br>
 * 这个也有个问题，会被回收<p>
 * 
 * 
 * 最好就是使用————LruCache<p>
 *  LruCache 可以将"最近最少使用的对象"回收掉, 从而保证内存不会超出范围(new时 固定容量的)<br>
 *  Lru: least recentlly used 最近最少使用算法<br>
 * private LruCache<String, Bitmap> mMemoryCache;<br>
 */
public class MemoryCacheUtils {

	// private HashMap<String, Bitmap> mMemoryCache = new HashMap<String,
	// Bitmap>();
	// private HashMap<String, SoftReference<Bitmap>> mMemoryCache = new
	// HashMap<String, SoftReference<Bitmap>>();

	/** 缓存的对象 (LruCache)*/
	private LruCache<String, Bitmap> mMemoryCache;

	/**
	 * 构造函数
	 */
	public MemoryCacheUtils() {
		// LruCache 可以将最近最少使用的对象回收掉, 从而保证内存不会超出范围
		// Lru: least recentlly used 最近最少使用算法
		
		
		// ***获取分配给app的内存大小(得到的是最大的内存)
		long maxMemory = Runtime.getRuntime().maxMemory();
		LogUtils.i("MemoryCacheUtils: maxMemory:" + maxMemory);

		// 要保守一点(防止溢出什么的)，使用内存的1/8为"LruCache"的大小
		mMemoryCache = new LruCache<String, Bitmap>((int) (maxMemory / 8)) {

			// 返回每个对象的大小
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// int byteCount = value.getByteCount();  这个要 12API以上
				// ***getByteCount()里面是————每行字节数*高度
				// 计算图片大小:每行字节数*高度
				int byteCount = value.getRowBytes() * value.getHeight();
				return byteCount;
			}
		};
	}

	/**
	 * 写缓存
	 */
	public void setMemoryCache(String url, Bitmap bitmap) {
		// mMemoryCache.put(url, bitmap);
		// SoftReference<Bitmap> soft = new SoftReference<Bitmap>(bitmap);//
		// 使用软引用将bitmap包装起来
		// mMemoryCache.put(url, soft);
		
		// 参数1为"键"，参数2为"值" 存放
		mMemoryCache.put(url, bitmap);
	}

	/**
	 * 读缓存
	 */
	public Bitmap getMemoryCache(String url) {
		// SoftReference<Bitmap> softReference = mMemoryCache.get(url);
		//
		// if (softReference != null) {
		// Bitmap bitmap = softReference.get();
		// return bitmap;
		// }

		// 根据"键"拿到值
		return mMemoryCache.get(url);
	}
}
