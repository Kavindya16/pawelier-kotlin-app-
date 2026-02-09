# Quick Start Guide - Camera & Gallery API

## 🎯 How to Use in Profile Page

### Step 1: Navigate to Profile
1. Open your Pawelier App
2. Tap on the "Account" icon in the bottom navigation bar
3. You'll see your profile page with a circular profile picture

### Step 2: Change Profile Picture
1. **Tap on the profile picture** (the circular image at the top)
   - OR -
2. **Tap on the camera icon badge** (bottom-right of the profile picture)

### Step 3: Select Image Source
A dialog will appear with two options:
- **Camera** - Take a new photo
- **Gallery** - Choose from existing photos

### Step 4: Grant Permissions (First Time Only)
If prompted, grant the following permissions:
- **Camera** - Required for taking photos
- **Storage/Photos** - Required for accessing gallery

### Step 5: Select Your Image
- **If Camera**: Take a photo and confirm
- **If Gallery**: Browse and select an image

### Step 6: Done! ✅
Your new profile picture will appear immediately in the circular frame.

---

## 🔧 For Developers: Using in Other Screens

### Basic Implementation

```kotlin
import com.example.pawelierapp.api.rememberImagePicker
import coil.compose.AsyncImage
import android.net.Uri

@Composable
fun MyCustomScreen() {
    // State to hold selected image URI
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Initialize image picker
    val imagePickerState = rememberImagePicker { uri ->
        selectedImageUri = uri
    }
    
    Column {
        // Display selected image
        selectedImageUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Selected Image",
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        
        // Button to trigger image picker
        Button(onClick = { imagePickerState.showDialog() }) {
            Text("Pick Image")
        }
    }
}
```

---

## 📱 Supported Android Versions

- **Android 13+ (API 33+)**: Full support with READ_MEDIA_IMAGES
- **Android 6-12 (API 23-32)**: Full support with READ_EXTERNAL_STORAGE
- **Camera**: All Android versions with camera hardware

---

## 🎨 Customization Options

### Custom Image Display (Square)
```kotlin
AsyncImage(
    model = selectedImageUri,
    contentDescription = "Image",
    modifier = Modifier
        .size(200.dp)
        .clip(RoundedCornerShape(16.dp)),
    contentScale = ContentScale.Crop
)
```

### Custom Image Display (Rectangle)
```kotlin
AsyncImage(
    model = selectedImageUri,
    contentDescription = "Image",
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .clip(RoundedCornerShape(8.dp)),
    contentScale = ContentScale.Crop
)
```

### With Loading Placeholder
```kotlin
AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data(selectedImageUri)
        .crossfade(true)
        .build(),
    contentDescription = "Image",
    modifier = Modifier.size(200.dp),
    placeholder = painterResource(R.drawable.placeholder),
    error = painterResource(R.drawable.error_image)
)
```

---

## 🐛 Troubleshooting

### Issue: Dialog doesn't appear
**Solution**: Make sure you're calling `imagePickerState.showDialog()` on a button click or user action.

### Issue: Permissions not requesting
**Solution**: Check that permissions are declared in AndroidManifest.xml:
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

### Issue: Image doesn't display
**Solution**: 
1. Verify Coil library is added: `implementation("io.coil-kt:coil-compose:2.5.0")`
2. Sync Gradle project
3. Rebuild the app

### Issue: Camera crashes on older devices
**Solution**: Add camera feature as optional in AndroidManifest.xml:
```xml
<uses-feature android:name="android.hardware.camera" android:required="false" />
```

### Issue: FileProvider error
**Solution**: 
1. Verify `file_paths.xml` exists in `res/xml/`
2. Check FileProvider is declared in AndroidManifest.xml
3. Clean and rebuild project

---

## 📦 Required Files Checklist

Make sure these files exist:

- ✅ `app/src/main/java/com/example/pawelierapp/api/ImagePickerManager.kt`
- ✅ `app/src/main/res/xml/file_paths.xml`
- ✅ AndroidManifest.xml (with permissions and FileProvider)
- ✅ build.gradle.kts (with Coil dependency)

---

## 🚀 Advanced Features (Coming Soon)

- [ ] Image cropping
- [ ] Multiple image selection
- [ ] Image compression
- [ ] Cloud upload integration
- [ ] Image filters
- [ ] Video capture support

---

## 💡 Tips & Best Practices

1. **Always handle null URIs**: User might cancel the operation
2. **Use ContentScale.Crop**: For best image display in constrained layouts
3. **Add placeholder images**: For better UX during loading
4. **Test on real devices**: Camera functionality works best on physical devices
5. **Handle permission denials**: Provide explanations if user denies permissions

---

## 📞 Need Help?

Check the comprehensive documentation:
- `api/README.md` - Full API documentation
- `api/ImagePickerManager.kt` - Source code with inline comments

---

**Happy Coding! 🐾**

