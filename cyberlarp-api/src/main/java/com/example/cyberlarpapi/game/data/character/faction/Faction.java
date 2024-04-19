package com.example.cyberlarpapi.game.data.character.faction;

import com.example.cyberlarpapi.game.data.Game;
import com.example.cyberlarpapi.game.exceptions.FactionException;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Faction {

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

    public static FactionBuilder builder() {
        return new FactionBuilder();
    }

    public static class FactionBuilder {
        private final Faction faction;

        public FactionBuilder() {
            faction = new Faction();
        }

        public FactionBuilder id(Integer id) {
            faction.setId(id);
            return this;
        }

        public FactionBuilder name(String name) {
            faction.setName(name);
            return this;
        }

        public FactionBuilder description(String description) {
            faction.setDescription(description);
            return this;
        }

        public Faction build() throws FactionException {
            if(faction.getName() == null) {
                throw new FactionException("Name is required");
            }
            if(faction.getDescription() == null) {
                throw new FactionException("Description is required");
            }
            return faction;
        }
    }
}
