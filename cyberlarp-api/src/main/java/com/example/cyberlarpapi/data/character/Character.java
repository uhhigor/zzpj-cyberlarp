package com.example.cyberlarpapi.data.character;

import com.example.cyberlarpapi.data.characterClass.CharacterClass;
import com.example.cyberlarpapi.data.faction.Faction;
import com.example.cyberlarpapi.data.style.Style;
import jakarta.persistence.*;
import org.apache.commons.lang3.RandomStringUtils;

@Entity
public class Character {

    @Id
    private Integer id;

    private String name;

    private String description;

    @OneToOne
    private CharacterClass characterClass;

    @OneToOne
    private Faction faction;

    @OneToOne
    private Style style;

    // BANK ACCOUNT

    int balance;
    String account_number = "#" + RandomStringUtils.randomNumeric(6);

    // ATTRIBUTES

    int strength;

    int agility;

    int presence;

    int toughness;

    int knowledge;

    // STATS
    int max_hp;

    int current_hp = max_hp;

    int armor;

    public Character(String name, String description, CharacterClass characterClass, Faction faction, Style style, int balance, int strength, int agility, int presence, int toughness, int knowledge, int max_hp, int armor) {
        this.name = name;
        this.description = description;
        this.characterClass = characterClass;
        this.faction = faction;
        this.style = style;
        this.balance = balance;
        this.strength = strength;
        this.agility = agility;
        this.presence = presence;
        this.toughness = toughness;
        this.knowledge = knowledge;
        this.max_hp = max_hp;
        this.armor = armor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public int getCurrent_hp() {
        return current_hp;
    }

    public void setCurrent_hp(int current_hp) {
        this.current_hp = current_hp;
    }

    public int getMax_hp() {
        return max_hp;
    }

    public void setMax_hp(int max_hp) {
        this.max_hp = max_hp;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }
}
