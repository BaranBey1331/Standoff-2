import kivy
from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.label import Label
from kivy.clock import Clock
from logic.memory import MemoryManager
from logic.math_utils import Vector3, calculate_aim_angles

class SimulationUI(BoxLayout):
    def __init__(self, **kwargs):
        super().__init__(orientation='vertical', **kwargs)
        self.mem = MemoryManager()
        self.info_label = Label(text="FPS Simulation Engine Initializing...", font_size='14sp')
        self.add_widget(self.info_label)

        # Simülasyon döngüsü
        Clock.schedule_interval(self.update, 1.0 / 30.0)

    def update(self, dt):
        data = self.mem.get_entity_data()
        lp = data["local_player"]
        enemy = data["enemies"][0]

        lp_vec = Vector3(lp["pos"][0], lp["pos"][1], lp["pos"][2])
        en_vec = Vector3(enemy["pos"][0], enemy["pos"][1], enemy["pos"][2])

        yaw, pitch = calculate_aim_angles(lp_vec, en_vec)

        status = (
            f"--- Educational Simulation ---\n"
            f"Local HP: {lp['hp']} | Pos: {lp['pos']}\n"
            f"Enemy HP: {enemy['hp']} | Pos: {enemy['pos']}\n"
            f"Target Angles -> Yaw: {yaw:.2f}, Pitch: {pitch:.2f}\n"
            f"Status: Analyzing mechanics..."
        )
        self.info_label.text = status

class FPS_SimulationApp(App):
    def build(self):
        return SimulationUI()

if __name__ == '__main__':
    FPS_SimulationApp().run()
