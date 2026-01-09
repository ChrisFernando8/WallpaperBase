package com.seupacote.wallpaper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    // 🔹 Diálogo simples (Samsung usa o próprio editor depois)
    private void showWallpaperOptions(int resId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Definir papel de parede");
        builder.setItems(
                new CharSequence[]{"Tela inicial", "Tela de bloqueio", "Ambas"},
                (dialog, which) -> applyWallpaperSamsung(resId)
        );
        builder.show();
    }

    // 🔥 MÉTODO OFICIAL QUE FUNCIONA NO SAMSUNG / KNOX
    private void applyWallpaperSamsung(int resId) {
        try {
            // 1️⃣ Criar diretório temporário
            File dir = new File(getCacheDir(), "wallpapers");
            if (!dir.exists()) dir.mkdirs();

            // 2️⃣ Criar arquivo temporário
            File file = new File(dir, "wallpaper.png");

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            // 3️⃣ Criar URI segura
            Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    file
            );

            // 4️⃣ Abrir editor oficial do sistema
            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("mimeType", "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Definir papel de parede"));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao aplicar wallpaper", Toast.LENGTH_SHORT).show();
        }
    }
}
