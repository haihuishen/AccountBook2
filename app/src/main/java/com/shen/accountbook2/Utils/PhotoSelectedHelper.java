package com.shen.accountbook2.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jack on 2015/7/14.
 */
public class PhotoSelectedHelper {

    /** 拍照获取图片*/
    public static final int TAKE_PHOTO = 2000;
    /** 从"相册"中获取图片*/
    public static final int PIC_PHOTO = 3000;
    /** 裁剪返回*/
    public static final int PHOTO_CROP = 4000;
    /** 拍照后，图片存放的路径(没有裁剪的)*/
    public static String camera_path;
    Activity mActivity;

    private Uri captureUri;

    private Uri cropUri;

    public PhotoSelectedHelper(Activity activity) {
        this.mActivity = activity;
    }

    public void imageSelection(String user, String action) {
        if (action.equals("take")) {
            intentCamera(user);
        } else if (action.equals("pic")) {
            intentPhoto();
        }
    }

    /**
     * 使用"拍照"，获取图片
     * @param user
     */
    private void intentCamera(String user) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureUri = getOutputMediaFileUri(mActivity, user);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, captureUri);
        mActivity.startActivityForResult(intent, TAKE_PHOTO);
    }

    /**
     * 从相册中获取"图片"
     */
    private void intentPhoto() {
        // Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // mActivity.startActivityForResult(i, PICK_PHOTO);
        
        // Intent intentss = new Intent(Intent.ACTION_GET_CONTENT);
        Intent intentss = new Intent(Intent.ACTION_PICK);
        intentss.setType("image/*");
        mActivity.startActivityForResult(intentss, PIC_PHOTO);
    }

    /**
     * 获取"要被截取"的"图片路径"Uri
     * @return Uri
     */
    public Uri getCaptureUri() {
        return captureUri;
    }

    public String getCapturePath() {
        return captureUri.getPath();
    }

    /*public String getPickPath(Context context,Uri data){
        String[] projection={MediaStore.Images.Media.DATA};
        Cursor cursor=context.getContentResolver().query(data,projection,null,null,null);
        cursor.moveToFirst();
        int columnIndex=cursor.getColumnIndex(projection[0]);
        String picturePath=cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }*/

    public String getPickPath(Context context, Uri data) {
        return PhotoURIUtils.getPath(context, data);
    }

    /**
     * 得到"裁剪"过后"图片的路径"
     * @return
     */
    public String getCropPath() {
        return cropUri.getPath();
    }

    // public static String DIRECTORY_PICTURES 图片存放的标准目录。
    // getExternalStoragePublicDirectory 外部存储媒体目录。

    /**
     * 根据，用户名生成图片文件，同事返回这个图片的"uri"<p>
     * public static String DIRECTORY_PICTURES 图片存放的标准目录。<br>
     * getExternalStoragePublicDirectory 外部存储媒体目录。<br>
     * @param context
     * @param user          用户名
     * @return
     */
    public static Uri getOutputMediaFileUri(Context context, String user) {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                context.getPackageName());
        if (!mediaStorageDir.exists()) {                // 文件不存在
            if (!mediaStorageDir.mkdirs()) {            // 文件创建不成功
                LogUtils.i("OutputMediaFileUri(拍照后存放的路径):failed to create directory");
                return null;
            }
        }
        //--> /storage/emulated/0/Pictures/com.shen.accountbook2/test_20161030_224557.jpg
        // 如这里 use是 "test"
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
        camera_path = mediaStorageDir.getPath() + File.separator + user + "_" + timeStamp + ".jpg";
        LogUtils.i("camera_path:"+camera_path);
        File mediaFile = new File(camera_path);
        return Uri.fromFile(mediaFile);
    }


    //    public static String getRealPathFromURI(Context context, Uri contentUri) {
    //        String[] proj = {MediaStore.Images.Media.DATA};
    //        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
    //        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    //        cursor.moveToFirst();
    //        return cursor.getString(column_index);
    //    }
    //Exta Options Table for image/* crop:
    //附加选项      数据类型                        描述
    //crop          String                          发送裁剪信号
    //aspectX       int                             X方向上的比例
    //aspectY       int                             Y方向上的比例
    //outputX       int                             裁剪区的宽
    //outputY       int                             裁剪区的高
    //scale         boolean                         是否保留比例
    //return-data   boolean                         是否将数据保留在Bitmap中返回
    //data          Parcelable                      相应的Bitmap数据
    //circleCrop    String                          圆形裁剪区域？
    //MediaStore.EXTRA_OUTPUT (“output”) URI 将URI指向相应的file:///…，
    //    .putExtra("scale", true)//黑边
    //    .putExtra("scaleUpIfNeeded", true)//黑边

    /**
     *
     * @param uri               被裁减图片的路径
     * @param outputX           裁剪区的宽
     * @param outputY           裁剪区的高
     * @param user              用户(用作图片的一部分)
     */
    public void cropImageUri(Uri uri, int outputX, int outputY, String user) {
        LogUtils.i("cropImageUri:(被裁减图片的路径)"+uri.getPath());
        cropUri = getOutputMediaFileUri(mActivity, user);
        LogUtils.i("cropUri:(裁减图片后的路径)"+cropUri.getPath());
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            //circleCrop    String                          圆形裁剪区域？
            //intent.setData("data",bitmap)data  Parcelable  相应的Bitmap数据
            intent.setDataAndType(uri, "image/*");                  // 图片的URI
            intent.putExtra("crop", "true");                        // 发送裁剪信号
            intent.putExtra("aspectX", 1);                          // 预览时：默认1:1显示
            intent.putExtra("aspectY", 1);                          // 预览时：默认1:1显示
            intent.putExtra("outputX", outputX);                    // 裁剪区的宽
            intent.putExtra("outputY", outputY);                    // 裁剪区的高
            intent.putExtra("scale", true);                         // 是否保留比例 // 去黑边
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);    // 截图后保存的位置
            intent.putExtra("return-data", false);                // 是否将数据保留在Bitmap中返回
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());  // 图片格式
            intent.putExtra("noFaceDetection", true);
            mActivity.startActivityForResult(intent, PHOTO_CROP);
        } catch (Exception e) {

        }
    }


}
