"""
Bellek Yönetimi ve Veri Okuma (Educational Simulation)
Bu modül, bir oyunun RAM üzerindeki verilerine nasıl erişileceğini açıklar.
"""

class MemoryManager:
    """
    Harici (External) bir araçta bellek okuma mantığını simüle eder.
    Gerçek senaryoda 'pymem' kütüphanesi kullanılır.
    """
    def __init__(self, process_name="game.exe"):
        self.process_name = process_name

    def get_entity_data(self):
        """
        Entity List üzerinden oyuncu verilerini okumayı simüle eder.
        """
        return {
            "local_player": {"hp": 100, "pos": [0, 0, 0], "team": 1, "yaw": 45},
            "enemies": [
                {"id": 1, "hp": 80, "pos": [100, 200, 10], "team": 2},
                {"id": 2, "hp": 100, "pos": [-50, 150, 5], "team": 2}
            ]
        }
