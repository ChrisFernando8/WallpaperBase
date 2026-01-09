package com.seupacote.wallpaper;

import android.app.WallpaperManager;
import android.content.res.Resources;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

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
                (dialog, which) -> {
                    if (which == 0) applyWallpaper(resId, "home");
                    else if (which == 1) applyWallpaper(resId, "lock");
                    else applyWallpaper(resId, "both");
                }
        );
        builder.show();
    }

    private void applyWallpaper(int resId, String target) {
        executor.execute(() -> {
            try {
                WallpaperManager wallpaperManager =
                        WallpaperManager.getInstance(MainActivity.this);

                Resources resources = getResources();
                Resources.DisplayMetrics metrics = resources.getDisplayMetrics();

                // 1️⃣ Ler apenas dimensões
                BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
                boundsOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(resources, resId, boundsOptions);

                // 2️⃣ Calcular inSampleSize
                int inSampleSize = 1;
                int halfWidth = boundsOptions.outWidth / 2;
                int halfHeight = boundsOptions.outHeight / 2;

                while ((halfWidth / inSampleSize) >= metrics.widthPixels &&
                       (halfHeight / inSampleSize) >= metrics.heightPixels) {
                    inSampleSize *= 2;
                }

                // 3️⃣ Decodificar bitmap reduzido
                BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
                decodeOptions.inSampleSize = inSampleSize;
                Bitmap bitmap = BitmapFactory.decodeResource(resources, resId, decodeOptions);

                if (bitmap == null) throw new RuntimeException("Bitmap inválido");

                // 4️⃣ Escalar exatamente para tela
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        metrics.widthPixels,
                        metrics.heightPixels,
                        true
                );

                bitmap.recycle();

                // 5️⃣ Aplicar wallpaper
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if ("home".equals(target)) {
                        wallpaperManager.setBitmap(
                                scaledBitmap, null, true,
                                WallpaperManager.FLAG_SYSTEM
                        );
                    } else if ("lock".equals(target)) {
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

                runOnUiThread(() ->
                        Toast.makeText(
                                MainActivity.this,
                                "Papel de parede aplicado",
                                Toast.LENGTH_SHORT
                        ).show()
                );

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(
                                MainActivity.this,
                                "Erro ao aplicar papel de parede",
                                Toast.LENGTH_SHORT
                        ).show()
                );
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
