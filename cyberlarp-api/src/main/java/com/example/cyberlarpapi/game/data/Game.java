package com.example.cyberlarpapi.game.data;

import com.example.cyberlarpapi.User;
import com.example.cyberlarpapi.game.data.character.Character;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Game {
    @Id
    @GeneratedValue
    private Integer id;

    @OneToMany
    @JsonManagedReference
    private List<Character> characters;

    @OneToMany
    private List<User> players;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
