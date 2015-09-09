package com.paul.camgaldemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.paul.camgaldemo.utils.CommonUtil;

/**
 * 调用系统相机. 步骤如下： <li>进入系统相册Gallery</li> <li>选取需要设置的图片</li> <li>
 * 结束后返回值为图片在Gallery的provider中提供的Uri，使用resolver来解析该图片的路径等信息</li> <li>
 * 根据路径解析该图片进行显示，为了防止OOM对图片进行scale处理</li>
 * 
 * @author Paul Chang
 * 
 */
public class CameraPhotoActivity extends Activity implements OnClickListener {
	private static final int REQUEST_CAMERA = 1;
	private static final boolean DEBUG = true;
	private ImageView mImageView;
	private String mCurrentPhotoPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		setContentView(R.layout.activity_takephoto);
		mImageView = (ImageView) findViewById(R.id.iv_image_shower);
		findViewById(R.id.btn_open_camera).setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
			Bundle extras = data.getExtras();
			Uri uri = data.getData();
			Bitmap imageBitmap = null;
			if(DEBUG){
				System.out.println(uri);
				imageBitmap = (Bitmap) extras.get("data");
				System.out.println("byteCount=" + CommonUtil.getByteCount(imageBitmap));
			}
			mImageView.setImageBitmap(imageBitmap);
			try {
				imageBitmap.compress(CompressFormat.JPEG, 100, new FileOutputStream(CommonUtil.createImageFile()));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		 
//		if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
//			galleryAddPic();
//		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_open_camera:
			goCapture();
			break;
		default:
			break;
		}
	}

	public void galleryAddPic() {
		//TODO mCurrentPhotoPath is null now
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	}
	

	/**
	 * 调用系统照相机
	 * 
	 * @param view
	 */
	private void goCapture() {
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
}
