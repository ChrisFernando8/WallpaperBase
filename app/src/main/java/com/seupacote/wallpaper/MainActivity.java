package com.seupacote.wallpaper;

import android.app.WallpaperManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
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

        WallpaperAdapter adapter = new WallpaperAdapter(wallpapers, this::setWallpaper);
        recyclerView.setAdapter(adapter);
    }

    private void setWallpaper(int resId) {
        WallpaperManager manager = WallpaperManager.getInstance(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Definir papel de parede");
        builder.setItems(
                new CharSequence[]{"Tela inicial", "Tela de bloqueio", "Ambas"},
                (dialog, which) -> {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            if (which == 0)
                                manager.setResource(resId, WallpaperManager.FLAG_SYSTEM);
                            else if (which == 1)
                                manager.setResource(resId, WallpaperManager.FLAG_LOCK);
                            else
                                manager.setResource(resId);
                        } else {
                            manager.setResource(resId);
                        }
                        Toast.makeText(this, "Papel de parede aplicado", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(this, "Erro ao aplicar", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        builder.show();
    }
}
