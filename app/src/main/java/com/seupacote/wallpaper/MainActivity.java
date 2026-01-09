package com.seupacote.wallpaper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
                new WallpaperAdapter(wallpapers, this::showWallpaperOptions);

        recyclerView.setAdapter(adapter);
    }

    private void showWallpaperOptions(int resId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Definir papel de parede");
        builder.setItems(
                new CharSequence[]{"Tela inicial", "Tela de bloqueio", "Ambas"},
                (dialog, which) -> openSystemWallpaperPicker(resId)
        );
        builder.show();
    }

    /**
     * MÉTODO SEGURO PARA SAMSUNG / ANDROID 13+
     * Abre o editor oficial do sistema
     */
    private void openSystemWallpaperPicker(int resId) {
        try {
            // Criar arquivo temporário
            File cacheDir = new File(getCacheDir(), "wallpapers");
            if (!cacheDir.exists()) cacheDir.mkdirs();

            File file = new File(cacheDir, "wallpaper.png");

            InputStream inputStream = getResources().openRawResource(resId);
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            inputStream.close();
            outputStream.close();

            Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
            intent.setDataAndType(uri, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Definir papel de parede"));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao abrir o papel de parede", Toast.LENGTH_SHORT).show();
        }
    }
}
