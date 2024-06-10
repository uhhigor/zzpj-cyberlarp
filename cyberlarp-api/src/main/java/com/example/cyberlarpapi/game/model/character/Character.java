package com.example.cyberlarpapi.game.model.character;

import com.example.cyberlarpapi.game.model.player.Player;
import com.example.cyberlarpapi.game.model.character.faction.Faction;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

@Entity
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Integer id;

    @OneToOne
    @Getter
    @Setter
    private Player player;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private CharacterClass characterClass;

    @Getter
    @Setter
    @ManyToOne
    private Faction faction;

    @Getter
    @Setter
    private Style style;

    // BANK ACCOUNT
    @Getter
    @Setter
    private Float balance;
    @Getter
    String accountNumber = "#" + RandomStringUtils.randomNumeric(6);

    // ATTRIBUTES
    private int strength;
    private int agility;
    private int presence;
    private int toughness;
    private int knowledge;

    // STATS
    @Getter
    @Setter
    private int maxHp;
    @Getter
    private int currentHp;
    @Getter
    private int armor;

    public int rollAttributeCheck(Attribute attribute) {
        return new Random().nextInt(20) + getAttribute(attribute);
    }

    public int takeDamage(int damage) {
        return currentHp -= (damage - armor);
    }

    public int heal(int amount) {
        return currentHp = Math.min(maxHp, currentHp + amount);
    }

    public int getAttribute(Attribute attribute) {
        return switch (attribute) {
            case STRENGTH -> strength;
            case AGILITY -> agility;
            case PRESENCE -> presence;
            case TOUGHNESS -> toughness;
            case KNOWLEDGE -> knowledge;
        };
    }

    public void setAttribute(Attribute attribute, int value) {
        switch (attribute) {
            case STRENGTH -> strength = value;
            case AGILITY -> agility = value;
            case PRESENCE -> presence = value;
            case TOUGHNESS -> toughness = value;
            case KNOWLEDGE -> knowledge = value;
        }
    }

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

        public CharacterBuilder balance(Float balance) {
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