package com.example.cyberlarpapi.game.data.character;

public class CharacterDTO {

    private Integer gameId;
    private Integer userId;
    private String name;

    private String description;

    private Integer characterClassId;

    private Integer factionId;

    private Integer styleId;

    // BANK ACCOUNT

    int balance;


    // ATTRIBUTES

    int strength;

    int agility;

    int presence;

    int toughness;

    int knowledge;

    // STATS
    int max_hp;

    int armor;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getCharacterClassId() {
        return characterClassId;
    }

    public Integer getFactionId() {
        return factionId;
    }

    public Integer getStyleId() {
        return styleId;
    }

    public int getBalance() {
        return balance;
    }

    public int getStrength() {
        return strength;
    }

    public int getAgility() {
        return agility;
    }

    public int getPresence() {
        return presence;
    }

    public int getToughness() {
        return toughness;
    }

    public int getKnowledge() {
        return knowledge;
    }

    public int getMax_hp() {
        return max_hp;
    }

    public int getArmor() {
        return armor;
    }

    public Integer getGameId() {
        return gameId;
    }

    public Integer getUserId() {
        return userId;
    }
}
