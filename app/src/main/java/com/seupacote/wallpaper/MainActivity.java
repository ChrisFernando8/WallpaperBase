package com.seupacote.wallpaper;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final int TARGET_HOME = 0;
    private static final int TARGET_LOCK = 1;
    private static final int TARGET_BOTH = 2;

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

        WallpaperAdapter adapter = new WallpaperAdapter(
                wallpapers,
                this::showWallpaperOptions
        );

        recyclerView.setAdapter(adapter);
    }

    private void showWallpaperOptions(int resId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Definir papel de parede");

        builder.setItems(
                new CharSequence[]{"Tela inicial", "Tela de bloqueio", "Ambas"},
                (dialog, which) -> applyWallpaper(resId, which)
        );

        builder.show();
    }

    private void applyWallpaper(int resId, int target) {
        executor.execute(() -> {
            try {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);

                DisplayMetrics metrics = getResources().getDisplayMetrics();

                // 1️⃣ Ler dimensões sem carregar bitmap
                BitmapFactory.Options bounds = new BitmapFactory.Options();
                bounds.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(getResources(), resId, bounds);

                int inSampleSize = 1;
                int halfWidth = bounds.outWidth / 2;
                int halfHeight = bounds.outHeight / 2;

                while ((halfWidth / inSampleSize) >= metrics.widthPixels &&
                        (halfHeight / inSampleSize) >= metrics.heightPixels) {
                    inSampleSize *= 2;
                }

                // 2️⃣ Decodificar bitmap otimizado
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = inSampleSize;

                Bitmap bitmap = BitmapFactory.decodeResource(
                        getResources(),
                        resId,
                        options
                );

                if (bitmap == null) throw new RuntimeException("Bitmap nulo");

                // 3️⃣ Ajustar para o tamanho da tela
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        metrics.widthPixels,
                        metrics.heightPixels,
                        true
                );

                // 4️⃣ Aplicar wallpaper
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (target == TARGET_HOME) {
                        wallpaperManager.setBitmap(
                                scaledBitmap, null, true,
                                WallpaperManager.FLAG_SYSTEM
                        );
                    } else if (target == TARGET_LOCK) {
                        wallpaperManager.setBitmap(
                                scaledBitmap, null, true,
                                WallpaperManager.FLAG_LOCK
                        );
                    } else {
                        wallpaperManager.setBitmap(
                                scaledBitmap, null, true,
                                WallpaperManager.FLAG_SYSTEM | WallpaperManager.FLAG_LOCK
                        );
                    }
                } else {
                    wallpaperManager.setBitmap(scaledBitmap);
                }

                // 5️⃣ Liberar memória
                bitmap.recycle();
                scaledBitmap.recycle();

                runOnUiThread(() ->
                        Toast.makeText(
                                this,
                                "Papel de parede aplicado",
                                Toast.LENGTH_SHORT
                        ).show()
                );

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(
                                this,
                                "Erro ao aplicar papel de parede",
                                Toast.LENGTH_SHORT
                        ).show()
                );
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
