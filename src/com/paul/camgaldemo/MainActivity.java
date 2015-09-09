package com.paul.camgaldemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * use system camera and gallery create and retrieve a photo.
 * refer to:<a href='http://developer.android.com/intl/zh-cn/training/camera/photobasics.html#TaskPhotoView'>android training for take photo</a>
 * @author Paul Chang
 *
 */
public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		findViewById(R.id.btn_camera).setOnClickListener(this);
		findViewById(R.id.btn_gallery).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_camera:{
			Intent intent = new Intent(this,CameraPhotoActivity.class);
			startActivity(intent);}
			break;
		case R.id.btn_gallery:
			Intent intent = new Intent(this,RetriPhotoActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

}
