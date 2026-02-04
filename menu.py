import kivy
from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.label import Label
from kivy.clock import Clock
from memory_reader import MemoryManager
from aimbot import Vector3, calculate_aim_angles

class MenuUI(BoxLayout):
    def __init__(self, **kwargs):
        super().__init__(orientation='vertical', **kwargs)
        self.mem = MemoryManager()
        self.info_label = Label(
            text="Standoff 2 - FPS Simulation Menu\nEducational Purpose Only",
            font_size='18sp',
            color=(0, 1, 0, 1)
        )
        self.add_widget(self.info_label)

        self.stats_label = Label(text="Initializing data...", font_size='14sp')
        self.add_widget(self.stats_label)

        Clock.schedule_interval(self.update, 1.0 / 30.0)

    def update(self, dt):
        data = self.mem.get_entity_data()
        lp = data["local_player"]
        enemy = data["enemies"][0]

        lp_vec = Vector3(lp["pos"][0], lp["pos"][1], lp["pos"][2])
        en_vec = Vector3(enemy["pos"][0], enemy["pos"][1], enemy["pos"][2])

        yaw, pitch = calculate_aim_angles(lp_vec, en_vec)

        status = (
            f"--- Player Info ---\n"
            f"HP: {lp['hp']} | Team: {lp['team']}\n"
            f"Pos: {lp['pos']}\n\n"
            f"--- Target Info ---\n"
            f"Enemy ID: {enemy['id']} | HP: {enemy['hp']}\n"
            f"Target Angles -> Yaw: {yaw:.2f}, Pitch: {pitch:.2f}\n"
        )
        self.stats_label.text = status

class Standoff2SimApp(App):
    def build(self):
        return MenuUI()

if __name__ == '__main__':
    Standoff2SimApp().run()
