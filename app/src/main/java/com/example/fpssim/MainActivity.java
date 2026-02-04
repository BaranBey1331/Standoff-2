package com.example.fpssim;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Android 6.0 ve üzeri için izin kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // İzin yoksa kullanıcıdan iste
                Toast.makeText(this, "Lütfen 'Üzerinde Göster' iznini verin!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 123);
            } else {
                // İzin varsa servisi başlat
                startMenuService();
            }
        } else {
            startMenuService();
        }
    }

    private void startMenuService() {
        startService(new Intent(this, FloatingMenuService.class));
        finish(); // Ana ekranı kapat, sadece menü kalsın
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 123) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    startMenuService();
                } else {
                    Toast.makeText(this, "İzin verilmedi, uygulama kapanıyor.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
