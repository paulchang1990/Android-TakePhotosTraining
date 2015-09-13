package com.paul.camgaldemo.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import com.paul.camgaldemo.MainActivity;
import com.paul.camgaldemo.R;

/**
 * 相关工具类.
 * @author Paul Chang
 *
 */
public class CommonUtil {
	/**
	 * Create image file based system time.
	 * Most part copy from google training refer,detail link in {@link MainActivity}
	 * @return a jpg File
	 * @throws Exception
	 */
	public static File createImageFile() throws Exception{
		// Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);
	    //##start## added by Paul Chang,Some device does not have this Dir,so create the dir if it's not exsits
	    if(!storageDir.exists()){
	    	storageDir.mkdirs();
	    }
	    //##end##
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );
	    
	    // Save a file: path for use with ACTION_VIEW intents
	    return image;
	}
	
	/**
	 * Delete a dir or a file.
	 * @param dir target delete file
	 */
	public static void deleAllFiles(File dir){
		if(!dir.isDirectory()){
			dir.delete();
		}else{
			File[] listFiles = dir.listFiles();
			for (File file : listFiles) {
				deleAllFiles(file);
			}
			dir.delete();
		}
	}
	
	/**
	 * 遍历cursor
	 * @param cursor
	 */
	public static void queryCursor(Cursor cursor) {
		if (cursor != null) {
			while (cursor.moveToNext()) {
				int columnCount = cursor.getColumnCount();
				for (int i = 0; i < columnCount; i++) {
					String columnName = cursor.getColumnName(i);
					String content = cursor.getString(i);
					System.out.println("columnIndex="+i+"; columnName=" + columnName + ";content="
							+ content);
				}
			}
		}
	}
	
	/**
	 * 显示吐司
	 * @param context
	 * @param content
	 */
	public static void toast(final Activity context,final String content){
		if(Thread.currentThread().getName().equalsIgnoreCase("main")){
			Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
		}else{
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	
	/**
	 * scale指定路径下的图片加载为一个Bitmap.
	 * @param imagePath
	 * @param width 目标宽度
	 * @param height 目标高度
	 * @return 按照指定宽高scale的bitmap
	 */
	public static Bitmap resizeBitmap(String imagePath,int width,int height){
		BitmapFactory.Options opts = new Options();

		// 设置这个，只得到Bitmap的属性信息放入opts，而不把Bitmap加载到内存中
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, opts);

		int bitmapWidth = opts.outWidth;
		int bitmapHeight = opts.outHeight;
		// 取最大的比例，保证整个图片的长或者宽必定在该屏幕中可以显示得下
		int scale = Math.min(bitmapWidth / width, bitmapHeight
				/ height);
		System.out.println("bitmapHeight=" + bitmapHeight + ";bitmapWidth="
				+ bitmapWidth + ";imageViewHeight=" + height
				+ ";imageViewWidth=" + width);

		// 缩放的比例
		opts.inSampleSize = scale;
		// 内存不足时可被回收
		opts.inPurgeable = true;
		// 设置为false,表示不仅Bitmap的属性，也要加载bitmap
		opts.inJustDecodeBounds = false;
		//调整显示色值
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, opts);
		return bitmap;
	}
	
	/**
	 * 将指定路径上的图片进行scale后显示到ImageView上.
	 * @param imageView
	 * @param imagePath
	 */
	public static void setPicToImageView(ImageView imageView, String imagePath) {
		int imageViewWidth = imageView.getWidth();
		int imageViewHeight = imageView.getHeight();
		Bitmap bitmap = resizeBitmap(imagePath, imageViewWidth, imageViewHeight);
		if(bitmap == null){
			imageView.setImageResource(R.drawable.ic_launcher);
//			imageView.setImageDrawable(new ColorDrawable(Color.BLACK));
		}else{
			imageView.setImageBitmap(bitmap);
		}
	}
	
	/**
	 * Bitmap的字节数量
	 * @param bitmap
	 * @return
	 */
	public static int getByteCount(Bitmap bitmap){
		if(bitmap == null){
			return -1;
		}
		return bitmap.getRowBytes() * bitmap.getHeight();
	}
	
	/**
	 * 通过provider的Uri来查询获取到文件路径.
	 * 
	 * @param uri
	 * @return 文件路径
	 */
	public static String getImagePathFromUri(Context context,Uri uri) {
		String filePath = null;
		String[] projection = new String[]{MediaStore.Images.ImageColumns.DATA};
		Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			filePath = cursor.getString(0);
			System.out.println(filePath);
		}
		return filePath;
	}
}
