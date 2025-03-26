class Character:
    def __init__(self, name: str, image: str):
        self.name = name
        self.image = image


class Survivor(Character):
    def __init__(self, survivor_id: int, name: str, image: str):
        super().__init__(name, image)
        self.survivor_id = survivor_id

    def __str__(self):
        return (f"Survivor:\n"
                f"ID: {self.survivor_id}\n"
                f"Name: {self.name}\n"
                f"Image: {self.image}\n")


class Killer(Character):
    def __init__(self, killer_id: int, name: str, title: str, image: str):
        super().__init__(name, image)
        self.killer_id = killer_id
        self.title = title

    def __str__(self):
        return (f"Killer:\n"
                f"ID: {self.killer_id}\n"
                f"Name: {self.name}\n"
                f"Title: {self.title}\n"
                f"Image: {self.image}\n")
