package com.seupacote.wallpaper;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.Resources;
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

    private int selectedResId = -1;

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
        selectedResId = resId;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Definir papel de parede");
        builder.setItems(
                new CharSequence[]{"Tela inicial", "Tela de bloqueio", "Ambas"},
                (dialog, which) -> applyWallpaperSamsung()
        );
        builder.show();
    }

    /**
     * MÉTODO OFICIAL — FUNCIONA EM SAMSUNG / ANDROID 13+
     */
    private void applyWallpaperSamsung() {
        try {
            // Copia o drawable para um arquivo temporário
            Uri uri = copyDrawableToCache(selectedResId);

            WallpaperManager wallpaperManager =
                    WallpaperManager.getInstance(this);

            Intent intent =
                    wallpaperManager.getCropAndSetWallpaperIntent(uri);

            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Erro ao aplicar papel de parede",
                    Toast.LENGTH_SHORT
            ).show();
            e.printStackTrace();
        }
    }

    /**
     * Converte drawable em arquivo para uso pelo sistema
     */
    private Uri copyDrawableToCache(int resId) throws Exception {
        File cacheDir = new File(getCacheDir(), "wallpapers");
        if (!cacheDir.exists()) cacheDir.mkdirs();

        File file = new File(cacheDir, "wallpaper.png");

        Resources res = getResources();
        InputStream inputStream = res.openRawResource(resId);
        FileOutputStream outputStream = new FileOutputStream(file);

        byte[] buffer = new byte[4096];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }

        inputStream.close();
        outputStream.flush();
        outputStream.close();

        return FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                file
        );
    }
}
