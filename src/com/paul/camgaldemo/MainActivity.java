package com.paul.camgaldemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

/**
 * use system camera and gallery create and retrieve a photo.
 * refer to:<a href='http://developer.android.com/intl/zh-cn/training/camera/photobasics.html#TaskPhotoView'>android training for take photo</a>
 * @author Paul Chang
 *
 */
public class MainActivity extends Activity {

	private static final int REQUEST_GALLERY = 1;
	private static final int REQUEST_CAMERA = 2;

	private ImageView mImageView;
	private String mCurrentPhotoPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		mImageView = (ImageView) findViewById(R.id.iv_image);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
			Uri uri = data.getData();
			System.out.println(uri);
			String imagePath = getImagePathFromUri(uri);
			Bitmap bitmap = decodeFile(imagePath);
			int byteCount = bitmap.getRowBytes() * bitmap.getHeight();
			System.out.println("byteCount=" + byteCount);
			setPicToImageView(mImageView, imagePath);
		}
		if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
			Bundle extras = data.getExtras();
			Uri uri = data.getData();
			System.out.println(uri);
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			int byteCount = imageBitmap.getRowBytes() * imageBitmap.getHeight();
			System.out.println("byteCount=" + byteCount);
			mImageView.setImageBitmap(imageBitmap);
			try {
				imageBitmap.compress(CompressFormat.JPEG, 100, new FileOutputStream(createImageFile()));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("byteCount=" + byteCount);
		}
		 
//		if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
//			galleryAddPic();
//		}
		

	}

	public void setPicToImageView(ImageView imageView, File imageFile) {
		setPicToImageView(imageView, imageFile.getPath());
	}

	public Bitmap resizeBitmap(String imagePath,int width,int height){
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
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, opts);
		int byteCount = bitmap.getRowBytes() * bitmap.getHeight();
		System.out.println("scaled byteCount=" + byteCount);
		return bitmap;
	}
	
	private void galleryAddPic() {
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	}
	
	
	public void setPicToImageView(ImageView imageView, String imagePath) {
		int imageViewWidth = imageView.getWidth();
		int imageViewHeight = imageView.getHeight();
		Bitmap bitmap = resizeBitmap(imagePath, imageViewWidth, imageViewHeight);
		imageView.setImageBitmap(bitmap);
	}

	public File getFile(String path) {
		File file = null;
		file = new File(path);
		return file;
	}

	public Bitmap decodeFile(String path) {
		Bitmap bitmap = null;
		bitmap = BitmapFactory.decodeFile(path);
		return bitmap;
	}

	public String getImagePathFromUri(Uri uri) {
		String filePath = null;
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		// queryCursor(cursor);
		if (cursor.moveToNext()) {
			filePath = cursor.getString(cursor
					.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
			System.out.println(filePath);
		}

		return filePath;
	}

	/**
	 * 调用系统图库
	 * 
	 * @param view
	 */
	public void goGallery(View view) {
		// 调用系统图库
		Intent intent = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, REQUEST_GALLERY);
	}

	public File createImageFile() throws Exception{
		// Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);
	    if(!storageDir.exists()){
	    	storageDir.mkdirs();
	    }
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );
	    System.out.println("imagePath="+image.getAbsolutePath());
	    // Save a file: path for use with ACTION_VIEW intents
	    mCurrentPhotoPath = "file:"+ image.getAbsolutePath();
	    return image;
	}
	
	/**
	 * 调用系统照相机
	 * 
	 * @param view
	 */
	public void goCapture(View view) {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, REQUEST_CAMERA);
		/**
		if (cameraIntent.resolveActivity(getPackageManager()) != null) {
			System.out.println("可以开启照相机");
			File photoFile = null;
			try {
				photoFile = createImageFile();
				if(!photoFile.exists()){
					System.out.println("创建了一个文件"+photoFile.getAbsolutePath());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(photoFile != null){
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
				startActivityForResult(cameraIntent, REQUEST_CAMERA);
			}else{
				
				Toast.makeText(this, "文件创建失败", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "无法开启相机", Toast.LENGTH_SHORT).show();
		}
		*/
	}

	public void queryCursor(Cursor cursor) {
		if (cursor != null) {
			while (cursor.moveToNext()) {
				int columnCount = cursor.getColumnCount();
				for (int i = 0; i < columnCount; i++) {
					String columnName = cursor.getColumnName(i);
					String content = cursor.getString(i);
					System.out.println("columnName=" + columnName + ";content="
							+ content);
				}
			}
		}
	}

	public void deleAllPics(View view){
		File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);
		deleAllFiles(storageDir);
	}
	
	public void deleAllFiles(File dir){
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
}
