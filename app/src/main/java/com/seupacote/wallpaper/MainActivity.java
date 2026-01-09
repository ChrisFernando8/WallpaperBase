package com.seupacote.wallpaper;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        List<Integer> wallpapers = Arrays.asList(
                R.drawable.wall_1,
                R.drawable.wall_2,
                R.drawable.wall_3
        );

        WallpaperAdapter adapter =
                new WallpaperAdapter(wallpapers, this::openSystemWallpaperPicker);

        recyclerView.setAdapter(adapter);
    }

    private void openSystemWallpaperPicker(int resId) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);

            File cacheDir = new File(getCacheDir(), "wallpapers");
            if (!cacheDir.exists()) cacheDir.mkdirs();

            File file = new File(cacheDir, "wallpaper.png");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            Uri uri = FileProvider.getUriForFile(
                    this,
                    "com.seupacote.wallpaper.provider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("mimeType", "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Definir papel de parede"));

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao abrir papel de parede", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
