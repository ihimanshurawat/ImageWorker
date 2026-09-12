# ImageWorker

[![](https://jitpack.io/v/ihimanshurawat/ImageWorker.svg)](https://jitpack.io/#ihimanshurawat/ImageWorker)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ImageWorker-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/7370)
[!["Buy Me A Coffee"](https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png)](https://www.buymeacoffee.com/ihimanshurawat)

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
## Pre-Requisites & Storage Permissions
- ImageWorker stores images in application-specific external storage (`context.getExternalFilesDir(null)`).
- On Android 4.4+ (API 19+) including modern Android (11, 12, 13, 14, 15+), **no runtime storage permissions are required**.
- On legacy Android versions below API 19, `WRITE_EXTERNAL_STORAGE` and `READ_EXTERNAL_STORAGE` permissions are required.
- Converting files does not require any permissions.

## Usage
Guide to use ImageWorker Library

### Add Dependencies

In Project Level Gradle (`settings.gradle` or `build.gradle`):
```groovy
dependencyResolutionManagement {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

In Application Level Gradle (`build.gradle`):

```groovy
dependencies {
    implementation 'com.github.ihimanshurawat:ImageWorker:1.3.0'
}
```

### Save
- **Required** Method **to(arg = Context)** is used to save Bitmap/Drawable/Base64
- Method **directory(arg = String Path)** is used to create a directory. Multiple calling will result in the directory name of the last called function. **ImageWorker works even without calling directory() because the default directory is the package name of your application.** 
- Method **subDirectory(arg = String Path)** is used to create a directory inside a directory. It can be nested to create directories inside directories.
- **Required** Method **setFileName(arg = String File Name)** to set File Name.
- **Required** Method **withExtension(arg = Extension)** the arguments of this method must be either `Extension.PNG`, `Extension.WEBP`, `Extension.JPEG`, or `Extension.JPG`.
- **Required** Method **save(arg = bitmap and quality/drawable and quality/base64 and quality)** default quality (Int) is 100 (1 <= quality <= 100).

```kotlin
ImageWorker.to(context)
    .directory("ImageWorker")
    .subDirectory("SubDirectory")
    .setFileName("Image")
    .withExtension(Extension.PNG)
    .save(sourceBitmap, 85)
```

### Retrieve
- **Required** Method **from(arg = Context)** is used to retrieve saved files.
- Method **directory(arg = String Path)** set directory where files are present. **Default directory is the package name of your application.** 
- Method **subDirectory(arg = String Path)** is used to get file in a sub-directory. This method can be nested.
- **Required** Method **setFileName(arg = String File Name)** to set File Name.
- **Required** Method **withExtension(arg = Extension)** to specify file extension.
- **Required** Method **load()** will return `Bitmap?`.

```kotlin
val bitmap: Bitmap? = ImageWorker.from(context)
    .directory("ImageWorker")
    .subDirectory("SubDirectory")
    .setFileName("Image")
    .withExtension(Extension.PNG)
    .load()
```

### Delete
- **Required** Method **from(arg = Context)** is used to delete saved images.
- Method **directory(arg = String Path)** set directory where file is present.
- Method **subDirectory(arg = String Path)** set sub-directory where file is present.
- **Required** Method **setFileName(arg = String File Name)** to set File Name to delete.
- **Required** Method **withExtension(arg = Extension)** to specify file extension.
- **Required** Method **delete()** returns `true` if the file was deleted successfully, otherwise `false`.

```kotlin
val isDeleted: Boolean = ImageWorker.from(context)
    .directory("ImageWorker")
    .subDirectory("SubDirectory")
    .setFileName("Image")
    .withExtension(Extension.PNG)
    .delete()
```

### Convert
Methods to convert between Bitmap, Drawable, and Base64 String:

```kotlin
val drawable: Drawable? = ImageWorker.convert().bitmapToDrawable(inputBitmap)
val base64: String? = ImageWorker.convert().bitmapToBase64(inputBitmap)
val bitmap: Bitmap? = ImageWorker.convert().base64ToBitmap(base64String)
val drawable: Drawable? = ImageWorker.convert().base64ToDrawable(base64String)
val bitmap: Bitmap? = ImageWorker.convert().drawableToBitmap(inputDrawable)
val base64: String? = ImageWorker.convert().drawableToBase64(inputDrawable)
```

