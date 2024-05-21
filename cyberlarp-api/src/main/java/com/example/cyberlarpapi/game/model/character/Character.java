package com.example.cyberlarpapi.game.model.character;

import com.example.cyberlarpapi.game.model.player.Player;
import com.example.cyberlarpapi.game.model.character.faction.Faction;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

@Getter
@Entity
@Setter
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToOne
    private Player player;

    private String name;

    private String description;

    private CharacterClass characterClass;

    @ManyToOne
    private Faction faction;
    private Style style;

    // BANK ACCOUNT

    private int balance;
    String account_number = "#" + RandomStringUtils.randomNumeric(6);

    // ATTRIBUTES

    private int agility;

    private int strength;

    private int presence;

    private int toughness;

    private int knowledge;

    // STATS
    private int maxHp;

    private int currentHp;

    private int armor;

    public static CharacterBuilder builder() {
        return new CharacterBuilder();
    }

    public static class CharacterBuilder {
        private final Character character;

        public CharacterBuilder() {
            character = new Character();
        }

        public CharacterBuilder player(Player player) {
            character.player = player;
            return this;
        }

        public CharacterBuilder name(String name) {
            character.name = name;
            return this;
        }

        public CharacterBuilder description(String description) {
            character.description = description;
            return this;
        }

        public CharacterBuilder characterClass(CharacterClass characterClass) {
            character.characterClass = characterClass;
            return this;
        }

        public CharacterBuilder faction(Faction faction) {
            character.faction = faction;
            return this;
        }

        public CharacterBuilder style(Style style) {
            character.style = style;
            return this;
        }

        public CharacterBuilder balance(int balance) {
            character.balance = balance;
            return this;
        }

        public CharacterBuilder strength(int strength) {
            character.strength = strength;
            return this;
        }

        public CharacterBuilder agility(int agility) {
            character.agility = agility;
            return this;
        }

        public CharacterBuilder presence(int presence) {
            character.presence = presence;
            return this;
        }

        public CharacterBuilder toughness(int toughness) {
            character.toughness = toughness;
            return this;
        }

        public CharacterBuilder knowledge(int knowledge) {
            character.knowledge = knowledge;
            return this;
        }

        public CharacterBuilder maxHp(int maxHp) {
            character.maxHp = maxHp;
            character.currentHp = maxHp;
            return this;
        }

        public CharacterBuilder armor(int armor) {
            character.armor = armor;
            return this;
        }

        public Character build() throws CharacterException {
            if(character.name == null)
                throw new CharacterException("Name is required");

            if(character.characterClass == null)
                throw new CharacterException("Class is required");

            if(character.style == null)
                throw new CharacterException("Style is required");

            if(character.agility
                    + character.strength
                    + character.presence
                    + character.toughness
                    + character.knowledge != 20)
                throw new CharacterException("Attributes must sum up to 20");

            if(character.maxHp == 0)
                throw new CharacterException("Max HP must be greater than 0");

            if(character.armor >= 5)
                throw new CharacterException("Armor must be less or equal to 5");

            return character;
        }
    }

}
