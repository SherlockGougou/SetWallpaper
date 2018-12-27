package cc.shinichi.wallpaperdemo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import cc.shinichi.wallpaperlib.SetWallpaper;
import com.bumptech.glide.Glide;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private final String APP_AUTHORITY = "cc.shinichi.wallpaperdemo.fileprovider";

    private ImageView imageView;
    private String path = "";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                AndPermission.with(MainActivity.this)
                    .runtime()
                    .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
                    .onDenied(new Action<List<String>>() {
                        @Override public void onAction(List<String> data) {
                            Toast.makeText(MainActivity.this, "^_^ 请开启存储权限", Toast.LENGTH_SHORT).show();
                            AndPermission.with(MainActivity.this).runtime().setting().start();
                        }
                    })
                    .onGranted(new Action<List<String>>() {
                        @Override public void onAction(List<String> data) {
                            onGrant();
                        }
                    })
                    .start();
            }
        });

        imageView = findViewById(R.id.imageView);

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                SetWallpaper.setWallpaper(MainActivity.this, path, APP_AUTHORITY);
            }
        });
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                List<String> pathResult = Matisse.obtainPathResult(data);
                path = pathResult.get(0);

                Glide.with(MainActivity.this)
                    .load(path)
                    .into(imageView);
            }
        }
    }

    private void onGrant() {
        Set<MimeType> mimeTypes = MimeType.of(MimeType.JPEG, MimeType.BMP, MimeType.PNG, MimeType.WEBP);
        Matisse.from(MainActivity.this)
            .choose(mimeTypes)
            .capture(false)
            .captureStrategy(new CaptureStrategy(true, APP_AUTHORITY, "壁纸"))
            .autoHideToolbarOnSingleTap(true)
            .maxSelectable(1)
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            .thumbnailScale(0.85f)
            .imageEngine(new GlideEngine())
            .theme(R.style.Matisse_Zhihu)
            .showSingleMediaType(true)
            .forResult(1);
    }
}