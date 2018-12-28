# 兼容国产系统的设置壁纸方式

## 废话不多说，主要代码：
```
public static void intent2SetWallPaper(Context context, String path) {
        Uri uriPath = getUriWithPath(path);
        Intent intent;

        // 针对EMUI
        if (RomUtil.isHuaweiRom()) {
            try {
                ComponentName componentName = new ComponentName("com.android.gallery3d", "com.android.gallery3d.app.Wallpaper");
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uriPath, "image/*");
                intent.putExtra("mimeType", "image/*");
                intent.setComponent(componentName);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    WallpaperManager.getInstance(context.getApplicationContext()).setBitmap(ImageUtil.getImageBitmap(path));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        // 针对MIUI
        } else if (RomUtil.isMiuiRom()) {
            try {
                ComponentName componentName = new ComponentName("com.android.thememanager", "com.android.thememanager.activity.WallpaperDetailActivity");
                intent = new Intent("miui.intent.action.START_WALLPAPER_DETAIL");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uriPath, "image/*");
                intent.putExtra("mimeType", "image/*");
                intent.setComponent(componentName);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    WallpaperManager.getInstance(context.getApplicationContext()).setBitmap(ImageUtil.getImageBitmap(path));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        // 其他
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                context.startActivity(WallpaperManager.getInstance(context.getApplicationContext())
                    .getCropAndSetWallpaperIntent(getUriWithPath(path)));
            } else {
                try {
                    WallpaperManager.getInstance(context.getApplicationContext()).setBitmap(ImageUtil.getImageBitmap(path));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
```

为方便大家使用，封装到了github：
https://github.com/SherlockGougou/SetWallpaper

## 使用方式：
#### 1.添加依赖：
##### Step 1. 在你project层级的build.gradle中，添加仓库地址:
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
##### Step 2. 在你主module的build.gradle中添加依赖：
```
dependencies {
	 implementation 'com.github.SherlockGougou:SetWallpaper:v1.2.0'
}
```
#### 2.调用代码：
```
SetWallpaper.setWallpaper(MainActivity.this, // 上下文
                path, // 图片绝对路径
                APP_AUTHORITY);// authority（7.0 文件共享权限）
```
