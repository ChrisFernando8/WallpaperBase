package com.seupacote.wallpaper;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
     * 🔐 MÉTODO OFICIAL SAMSUNG / GOOGLE
     * Abre o seletor nativo do sistema
     */
    private void openSystemWallpaperPicker(int resId) {
        try {
            Uri uri = Uri.parse(
                    ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                            + getResources().getResourcePackageName(resId) + "/"
                            + getResources().getResourceTypeName(resId) + "/"
                            + getResources().getResourceEntryName(resId)
            );

            Intent intent = WallpaperManager
                    .getInstance(this)
                    .getCropAndSetWallpaperIntent(uri);

            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Erro ao abrir o seletor de papel de parede",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
