package com.example.cyberlarpapi.game.model.game;

import com.example.cyberlarpapi.game.model.user._User;
import com.example.cyberlarpapi.game.DefaultGameData;
import com.example.cyberlarpapi.game.model.Transaction;
import com.example.cyberlarpapi.game.model.character.Character;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Game {
    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private String description;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Character> availableCharacters = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<_User> users;

    @ManyToOne(cascade = CascadeType.ALL)
    private _User gameMaster;

    public void addCharacter(_User character) {
        users.add(character);
    }

    @OneToMany(cascade = CascadeType.ALL)
    private List<Transaction> transactions;


    public void removeCharacter(_User character) {
        users.remove(character);
    }

    public void addAvailableCharacter(Character character) {
        availableCharacters.add(character);
    }

    public void removeAvailableCharacter(Character character) {
        availableCharacters.remove(character);
    }

    public static GameBuilder builder() {
        return new GameBuilder();
    }

    public static class GameBuilder {
        private final Game game;

        public GameBuilder() {
            game = new Game();
        }

        public GameBuilder availableCharacters(List<Character> availableCharacters) {
            game.setAvailableCharacters(availableCharacters);
            return this;
        }

        public GameBuilder users(List<_User> users) {
            game.setUsers(users);
            return this;
        }

        public GameBuilder name(String name) {
            game.setName(name);
            return this;
        }

        public GameBuilder description(String description) {
            game.setDescription(description);
            return this;
        }

        public GameBuilder gameMaster(_User gameMaster) {
            game.setGameMaster(gameMaster);
            return this;
        }

        public Game build() {
            if(game.getName() == null || game.getDescription() == null) {
                throw new IllegalArgumentException("Name and description are required");
            }
            if(game.getAvailableCharacters() == null) {
                game.setAvailableCharacters(DefaultGameData.getDefaultCharacters());
            }
            if(game.getUsers() == null) {
                game.setUsers(List.of());
            }
            if(game.getGameMaster() == null) {
                throw new IllegalArgumentException("Game master is required");
            }
            return game;
        }
    }
}
