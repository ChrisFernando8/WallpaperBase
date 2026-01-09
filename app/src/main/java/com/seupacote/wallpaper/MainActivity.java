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

    private static final String AUTHORITY = "com.seupacote.wallpaper.provider";

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
                new WallpaperAdapter(wallpapers, this::showOptions);

        recyclerView.setAdapter(adapter);
    }

    private void showOptions(int resId) {
        new AlertDialog.Builder(this)
                .setTitle("Definir papel de parede")
                .setItems(
                        new CharSequence[]{"Tela inicial", "Tela de bloqueio", "Ambas"},
                        (dialog, which) -> openSamsungWallpaperEditor(resId)
                )
                .show();
    }

    private void openSamsungWallpaperEditor(int resId) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2; // evita crash por memória

            Bitmap bitmap = BitmapFactory.decodeResource(
                    getResources(),
                    resId,
                    options
            );

            File dir = new File(getCacheDir(), "wallpapers");
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, "wallpaper.jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            Uri uri = FileProvider.getUriForFile(
                    this,
                    AUTHORITY,
                    file
            );

            Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
            intent.setDataAndType(uri, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao abrir editor de papel de parede", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
