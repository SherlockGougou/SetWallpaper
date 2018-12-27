/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhihu.matisse.internal.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.R;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.IncapableCause;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public final class PhotoMetadataUtils {
    private static final String TAG = PhotoMetadataUtils.class.getSimpleName();
    private static final int MAX_WIDTH = 1600;
    private static final String SCHEME_CONTENT = "content";

    private PhotoMetadataUtils() {
        throw new AssertionError("oops! the utility class is about to be instantiated...");
    }

    public static int getPixelsCount(ContentResolver resolver, Uri uri) {
        Point size = getBitmapBound(resolver, uri);
        return size.x * size.y;
    }

    public static Point getBitmapSize(Uri uri, Activity activity) {
        ContentResolver resolver = activity.getContentResolver();
        Point imageSize = getBitmapBound(resolver, uri);
        int w = imageSize.x;
        int h = imageSize.y;
        if (PhotoMetadataUtils.shouldRotate(resolver, uri)) {
            w = imageSize.y;
            h = imageSize.x;
        }
        if (h == 0) return new Point(MAX_WIDTH, MAX_WIDTH);
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float screenWidth = (float) metrics.widthPixels;
        float screenHeight = (float) metrics.heightPixels;
        float widthScale = screenWidth / w;
        float heightScale = screenHeight / h;
        if (widthScale > heightScale) {
            return new Point((int) (w * widthScale), (int) (h * heightScale));
        }
        return new Point((int) (w * widthScale), (int) (h * heightScale));
    }

    public static Point getBitmapBound(ContentResolver resolver, Uri uri) {
        InputStream is = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            is = resolver.openInputStream(uri);
            BitmapFactory.decodeStream(is, null, options);
            int width = options.outWidth;
            int height = options.outHeight;
            return new Point(width, height);
        } catch (FileNotFoundException e) {
            return new Point(0, 0);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getPath(ContentResolver resolver, Uri uri) {
        if (uri == null) {
            return null;
        }

        if (SCHEME_CONTENT.equals(uri.getScheme())) {
            Cursor cursor = null;
            try {
                cursor = resolver.query(uri, new String[]{MediaStore.Images.ImageColumns.DATA},
                        null, null, null);
                if (cursor == null || !cursor.moveToFirst()) {
                    return null;
                }
                return cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return uri.getPath();
    }

    public static IncapableCause isAcceptable(Context context, Item item) {
        if (!isSelectableType(context, item)) {
            return new IncapableCause(context.getString(R.string.error_file_type));
        }

        if (SelectionSpec.getInstance().filters != null) {
            for (Filter filter : SelectionSpec.getInstance().filters) {
                IncapableCause incapableCause = filter.filter(context, item);
                if (incapableCause != null) {
                    return incapableCause;
                }
            }
        }
        return null;
    }

    private static boolean isSelectableType(Context context, Item item) {
        if (context == null) {
            return false;
        }

        ContentResolver resolver = context.getContentResolver();
        for (MimeType type : SelectionSpec.getInstance().mimeTypeSet) {
            if (type.checkType(resolver, item.getContentUri())) {
                return true;
            }
        }
        return false;
    }

    private static boolean shouldRotate(ContentResolver resolver, Uri uri) {
        ExifInterface exif;
        try {
            exif = ExifInterfaceCompat.newInstance(getPath(resolver, uri));
        } catch (IOException e) {
            Log.e(TAG, "could not read exif info of the image: " + uri);
            return false;
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
        return orientation == ExifInterface.ORIENTATION_ROTATE_90
                || orientation == ExifInterface.ORIENTATION_ROTATE_270;
    }

    public static float getSizeInMB(long sizeInBytes) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        df.applyPattern("0.0");
        String result = df.format((float) sizeInBytes / 1024 / 1024);
        Log.e(TAG, "getSizeInMB: " + result);
        result = result.replaceAll(",", "."); // in some case , 0.0 will be 0,0
        return Float.valueOf(result);
    }

    public static int[] getWidthHeight(String imagePath) {
        if (imagePath.isEmpty()) {
            return new int[] { 0, 0 };
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            Bitmap originBitmap = BitmapFactory.decodeFile(imagePath, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 使用第一种方式获取原始图片的宽高
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        // 使用第二种方式获取原始图片的宽高
        if (srcHeight == -1 || srcWidth == -1) {
            try {
                android.support.media.ExifInterface exifInterface = new android.support.media.ExifInterface(imagePath);
                srcHeight = exifInterface.getAttributeInt(android.support.media.ExifInterface.TAG_IMAGE_LENGTH,
                    android.support.media.ExifInterface.ORIENTATION_NORMAL);
                srcWidth = exifInterface.getAttributeInt(android.support.media.ExifInterface.TAG_IMAGE_WIDTH,
                    android.support.media.ExifInterface.ORIENTATION_NORMAL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 使用第三种方式获取原始图片的宽高
        if (srcWidth <= 0 || srcHeight <= 0) {
            Bitmap bitmap2 = BitmapFactory.decodeFile(imagePath);
            if (bitmap2 != null) {
                srcWidth = bitmap2.getWidth();
                srcHeight = bitmap2.getHeight();
                try {
                    if (!bitmap2.isRecycled()) {
                        bitmap2.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        int orient = getOrientation(imagePath);
        if (orient == 90 || orient == 270) {
            return new int[] { srcHeight, srcWidth };
        }
        return new int[] { srcWidth, srcHeight };
    }

    public static float getImageRatio(String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        if (wh[0] > 0 && wh[1] > 0) {
            return (float) Math.max(wh[0], wh[1]) / (float) Math.min(wh[0], wh[1]);
        }
        return 1;
    }

    public static boolean isLongImage(Context context, String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        float w = wh[0];
        float h = wh[1];
        float imageRatio = (h / w);
        float phoneRatio = UIUtils.getPhoneRatio(context) + 0.1F;
        return (w > 0 && h > 0) && (h > w) && (imageRatio >= phoneRatio);
    }

    public static boolean isWideImage(Context context, String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        float w = wh[0];
        float h = wh[1];
        float imageRatio = (w / h);
        //float phoneRatio = DeviceUtils.getPhoneRatio(FanApplication.getInstance()) + 0.1F;
        return (w > 0 && h > 0) && (w > h) && (imageRatio >= 2);
    }

    public static boolean isSmallImage(Context context, String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        if (wh[0] < UIUtils.getPhoneWid(context)) {
            return true;
        }
        return false;
    }

    public static float getLongImageMinScale(Context context, String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        float imageWid = wh[0];
        float phoneWid = UIUtils.getPhoneWid(context);
        return phoneWid / imageWid;
    }

    public static float getLongImageMaxScale(Context context, String imagePath) {
        return getLongImageMinScale(context, imagePath) * 2;
    }

    public static float getSmallImageMinScale(Context context, String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        float imageWid = wh[0];
        float phoneWid = UIUtils.getPhoneWid(context);
        return phoneWid / imageWid;
    }

    public static float getSmallImageMaxScale(Context context, String imagePath) {
        return getSmallImageMinScale(context, imagePath) * 2;
    }

    public static float getWideImageMinScale(Context context, String imagePath) {
        return getWideImageDoubleScale(context, imagePath) / 2;
    }

    public static float getWideImageDoubleScale(Context context, String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        float imageHei = wh[1];
        float phoneHei = UIUtils.getPhoneHei(context);
        return phoneHei / imageHei;
    }

    private static int getOrientation(String imagePath) {
        try {
            ExifInterface exifInterface = new ExifInterface(imagePath);
            int orientation =
                exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}