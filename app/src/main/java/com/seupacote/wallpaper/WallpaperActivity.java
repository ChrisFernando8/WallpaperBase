package com.seupacote.wallpaper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;

public class WallpaperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);

        ImageView preview = findViewById(R.id.previewImage);

        int resId = getIntent().getIntExtra("wallpaperRes", -1);
        if (resId == -1) {
            finish();
            return;
        }

        preview.setImageResource(resId);

        preview.setOnClickListener(v -> openSystemWallpaperEditor(resId));
    }

    private void openSystemWallpaperEditor(int resId) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);

            File cacheFile = new File(getCacheDir(), "wallpaper.png");
            FileOutputStream fos = new FileOutputStream(cacheFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    cacheFile
            );

            Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
            intent.setDataAndType(uri, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Definir papel de parede"));

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao abrir editor de wallpaper", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
