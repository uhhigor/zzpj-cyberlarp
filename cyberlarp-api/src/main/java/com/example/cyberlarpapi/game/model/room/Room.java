package com.example.cyberlarpapi.game.model.room;

import com.example.cyberlarpapi.User;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Room {
    
    @Id
    private Integer Id;

    @OneToOne
    private User owner;

    @OneToMany
    private List<User> users;

    public Room(User owner) {
        this.owner = owner;
    }

    public Room() {

    }

    public List<User> getUsers() {
        return users;
    }

    public boolean addUser(User u) {
        return this.users.add(u);
    }

    public boolean removeUser(User u) {
        return this.users.remove(u);
    }

    public User findUser(Integer userId) {
        for (User u : users) {
            if (u.getId().equals(userId)) {
                return u;
            }
        }
        return null;
    }

    public Integer getId() {
        return Id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}