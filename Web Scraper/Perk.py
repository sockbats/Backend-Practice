class Perk:
    def __init__(self, perk_id: int, name: str, icon: str, description: str):
        self.perk_id = perk_id
        self.name = name
        self.icon = icon
        self.description = description


class SurvivorPerk(Perk):
    def __init__(self, perk_id: int, name: str, icon: str, description: str, survivor_name: str, survivor_id: int):
        super().__init__(perk_id, name, icon, description)
        self.survivor_name = survivor_name
        self.survivor_id = survivor_id


class KillerPerk(Perk):
    def __init__(self, perk_id: int, name: str, icon: str, description: str, killer_title: str, killer_id: int):
        super().__init__(perk_id, name, icon, description)
        self.killer_title = killer_title
        self.killer_id = killer_id