package com.example.cyberlarpapi.game.repositories;

import com.example.cyberlarpapi.game.model.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
}
