import json
from requests import post


def main():
    backend_url = "http://localhost:8000"

    killers = json.load(open("Web Scraper/Data Output/killer_list.json"))
    survivors = json.load(open("Web Scraper/Data Output/survivor_list.json"))
    killer_perks = json.load(open("Web Scraper/Data Output/killer_perk_list.json"))
    survivor_perks = json.load(open("Web Scraper/Data Output/survivor_perk_list.json"))

    for killer in killers:
        response = post(backend_url + "/api/killers", json=killer)
        if response.status_code != 200:
            print(f"Error: {response.status_code} on killer {killer.get("name")}")

    for perk in killer_perks:
        perk.pop("killer_title")
        response = post(backend_url + "/api/killer_perks", json=perk)
        if response.status_code != 200:
            print(f"Error: {response.status_code} on killer perk {perk.get("name")}")

    for survivor in survivors:
        response = post(backend_url + "/api/survivors", json=survivor)
        if response.status_code != 200:
            print(f"Error: {response.status_code} on survivor {survivor.get("name")}")

    for perk in survivor_perks:
        perk.pop("survivor_name")
        response = post(backend_url + "/api/survivor_perks", json=perk)
        if response.status_code != 200:
            print(f"Error: {response.status_code} on survivor perk {perk.get("name")}")


if __name__ == '__main__':
    main()
