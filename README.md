# ImageWorker

[![](https://jitpack.io/v/ihimanshurawat/ImageWorker.svg)](https://jitpack.io/#ihimanshurawat/ImageWorker)


ImageWorker is the Library to handle all your Image needs Save, Retrieve, and Convert. To and From external storage dedicated for your application. For information you can refer the documentation:
https://developer.android.com/about/versions/11/privacy/storage

** UPDATE 29/03/2021 **

Now the new Path where the files are store would be:

Internal shared storage\Android\data\<your-app-package>\<your-specified-directory>

Note - It could be possible that the Directories are not visible File Managers for Android 11. So don't panic your content will be in Android\data\<your-app-package>\<your-specified-directory>\<your-filename>

Raise issues if you have more queries.

Additional Notes

If you want to contine using the previous implementation then please modify the Manifest file. 

Google Say's "If your app opts out of scoped storage when running on Android 10 devices, it's recommended that you continue to set requestLegacyExternalStorage to true in your app's manifest file. That way, your app can continue to behave as expected on devices that run Android 10."

Also, "Apps that run on Android 11 but target Android 10 (API level 29) can still request the requestLegacyExternalStorage attribute. This flag allows apps to temporarily opt out of the changes associated with scoped storage, such as granting access to different directories and different types of media files. After you update your app to target Android 11, the system ignores the requestLegacyExternalStorage flag."

You can't bypass this unless your application is a File Explorer, an Anti-virus etc. 

## Features
- Save Bitmap, Drawable and Base64 encoded String directly to the External Storage. In following *.png, *.jpeg, and *.webp formats. 
- Retrieve saved files and use them as Bitmap.
- Convert Bitmap to Drawable or Base64 and vice versa.
## Pre-Requisite 
- Saving files would require WRITE_EXTERNAL_STORAGE permission.
- Retrieving files would require READ_EXTERNAL_STORAGE permission.

Converting files doesn't require any special permission.

## Usage
Guide to use ImageWorker Library

### Add Dependencies

In Project Level Gradle
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

In Application Level Gradle

```
	dependencies {
	        implementation 'com.github.ihimanshurawat:ImageWorker:1.2.0'
	}

```

### Save
- **Required** Method **to(arg = Context)** is used to save Bitmap/Drawable/Base64
- Method **directory(arg = String Path)** is used to create a directory. Multiple calling will result in the directory name of the last called function. **ImageWorker should work even without calling directory() method because the default directory would be the package name of your application.** 
- Method **subDirectory(arg = String Path)** is used to create a directory inside a directory. It can be nested to create directories inside directories.
- **Required** Method **setFileName(arg = String File Name)** to set File Name.
- **Required** Method **withExtension(arg = Extension)** the arguments of this method must be either one of these Extension.PNG, Extension.WEBP, and Extension.JPEG.
- **Required** Method **save(arg = bitmap and quality/drawable and quality/base64 and quality)** default quality (Int) will be 100. 
quality must be 0 < quality <= 100

```
ImageWorker.to(context).
    directory("ImageWorker").
    subDirectory("SubDirectory").
    setFileName("Image").
    withExtension(Extension.PNG).
    save(sourceBitmap,85)
```

### Retrieve
- **Required** Method **from(arg = Context)** is used to retrieve saved files.
- Method **directory(arg = String Path)** set directory where files is present. Multiple calling will result in the directory name of the last called function. **ImageWorker should work even without calling directory() method because the default directory would be the package name of your application.** 
- Method **subDirectory(arg = String Path)** is used to get file in a sub-directory. This method can be nested.
- **Required** Method **setFileName(arg = String File Name)** to set File Name.
- **Required** Method **withExtension(arg = Extension)** the arguments of this method must be either one of these Extension.PNG, Extension.WEBP, and Extension.JPEG.
- **Required** Method **load()** will return Bitmap.

```
val bitmap: Bitmap? = ImageWorker.from(context).
    directory("ImageWorker").
    subDirectory("SubDirectory").
    setFileName("Image").
    withExtension(Extension.PNG).
    load()
```
### Delete - Added in v1.2.0
- **Required** Method **from(arg = Context)** is used to delete the saved images.
- Method **directory(arg = String Path)** set directory where files is present. Multiple calling will result in the directory name of the last called function. **ImageWorker should work even without calling directory() method because the default directory would be the package name of your application.** 
- Method **subDirectory(arg = String Path)** is used to get file in a sub-directory. This method can create nested directories.  
- **Required** Method **setFileName(arg = String File Name)** to set File Name which you want to delete.
- **Required** Method **withExtension(arg = Extension)** the arguments of this method must be either one of these Extension.PNG, Extension.WEBP, and Extension.JPEG.
- **Required** Method **delete()** will delete Image File.
- The function returns a boolean if the file is successfully deleted and vice versa. 
- To prevent missue, you can only delete files with the extensions .PNG, .JPEG, and .WEBP. 

```
val bitmap: Bitmap? = ImageWorker.from(context).
    directory("ImageWorker").
    subDirectory("SubDirectory").
    setFileName("Image").
    withExtension(Extension.PNG).
    delete()
```

### Convert
Methods to convert Bitmap to Drawable/Base64 String.

```val drawable: Drawable? = ImageWorker.convert().bitmapToDrawable(inputBitmap)```


```val base64: String? = ImageWorker.convert().bitmapToBase64(inputBitmap)```

Similar methods for Drawable and Base64.

## Issues

Requires through testing for edge cases and large file sizes. 

If you happen to found some don't hesitate to report em.

