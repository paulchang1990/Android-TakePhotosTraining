package com.paul.camgaldemo;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.paul.camgaldemo.utils.CommonUtil;

/**
 * 调用系统相机. 步骤如下： <li>进入系统相机</li> <li>拍照后储存</li> <li>
 * 结束后默认返回一个小的Bitmap对象</li> <li>
 * 根据路径解析该图片进行显示，为了防止OOM对图片进行scale处理</li>
 * 
 * @author Paul Chang
 * 
 */
public class CameraPhotoActivity extends Activity implements OnClickListener {
	private static final int REQUEST_CAMERA = 1;
	private static final int REQUEST_CAMERA_SAVE_FILE = 2;

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
		//直接拍照
		findViewById(R.id.btn_capture).setOnClickListener(this);
		//拍照后存储到指定路径下
		findViewById(R.id.btn_capture_file_specify).setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
			//直接返回一个较小的bitmap
			Bundle extras = data.getExtras();
			Uri uri = data.getData();
			Bitmap imageBitmap = null;
			if (DEBUG) {
				System.out.println(uri);
				String imagePath = CommonUtil.getImagePathFromUri(this, uri);
				System.out.println("imagePath="+imagePath);
				imageBitmap = (Bitmap) extras.get("data");
				System.out.println("byteCount="
						+ CommonUtil.getByteCount(imageBitmap));
			}
			mImageView.setImageBitmap(imageBitmap);
			/*
			try {
				imageBitmap.compress(CompressFormat.PNG, 100,
						new FileOutputStream(CommonUtil.createImageFile()));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			*/
		}

		if (requestCode == REQUEST_CAMERA_SAVE_FILE
				&& resultCode == Activity.RESULT_OK) {
			CommonUtil.toast(this, "添加到图库");
			galleryAddPic();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_capture:
			goCapture();
			break;
		case R.id.btn_capture_file_specify:
			goCaptureSaveSpecFile();
			break;
		default:
			break;
		}
	}

	private void goCaptureSaveSpecFile() {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (cameraIntent.resolveActivity(getPackageManager()) != null) {
			File photoFile = null;
			try {
				photoFile = CommonUtil.createImageFile();
				//##start## modified by Paul Chang:google training source has a small bug,remove the prefix
//				mCurrentPhotoPath = "file:"+photoFile.getAbsolutePath();
				//##end##
				mCurrentPhotoPath = photoFile.getAbsolutePath();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (photoFile != null) {
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(cameraIntent, REQUEST_CAMERA_SAVE_FILE);
			} else {
				CommonUtil.toast(this, "文件创建失败");
			}
		} else {
			CommonUtil.toast(this, "相机开启失败");
		}

	}

	public void galleryAddPic() {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

	/**
	 * 直接调用系统照相机，生成的图片信息会保存到Gallery的数据库中
	 * 
	 * @param view
	 */
	private void goCapture() {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (cameraIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(cameraIntent, REQUEST_CAMERA);
		}

	}
}
