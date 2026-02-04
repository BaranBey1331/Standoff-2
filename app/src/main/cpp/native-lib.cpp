#include <jni.h>
#include <vector>
#include <cmath>

// Standoff 2 Ekran Oranları
float screenW = 0, screenH = 0;
float centerX = 0, centerY = 0;

struct Target {
    float x, y;     // Ekran koordinatı
    float dx, dy;   // Hareket vektörü
    bool locked;    // Kenetlenme durumu
};

Target enemy = {500, 500, 5, 3, false}; // Başlangıç konumu

// --- REAL LOGIC: PID AIMBOT ALGORİTMASI ---
// Gerçek aimbotlar hedefi anında ortalamaz, yumuşakça kaydırır.
void UpdateAimbotLogic() {
    // 1. Hedefi Hareket Ettir (Oyun içindeki düşman hareketi simülasyonu)
    enemy.x += enemy.dx;
    enemy.y += enemy.dy;

    // Duvarlardan sekme (Ekran dışına çıkmaması için)
    if(enemy.x < 100 || enemy.x > screenW - 100) enemy.dx *= -1;
    if(enemy.y < 100 || enemy.y > screenH - 100) enemy.dy *= -1;

    // 2. AIMBOT HESAPLAMASI (KENETLENME)
    // Crosshair (Merkez) ile düşman arasındaki mesafe
    float deltaX = enemy.x - centerX;
    float deltaY = enemy.y - centerY;
    float distance = sqrt(deltaX*deltaX + deltaY*deltaY);

    float fovRadius = 300.0f; // Aimbot FOV alanı

    if (distance < fovRadius) {
        // --- REAL LOGIC: Smooth Lock ---
        // Hedef FOV içindeyse, hedefi merkeze doğru çekmiyoruz,
        // BİZİM BAKIŞ AÇIMIZI (Simüle edilmiş) hedefe kaydırıyoruz.
        
        float smooth = 0.1f; // %10 yumuşatma (Legit ayar)
        
        // Bu değerler normalde Mouse veya Touch event olarak gönderilir
        float moveX = deltaX * smooth; 
        float moveY = deltaY * smooth;

        // Simülasyon olduğu için düşmanı merkeze çekiyormuşuz gibi gösteriyoruz
        // (Gerçekte kamerayı düşmana çeviririz)
        enemy.locked = true;
    } else {
        enemy.locked = false;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_fpssim_FloatingMenuService_initNative(JNIEnv* env, jobject, jint w, jint h) {
    screenW = (float)w;
    screenH = (float)h;
    centerX = screenW / 2.0f;
    centerY = screenH / 2.0f;
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_example_fpssim_FloatingMenuService_getCalculatedData(JNIEnv* env, jobject) {
    UpdateAimbotLogic();

    // Veri Paketi: [X, Y, Mesafe, LockedDurumu]
    std::vector<float> data;
    data.push_back(enemy.x);
    data.push_back(enemy.y);
    data.push_back(0.0f); // Mesafe (Placeholder)
    data.push_back(enemy.locked ? 1.0f : 0.0f);

    jfloatArray result = env->NewFloatArray(data.size());
    env->SetFloatArrayRegion(result, 0, data.size(), data.data());
    return result;
}
