package cc.shinichi.wallpaperlib;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import cc.shinichi.wallpaperlib.util.FileUtil;
import cc.shinichi.wallpaperlib.util.ImageUtil;
import cc.shinichi.wallpaperlib.util.RomUtil;
import java.io.IOException;

/**
 * @author 工藤
 * @email gougou@16fan.com
 * cc.shinichi.wallpaperlib
 * create at 2018/12/27  16:36
 * description:
 */
public class SetWallpaper {

    public static void setWallpaper(Context context, String path, String authority) {
        if (context == null || TextUtils.isEmpty(path) || TextUtils.isEmpty(authority)) {
            return;
        }
        Uri uriPath = FileUtil.getUriWithPath(context, path, authority);
        Intent intent;
        if (RomUtil.isHuaweiRom()) {
            try {
                ComponentName componentName =
                    new ComponentName("com.android.gallery3d", "com.android.gallery3d.app.Wallpaper");
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uriPath, "image/*");
                intent.putExtra("mimeType", "image/*");
                intent.setComponent(componentName);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    WallpaperManager.getInstance(context.getApplicationContext())
                        .setBitmap(ImageUtil.getImageBitmap(path));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else if (RomUtil.isMiuiRom()) {
            try {
                ComponentName componentName = new ComponentName("com.android.thememanager",
                    "com.android.thememanager.activity.WallpaperDetailActivity");
                intent = new Intent("miui.intent.action.START_WALLPAPER_DETAIL");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uriPath, "image/*");
                intent.putExtra("mimeType", "image/*");
                intent.setComponent(componentName);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    WallpaperManager.getInstance(context.getApplicationContext())
                        .setBitmap(ImageUtil.getImageBitmap(path));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    intent =
                        WallpaperManager.getInstance(context.getApplicationContext()).getCropAndSetWallpaperIntent(uriPath);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                } catch (IllegalArgumentException e) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(context.getApplicationContext().getContentResolver(), uriPath);
                        if (bitmap != null) {
                            WallpaperManager.getInstance(context.getApplicationContext()).setBitmap(bitmap);
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                try {
                    WallpaperManager.getInstance(context.getApplicationContext())
                        .setBitmap(ImageUtil.getImageBitmap(path));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}