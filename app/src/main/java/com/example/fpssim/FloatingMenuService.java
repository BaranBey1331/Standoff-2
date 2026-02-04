package com.example.fpssim;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FloatingMenuService extends Service {

    private WindowManager windowManager;
    private View floatingView;
    private View menuLayout;
    private boolean isMenuVisible = false;

    static {
        System.loadLibrary("fpssim");
    }
    public native String getAimStatus();

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // --- 1. KÜÇÜK İKON (LOGO) ---
        // Bu buton her zaman ekranda durur, tıklayınca menüyü açar.
        Button iconBtn = new Button(this);
        iconBtn.setText("S2");
        iconBtn.setBackgroundColor(Color.RED);
        iconBtn.setTextColor(Color.WHITE);
        
        // --- 2. ANA MENÜ (MOD PANELİ) ---
        menuLayout = createMenuLayout();
        menuLayout.setVisibility(View.GONE); // Başlangıçta gizli

        // --- WINDOW MANAGER AYARLARI ---
        int layoutType;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutType = WindowManager.LayoutParams.TYPE_PHONE;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // Oyuna dokunmayı engelleme
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        // Container (İkon + Menü)
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.addView(iconBtn);
        rootLayout.addView(menuLayout);
        
        floatingView = rootLayout;
        windowManager.addView(floatingView, params);

        // --- SÜRÜKLEME VE TIKLAMA MANTIĞI ---
        iconBtn.setOnClickListener(v -> {
            isMenuVisible = !isMenuVisible;
            menuLayout.setVisibility(isMenuVisible ? View.VISIBLE : View.GONE);
        });

        iconBtn.setOnTouchListener(new View.OnTouchListener() {
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
                        windowManager.updateViewLayout(floatingView, params);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Eğer sürükleme çok azsa tıklama say
                        if (Math.abs(event.getRawX() - initialTouchX) < 10) {
                            v.performClick();
                        }
                        return true;
                }
                return false;
            }
        });
    }

    private LinearLayout createMenuLayout() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.parseColor("#CC000000")); // Yarı saydam siyah
        layout.setPadding(20, 20, 20, 20);

        TextView title = new TextView(this);
        title.setText("STANDOFF 2 TOOL");
        title.setTextColor(Color.CYAN);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        CheckBox chkEsp = new CheckBox(this);
        chkEsp.setText("ESP (Box)");
        chkEsp.setTextColor(Color.WHITE);
        layout.addView(chkEsp);

        CheckBox chkAimbot = new CheckBox(this);
        chkAimbot.setText("Aimbot (Legit)");
        chkAimbot.setTextColor(Color.WHITE);
        layout.addView(chkAimbot);
        
        // Native Test Butonu
        Button btnTest = new Button(this);
        btnTest.setText("Durum Kontrol");
        btnTest.setOnClickListener(v -> {
             chkAimbot.setText(getAimStatus());
        });
        layout.addView(btnTest);

        return layout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) windowManager.removeView(floatingView);
    }
}

