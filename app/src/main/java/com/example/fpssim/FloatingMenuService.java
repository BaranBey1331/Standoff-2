package com.example.fpssim;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout;

public class FloatingMenuService extends Service {

    static { System.loadLibrary("fpssim"); }
    public native void initNative(int w, int h);
    public native float[] getCalculatedData(); // C++'dan gelen gerçekçi veriler

    private WindowManager wm;
    private FrameLayout rootContainer; // İkon ve Menüyü tutan ana kapsayıcı
    private LinearLayout menuLayout;
    private TextView iconView; // Küçük ikon
    private ESPView espView;
    private WindowManager.LayoutParams params;

    private boolean isMenuOpen = true;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        initNative(metrics.widthPixels, metrics.heightPixels);

        // 1. ESP Katmanı (Tüm ekran)
        initESP();

        // 2. Menü ve İkon Sistemi
        initFloatingWidget();
    }

    private void initFloatingWidget() {
        // --- ANA KONTEYNER ---
        rootContainer = new FrameLayout(this);
        
        // --- A) KÜÇÜK İKON (LOGO) ---
        iconView = new TextView(this);
        iconView.setText("S2");
        iconView.setTextSize(18f);
        iconView.setTextColor(Color.WHITE);
        iconView.setBackgroundColor(Color.RED);
        iconView.setGravity(Gravity.CENTER);
        iconView.setPadding(20, 20, 20, 20);
        iconView.setVisibility(View.GONE); // Başlangıçta gizli
        // Yuvarlak İkon Yapalım
        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setShape(GradientDrawable.OVAL);
        iconBg.setColor(Color.RED);
        iconBg.setStroke(2, Color.WHITE);
        iconView.setBackground(iconBg);

        iconView.setOnClickListener(v -> toggleMenu(true)); // Tıklayınca menüyü aç
        rootContainer.addView(iconView, new FrameLayout.LayoutParams(120, 120));

        // --- B) MENÜ TASARIMI ---
        menuLayout = new LinearLayout(this);
        menuLayout.setOrientation(LinearLayout.VERTICAL);
        // Gradient Tasarım
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] {0xFF1F1F1F, 0xFF000000});
        gd.setCornerRadius(20f);
        gd.setStroke(2, 0xFF00FFFF);
        menuLayout.setBackground(gd);
        menuLayout.setPadding(30, 30, 30, 30);

        // Başlık
        TextView title = new TextView(this);
        title.setText("S2 EXTERNAL TOOL");
        title.setTextColor(Color.CYAN);
        title.setGravity(Gravity.CENTER);
        menuLayout.addView(title);

        // Özellikler
        CheckBox chkEsp = new CheckBox(this);
        chkEsp.setText("ESP Box");
        chkEsp.setTextColor(Color.WHITE);
        menuLayout.addView(chkEsp);

        CheckBox chkAim = new CheckBox(this);
        chkAim.setText("Aimbot (Touch Sim)");
        chkAim.setTextColor(Color.WHITE);
        menuLayout.addView(chkAim);

        // GİZLE BUTONU (Fix Burası)
        TextView btnHide = new TextView(this);
        btnHide.setText("▼ GİZLE");
        btnHide.setTextColor(Color.YELLOW);
        btnHide.setGravity(Gravity.CENTER);
        btnHide.setPadding(0, 20, 0, 0);
        btnHide.setOnClickListener(v -> toggleMenu(false)); // Tıklayınca menüyü kapat
        menuLayout.addView(btnHide);

        rootContainer.addView(menuLayout);

        // --- PENCERE AYARLARI ---
        int type = (Build.VERSION.SDK_INT >= 26) ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
        params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 100;
        params.y = 200;

        // Sürükleme Mantığı (Hem ikon hem menü sürüklenebilsin)
        View.OnTouchListener dragListener = new View.OnTouchListener() {
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
                        wm.updateViewLayout(rootContainer, params);
                        return true;
                }
                return false; // Click eventini engellememek için
            }
        };
        menuLayout.setOnTouchListener(dragListener);
        iconView.setOnTouchListener(dragListener);

        wm.addView(rootContainer, params);
    }

    // Menü <-> İkon Geçişi
    private void toggleMenu(boolean show) {
        isMenuOpen = show;
        if (show) {
            menuLayout.setVisibility(View.VISIBLE);
            iconView.setVisibility(View.GONE);
        } else {
            menuLayout.setVisibility(View.GONE);
            iconView.setVisibility(View.VISIBLE);
        }
    }

    private void initESP() {
        espView = new ESPView(this);
        WindowManager.LayoutParams p = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, (Build.VERSION.SDK_INT >= 26) ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        wm.addView(espView, p);
        
        final Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                espView.invalidate();
                h.postDelayed(this, 16);
            }
        });
    }

    private class ESPView extends View {
        Paint paint = new Paint();
        public ESPView(Context c) { super(c); paint.setColor(Color.GREEN); paint.setStyle(Paint.Style.STROKE); paint.setStrokeWidth(3f); }
        @Override
        protected void onDraw(Canvas canvas) {
            float[] data = getCalculatedData(); // C++'dan [x, y, distance, isLocked] al
            if(data == null) return;
            for(int i=0; i<data.length; i+=4) {
                float x = data[i];
                float y = data[i+1];
                boolean isLocked = data[i+3] > 0.5f;
                
                // Real Logic: Eğer kilitlendiyse renk Kırmızı olur
                paint.setColor(isLocked ? Color.RED : Color.GREEN);
                canvas.drawRect(x-50, y-100, x+50, y+100, paint);
                canvas.drawLine(getWidth()/2f, getHeight()/2f, x, y, paint); // Crosshair'den çizgi
            }
        }
    }
}
