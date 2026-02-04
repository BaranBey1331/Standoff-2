#include <jni.h>
#include <vector>
#include <cmath>
#include <cstdlib>

// --- MATEMATİK MOTORU ---
struct Vector3 { float x, y, z; };
struct Vector2 { float x, y; };

// Basit bir 4x4 Matrix Simülasyonu (Kamera Açısı)
float viewMatrix[16] = {
    1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1
};

// Düşman Yapısı
struct Enemy {
    Vector3 position;
    Vector3 velocity; // Hareket yönü
    bool isAlive;
};

std::vector<Enemy> enemies;
int screenWidth = 1920;
int screenHeight = 1080;

// World to Screen (Dünya -> Ekran Dönüşümü)
bool WorldToScreen(Vector3 pos, Vector2& screen, int width, int height) {
    // Basit perspektif simülasyonu (Gerçek oyunlarda Matrix çarpımı yapılır)
    // Burada düşmanın ekranın ortasına göre konumunu hesaplıyoruz
    
    // Z ekseni derinliktir. Eğer arkamızdaysa çizme.
    if (pos.z < 0.1f) return false;

    float fov = 1000.0f; // Field of View katsayısı
    
    // 3D Perspektif Bölmesi
    screen.x = (pos.x * fov) / pos.z + (width / 2.0f);
    screen.y = (pos.y * fov) / pos.z + (height / 2.0f);

    return true;
}

// Oyunu Başlat / Resetle
void InitSimulation() {
    enemies.clear();
    // 5 Adet sahte düşman oluştur
    for(int i=0; i<5; i++) {
        Enemy e;
        e.position = { (float)(rand()%20 - 10), (float)(rand()%10 - 5), (float)(rand()%20 + 5) }; // X, Y, Z
        e.velocity = { 0.05f, 0.02f, 0.0f };
        e.isAlive = true;
        enemies.push_back(e);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_fpssim_FloatingMenuService_initNative(JNIEnv* env, jobject, jint w, jint h) {
    screenWidth = w;
    screenHeight = h;
    InitSimulation();
}

// Java'ya Düşman Koordinatlarını Gönder (ESP İçin)
// Format: [x1, y1, x2, y2, ...]
extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_example_fpssim_FloatingMenuService_getEnemyPositions(JNIEnv* env, jobject) {
    std::vector<float> rawData;

    for (auto &e : enemies) {
        // 1. Düşmanı Hareket Ettir (Logic)
        e.position.x += e.velocity.x;
        e.position.y += e.velocity.y;

        // Ekran dışına çıkarsa yön değiştir (Ping-Pong hareketi)
        if(e.position.x > 10 || e.position.x < -10) e.velocity.x *= -1;
        if(e.position.y > 5 || e.position.y < -5) e.velocity.y *= -1;

        // 2. Ekrana Çevir (W2S)
        Vector2 screenPos;
        if(WorldToScreen(e.position, screenPos, screenWidth, screenHeight)) {
            rawData.push_back(screenPos.x);
            rawData.push_back(screenPos.y);
        }
    }

    // C++ Vector -> Java Float Array Dönüşümü
    jfloatArray result = env->NewFloatArray(rawData.size());
    env->SetFloatArrayRegion(result, 0, rawData.size(), rawData.data());
    return result;
}
