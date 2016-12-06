package com.shen.accountbook2.ui.fragment;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.BitmapUtils.CacheUtils;
import com.shen.accountbook2.Utils.BitmapUtils.MyBitmapUtils;
import com.shen.accountbook2.Utils.LogUtils;
import com.shen.accountbook2.Utils.ToastUtil;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.domain.PhotosBean;

import java.util.ArrayList;

/**
 * Created by shen on 9/9 0009.
 */
public class ShoppingFragment extends BaseFragment{

    // 标志位，标志已经初始化完成。
    private boolean isPrepared;

    /******************************标题***********************************/
    private TextView tvTitle;
    private ImageButton mBtnLvGv;

    /******************************下拉刷新布局***********************************/
    private SwipeRefreshLayout mSwipeRefreshLayout_Lv;
    private static boolean Loading = false;           // 是否正在加载


    /******************************加载成功***********************************/
    /** 加载成功显示的layout*/
    private View mLayoutLoadSuccess;
    /** 组图的样式——listview*/
    private ListView mLvPhoto;
    /** 组图的样式——GridView 九宫格之类的*/
    private GridView mGvPhoto;

    /******************************加载失败***********************************/
    /** 加载失败显示的layout*/
    private View mLayoutLoadError;
    /** 重新加载的按钮*/
    private Button mRefreshTry;

    /**
     *  图片对象	————"{}"	<br>
     *  public class PhotoNews {<br>
     *  	public int id;<br>
     *  	public String listimage;<br>
     *  	public String title;<br>
     *  }
     */
    private ArrayList<PhotosBean.Shens> mShensList;

    public ShoppingFragment() {
    }

    @Override
    public View initUI() {

        // 將 R.layout.base_pager布局 填充成 view,作为其布局
        View view = View.inflate(mContext, R.layout.fragment_shopping, null);

        /******************************标题***********************************/
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        mBtnLvGv = (ImageButton) view.findViewById(R.id.btn_lv_gv);

        // 下拉刷新布局
        mSwipeRefreshLayout_Lv = (SwipeRefreshLayout)  view.findViewById(R.id.swipeLayout_lv);

        //加载成功
        mLayoutLoadSuccess = view.findViewById(R.id.layout_Load_success);
        mLvPhoto = (ListView) view.findViewById(R.id.lv_photo);
        mGvPhoto = (GridView) view.findViewById(R.id.gv_photo);

        //加载失败
        mLayoutLoadError = view.findViewById(R.id.layout_load_error);
        mRefreshTry = (Button) view.findViewById(R.id.btn_retry);



        return view;
    }

    @Override
    public void initListener() {
        mBtnLvGv.setOnClickListener(this);

        mRefreshTry.setOnClickListener(this);


        //设置刷新时动画的颜色，可以设置4个
        mSwipeRefreshLayout_Lv.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        // 下拉刷新
        mSwipeRefreshLayout_Lv.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //if(!Loading)
                    getDataFromServer();
            }
        });


        // 为 listview设置 "项点击事件"监听
        mLvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                LogUtils.i("第" + position + "个被点击了");
            }
        });
    }


    @Override
    public void initData(){
        tvTitle.setText("商城");
        // mBtnLvGv.setVisibility(View.VISIBLE);
        mBtnLvGv.setVisibility(View.GONE);

        mLayoutLoadSuccess.setVisibility(View.GONE);      // 一开始隐藏
        mLayoutLoadError.setVisibility(View.GONE);        // 一开始隐藏

        isPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        LogUtils.i("ShoppingFragment:========isPrepared:"+isPrepared+"=======isVisible:"+isVisible);
        if(!isPrepared || !isVisible) {
            return;
        }


        // 拿缓存
        String cache = CacheUtils.getCache(Constant.PHOTOS_URL,mContext);
        if (!TextUtils.isEmpty(cache)) {
            processData(cache);
            mLayoutLoadSuccess.setVisibility(View.VISIBLE);
            mLayoutLoadError.setVisibility(View.GONE);
        }else {
            if(!Loading)
                //填充各控件的数据
                getDataFromServer();
        }
    }


    /**
     * 使用xUtils  网络
     * 拿到数据
     */
    private void getDataFromServer() {
        Loading = true;
        LogUtils.i("开始啦");
        // 使用xUtils  网络
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, Constant.PHOTOS_URL,
                new RequestCallBack<String>() {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        // 请求到的数据(应该都是json数据)
                        String result = responseInfo.result;
                        processData(result);
                        CacheUtils.setCache(Constant.PHOTOS_URL, result, mContext);
                        mLayoutLoadSuccess.setVisibility(View.VISIBLE);
                        mLayoutLoadError.setVisibility(View.GONE);

                        Loading = false;
                        LogUtils.i("成功");

                        mSwipeRefreshLayout_Lv.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        // 请求失败
                        error.printStackTrace();
                        //ToastUtil.show(msg);
                        ToastUtil.show("请求连接失败!");

                        if(mLayoutLoadSuccess.getVisibility() == View.GONE){
                            mLayoutLoadSuccess.setVisibility(View.GONE);
                            mLayoutLoadError.setVisibility(View.VISIBLE);
                        }
                        Loading = false;
                        LogUtils.i("失败");

                        mSwipeRefreshLayout_Lv.setRefreshing(false);
                    }
                });
    }

    /**
     * 解析json 数据
     *
     * Gson: Google Json<br>
     * 要使用到gson-2.3.1.jar
     *
     * @param result			String类型（json文件的内容）
     */
    protected void processData(String result) {
        // Gson: Google Json
        Gson gson = new Gson();


        PhotosBean photosBean = null;
        try {
            // 將json解析到  参数2：Javabean类字节码
            // ***返回一个 参数2的javabean类
            photosBean = gson.fromJson(result, PhotosBean.class);
        }catch (Exception e){
            LogUtils.i("AAA",e.getMessage());
        }
        if(photosBean != null) {
            mShensList = photosBean.getShen();
        }

        mLvPhoto.setAdapter(new PhotoAdapter());
        // mGvPhoto.setAdapter(new PhotoAdapter());// gridview的布局结构和listview完全一致,
        // 所以可以共用一个adapter
    }

    /**
     * 适配器<p>
     *
     * 适合listview  和  GridView
     *
     *
     */
    class PhotoAdapter extends BaseAdapter {

        // BitmapUtils 是  xUtils的三级缓存图片
        // private BitmapUtils mBitmapUtils;
        private MyBitmapUtils mBitmapUtils;

        public PhotoAdapter() {
            mBitmapUtils = new MyBitmapUtils();

            // xUtils的三级缓存图片
            // mBitmapUtils = new BitmapUtils(mContext);
            // 设置默认图片
            // mBitmapUtils.configDefaultLoadingImage(R.mipmap.pic_item_list_default);
        }

        @Override
        public int getCount() {
            return mShensList.size();
        }

        @Override
        public PhotosBean.Shens getItem(int position) {
            return mShensList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                // 讲R.layout.list_item_photos布局 填充成  view 作为"每一个项"
                convertView = View.inflate(mContext, R.layout.item_list_photos, null);

                holder = new ViewHolder();
                holder.ivPic = (ImageView) convertView.findViewById(R.id.iv_pic);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);

                convertView.setTag(holder);	// 将复用控件类，设到view中

            } else {
                holder = (ViewHolder) convertView.getTag();  // 从 缓存view中拿到，复用控件类
            }

            PhotosBean.Shens item = getItem(position);

            holder.tvTitle.setText(item.title);
            mBitmapUtils.display(holder.ivPic, item.listimage);

            return convertView;
        }

    }


    /**
     * 存放"R.layout.list_item_photos布局"的控件的声明的了<p>
     * 为了复用(省内存)<p>
     *	复用控件类
     */
    static class ViewHolder {
        public ImageView ivPic;
        public TextView tvTitle;
    }


    /**
     *  标记当前是否是listview展示<br>
     *  或是想使用(GridView)
     */
    private boolean isListView = true;


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_lv_gv:
                if (isListView) {
                    // 切成gridview
                    mLvPhoto.setVisibility(View.GONE);
                    mGvPhoto.setVisibility(View.VISIBLE);
                    mBtnLvGv.setImageResource(R.mipmap.icon_pic_list_type);

                    isListView = false;
                } else {
                    // 切成listview
                    mLvPhoto.setVisibility(View.VISIBLE);
                    mGvPhoto.setVisibility(View.GONE);
                    mBtnLvGv.setImageResource(R.mipmap.icon_pic_grid_type);

                    isListView = true;
                }
                break;

            case R.id.btn_retry:
                if(!Loading)
                    getDataFromServer();
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LogUtils.i("来了这里了：HomeFragment");
    }

}
