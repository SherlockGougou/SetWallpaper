package cc.shinichi.wallpaperlib;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

import cc.shinichi.wallpaperlib.util.FileUtil;
import cc.shinichi.wallpaperlib.util.ImageUtil;
import cc.shinichi.wallpaperlib.util.RomUtil;

/**
 * @author 工藤
 * cc.shinichi.wallpaperlib
 * create at 2018/12/27  16:36
 * description:
 */
public class SetWallpaper {

    public static void setWallpaper(Context context, String imageFilePath, String authority) {
        if (context == null || imageFilePath == null) {
            return;
        }
        Log.d("SetWallpaper", "setWallpaper: imageFilePath = " + imageFilePath);
        Uri uriPath;
        if (imageFilePath.toLowerCase(Locale.ROOT).startsWith("content:")) {
            uriPath = Uri.parse(imageFilePath);
        } else if (imageFilePath.toLowerCase(Locale.ROOT).startsWith("file:")) {
            uriPath = FileUtil.getUriWithPath(context, imageFilePath, authority);
        } else {
            uriPath = ImageUtil.getImageContentUri(context, new File(imageFilePath));
        }
        Log.d("SetWallpaper", "setWallpaper: uriPath = " + uriPath);
        Intent intent;
        if (RomUtil.isHuaweiRom()) {
            try {
                ComponentName componentName = new ComponentName("com.android.gallery3d", "com.android.gallery3d.app.Wallpaper");
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uriPath, "image/*");
                intent.putExtra("mimeType", "image/*");
                intent.setComponent(componentName);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                defaultWay(context, uriPath);
            }
        } else if (RomUtil.isMiuiRom()) {
            try {
                ComponentName componentName = new ComponentName("com.android.thememanager", "com.android.thememanager.activity.WallpaperDetailActivity");
                intent = new Intent("miui.intent.action.START_WALLPAPER_DETAIL");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uriPath, "image/*");
                intent.putExtra("mimeType", "image/*");
                intent.setComponent(componentName);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                defaultWay(context, uriPath);
            }
        } else if (RomUtil.isOppoRom()) {
            try {
                ComponentName componentName = new ComponentName("com.oplus.wallpapers", "com.oplus.wallpapers.wallpaperpreview.PreviewStatementActivity");
                intent = new Intent("miui.intent.action.START_WALLPAPER_DETAIL");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uriPath, "image/*");
                intent.putExtra("mimeType", "image/*");
                intent.setComponent(componentName);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                defaultWay(context, uriPath);
            }
        } else if (RomUtil.isVivoRom()) {
            try {
                ComponentName componentName = new ComponentName("com.vivo.gallery", "com.android.gallery3d.app.Wallpaper");
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uriPath, "image/*");
                intent.putExtra("mimeType", "image/*");
                intent.setComponent(componentName);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                defaultWay(context, uriPath);
            }
        } else {
            try {
                intent = WallpaperManager.getInstance(context.getApplicationContext()).getCropAndSetWallpaperIntent(uriPath);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                defaultWay(context, uriPath);
            }
        }
    }

    private static void defaultWay(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getApplicationContext().getContentResolver(), uri);
            if (bitmap != null) {
                WallpaperManager.getInstance(context.getApplicationContext()).setBitmap(bitmap);
                Toast.makeText(context, "设置成功", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}