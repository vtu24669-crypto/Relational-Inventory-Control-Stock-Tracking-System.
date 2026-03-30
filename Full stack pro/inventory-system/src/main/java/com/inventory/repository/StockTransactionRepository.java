package com.inventory.repository;

import com.inventory.entity.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

    List<StockTransaction> findByProductIdOrderByTransactionDateDesc(Long productId);

    List<StockTransaction> findTop20ByOrderByTransactionDateDesc();

    // FIX: Added this method used by StockTransactionService.findAll()
    List<StockTransaction> findAllByOrderByTransactionDateDesc();

    // FIX: Use fully-qualified enum constant in JPQL (not a plain string)
    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM StockTransaction t " +
            "WHERE t.transactionType = com.inventory.entity.StockTransaction.TransactionType.IN")
    Long totalStockIn();

    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM StockTransaction t " +
            "WHERE t.transactionType = com.inventory.entity.StockTransaction.TransactionType.OUT")
    Long totalStockOut();
}
