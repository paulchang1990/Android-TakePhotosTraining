# Android-TakePhotosTraining
refer to this link---[Google Training](http://developer.android.com/intl/zh-cn/training/camera/photobasics.html#TaskPhotoView)
##Two parts:
* Retrieve a photo from system gallery
* Take a photo from system camera and save this photo as a File
###Part-1:Retrieve a photo from system gallery
step1:开启Gallery

    Intent intent = new Intent(Intent.ACTION_PIC,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	if (cameraIntent.resolveActivity(getPackageManager()) != null) {//intent是否合法，避免异常
    	startActivityForResult(intent, REQUEST_GALLERY);
	｝
step2:在onActivityResult方法中获得返回值

	Uri uri = data.getData();//获得如content://media/external/images/media/327格式的uri值，通过ContentProvider可以进行查询。对应的数据在/data/data/com.android.providers.media/databases/external.db的files表中，根据对应的列名即可获得想要的数据进行相应的处理
##
###Part-2:Take a photo from sytsem camera
####方法1. 拍照完成后无需处理会自动添加到Gallery中
step1:开启系统相机，且拍照后的图片信息会保存到上述media数据库中

    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	if (cameraIntent.resolveActivity(getPackageManager()) != null) {
		startActivityForResult(cameraIntent, REQUEST_CAMERA);
	}
step2:在onActivityResult方法中获得返回值

	Bundle extras = data.getExtras;
	Bitmap imageBitmap = (Bitmap) extras.get("data");//获得一个占用内存很小的bitmap对象
	//或
	Uri uri = data.getData();//类似从Gallery获得的数据
	//可以根据需求选择其中的一个或者两个进行处理
####方法2. 拍照时指定需要保存的路径，然后通过发送一个广播让系统将该图片添加到Gallery中
step1:指定照片的储存路径，开启相机
	
	Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	if (cameraIntent.resolveActivity(getPackageManager()) != null) {
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
		startActivityForResult(cameraIntent, REQUEST_CAMERA_SAVE_FILE);
	}
step2:在onActivityResult方法中将该图片添加到Gallery中

	Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	Uri contentUri = Uri.fromFile(photoFile);
	mediaScanIntent.setData(contentUri);
	this.sendBroadcast(mediaScanIntent);


##Extra parts:
###Part-1:创建一个文件名不重复的.jpg文件
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

###Part-2:根据View的长宽来调整要读取Bitmap加载到内存大小，防止OOM
    	public static Bitmap resizeBitmap(String imagePath,int width,int height){
		BitmapFactory.Options opts = new Options();

		// 只得到Bitmap的属性信息放入opts，而不把Bitmap加载到内存中
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, opts);

		int bitmapWidth = opts.outWidth;
		int bitmapHeight = opts.outHeight;
		// 取最大的比例，保证整个图片的长或者宽必定在该屏幕中可以显示的下
		int scale = Math.min(bitmapWidth / width, bitmapHeight
				/ height);
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