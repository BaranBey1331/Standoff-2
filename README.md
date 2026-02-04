# FPS Mekanikleri Teknik Analiz ve Simülasyonu

Bu proje, FPS oyunlarındaki (örneğin Standoff 2) temel mekaniklerin teknik olarak nasıl çalıştığını anlamak için geliştirilmiş akademik amaçlı bir simülasyondur. Proje, Python dili kullanılarak 'External' (harici) yapı mantığına uygun olarak tasarlanmıştır.

## 🚀 İçerik ve Teknik Analiz

### 1. Bellek Yönetimi ve Veri Okuma
Oyunların çalışma zamanında (runtime) verileri RAM üzerinde saklanır. Harici bir yazılım, bu verilere erişmek için:
- **Pymem:** Python ile süreç (process) belleğine erişmek için kullanılan kütüphanedir.
- **Offsets (Sapmalar):** Oyun güncellendikçe değişen bellek adreslerini bulmak için statik baz adreslere eklenen değerlerdir.
- **Entity List:** Oyundaki tüm oyuncuların (düşman/dost) bilgilerinin (can, koordinat, isim) tutulduğu bir dizidir.

### 2. Wallhack (ESP) Mantığı
Dünya üzerindeki 3D koordinatların (X, Y, Z), oyuncunun 2D ekranına yansıtılması işlemine **World-to-Screen (W2S)** denir.
- **ViewMatrix:** Oyunun kamerasının bakış açısını, uzaklığını ve yönünü içeren 4x4'lük bir matristir.
- **Matematik:** 3D nokta, ViewMatrix ile çarpılarak 'Clip Space'e taşınır, ardından ekran çözünürlüğüne oranlanarak 2D koordinat elde edilir.

### 3. Aimbot (Kilitlenme) Matematiği
Aimbot, farenin veya görüş açısının düşman koordinatlarına otomatik olarak döndürülmesidir.
- **Yaw (Yatay Açı):** `atan2(delta_y, delta_x)` ile hesaplanır.
- **Pitch (Dikey Açı):** `atan2(delta_z, mesafe_2d)` ile hesaplanır.
- **Smoothing (Yumuşatma):** Keskin hareketleri önlemek ve 'Anti-Cheat' sistemlerine yakalanmamak için mevcut açı ile hedef açı arasında kademeli geçiş yapılır.

### 4. Harici Radar Sistemi
Oyun içi koordinatların, oyun penceresinden bağımsız bir radar ekranında gösterilmesidir.
- **Dönüşüm:** Oyuncunun kendi açısı (Yaw) referans alınarak düşman koordinatları döndürülür ve radar ölçeğine (scale) göre 2D düzleme yansıtılır.

### 5. Güvenlik ve Tespit Edilebilirlik (Anti-Cheat)
Anti-Cheat yazılımları (VAC, FaceIT, BattlEye) bu tür sistemleri şu yöntemlerle tespit eder:
- **Signature Scanning:** Bellekteki bilinen hile kod parçacıklarının aranması.
- **Overlay Detection:** Oyunun üzerinde çizim yapan pencerelerin (ESP/Radar) tespit edilmesi.
- **Heuristic Analysis:** İnsanüstü tepki süreleri veya imkansız vuruş açılarının analizi.

## 🛠 Kurulum ve Çalıştırma
Proje Kivy kütüphanesi ile UI desteği sunar.

```bash
pip install kivy
python menu.py
```

## 📱 Mobil Derleme (Android)
GitHub Actions kullanılarak **arm64-v8a** mimarisinde ve **Target SDK 36** olacak şekilde APK derlemesi yapılmaktadır.

---
*Not: Bu proje tamamen eğitim amaçlıdır. Oyunların kullanım şartlarını ihlal eden eylemler önerilmez.*
