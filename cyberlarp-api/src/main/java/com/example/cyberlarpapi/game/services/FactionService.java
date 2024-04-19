package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.game.data.character.faction.Faction;
import com.example.cyberlarpapi.game.data.character.faction.FactionDTO;
import com.example.cyberlarpapi.game.data.character.style.Style;
import com.example.cyberlarpapi.game.data.character.style.StyleDTO;
import com.example.cyberlarpapi.game.exceptions.FactionException;
import com.example.cyberlarpapi.game.exceptions.FactionServiceException;
import com.example.cyberlarpapi.game.exceptions.StyleException;
import com.example.cyberlarpapi.game.exceptions.StyleServiceException;
import com.example.cyberlarpapi.game.repositories.character.FactionRepository;
import com.example.cyberlarpapi.game.repositories.character.StyleRepository;
import org.springframework.data.util.StreamUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FactionService {
    private final FactionRepository factionRepository;


    public FactionService(FactionRepository factionRepository) {
        this.factionRepository = factionRepository;
    }

    public Faction getById(int id) throws FactionServiceException {
        return factionRepository.findById(id).orElseThrow(() -> new FactionServiceException("Faction not found"));
    }

    public List<Faction> getAll() {
        return StreamUtils.createStreamFromIterator(factionRepository.findAll().iterator()).toList();
    }

    public void deleteById(int id) {
        factionRepository.deleteById(id);
    }

    public Faction create(FactionDTO factionDTO) throws FactionServiceException {
        try {
            return factionRepository.save(Faction.builder()
                    .name(factionDTO.getName())
                    .description(factionDTO.getDescription())
                    .build());
        } catch (FactionException e) {
            throw new FactionServiceException("Error while creating faction", e);
        }
    }

    public Faction update(int id, FactionDTO factionDTO) throws FactionServiceException {
        Faction faction = getById(id);
        faction.setName(factionDTO.getName());
        faction.setDescription(factionDTO.getDescription());
        return factionRepository.save(faction);
    }

    public Faction update(Faction faction) {
        return factionRepository.save(faction);
    }

}
