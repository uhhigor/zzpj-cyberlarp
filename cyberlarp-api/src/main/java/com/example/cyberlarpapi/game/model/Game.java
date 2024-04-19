package com.example.cyberlarpapi.game.model;

import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.player.Player;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Game {
    @Id
    @GeneratedValue
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "game")
    @JsonManagedReference
    private List<Character> availableCharacters;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "game")
    private List<Player> players;


    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public List<Character> getAvailableCharacters() {
        return availableCharacters;
    }

    public void setAvailableCharacters(List<Character> availableCharacters) {
        this.availableCharacters = availableCharacters;
    }

    public void addAvailableCharacter(Character character) {
        this.availableCharacters.add(character);
    }

    public void removeAvailableCharacter(Character character) {
        this.availableCharacters.remove(character);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }
}
