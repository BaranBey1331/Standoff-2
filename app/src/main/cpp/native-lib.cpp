#include <jni.h>
#include <string>
#include <cmath>
#include <vector>
#include <android/log.h>

#define LOG_TAG "FPS_SIMULATOR"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

// --- 1. MATEMATİK KATMANI (VECTOR3) ---
struct Vector3 {
    float x, y, z;

    Vector3 operator-(const Vector3& other) const {
        return {x - other.x, y - other.y, z - other.z};
    }

    float distance(const Vector3& other) const {
        float dx = x - other.x;
        float dy = y - other.y;
        float dz = z - other.z;
        return std::sqrt(dx*dx + dy*dy + dz*dz);
    }
};

// --- 2. OYUN MANTIĞI (AIMBOT MATH) ---
class GameCore {
public:
    // Aimbot: İki 3D nokta arasındaki Yaw/Pitch açısını hesaplar
    static std::pair<float, float> calculateAimAngles(Vector3 localPos, Vector3 enemyPos) {
        Vector3 delta = enemyPos - localPos;
        float distance = std::sqrt(delta.x*delta.x + delta.y*delta.y);

        float yaw = std::atan2(delta.y, delta.x) * (180.0f / M_PI);
        float pitch = -std::atan2(delta.z, distance) * (180.0f / M_PI);

        return {yaw, pitch};
    }

    // Basit bir "Smoothing" (Yumuşatma) simülasyonu
    static float smoothAngle(float current, float target, float smoothFactor) {
        return current + (target - current) / smoothFactor;
    }
};

// --- 3. SİMÜLASYON VERİLERİ (MEMORY MOCK) ---
// Gerçek oyuna bağlanamadığımız için sanal verilerle çalışıyoruz (Crash'i önler)
Vector3 localPlayerHead = {0.0f, 0.0f, 1.8f};
Vector3 enemyHead = {10.0f, 5.0f, 1.8f}; // Düşman 10m ileride, 5m sağda

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_fpssim_MainActivity_processSimulation(JNIEnv* env, jobject /* this */) {
    
    // 1. Açıları Hesapla
    std::pair<float, float> angles = GameCore::calculateAimAngles(localPlayerHead, enemyHead);
    float targetYaw = angles.first;
    float targetPitch = angles.second;

    // 2. Düşmanı hareket ettir (Simülasyon)
    enemyHead.x += 0.1f; 
    if(enemyHead.x > 20.0f) enemyHead.x = 0.0f; // Reset

    // 3. Logla ve String oluştur
    std::string result = "--- FPS AKADEMİK ANALİZ ---\n";
    result += "Benim Konumum: [0, 0, 1.8]\n";
    result += "Düşman Konumu: [" + std::to_string(enemyHead.x) + ", " + std::to_string(enemyHead.y) + "]\n";
    result += "Hesaplanan Yaw: " + std::to_string(targetYaw) + "\n";
    result += "Hesaplanan Pitch: " + std::to_string(targetPitch) + "\n";
    result += "Durum: KİLİTLENDİ (LOCKED) ✅";

    LOGI("Aimbot Hesaplandı: Yaw=%f Pitch=%f", targetYaw, targetPitch);

    return env->NewStringUTF(result.c_str());
}
