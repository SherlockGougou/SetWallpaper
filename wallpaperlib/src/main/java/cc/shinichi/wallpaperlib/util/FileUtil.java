package cc.shinichi.wallpaperlib.util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

/**
 * @author 工藤
 * @email gougou@16fan.com
 * cc.shinichi.wallpaperlib.util
 * create at 2018/12/27  16:34
 * description:
 */
public class FileUtil {

    public static Uri getUriWithPath(Context context, String filepath, String authority) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 7.0以上的读取文件uri要用这种方式了
            return FileProvider.getUriForFile(context.getApplicationContext(), authority, new File(filepath));
        } else {
            return Uri.fromFile(new File(filepath));
        }
    }
}