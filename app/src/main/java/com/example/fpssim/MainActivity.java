package com.example.fpssim;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    // Kütüphane yükleme işlemi (Hata korumalı)
    private boolean isLibLoaded = false;
    static {
        try {
            System.loadLibrary("fpssim");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    private TextView statusText;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isRunning = false;

    // Native fonksiyon tanımı
    public native String processSimulation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- UI OLUŞTURMA (XML YOK, SAF KOD) ---
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.parseColor("#121212")); // Koyu Tema

        // Başlık
        TextView title = new TextView(this);
        title.setText("FPS MECHANIC SIMULATOR");
        title.setTextColor(Color.CYAN);
        title.setTextSize(24f);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        // Durum Ekranı
        statusText = new TextView(this);
        statusText.setText("Sistem Beklemede...");
        statusText.setTextColor(Color.GREEN);
        statusText.setTextSize(16f);
        statusText.setPadding(50, 50, 50, 50);
        layout.addView(statusText);

        // Başlat Butonu
        Button btnStart = new Button(this);
        btnStart.setText("SİMÜLASYONU BAŞLAT");
        btnStart.setBackgroundColor(Color.DKGRAY);
        btnStart.setTextColor(Color.WHITE);
        btnStart.setOnClickListener(v -> toggleSimulation());
        layout.addView(btnStart);

        setContentView(layout);
    }

    private void toggleSimulation() {
        isRunning = !isRunning;
        if (isRunning) {
            statusText.setText("Simülasyon Başlatılıyor...");
            runSimulationLoop();
        } else {
            statusText.setText("Simülasyon Durduruldu.");
        }
    }

    private void runSimulationLoop() {
        if (!isRunning) return;

        try {
            // C++ Tarafından hesaplanan veriyi al
            String debugData = processSimulation();
            statusText.setText(debugData);
        } catch (UnsatisfiedLinkError e) {
            statusText.setText("HATA: Native Kütüphane Yüklenemedi!");
            return;
        } catch (Exception e) {
            statusText.setText("CRASH ÖNLENDİ: " + e.getMessage());
            return;
        }

        // 100ms sonra tekrar çalıştır (10 FPS döngü)
        handler.postDelayed(this::runSimulationLoop, 100);
    }
}
