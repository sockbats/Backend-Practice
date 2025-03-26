import json
from requests import post


def main():
    backend_url = ""

    killers = json.load(open("Web Scraper/Data Output/killer_list.json"))
    survivors = json.load(open("Web Scraper/Data Output/survivor_list.json"))
    killer_perks = json.load(open("Web Scraper/Data Output/killer_perk_list.json"))
    survivor_perks = json.load(open("Web Scraper/Data Output/survivor_perk_list.json"))

    for killer in killers:
        killer.pop("title")
        print(killer)
        post(backend_url, json=killer)

if __name__ == '__main__':
    main()
