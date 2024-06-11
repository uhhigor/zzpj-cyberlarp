package com.example.cyberlarpapi.game.model.character.faction;

import com.example.cyberlarpapi.game.exceptions.FactionException.FactionException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class Faction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Integer id;

    private String name;

    private String description;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
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
