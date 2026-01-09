package com.seupacote.wallpaper;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WallpaperActivity extends AppCompatActivity {

    private int wallpaperResId;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);

        executor = Executors.newSingleThreadExecutor();

        ImageView imageView = findViewById(R.id.previewImage);

        // Recebe o ID do wallpaper
        wallpaperResId = getIntent().getIntExtra("wallpaperRes", -1);

        if (wallpaperResId == -1) {
            Toast.makeText(this, "Imagem inválida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        imageView.setImageResource(wallpaperResId);

        imageView.setOnClickListener(v -> showApplyDialog());
    }

    private void showApplyDialog() {
        String[] options = {"Tela inicial", "Tela de bloqueio", "Ambas"};

        new AlertDialog.Builder(this)
                .setTitle("Definir papel de parede")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) applyWallpaper(WallpaperManager.FLAG_SYSTEM);
                    else if (which == 1) applyWallpaper(WallpaperManager.FLAG_LOCK);
                    else applyWallpaper(
                            WallpaperManager.FLAG_SYSTEM | WallpaperManager.FLAG_LOCK
                    );
                })
                .show();
    }

    private void applyWallpaper(int flag) {
        executor.execute(() -> {
            try {
                WallpaperManager manager = WallpaperManager.getInstance(this);

                Bitmap bitmap = decodeBitmapToScreenSize();

                if (bitmap == null) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    manager.setBitmap(bitmap, null, true, flag);
                } else {
                    manager.setBitmap(bitmap);
                }

                runOnUiThread(() ->
                        Toast.makeText(this, "Wallpaper aplicado!", Toast.LENGTH_SHORT).show()
                );

            } catch (IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Falha ao aplicar wallpaper", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    /**
     * Decodifica a imagem no tamanho exato da tela
     * (ESSENCIAL para Samsung – evita crash e tela preta)
     */
    private Bitmap decodeBitmapToScreenSize() {
        Resources res = getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, wallpaperResId, bounds);

        int inSampleSize = 1;
        while ((bounds.outWidth / inSampleSize) > metrics.widthPixels ||
               (bounds.outHeight / inSampleSize) > metrics.heightPixels) {
            inSampleSize *= 2;
        }

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = inSampleSize;

        Bitmap decoded = BitmapFactory.decodeResource(res, wallpaperResId, opts);

        if (decoded == null) return null;

        return Bitmap.createScaledBitmap(
                decoded,
                metrics.widthPixels,
                metrics.heightPixels,
                true
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
