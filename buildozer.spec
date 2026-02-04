[app]

# Uygulama adı
title = CheatSimulator

# Paket adı (Benzersiz olmalı)
package.name = cheatsim
package.domain = org.test

# Kaynak kodun olduğu yer (Nokta şu anki klasör demek)
source.dir = .

# Dahil edilecek dosya uzantıları
source.include_exts = py,png,jpg,kv,atlas

# Versiyon
version = 0.1

# GEREKSİNİMLER (Burası kritik)
# pyjnius: Java API'lerine erişim için (Secure Flag için şart)
# kivy: Arayüz için
requirements = python3,kivy==2.2.1,pyjnius,android

# İZİNLER
# INTERNET: Socket bağlantısı için
# SYSTEM_ALERT_WINDOW: Menülerin üstte durması gerekirse
android.permissions = INTERNET,SYSTEM_ALERT_WINDOW

# Başlangıç ekranı (Presplash) rengi
android.presplash_color = #000000

# Android API Hedefi (API 33-34 günceldir)
android.api = 33
android.minapi = 21

# Mimariler (Hepsini desteklesin)
android.archs = arm64-v8a, armeabi-v7a

# Kivy'nin loglarını görebilmek için
android.logcat_filters = *:S python:D

[buildozer]
log_level = 2
warn_on_root = 1

