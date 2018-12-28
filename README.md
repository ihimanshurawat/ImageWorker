# ImageWorker
ImageWorker is the Library to handle all your Image needs Save, Retrieve, and Convert. To and From external storage. 

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
	        implementation 'com.github.1AboveAll:ImageWorker:0.6'
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
### Convert

Methods to convert Bitmap to Drawable/Base64 String.

```val drawable: Drawable? = ImageWorker.convert().bitmapToDrawable(inputBitmap)```


```val base64: String? = ImageWorker.convert().bitmapToBase64(inputBitmap)```

Similar methods for Drawable and Base64.

## Issues

Requires through testing for edge cases and large file sizes. 

If you happen to found some don't hesitate to report em.

