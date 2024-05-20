package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.game.exceptions.FactionException.FactionNotFoundException;
import com.example.cyberlarpapi.game.model.character.faction.Faction;
import com.example.cyberlarpapi.game.model.character.faction.FactionDTO;
import com.example.cyberlarpapi.game.exceptions.FactionException.FactionException;
import com.example.cyberlarpapi.game.exceptions.FactionException.FactionServiceException;
import com.example.cyberlarpapi.game.repositories.character.FactionRepository;
import org.springframework.data.util.StreamUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FactionService {
    private final FactionRepository factionRepository;


    public FactionService(FactionRepository factionRepository) {
        this.factionRepository = factionRepository;
    }

    public Faction getById(int id) throws FactionNotFoundException {
        return factionRepository.findById(id).orElseThrow(() -> new FactionNotFoundException("Faction with id " + id + " not found"));
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

    public Faction update(int id, FactionDTO factionDTO) throws FactionNotFoundException {
        Faction faction = getById(id);
        faction.setName(factionDTO.getName());
        faction.setDescription(factionDTO.getDescription());
        return factionRepository.save(faction);
    }

    public Faction update(Faction faction) {
        return factionRepository.save(faction);
    }

}
