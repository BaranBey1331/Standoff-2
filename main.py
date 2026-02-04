from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.button import Button
from kivy.uix.label import Label
from kivy.uix.switch import Switch
from kivy.utils import platform

# Android API'lerine erişim (Sadece Android'de çalışır)
if platform == 'android':
    from jnius import autoclass
    from android.runnable import run_on_ui_thread

    @run_on_ui_thread
    def enable_secure_flag():
        """
        Bu fonksiyon Android penceresine FLAG_SECURE ekler.
        Bu sayede ekran görüntüsü alınamaz ve ekran kaydında
        bu pencere siyah görünür (Bypass mantığı).
        """
        try:
            PythonActivity = autoclass('org.kivy.android.PythonActivity')
            View = autoclass('android.view.View')
            WindowManager = autoclass('android.view.WindowManager$LayoutParams')
            
            activity = PythonActivity.mActivity
            window = activity.getWindow()
            
            # FLAG_SECURE ekle
            window.setFlags(WindowManager.FLAG_SECURE, WindowManager.FLAG_SECURE)
        except Exception as e:
            print(f"Secure Flag Hatası: {e}")

class CheatMenu(BoxLayout):
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.orientation = 'vertical'
        self.padding = 20
        self.spacing = 10

        # Başlık
        self.add_widget(Label(text="[ FPS SIMULATOR MENU ]", font_size='24sp', color=(0, 1, 0, 1)))

        # Özellik 1: ESP / Wallhack
        self.add_row("ESP (Wallhack)", self.toggle_esp)
        
        # Özellik 2: Aimbot
        self.add_row("Aimbot Logic", self.toggle_aim)
        
        # Özellik 3: Radar
        self.add_row("External Radar", self.toggle_radar)

        # Çıkış Butonu
        btn_exit = Button(text="Menüyü Kapat", size_hint=(1, 0.2), background_color=(1, 0, 0, 1))
        btn_exit.bind(on_press=App.get_running_app().stop)
        self.add_widget(btn_exit)

    def add_row(self, text, callback):
        row = BoxLayout(orientation='horizontal', size_hint=(1, 0.15))
        lbl = Label(text=text)
        sw = Switch(active=False)
        sw.bind(active=callback)
        row.add_widget(lbl)
        row.add_widget(sw)
        self.add_widget(row)

    def toggle_esp(self, instance, value):
        print(f"ESP Durumu: {value}")
        # Buraya oyun içi çizim mantığı bağlanır

    def toggle_aim(self, instance, value):
        print(f"Aimbot Durumu: {value}")

    def toggle_radar(self, instance, value):
        print(f"Radar Durumu: {value}")

class SimulatorApp(App):
    def build(self):
        # Uygulama başladığında ekran korumasını aktif et
        if platform == 'android':
            enable_secure_flag()
        return CheatMenu()

if __name__ == '__main__':
    SimulatorApp().run()

