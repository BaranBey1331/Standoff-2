package com.example.fpssim;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FloatingMenuService extends Service {

    // Native Bağlantılar
    static { System.loadLibrary("fpssim"); }
    public native void initNative(int width, int height);
    public native float[] getEnemyPositions();

    private WindowManager wm;
    private FrameLayout overlayContainer; // Hem Menü Hem ESP çizim alanı
    private LinearLayout menuLayout;
    private ESPView espView;
    private boolean isMenuVisible = true;
    private boolean isEspEnabled = false;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        // Ekran Boyutlarını Al ve Native'e Gönder
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        initNative(metrics.widthPixels, metrics.heightPixels);

        // --- 1. ESP KATMANI (Tüm Ekranı Kaplar) ---
        espView = new ESPView(this);
        WindowManager.LayoutParams espParams = getParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        // Dokunmaları geçir (Pass-through) ki oyunu oynayabilelim
        espParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wm.addView(espView, espParams);

        // --- 2. MENU KATMANI ---
        createMenu();
        
        // ESP Döngüsünü Başlat
        final Handler handler = new Handler(Looper.getMainLooper());
        Runnable drawRunnable = new Runnable() {
            @Override
            public void run() {
                if(isEspEnabled) espView.invalidate(); // Ekrani yenile (onDraw çağırır)
                handler.postDelayed(this, 16); // 60 FPS (~16ms)
            }
        };
        handler.post(drawRunnable);
    }

    // --- PRO UI TASARIMI ---
    private void createMenu() {
        // Ana Container
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        // Gradient Arka Plan (Siyah -> Koyu Gri)
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {0xFF1A1A1A, 0xFF000000});
        gd.setCornerRadius(30f);
        gd.setStroke(2, 0xFF00FFFF); // Cyan Çerçeve
        root.setBackground(gd);
        root.setPadding(30, 30, 30, 30);
        
        // Başlık
        TextView title = new TextView(this);
        title.setText("⚡ PROJECT GODMODE");
        title.setTextColor(Color.CYAN);
        title.setTextSize(16f);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0,0,0,20);
        root.addView(title);

        // Checkbox Tasarımı
        CheckBox chkEsp = new CheckBox(this);
        chkEsp.setText("ESP Box [Visual]");
        chkEsp.setTextColor(Color.WHITE);
        chkEsp.setOnCheckedChangeListener((btn, isChecked) -> isEspEnabled = isChecked);
        root.addView(chkEsp);

        CheckBox chkLine = new CheckBox(this);
        chkLine.setText("Snaplines");
        chkLine.setTextColor(Color.WHITE);
        root.addView(chkLine);

        // Kapat Butonu (Simge)
        TextView closeBtn = new TextView(this);
        closeBtn.setText("MENÜYÜ GİZLE / AÇ");
        closeBtn.setTextColor(Color.RED);
        closeBtn.setGravity(Gravity.CENTER);
        closeBtn.setPadding(0, 20, 0, 0);
        closeBtn.setOnClickListener(v -> {
            // Gizle/Göster Mantığı eklenebilir
            stopSelf(); // Şimdilik uygulamayı kapatır
        });
        root.addView(closeBtn);

        // Pencere Ayarları
        WindowManager.LayoutParams params = getParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 100;
        params.y = 200;
        
        // Sürükleme Mantığı
        root.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        wm.updateViewLayout(root, params);
                        return true;
                }
                return false;
            }
        });

        wm.addView(root, params);
    }

    private WindowManager.LayoutParams getParams(int w, int h) {
        int type = (Build.VERSION.SDK_INT >= 26) ? 
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : 
            WindowManager.LayoutParams.TYPE_PHONE;
        
        return new WindowManager.LayoutParams(w, h, type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
    }

    // --- ESP ÇİZİM KATMANI (CANVAS) ---
    private class ESPView extends View {
        Paint boxPaint, linePaint, textPaint;

        public ESPView(Context context) {
            super(context);
            // Kutu Kalemi
            boxPaint = new Paint();
            boxPaint.setColor(Color.CYAN);
            boxPaint.setStyle(Paint.Style.STROKE);
            boxPaint.setStrokeWidth(3f);

            // Çizgi Kalemi
            linePaint = new Paint();
            linePaint.setColor(Color.GREEN);
            linePaint.setStrokeWidth(2f);
            
            // Text Kalemi
            textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(30f);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            // Ekranı temizle (Gerek yok, transparent zaten)
            if(!isEspEnabled) return;

            // C++'dan verileri al
            float[] enemies = getEnemyPositions();
            if (enemies == null) return;

            // Düşmanları Çiz (Her 2 float = 1 Düşman X,Y)
            for(int i=0; i < enemies.length; i+=2) {
                float x = enemies[i];
                float y = enemies[i+1];

                // 1. Kutu Çiz (Kafadan aşağı doğru bir kutu simülasyonu)
                float boxHeight = 200; 
                float boxWidth = 100;
                canvas.drawRect(x - boxWidth/2, y, x + boxWidth/2, y + boxHeight, boxPaint);

                // 2. Snapline (Ekran altından düşmana çizgi)
                canvas.drawLine(getWidth()/2f, getHeight(), x, y + boxHeight, linePaint);

                // 3. Mesafe Yazısı
                canvas.drawText("Enemy [" + (int)x + "]", x, y - 10, textPaint);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Viewleri temizle
    }
}
