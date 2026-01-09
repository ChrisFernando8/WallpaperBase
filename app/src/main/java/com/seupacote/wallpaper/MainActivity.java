package com.seupacote.wallpaper;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
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

        WallpaperAdapter adapter = new WallpaperAdapter(wallpapers, this::showWallpaperDialog);
        recyclerView.setAdapter(adapter);
    }

    private void showWallpaperDialog(int resId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Definir papel de parede");

        builder.setItems(
                new CharSequence[]{"Tela inicial", "Tela de bloqueio", "Ambas"},
                (dialog, which) -> applyWallpaper(resId, which)
        );

        builder.show();
    }

    private void applyWallpaper(int resId, int option) {
        WallpaperManager manager = WallpaperManager.getInstance(this);

        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (option == 0) {
                    manager.setBitmap(bitmap, null, true,
                            WallpaperManager.FLAG_SYSTEM);
                } else if (option == 1) {
                    manager.setBitmap(bitmap, null, true,
                            WallpaperManager.FLAG_LOCK);
                } else {
                    manager.setBitmap(bitmap, null, true,
                            WallpaperManager.FLAG_SYSTEM | WallpaperManager.FLAG_LOCK);
                }
            } else {
                manager.setBitmap(bitmap);
            }

            Toast.makeText(
                    this,
                    "Papel de parede aplicado com sucesso",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(
                    this,
                    "Erro ao aplicar papel de parede",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
