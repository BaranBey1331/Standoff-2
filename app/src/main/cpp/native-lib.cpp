#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_fpssim_FloatingMenuService_getAimStatus(JNIEnv* env, jobject /* this */) {
    // Burada ileride bellek okuma işlemleri yapılır.
    // Şimdilik sadece menünün çalıştığını doğruluyoruz.
    return env->NewStringUTF("Aimbot: AKTİF (Logic Ready)");
}
