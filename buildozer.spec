[app]
title = FPS Simulation
package.name = fps_sim
package.domain = org.educational
source.dir = .
source.include_exts = py,png,jpg,kv,atlas
version = 0.1
requirements = python3,kivy

orientation = landscape
osx.python_version = 3
osx.kivy_version = 1.9.1
fullscreen = 0

# Android specific
android.api = 35
android.minapi = 21
android.ndk = 25b
android.archs = arm64-v8a
android.allow_backup = True

# Python for android
p4a.branch = master

[buildozer]
log_level = 2
warn_on_root = 1
