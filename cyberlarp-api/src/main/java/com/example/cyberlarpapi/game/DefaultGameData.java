package com.example.cyberlarpapi.game;

import java.util.List;

import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterException;
import com.example.cyberlarpapi.game.exceptions.FactionException.FactionException;
import com.example.cyberlarpapi.game.model.Game;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.character.CharacterClass;
import com.example.cyberlarpapi.game.model.character.Style;
import com.example.cyberlarpapi.game.model.character.faction.Faction;

public class DefaultGameData {
    public static List<Character> getDefaultCharacters(Game game) {
        try {
            return List.of(
                    Character.builder()
                            .name("John")
                            .characterClass(CharacterClass.NETRUNNER)
                            .style(Style.HighTech)
                            .balance(100)
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
                            .style(Style.Neokitsch)
                            .balance(100)
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
                            .style(Style.HighTech)
                            .balance(100)
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
    public static List<Faction> getDefaultFactions() {
        try {
            return List.of(
                    Faction.builder()
                            .name("Arasaka")
                            .description("Arasaka Corporation is a world-wide megacorporation dealing in corporate security, banking, and manufacturing.")
                            .build(),
                    Faction.builder()
                            .name("Militech")
                            .description("Militech International Armaments is a megacorporation specializing in weapons manufacturing and private military contracting.")
                            .build(),
                    Faction.builder()
                            .name("Trauma Team International")
                            .description("Trauma Team International is a megacorporation specializing in medical services and paramilitary operations.")
                            .build()
            );
        } catch (FactionException e) {
            throw new RuntimeException(e);
        }
    }
}
