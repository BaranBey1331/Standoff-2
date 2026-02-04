cmake_minimum_required(VERSION 3.18.1)

project("fpssim")

add_library( # Kütüphane adı
        fpssim
        # Kütüphane tipi (Shared = .so dosyası üretir)
        SHARED
        # Kaynak dosya
        native-lib.cpp)

find_library( # Android log kütüphanesini bul
        log-lib
        log)

target_link_libraries( # Kütüphaneleri bağla
        fpssim
        ${log-lib})

