# Android-TakePhotosTraining
参考链接---[Google Training](http://developer.android.com/intl/zh-cn/training/camera/photobasics.html#TaskPhotoView)

## 目录
* [从系统相册中获取图片](#%e4%bb%8e%e7%b3%bb%e7%bb%9f%e7%9b%b8%e5%86%8c%e4%b8%ad%e8%8e%b7%e5%8f%96%e5%9b%be%e7%89%87)
* [使用系统相机拍照](#%e4%bd%bf%e7%94%a8%e7%b3%bb%e7%bb%9f%e7%9b%b8%e6%9c%ba%e6%8b%8d%e7%85%a7)
  * [拍完自动添加到系统相册](#%e6%8b%8d%e5%ae%8c%e8%87%aa%e5%8a%a8%e6%b7%bb%e5%8a%a0%e5%88%b0%e7%b3%bb%e7%bb%9f%e7%9b%b8%e5%86%8c)
  * [拍完保存文件并添加到相册](%e6%8b%8d%e5%ae%8c%e4%bf%9d%e5%ad%98%e6%96%87%e4%bb%b6%e5%b9%b6%e6%b7%bb%e5%8a%a0%e5%88%b0%e7%9b%b8%e5%86%8c)
* [其他](#%e5%85%b6%e4%bb%96)
  * [创建一个文件名不重复的jpg文件](#%e5%88%9b%e5%bb%ba%e4%b8%80%e4%b8%aa%e6%96%87%e4%bb%b6%e5%90%8d%e4%b8%8d%e9%87%8d%e5%a4%8d%e7%9a%84jpg%e6%96%87%e4%bb%b6)
  * [根据View调整Bitmap内存大小](#%e6%a0%b9%e6%8d%aeview%e7%9a%84%e9%95%bf%e5%ae%bd%e6%9d%a5%e8%b0%83%e6%95%b4%e8%a6%81%e8%af%bb%e5%8f%96bitmap%e5%8a%a0%e8%bd%bd%e5%88%b0%e5%86%85%e5%ad%98%e5%a4%a7%e5%b0%8f%e9%98%b2%e6%ad%a2oom)

## 从系统相册中获取图片

第一步：开启Gallery
```java
  Intent intent = new Intent(Intent.ACTION_PIC,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
  if (cameraIntent.resolveActivity(getPackageManager()) != null) {//intent是否合法，避免异常
  	startActivityForResult(intent, REQUEST_GALLERY);
  ｝
```
第二步：在onActivityResult方法中获得返回值
```java
  Uri uri = data.getData();//获得如content://media/external/images/media/327格式的uri值，通过ContentProvider可以进行查询。对应的数据在/data/data/com.android.providers.media/databases/external.db的files表中，根据对应的列名即可获得想要的数据进行相应的处理
```


## 使用系统相机拍照

### 拍完自动添加到系统相册

step1:开启系统相机，且拍照后的图片信息会保存到上述media数据库中
```java
  Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
  if (cameraIntent.resolveActivity(getPackageManager()) != null) {
      startActivityForResult(cameraIntent, REQUEST_CAMERA);
  }
```
step2:在onActivityResult方法中获得返回值
```java
  Bundle extras = data.getExtras;
  Bitmap imageBitmap = (Bitmap) extras.get("data");//获得一个占用内存很小的bitmap对象
  //或
  Uri uri = data.getData();//类似从Gallery获得的数据
  //可以根据需求选择其中的一个或者两个进行处理
 ```
 
### 拍完保存文件并添加到相册

第一步：指定照片的储存路径，开启相机

```java
  Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
  if (cameraIntent.resolveActivity(getPackageManager()) != null) {
  	cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
	startActivityForResult(cameraIntent, REQUEST_CAMERA_SAVE_FILE);
  }
```

第二步：在onActivityResult方法中将该图片添加到Gallery中

```java
  Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
  Uri contentUri = Uri.fromFile(photoFile);
  mediaScanIntent.setData(contentUri);
  this.sendBroadcast(mediaScanIntent);
```

## 其他

### 创建一个文件名不重复的jpg文件

```java
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
```

### 根据View的长宽来调整要读取Bitmap加载到内存大小防止OOM

```java
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
```
