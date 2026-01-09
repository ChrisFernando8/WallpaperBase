package com.seupacote.wallpaper;

import android.app.WallpaperManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.IOException;

public class WallpaperActivity extends AppCompatActivity {

    private int wallpaperRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);

        ImageView imageView = findViewById(R.id.fullWallpaper);

        // 🔴 VALIDAÇÃO CRÍTICA (impede crash)
        if (!getIntent().hasExtra("wallpaperRes")) {
            Toast.makeText(this, "Wallpaper inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        wallpaperRes = getIntent().getIntExtra("wallpaperRes", 0);

        if (wallpaperRes == 0) {
            Toast.makeText(this, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Carrega imagem
        Glide.with(this)
                .load(wallpaperRes)
                .into(imageView);

        // Clique para aplicar papel de parede
        imageView.setOnClickListener(v -> applyWallpaper());
    }

    private void applyWallpaper() {
        WallpaperManager manager = WallpaperManager.getInstance(this);
        try {
            manager.setResource(wallpaperRes);
            Toast.makeText(this, "Papel de parede aplicado!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Erro ao aplicar papel de parede", Toast.LENGTH_SHORT).show();
        }
    }
}
