# ImageWorker

<p align="center">
  <img src="https://raw.githubusercontent.com/ihimanshurawat/ImageWorker/master/app/src/main/res/mipmap-xxhdpi/ic_launcher.png" height="80" alt="ImageWorker Logo"/>
</p>

<p align="center">
  <strong>A modern, lightweight, zero-permission image handling library for Android.</strong><br>
  Save, retrieve, delete, and convert images seamlessly in application-specific scoped storage.
</p>

<p align="center">
  <a href="https://jitpack.io/#ihimanshurawat/ImageWorker"><img src="https://jitpack.io/v/ihimanshurawat/ImageWorker.svg?style=flat-square" alt="JitPack Release"/></a>
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=flat-square&logo=android" alt="Platform"/>
  <img src="https://img.shields.io/badge/Min%20SDK-21-blue.svg?style=flat-square" alt="Min SDK 21"/>
  <img src="https://img.shields.io/badge/Target%20SDK-34%2B-orange.svg?style=flat-square" alt="Target SDK 34+"/>
  <img src="https://img.shields.io/badge/Kotlin-1.9%2B-7F52FF.svg?style=flat-square&logo=kotlin" alt="Kotlin 1.9+"/>
  <img src="https://img.shields.io/badge/Compose-Compatible-4285F4.svg?style=flat-square&logo=jetpackcompose" alt="Jetpack Compose Ready"/>
  <a href="https://www.buymeacoffee.com/ihimanshurawat"><img src="https://img.shields.io/badge/Buy%20Me%20A%20Coffee-Donate-yellow.svg?style=flat-square&logo=buymeacoffee" alt="Buy Me A Coffee"/></a>
</p>

---

## 🚀 Key Highlights

- 🛡️ **Zero Runtime Permissions**: Fully compliant with modern Android Scoped Storage (Android 11, 12, 13, 14, 15+). Files are saved to app-specific external storage (`context.getExternalFilesDir(null)`), eliminating Google Play permission declarations.
- ⚡ **Lightweight & Efficient**: Pure native Android APIs with automatic stream lifecycle management (`.use { ... }`) to avoid memory leaks.
- 🎨 **Multi-Format Compression**: Seamlessly compress and store in `PNG`, `JPEG`, `JPG`, and `WEBP` (including `WEBP_LOSSY` on Android 11+).
- 🔄 **Bidirectional Converter**: Effortlessly convert between `Bitmap`, `Drawable`, and `Base64` strings.
- 🧩 **Compose & Coroutine Ready**: Clean fluent API that slots naturally into `Dispatchers.IO` and Jetpack Compose state pipelines.

---

## 📦 Installation

ImageWorker is published via **JitPack**.

### 1. Add Maven Repository

In your root `settings.gradle.kts` (or `settings.gradle`):

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

### 2. Add Dependency

#### Option A: Version Catalog (`gradle/libs.versions.toml`) *(Recommended)*

```toml
[versions]
imageWorker = "1.3.0"

[libraries]
imageworker = { module = "com.github.ihimanshurawat:ImageWorker", version.ref = "imageWorker" }
```

In your module's `build.gradle.kts`:
```kotlin
dependencies {
    implementation(libs.imageworker)
}
```

#### Option B: Kotlin DSL (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("com.github.ihimanshurawat:ImageWorker:1.3.0")
}
```

#### Option C: Groovy DSL (`build.gradle`)

```groovy
dependencies {
    implementation 'com.github.ihimanshurawat:ImageWorker:1.3.0'
}
```

---

## 🛠️ Usage Guide

### 1. Saving an Image (`ImageWorker.to`)

Save `Bitmap`, `Drawable`, or `Base64` encoded strings directly to external app-dedicated storage:

```kotlin
// Save a Bitmap with 85% quality in PNG format
ImageWorker.to(context)
    .directory("ProfilePhotos")          // Optional: Defaults to package name
    .subDirectory("Avatars")             // Optional: Supports nesting
    .setFileName("user_avatar_101")      // Required
    .withExtension(Extension.PNG)        // Required: PNG, JPEG, JPG, WEBP
    .save(bitmap = profileBitmap, quality = 85)

// Save a Drawable
ImageWorker.to(context)
    .setFileName("badge_icon")
    .withExtension(Extension.WEBP)
    .save(drawable = context.getDrawable(R.drawable.ic_badge))

// Save a Base64 string
ImageWorker.to(context)
    .setFileName("receipt")
    .withExtension(Extension.JPEG)
    .save(base64 = base64ImageString, quality = 90)
```

> **Quality parameter**: Integer between `1` and `100` (defaults to `100`).

---

### 2. Loading an Image (`ImageWorker.from`)

Retrieve a previously saved image as an Android `Bitmap?`:

```kotlin
val bitmap: Bitmap? = ImageWorker.from(context)
    .directory("ProfilePhotos")
    .subDirectory("Avatars")
    .setFileName("user_avatar_101")
    .withExtension(Extension.PNG)
    .load()
```

---

### 3. Deleting an Image (`delete`)

Remove stored files when no longer needed:

```kotlin
val isDeleted: Boolean = ImageWorker.from(context)
    .directory("ProfilePhotos")
    .subDirectory("Avatars")
    .setFileName("user_avatar_101")
    .withExtension(Extension.PNG)
    .delete()
```

---

### 4. Format Conversion (`ImageWorker.convert`)

Convert between `Bitmap`, `Drawable`, and `Base64` representations without writing to disk:

```kotlin
val converter = ImageWorker.convert()

// Bitmap conversions
val drawable: Drawable? = converter.bitmapToDrawable(bitmap)
val base64String: String? = converter.bitmapToBase64(bitmap)

// Base64 conversions
val bitmap: Bitmap? = converter.base64ToBitmap(base64String)
val drawable: Drawable? = converter.base64ToDrawable(base64String)

// Drawable conversions
val bitmap: Bitmap? = converter.drawableToBitmap(drawable)
val base64String: String? = converter.drawableToBase64(drawable)
```

---

## 📱 Modern Android Patterns

### Asynchronous Execution with Kotlin Coroutines

Disk operations should always be performed off the main thread. Combine ImageWorker with `withContext(Dispatchers.IO)`:

```kotlin
suspend fun saveUserProfile(context: Context, bitmap: Bitmap): Boolean = withContext(Dispatchers.IO) {
    try {
        ImageWorker.to(context)
            .directory("UserProfiles")
            .setFileName("current_user")
            .withExtension(Extension.WEBP)
            .save(bitmap, quality = 90)
        true
    } catch (e: Exception) {
        false
    }
}

suspend fun loadUserProfile(context: Context): Bitmap? = withContext(Dispatchers.IO) {
    ImageWorker.from(context)
        .directory("UserProfiles")
        .setFileName("current_user")
        .withExtension(Extension.WEBP)
        .load()
}
```

### Integration with Jetpack Compose

Load and display your images easily in a `@Composable`:

```kotlin
@Composable
fun ProfileImageCard(context: Context) {
    var userBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(Unit) {
        userBitmap = withContext(Dispatchers.IO) {
            ImageWorker.from(context)
                .directory("UserProfiles")
                .setFileName("current_user")
                .withExtension(Extension.WEBP)
                .load()
        }
    }

    userBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } ?: Box(
        modifier = Modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
```

---

## 🔒 Storage & Permissions Breakdown

Starting in modern Android versions, broad storage permissions (`WRITE_EXTERNAL_STORAGE`, `READ_EXTERNAL_STORAGE`) are deprecated and blocked on Google Play for general apps.

ImageWorker leverages **application-specific storage**:
```
/storage/emulated/0/Android/data/<your-application-id>/files/<directory>/<subDirectory>/<filename>.<ext>
```

| Android Version | API Level | Permissions Required? | Details |
| :--- | :--- | :---: | :--- |
| **Android 11 – Android 15+** | API 30+ | **None** | Full scoped storage compliance; zero user permission prompts needed. |
| **Android 4.4 – Android 10** | API 19 – 29 | **None** | App-specific external storage does not require runtime permissions. |
| **Android 4.3 & below** | API <= 18 | `WRITE_EXTERNAL_STORAGE` | Legacy fallback (handled automatically). |

> [!NOTE]
> Files stored in application-specific directories are automatically cleared by the Android OS when your app is uninstalled.

---

## ⚙️ Supported Extensions

| Extension Enum | File Suffix | Format Details |
| :--- | :--- | :--- |
| `Extension.PNG` | `.png` | Lossless compression, preserves alpha channel |
| `Extension.JPEG` | `.jpeg` | Standard lossy compression, adjustable quality |
| `Extension.JPG` | `.jpg` | Alias for JPEG format |
| `Extension.WEBP` | `.webp` | Modern web format (`WEBP_LOSSY` on Android 11+) |

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to check the [issues page](https://github.com/ihimanshurawat/ImageWorker/issues).

---

## 📄 License

This project is licensed under the open-source community standards. See individual source files for details.
