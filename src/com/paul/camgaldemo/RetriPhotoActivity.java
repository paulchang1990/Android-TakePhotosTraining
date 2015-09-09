package com.paul.camgaldemo;

import com.paul.camgaldemo.utils.CommonUtil;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * 从相册中获取图片显示在ImageView中. 步骤如下： <li>进入系统相册Gallery</li> <li>选取需要设置的图片</li> <li>
 * 结束后返回值为图片在Gallery的provider中提供的Uri，使用resolver来解析该图片的路径等信息</li> <li>
 * 根据路径解析该图片进行显示，为了防止OOM对图片进行scale处理</li>
 * 
 * @author Paul Chang
 * 
 */
public class RetriPhotoActivity extends Activity implements OnClickListener {
	private static final int REQUEST_GALLERY = 1;
	private static final boolean DEBUG = true;
	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		setContentView(R.layout.activity_retriphoto);
		mImageView = (ImageView) findViewById(R.id.iv_image_shower);
		findViewById(R.id.btn_goto_gallery).setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
			Uri uri = data.getData();
			String imagePath = getImagePathFromUri(uri);
			if (DEBUG) {
				Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
				System.out.println("imagePath="
						+ imagePath);
				System.out.println("byteCount="
						+ CommonUtil.getByteCount(bitmap));
			}
			CommonUtil.setPicToImageView(mImageView, imagePath);
		}
	}

	/**
	 * 通过provider的Uri来查询获取到文件路径.
	 * @param uri
	 * @return
	 */
	private String getImagePathFromUri(Uri uri) {
		String filePath = null;
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		if(DEBUG){
			CommonUtil.queryCursor(cursor);
		}
		if (cursor.moveToNext()) {
			filePath = cursor.getString(cursor
					.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
			System.out.println(filePath);
		}

		return filePath;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_goto_gallery:
			goGallery();
			break;
		default:
			break;
		}
	}

	/**
	 * 通过Intent进入Gallery界面.
	 */
	private void goGallery() {
		// 调用系统图库，如果选择图片就能通过intent.getData()获得图片在provider中的Uri
		Intent intent = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, REQUEST_GALLERY);
	}
}
