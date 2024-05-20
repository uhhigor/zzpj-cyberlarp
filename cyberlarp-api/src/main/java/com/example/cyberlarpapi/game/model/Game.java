package com.example.cyberlarpapi.game.model;

import com.example.cyberlarpapi.game.DefaultGameData;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.player.Player;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private List<Character> availableCharacters;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Player> players;

    @ManyToOne(cascade = CascadeType.ALL)
    private Player gameMaster;

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
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

        public GameBuilder players(List<Player> players) {
            game.setPlayers(players);
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

        public GameBuilder gameMaster(Player gameMaster) {
            game.setGameMaster(gameMaster);
            return this;
        }

        public Game build() {
            if(game.getName() == null || game.getDescription() == null) {
                throw new IllegalArgumentException("Name and description are required");
            }
            if(game.getAvailableCharacters() == null) {
                game.setAvailableCharacters(DefaultGameData.getDefaultCharacters(game));
            }
            if(game.getPlayers() == null) {
                game.setPlayers(List.of());
            }
            if(game.getGameMaster() == null) {
                throw new IllegalArgumentException("Game master is required");
            }
            return game;
        }
    }
}
