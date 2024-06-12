package com.example.cyberlarpapi.game.model.user;


import jakarta.persistence.*;
import com.example.cyberlarpapi.game.model.character.Character;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class _User {
    @Id
    @GeneratedValue
    private Integer id;

    private String email;

    private String username;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Character> characters = new ArrayList<>();

    public void addCharacter(Character character) {
        characters.add(character);
    }
}
