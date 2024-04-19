package com.example.cyberlarpapi.game.data.character.style;

import com.example.cyberlarpapi.game.data.Game;
import com.example.cyberlarpapi.game.exceptions.StyleException;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Style {

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

    public static StyleBuilder builder() {
        return new StyleBuilder();
    }

    public static class StyleBuilder {
        private final Style style;

        public StyleBuilder() {
            this.style = new Style();
        }

        public StyleBuilder id(Integer id) {
            style.id = id;
            return this;
        }

        public StyleBuilder name(String name) {
            style.name = name;
            return this;
        }

        public StyleBuilder description(String description) {
            style.description = description;
            return this;
        }

        public Style build() throws StyleException {
            if(style.name == null)
                throw new StyleException("Name is required");
            if(style.description == null)
                throw new StyleException("Description is required");

            return style;
        }
    }
}
