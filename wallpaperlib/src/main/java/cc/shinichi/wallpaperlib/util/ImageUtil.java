package cc.shinichi.wallpaperlib.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author 工藤
 * @email gougou@16fan.com
 * cc.shinichi.wallpaperlib.util
 * create at 2018/12/27  16:38
 * description:
 */
public class ImageUtil {

    public static Bitmap getImageBitmap(String srcPath) {
        return BitmapFactory.decodeFile(srcPath);
    }
}