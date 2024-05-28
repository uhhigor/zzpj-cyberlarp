package com.example.cyberlarpapi.game.model.player;

import com.example.cyberlarpapi.User;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.exceptions.PlayerException.PlayerException;
import jakarta.persistence.*;


@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    private User user;

    @OneToOne
    private Character character;

    @ManyToOne
    private Game game;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player() {
    }

    public static PlayerBuilder builder() {
        return new PlayerBuilder();
    }

    public static class PlayerBuilder {
        private final Player player;

        public PlayerBuilder() {
            player = new Player();
        }

        public PlayerBuilder id(Integer id) {
            player.id = id;
            return this;
        }

        public PlayerBuilder user(User user) {
            player.user = user;
            return this;
        }

        public PlayerBuilder character(Character character) {
            player.character = character;
            return this;
        }

        public PlayerBuilder game(Game game) {
            player.game = game;
            return this;
        }
        public Player build() throws PlayerException {
            if(player.user == null || player.game == null)
                throw new PlayerException("User and game are required");
            return player;
        }
    }
}
