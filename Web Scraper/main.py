from bs4 import BeautifulSoup
from requests import get
import json
from Character import Survivor, Killer
from Perk import SurvivorPerk, KillerPerk

WIKI_URL = r"https://deadbydaylight.wiki.gg"


def get_survivor_data() -> list:
    response = get(WIKI_URL + r"/wiki/Survivors")
    soup = BeautifulSoup(response.text, "html.parser")

    survivor_list = [Survivor(0, "All Survivors", WIKI_URL + "/images/b/b3/IconHelpLoading_survivor.png")]
    survivor_dict = {"All Survivors": 0}
    survivor_perk_list = []

    # Get list of survivors
    list_start = soup.select("span#List_of_Survivors")[0].parent
    survivors_data = list_start.find_all_next("div")[0]
    survivors = survivors_data.children
    for survivor in survivors:
        if survivor == "\n":
            continue
        survivor_id = len(survivor_list)
        survivor_name = survivor.find_next("a").text
        if survivor_name == "Aestri Yazar & Baermar Uraz":
            survivor_name = "Aestri Yazar"
        survivor_icon = WIKI_URL + survivor.find_next("img")["src"]
        survivor_list.append(Survivor(survivor_id, survivor_name, survivor_icon))
        survivor_dict[survivor_name] = survivor_id
    
    # Get list of perks
    list_start = soup.select("span#List_of_Survivor_Perks")[0].parent
    survivor_perk_data = list_start.find_all_next("tbody")[0].find_all("tr")
    survivor_perk_data.pop(0)
    for perk in survivor_perk_data:
        row = perk.find_all(["th", "td"])
        perk_id = len(survivor_perk_list)
        perk_name = row[1].find("a").text
        perk_icon = WIKI_URL + row[0].find("img")["src"]
        perk_description = str(row[2])
        if row[3].find("a"):
            perk_survivor = row[3].find("a")["title"]
            if perk_survivor == "Troupe":
                perk_survivor = "Aestri Yazar"
        else:
            perk_survivor = "All Survivors"
        perk_survivor_id = survivor_dict[perk_survivor]
        survivor_perk_list.append(
            SurvivorPerk(perk_id, perk_name, perk_icon, perk_description, perk_survivor, perk_survivor_id))

    return [survivor_list, survivor_perk_list]


def get_killer_data() -> list:
    response = get(r"https://deadbydaylight.wiki.gg/wiki/Killers")
    soup = BeautifulSoup(response.text, "html.parser")

    killer_list = [Killer(0, "All Killers", "All Killer", WIKI_URL + "/images/0/06/IconHelpLoading_killer.png")]
    killer_dict = {"All Killer": 0}
    killer_perk_list = []

    # Get list of killers
    list_start = soup.select("span#List_of_Killers")[0].parent
    killers_data = list_start.find_all_next("div")[0]
    killers = killers_data.children
    for killer in killers:
        if killer == "\n":
            continue
        killer_id = len(killer_list)
        killer_name = killer.find_next("a").text
        killer_title = killer.find_all("a")[1]["title"]
        killer_icon = WIKI_URL + killer.find_next("img")["src"]
        killer_list.append(Killer(killer_id, killer_name, killer_title, killer_icon))
        killer_dict[killer_title.replace("The ", "")] = killer_id
        
    # Get list of perks
    list_start = soup.select("span#List_of_Killer_Perks")[0].parent
    killer_perk_data = list_start.find_all_next("tbody")[0].find_all("tr")
    killer_perk_data.pop(0)
    for perk in killer_perk_data:
        row = perk.find_all(["th", "td"])
        perk_id = len(killer_perk_list)
        perk_name = row[1].find("a").text
        perk_icon = WIKI_URL + row[0].find("img")["src"]
        perk_description = str(row[2])
        if row[3].find("a"):
            perk_killer = row[3].find("a").text
        else:
            perk_killer = "All Killer"
        perk_killer_id = killer_dict[perk_killer]
        killer_perk_list.append(
            KillerPerk(perk_id, perk_name, perk_icon, perk_description, perk_killer, perk_killer_id))

    return [killer_list, killer_perk_list]


def export_to_json(data: list, file_path: str):
    json_list = json.dumps([obj.__dict__ for obj in data])
    with open(file_path, "w") as f:
        f.write(json_list)


def main():
    survivor_list, survivor_perk_list = get_survivor_data()
    killer_list, killer_perk_list = get_killer_data()

    export_to_json(survivor_list, "Data Output/survivor_list.json")
    export_to_json(survivor_perk_list, "Data Output/survivor_perk_list.json")
    export_to_json(killer_list, "Data Output/killer_list.json")
    export_to_json(killer_perk_list, "Data Output/killer_perk_list.json")


if __name__ == '__main__':
    main()
