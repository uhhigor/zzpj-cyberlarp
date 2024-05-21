package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.game.exceptions.BankingException.BankingServiceException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterServiceException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameServiceException;
import com.example.cyberlarpapi.game.model.Game;
import com.example.cyberlarpapi.game.model.Transaction;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.player.Player;
import com.example.cyberlarpapi.game.repositories.GameRepository;
import com.example.cyberlarpapi.game.repositories.TransactionRepository;
import com.example.cyberlarpapi.game.repositories.character.CharacterRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final TransactionRepository transactionRepository;
    private final GameRepository gameRepository;
    private final PlayerService playerService;

    public CharacterService(CharacterRepository characterRepository, PlayerService playerService, TransactionRepository transactionRepository, GameRepository gameRepository) {
        this.characterRepository = characterRepository;
        this.playerService = playerService;
        this.transactionRepository = transactionRepository;
        this.gameRepository = gameRepository;
    }


    public Character save(Character character) {
        return characterRepository.save(character);
    }

    public Character getById(int id) throws CharacterNotFoundException {
        return characterRepository.findById(id).orElseThrow(() -> new CharacterNotFoundException("Character with id " + id + " not found"));
    }

    public void deleteById(int id) throws CharacterNotFoundException {
        if(!characterRepository.existsById(id)){
            throw new CharacterNotFoundException("Character with id " + id + " not found");
        }
        characterRepository.deleteById(id);
    }

    public Character setPlayer(Character character, int playerId) throws CharacterServiceException {
        try {

            Player player = playerService.getById(playerId);
            character.setPlayer(player); // Set the character to the player
            player.setCharacter(character); // Set the player to the character
            playerService.update(player); // Update the player
            return characterRepository.save(character); // Update the character
        } catch (Exception e) {
            throw new CharacterServiceException("Error while setting player", e);
        }
    }


    // ====================== Banking ========================== //

    @Transactional
    public Transaction transferMoney(String senderAccountNumber, String receiverAccountNumber, int amount, Integer gameId) throws BankingServiceException, GameServiceException {
        if(!this.gameRepository.existsById(gameId))
            throw new GameServiceException("Game " + gameId + " not found");

        Character receiver = characterRepository.findByAccountNumber(receiverAccountNumber);
        Character sender = characterRepository.findByAccountNumber(senderAccountNumber);
        if (receiver == null || sender == null) {
            throw new BankingServiceException("There is no character with given account number!");
        }
        Optional<Game> game = gameRepository.findById(gameId);
        if (game.isPresent()) {
            if (!game.get().getAvailableCharacters().contains(sender) ||
                !game.get().getAvailableCharacters().contains(receiver)) {
                throw new BankingServiceException("Characters are not in the same game!");
            }
        }
        if (sender.getBalance() < amount) {
            throw new BankingServiceException("Not enough money on your bank account!");
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        Transaction newTransaction = new Transaction(sender, receiver, amount, LocalDateTime.now());
        transactionRepository.save(newTransaction);
        return newTransaction;
    }

}
