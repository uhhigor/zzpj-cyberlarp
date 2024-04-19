package com.example.cyberlarpapi.game.model.character;

import com.example.cyberlarpapi.game.model.Game;
import com.example.cyberlarpapi.game.model.player.Player;
import com.example.cyberlarpapi.game.model.character.characterClass.CharacterClass;
import com.example.cyberlarpapi.game.model.character.faction.Faction;
import com.example.cyberlarpapi.game.model.character.style.Style;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterException;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import org.apache.commons.lang3.RandomStringUtils;

@Entity
public class Character {

    @Id
    private Integer id;

    @ManyToOne
    @JsonBackReference
    private Game game;

    @OneToOne
    private Player player;

    private String name;

    private String description;

    @ManyToOne
    private CharacterClass characterClass;

    @ManyToOne
    private Faction faction;

    @ManyToOne
    private Style style;

    // BANK ACCOUNT

    private int balance;
    String account_number = "#" + RandomStringUtils.randomNumeric(6);

    // ATTRIBUTES

    private int strength;

    private int agility;

    private int presence;

    private int toughness;

    private int knowledge;

    // STATS
    private int max_hp;

    private int current_hp;

    private int armor;

    public Character() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CharacterClass getCharacterClass() {
        return characterClass;
    }

    public void setCharacterClass(CharacterClass characterClass) {
        this.characterClass = characterClass;
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getPresence() {
        return presence;
    }

    public void setPresence(int presence) {
        this.presence = presence;
    }

    public int getToughness() {
        return toughness;
    }

    public void setToughness(int toughness) {
        this.toughness = toughness;
    }

    public int getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(int knowledge) {
        this.knowledge = knowledge;
    }

    public int getMax_hp() {
        return max_hp;
    }

    public void setMax_hp(int max_hp) {
        this.max_hp = max_hp;
    }

    public int getCurrent_hp() {
        return current_hp;
    }

    public void setCurrent_hp(int current_hp) {
        this.current_hp = current_hp;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public static CharacterBuilder builder() {
        return new CharacterBuilder();
    }

    public static class CharacterBuilder {
        private final Character character;

        public CharacterBuilder() {
            character = new Character();
        }

        public CharacterBuilder game(Game game) {
            character.game = game;
            return this;
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

        public CharacterBuilder max_hp(int max_hp) {
            character.max_hp = max_hp;
            character.current_hp = max_hp;
            return this;
        }

        public CharacterBuilder armor(int armor) {
            character.armor = armor;
            return this;
        }

        public Character build() throws CharacterException {
            if(character.game == null)
                throw new CharacterException("Game is required");

            if(character.name == null)
                throw new CharacterException("Name is required");

            if(character.characterClass == null)
                throw new CharacterException("Class is required");

            if(character.faction == null)
                throw new CharacterException("Faction is required");

            if(character.style == null)
                throw new CharacterException("Style is required");

            if(character.agility + character.strength + character.presence + character.toughness + character.knowledge != 20)
                throw new CharacterException("Attributes must sum up to 20");

            if(character.max_hp == 0)
                throw new CharacterException("Max HP must be greater than 0");

            if(character.armor >= 5)
                throw new CharacterException("Armor must be less or equal to 5");

            return character;
        }
    }

}
