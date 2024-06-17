package com.example.cyberlarpapi.game.model.game;

import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.model.chat.GroupChat;
import com.example.cyberlarpapi.game.model.task.Task;
import com.example.cyberlarpapi.game.model.user._User;
import com.example.cyberlarpapi.game.DefaultGameData;
import com.example.cyberlarpapi.game.model.Transaction;
import com.example.cyberlarpapi.game.model.character.Character;
import jakarta.persistence.*;
import javassist.NotFoundException;
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
    private List<Character> characters = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<GroupChat> groupChats = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    private _User gameMaster;

    public void addCharacter(Character character) {
        characters.add(character);
    }

    public void removeCharacter(Character character) {
        characters.remove(character);
    }

    public Character getUserCharacter(_User user) throws CharacterNotFoundException {
        for(Character character : characters) {
            if(character.getUser() == null) {
                continue;
            }
            if(character.getUser().getId().equals(user.getId())) {
                return character;
            }
        }
        throw new CharacterNotFoundException("Character not found");
    }

    public static GameBuilder builder() {
        return new GameBuilder();
    }

    public static class GameBuilder {
        private final Game game;

        public GameBuilder() {
            game = new Game();
        }

        public GameBuilder characters(List<Character> characters) {
            game.setCharacters(characters);
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
            if(game.getCharacters() == null) {
                game.setCharacters(List.of());
            }
            if(game.getGameMaster() == null) {
                throw new IllegalArgumentException("Game master is required");
            }
            return game;
        }
    }
}
