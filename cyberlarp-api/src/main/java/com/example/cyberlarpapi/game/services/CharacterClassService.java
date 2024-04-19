package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.game.model.character.characterClass.CharacterClass;
import com.example.cyberlarpapi.game.model.character.characterClass.CharacterClassDTO;
import com.example.cyberlarpapi.game.exceptions.CharacterClassException.CharacterClassException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterServiceException;
import com.example.cyberlarpapi.game.repositories.character.ClassRepository;
import org.springframework.data.util.StreamUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CharacterClassService {
    private final ClassRepository classRepository;


    public CharacterClassService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    public CharacterClass getById(int id) throws CharacterServiceException {
        return classRepository.findById(id).orElseThrow(() -> new CharacterServiceException("Class not found"));
    }

    public List<CharacterClass> getAll() {
        return StreamUtils.createStreamFromIterator(classRepository.findAll().iterator()).toList();
    }

    public void deleteById(int id) {
        classRepository.deleteById(id);
    }

    public CharacterClass create(CharacterClassDTO characterClassDTO) throws CharacterClassException {
        return classRepository.save(CharacterClass.builder()
                .name(characterClassDTO.getName())
                .description(characterClassDTO.getDescription())
                .build());
    }

    public CharacterClass update(int id, CharacterClassDTO characterClassDTO) throws CharacterServiceException {
        CharacterClass characterClass = getById(id);
        characterClass.setName(characterClassDTO.getName());
        characterClass.setDescription(characterClassDTO.getDescription());
        return classRepository.save(characterClass);
    }

    public CharacterClass update(CharacterClass characterClass) {
        return classRepository.save(characterClass);
    }

}
