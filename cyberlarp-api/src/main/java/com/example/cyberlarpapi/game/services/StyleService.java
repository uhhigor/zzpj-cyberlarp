package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.game.data.character.style.Style;
import com.example.cyberlarpapi.game.data.character.style.StyleDTO;
import com.example.cyberlarpapi.game.exceptions.StyleException;
import com.example.cyberlarpapi.game.exceptions.StyleServiceException;
import com.example.cyberlarpapi.game.repositories.character.StyleRepository;
import org.springframework.data.util.StreamUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StyleService {
    private final StyleRepository styleRepository;


    public StyleService(StyleRepository styleRepository) {
        this.styleRepository = styleRepository;
    }

    public Style getById(int id) throws StyleServiceException {
        return styleRepository.findById(id).orElseThrow(() -> new StyleServiceException("Style not found"));
    }

    public List<Style> getAll() {
        return StreamUtils.createStreamFromIterator(styleRepository.findAll().iterator()).toList();
    }

    public void deleteById(int id) {
        styleRepository.deleteById(id);
    }

    public Style create(StyleDTO styleDTO) throws StyleServiceException {
        try {
            return styleRepository.save(Style.builder()
                    .name(styleDTO.getName())
                    .description(styleDTO.getDescription())
                    .build());
        } catch (StyleException e) {
            throw new StyleServiceException("Error while creating style", e);
        }
    }

    public Style update(StyleDTO styleDTO) throws StyleServiceException {
        Style style = getById(styleDTO.getId());
        style.setName(styleDTO.getName());
        style.setDescription(styleDTO.getDescription());
        return styleRepository.save(style);
    }

    public Style update(Style style) {
        return styleRepository.save(style);
    }

}
