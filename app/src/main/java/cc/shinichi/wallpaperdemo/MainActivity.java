package cc.shinichi.wallpaperdemo;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.util.ArrayList;

import cc.shinichi.wallpaperlib.SetWallpaper;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private final String APP_AUTHORITY = "cc.shinichi.wallpaperdemo.fileprovider";

    private ImageView imageView;
    private String path = "";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onGrant();
            }
        });

        imageView = findViewById(R.id.imageView);

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (!TextUtils.isEmpty(path)) {
                    SetWallpaper.setWallpaper(MainActivity.this, path, APP_AUTHORITY);
                } else {
                    Toast.makeText(MainActivity.this, "请先选择壁纸", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onGrant() {
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setMaxSelectNum(1)
                .setImageEngine(GlideEngine.createGlideEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        if (result == null || result.size() == 0) {
                            return;
                        }
                        LocalMedia localMedia = result.get(0);
                        path = localMedia.getAvailablePath();
                        Log.d(TAG, "onResult: path = " + path);
                        Glide.with(imageView).load(path).into(imageView);
                    }

                    @Override
                    public void onCancel() {
                    }
                });
    }
}