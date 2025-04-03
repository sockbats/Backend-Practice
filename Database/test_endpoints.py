import unittest
from requests import post, get, put, delete

backend_url = "http://localhost:8000"


class TestKillerEndpoints(unittest.TestCase):
    endpoint = "/api/killers"

    killer_id = -1
    killer = {
        "name": "Fake' Killer",
        "image": "https://deadbydaylight.wiki.gg/images/0/06/IconHelpLoading_killer.png",
        "killer_id": killer_id,
        "title": "The Fake"}
    updated_killer = {
        "name": "Not Real' Killer",
        "image": "https://deadbydaylight.wiki.gg/images/0/06/IconHelpLoading_killer.png",
        "killer_id": killer_id,
        "title": "The False"
    }

    def test_post(self):
        delete(backend_url + self.endpoint + f"?id={self.killer_id}")

        response = post(backend_url + self.endpoint, json=self.killer)
        self.assertEqual(response.status_code, 201)
        self.assertEqual(response.json().get("message"), "1 lines affected.")

        delete(backend_url + self.endpoint + f"?id={self.killer_id}")

    def test_get_by_id(self):
        post(backend_url + self.endpoint, json=self.killer)

        response = get(backend_url + self.endpoint + f"?id={self.killer_id}")
        print(response.json())
        self.assertEqual(response.status_code, 200)
        self.assertEqual(self.killer, response.json())

        delete(backend_url + self.endpoint + f"?id={self.killer_id}")

    def test_get_all(self):
        post(backend_url + self.endpoint, json=self.killer)

        response = get(backend_url + self.endpoint)
        print(response.json())
        self.assertEqual(response.status_code, 200)
        self.assertIn(self.killer, response.json())

        delete(backend_url + self.endpoint + f"?id={self.killer_id}")

    def test_put(self):
        post(backend_url + self.endpoint, json=self.killer)

        response = put(backend_url + self.endpoint + f"?id={self.killer_id}", json=self.killer)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json().get("message"), "1 lines affected.")

        delete(backend_url + self.endpoint + f"?id={self.killer_id}")

    def test_delete(self):
        post(backend_url + self.endpoint, json=self.killer)

        response = delete(backend_url + self.endpoint + f"?id={self.killer_id}")
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json().get("message"), "1 lines deleted.")


class TestKillerPerkEndpoints(unittest.TestCase):
    endpoint = "/api/killer_perks"

    perk_id = -1
    perk = {
        "perk_id": perk_id,
        "name": "A Nurse's Calling",
        "icon": "https://deadbydaylight.wiki.gg/images/thumb/f/fe/IconPerks_aNursesCalling.png/96px-IconPerks_aNursesCalling.png?e79e94",
        "description": "description",
        "killer_id": 4,
        "title": "The Nurse"
    }
    updated_perk = {
        "perk_id": perk_id,
        "name": "A Calling Nurse",
        "icon": "https://deadbydaylight.wiki.gg/images/thumb/f/fe/IconPerks_aNursesCalling.png/96px-IconPerks_aNursesCalling.png?e79e94",
        "description": "description",
        "killer_id": 4,
        "title": "The Nurse"
    }

    def test_post(self):
        delete(backend_url + self.endpoint + f"?id={self.perk_id}")

        response = post(backend_url + self.endpoint, json=self.perk)
        self.assertEqual(response.status_code, 201)
        self.assertEqual(response.json().get("message"), "1 lines affected.")

        delete(backend_url + self.endpoint + f"?id={self.perk_id}")

    def test_get_by_id(self):
        post(backend_url + self.endpoint, json=self.perk)

        response = get(backend_url + self.endpoint + f"?id={self.perk_id}")
        print(response.json())
        self.assertEqual(response.status_code, 200)
        self.assertEqual(self.perk, response.json())

        delete(backend_url + self.endpoint + f"?id={self.perk_id}")

    def test_get_all(self):
        post(backend_url + self.endpoint, json=self.perk)

        response = get(backend_url + self.endpoint)
        print(response.json())
        self.assertEqual(response.status_code, 200)
        self.assertIn(self.perk, response.json())

        delete(backend_url + self.endpoint + f"?id={self.perk_id}")

    def test_put(self):
        post(backend_url + self.endpoint, json=self.perk)

        response = put(backend_url + self.endpoint + f"?id={self.perk_id}", json=self.perk)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json().get("message"), "1 lines affected.")

        delete(backend_url + self.endpoint + f"?id={self.perk_id}")

    def test_delete(self):
        post(backend_url + self.endpoint, json=self.perk)

        response = delete(backend_url + self.endpoint + f"?id={self.perk_id}")
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json().get("message"), "1 lines deleted.")


class TestSurvivorEndpoints(unittest.TestCase):
    endpoint = "/api/survivors"

    survivor_id = -1
    survivor = {
        "name": "Fake' Survivor",
        "image": "https://deadbydaylight.wiki.gg/images/0/06/IconHelpLoading_survivor.png",
        "survivor_id": survivor_id
    }
    updated_survivor = {
        "name": "Not Real' Survivor",
        "image": "https://deadbydaylight.wiki.gg/images/0/06/IconHelpLoading_survivor.png",
        "survivor_id": survivor_id
    }

    def test_post(self):
        delete(backend_url + self.endpoint + f"?id={self.survivor_id}")

        response = post(backend_url + self.endpoint, json=self.survivor)
        self.assertEqual(response.status_code, 201)
        self.assertEqual(response.json().get("message"), "1 lines affected.")

        delete(backend_url + self.endpoint + f"?id={self.survivor_id}")

    def test_get_by_id(self):
        post(backend_url + self.endpoint, json=self.survivor)

        response = get(backend_url + self.endpoint + f"?id={self.survivor_id}")
        print(response.json())
        self.assertEqual(response.status_code, 200)
        self.assertEqual(self.survivor, response.json())

        delete(backend_url + self.endpoint + f"?id={self.survivor_id}")

    def test_get_all(self):
        post(backend_url + self.endpoint, json=self.survivor)

        response = get(backend_url + self.endpoint)
        print(response.json())
        self.assertEqual(response.status_code, 200)
        self.assertIn(self.survivor, response.json())

        delete(backend_url + self.endpoint + f"?id={self.survivor_id}")

    def test_put(self):
        post(backend_url + self.endpoint, json=self.survivor)

        response = put(backend_url + self.endpoint + f"?id={self.survivor_id}", json=self.survivor)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json().get("message"), "1 lines affected.")

        delete(backend_url + self.endpoint + f"?id={self.survivor_id}")

    def test_delete(self):
        post(backend_url + self.endpoint, json=self.survivor)

        response = delete(backend_url + self.endpoint + f"?id={self.survivor_id}")
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json().get("message"), "1 lines deleted.")


class TestSurvivorPerkEndpoints(unittest.TestCase):
    endpoint = "/api/survivor_perks"

    perk_id = -1
    perk = {
        "perk_id": perk_id,
        "name": "Ace in the Hole",
        "icon": "https://deadbydaylight.wiki.gg/images/thumb/2/24/IconPerks_aceInTheHole.png/96px-IconPerks_aceInTheHole.png?528a2e",
        "description": "description",
        "survivor_id": 7,
        "survivor_name": "Ace Visconti"
    }
    updated_perk = {
        "perk_id": perk_id,
        "name": "Hole in the Ace",
        "icon": "https://deadbydaylight.wiki.gg/images/thumb/f/fe/IconPerks_aNursesCalling.png/96px-IconPerks_aNursesCalling.png?e79e94",
        "description": "description",
        "killer_id": 7,
        "survivor_name": "Ace Visconti"
    }

    def test_post(self):
        delete(backend_url + self.endpoint + f"?id={self.perk_id}")

        response = post(backend_url + self.endpoint, json=self.perk)
        self.assertEqual(response.status_code, 201)
        self.assertEqual(response.json().get("message"), "1 lines affected.")

        delete(backend_url + self.endpoint + f"?id={self.perk_id}")

    def test_get_by_id(self):
        post(backend_url + self.endpoint, json=self.perk)

        response = get(backend_url + self.endpoint + f"?id={self.perk_id}")
        print(response.json())
        self.assertEqual(response.status_code, 200)
        self.assertEqual(self.perk, response.json())

        delete(backend_url + self.endpoint + f"?id={self.perk_id}")

    def test_get_all(self):
        post(backend_url + self.endpoint, json=self.perk)

        response = get(backend_url + self.endpoint)
        print(response.json())
        self.assertEqual(response.status_code, 200)
        self.assertIn(self.perk, response.json())

        delete(backend_url + self.endpoint + f"?id={self.perk_id}")

    def test_put(self):
        post(backend_url + self.endpoint, json=self.perk)

        response = put(backend_url + self.endpoint + f"?id={self.perk_id}", json=self.perk)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json().get("message"), "1 lines affected.")

        delete(backend_url + self.endpoint + f"?id={self.perk_id}")

    def test_delete(self):
        post(backend_url + self.endpoint, json=self.perk)

        response = delete(backend_url + self.endpoint + f"?id={self.perk_id}")
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json().get("message"), "1 lines deleted.")


if __name__ == '__main__':
    test_classes = [TestKillerEndpoints, TestKillerPerkEndpoints, TestSurvivorEndpoints, TestSurvivorPerkEndpoints]

    test_loader = unittest.TestLoader()
    suites = [test_loader.loadTestsFromTestCase(test_class) for test_class in test_classes]

    big_suite = unittest.TestSuite(suites)

    unittest.TextTestRunner().run(big_suite)
