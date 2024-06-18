package com.example.cyberlarpapi.game.model.game;

import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.ChatExceptions.InvalidFactionException;
import com.example.cyberlarpapi.game.exceptions.ChatExceptions.MessageNotFoundException;
import com.example.cyberlarpapi.game.model.chat.SCOPE;
import com.example.cyberlarpapi.game.model.chat.message.Message;
import com.example.cyberlarpapi.game.model.task.Task;
import com.example.cyberlarpapi.game.model.user._User;
import com.example.cyberlarpapi.game.model.character.Character;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.thymeleaf.standard.expression.MessageExpression;

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
    private List<Message> messages = new ArrayList<>();

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

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void deleteMessage(Message message) {
        messages.remove(message);
    }

    public List<Message> getMessages(Character character, SCOPE scope) {
        List<Message> publicMessages = new ArrayList<>();
        List<Message> factionMessages = new ArrayList<>();

        for (Message message : messages) {
            if (message.getScope().equals(SCOPE.PUBLIC)) {
                publicMessages.add(message);
            }
            if (isScopeValid(scope) && message.getScope().equals(scope) && character.getFaction().toString().equals(scope.toString())) {
                factionMessages.add(message);
            }
        }

        if (scope.equals(SCOPE.PUBLIC)) {
            return publicMessages;
        } else if (scope.equals(SCOPE.ALL)) {
            List<Message> allMessages = new ArrayList<>(publicMessages);
            allMessages.addAll(factionMessages);
            return allMessages;
        } else if (!scope.toString().equals(character.getFaction().toString())) {
            throw new InvalidFactionException("Invalid faction");
        } else if (isScopeValid(scope)) {
            return factionMessages;
        } else {
            System.err.println("Invalid scope: " + scope);
            return new ArrayList<>();
        }
    }

    private boolean isScopeValid(SCOPE scope) {
        try {
            SCOPE.valueOf(scope.toString());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
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
