package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.game.exceptions.BankingException.BankingServiceException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameServiceException;
import com.example.cyberlarpapi.game.exceptions.MoneyServiceException;
import com.example.cyberlarpapi.game.model.Transaction;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.character.CharacterClass;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MoneyService {

    private final TransactionRepository transactionRepository;
    private final CharacterService characterService;

    public MoneyService(TransactionRepository transactionRepository, CharacterService characterService) {
        this.transactionRepository = transactionRepository;
        this.characterService = characterService;
    }

    @Transactional
    public Transaction transferMoney(String senderAccountNumber, String receiverAccountNumber, int amount, Game game) throws BankingServiceException, GameServiceException, CharacterNotFoundException {
        Character receiver = getCharacterByBankAccountNumber(game, receiverAccountNumber);
        Character sender = getCharacterByBankAccountNumber(game, senderAccountNumber);

        if (sender.getBalance() < amount) {
            throw new BankingServiceException("Not enough money on sender's bank account.");
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        characterService.save(sender);
        characterService.save(receiver);

        Transaction newTransaction = new Transaction(sender, receiver, amount, LocalDateTime.now());
        transactionRepository.save(newTransaction);
        return newTransaction;
    }

    private Character getCharacterByBankAccountNumber(Game game, String bankAccountNumber) throws CharacterNotFoundException {
        List<Character> characters = game.getCharacters();
        return characters.stream().filter(c -> c.getAccountNumber().equals(bankAccountNumber)).findFirst().orElseThrow(() -> new CharacterNotFoundException("Character with bank account number " + bankAccountNumber + " not found"));
    }


    // ====================== Banking ========================== //

    public void addTransaction(Transaction transaction, Character character) throws GameServiceException {
        character.getTransactions().add(transaction);
        characterService.save(character);
    }

    public List<Transaction> getTransactions(Game game, Character sender, String characterBankNumber) throws CharacterNotFoundException, MoneyServiceException {
        Character character = getCharacterByBankAccountNumber(game, characterBankNumber);
        if (character.getCharacterClass() == CharacterClass.NETRUNNER || character.getId().equals(sender.getId())) {
            return character.getTransactions();
        } else {
            throw new MoneyServiceException("You are not allowed to see transactions of this character.");
        }
    }
}
