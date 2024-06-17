package com.example.cyberlarpapi.game;

import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterException;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.character.CharacterClass;
import com.example.cyberlarpapi.game.model.character.Faction;
import com.example.cyberlarpapi.game.model.character.Style;

import java.util.List;

public class DefaultGameData {
    public static List<Character> getDefaultCharacters() {
        try {
            return List.of(
                    Character.builder()
                            .name("John")
                            .characterClass(CharacterClass.NETRUNNER)
                            .style(Style.HIGHTECH)
                            .faction(Faction.CORPORATE)
                            .balance(100F)
                            .strength(4)
                            .agility(3)
                            .presence(2)
                            .toughness(3)
                            .knowledge(8)
                            .maxHp(10)
                            .armor(0)
                            .build(),
                    Character.builder()
                            .name("Jane")
                            .characterClass(CharacterClass.PUNK)
                            .style(Style.NEOKITSCH)
                            .faction(Faction.STREET)
                            .balance(100F)
                            .strength(6)
                            .agility(2)
                            .presence(4)
                            .toughness(4)
                            .knowledge(4)
                            .maxHp(12)
                            .armor(2)
                            .build(),
                    Character.builder()
                            .name("Jack")
                            .characterClass(CharacterClass.TECHIE)
                            .style(Style.HIGHTECH)
                            .faction(Faction.TECHIES)
                            .balance(100F)
                            .strength(6)
                            .agility(2)
                            .presence(3)
                            .toughness(3)
                            .knowledge(6)
                            .maxHp(8)
                            .armor(1)
                            .build()
            );
        } catch (CharacterException e) {
            throw new RuntimeException(e);
        }
    }
}