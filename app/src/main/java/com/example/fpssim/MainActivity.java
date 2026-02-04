package com.example.fpssim;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Kütüphaneyi yükle (libfpssim.so)
    static {
        System.loadLibrary("fpssim");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Basit bir UI oluştur (XML kullanmadan, kod ile)
        TextView tv = new TextView(this);
        tv.setText(stringFromJNI());
        tv.setTextSize(20f);
        setContentView(tv);

        // Bellek işlemlerini başlat
        initMemory();
    }

    // Native metod tanımları
    public native String stringFromJNI();
    public native void initMemory();
}

