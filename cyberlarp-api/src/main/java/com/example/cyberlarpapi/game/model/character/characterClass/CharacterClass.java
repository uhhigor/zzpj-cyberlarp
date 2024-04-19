package com.example.cyberlarpapi.game.model.character.characterClass;

import com.example.cyberlarpapi.game.exceptions.CharacterClassException.CharacterClassException;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class CharacterClass {

    @Id
    private Integer id;

    private String name;

    private String description;

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

    public static CharacterClassBuilder builder() {
        return new CharacterClassBuilder();
    }

    public static class CharacterClassBuilder {
        private final CharacterClass characterClass;

        public CharacterClassBuilder() {
            characterClass = new CharacterClass();
        }

        public CharacterClassBuilder id(Integer id) {
            characterClass.id = id;
            return this;
        }

        public CharacterClassBuilder name(String name) {
            characterClass.name = name;
            return this;
        }

        public CharacterClassBuilder description(String description) {
            characterClass.description = description;
            return this;
        }

        public CharacterClass build() throws CharacterClassException {
            if(characterClass.id == null) {
                throw new CharacterClassException("Character class id is required");
            }
            if(characterClass.name == null) {
                throw new CharacterClassException("Character class name is required");
            }
            return characterClass;
        }
    }
}
