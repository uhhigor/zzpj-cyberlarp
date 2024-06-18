package com.example.cyberlarpapi.unit;

import com.example.cyberlarpapi.game.DefaultGameData;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.character.faction.Faction;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DefaultGameDataTests {
    DefaultGameData defaultGameData = new DefaultGameData();
    List<Character> defaultCharacters = defaultGameData.getDefaultCharacters();
    List<Faction> defaultFactions = defaultGameData.getDefaultFactions();
    @Test
    public void testDefaultCharactersData() {
        assert defaultCharacters.size() == 3;

        assert defaultCharacters.get(0).getName().equals("John");
        assert defaultCharacters.get(0).getCharacterClass().toString().equals("NETRUNNER");
        assert defaultCharacters.get(0).getStyle().toString().equals("HIGHTECH");
        assert defaultCharacters.get(0).getBalance() == 100F;
        assert defaultCharacters.get(0).getStrength() == 4;
        assert defaultCharacters.get(0).getAgility() == 3;
        assert defaultCharacters.get(0).getPresence() == 2;
        assert defaultCharacters.get(0).getToughness() == 3;
        assert defaultCharacters.get(0).getKnowledge() == 8;
        assert defaultCharacters.get(0).getMaxHp() == 10;
        assert defaultCharacters.get(0).getArmor() == 0;

        assert defaultCharacters.get(1).getName().equals("Jane");
        assert defaultCharacters.get(1).getCharacterClass().toString().equals("PUNK");
        assert defaultCharacters.get(1).getStyle().toString().equals("NEOKITSCH");
        assert defaultCharacters.get(1).getBalance() == 100F;
        assert defaultCharacters.get(1).getStrength() == 6;
        assert defaultCharacters.get(1).getAgility() == 2;
        assert defaultCharacters.get(1).getPresence() == 4;
        assert defaultCharacters.get(1).getToughness() == 4;
        assert defaultCharacters.get(1).getKnowledge() == 4;
        assert defaultCharacters.get(1).getMaxHp() == 12;
        assert defaultCharacters.get(1).getArmor() == 2;

        assert defaultCharacters.get(2).getName().equals("Jack");
        assert defaultCharacters.get(2).getCharacterClass().toString().equals("TECHIE");
        assert defaultCharacters.get(2).getStyle().toString().equals("HIGHTECH");
        assert defaultCharacters.get(2).getBalance() == 100F;
        assert defaultCharacters.get(2).getStrength() == 6;
        assert defaultCharacters.get(2).getAgility() == 2;
        assert defaultCharacters.get(2).getPresence() == 3;
        assert defaultCharacters.get(2).getToughness() == 3;
        assert defaultCharacters.get(2).getKnowledge() == 6;
        assert defaultCharacters.get(2).getMaxHp() == 8;
        assert defaultCharacters.get(2).getArmor() == 1;
    }


    @Test
        public void testDefaultFactionsData() {

        assert defaultFactions.size() == 3;
        assert defaultFactions.get(0).getName().equals("Arasaka");
        assert defaultFactions.get(0).getDescription().equals("Arasaka Corporation is a world-wide megacorporation dealing in corporate security, banking, and manufacturing.");

        assert defaultFactions.get(1).getName().equals("Militech");
        assert defaultFactions.get(1).getDescription().equals("Militech International Armaments is a megacorporation specializing in weapons manufacturing and private military contracting.");

        assert defaultFactions.get(2).getName().equals("Trauma Team International");
        assert defaultFactions.get(2).getDescription().equals("Trauma Team International is a megacorporation specializing in medical services and paramilitary operations.");
    }
}
