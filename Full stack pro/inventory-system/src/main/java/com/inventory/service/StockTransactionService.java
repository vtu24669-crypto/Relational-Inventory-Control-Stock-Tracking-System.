package com.inventory.service;

import com.inventory.entity.Product;
import com.inventory.entity.StockTransaction;
import com.inventory.entity.StockTransaction.TransactionType;
import com.inventory.repository.StockTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StockTransactionService {

    private final StockTransactionRepository transactionRepository;
    private final ProductService productService;

    public List<StockTransaction> findAll() {
        // FIX: order by date desc so newest transactions appear first in list page
        return transactionRepository.findAllByOrderByTransactionDateDesc();
    }

    public List<StockTransaction> findRecent20() {
        return transactionRepository.findTop20ByOrderByTransactionDateDesc();
    }

    public List<StockTransaction> findByProductId(Long productId) {
        return transactionRepository.findByProductIdOrderByTransactionDateDesc(productId);
    }

    /**
     * FIX: Wraps entire save + quantity-update in one @Transactional method,
     * so if anything fails the DB rolls back automatically.
     */
    public StockTransaction save(StockTransaction transaction) {
        Product product = productService.findById(transaction.getProduct().getId());
        int qty = transaction.getQuantity();

        if (transaction.getTransactionType() == TransactionType.IN) {
            product.setQuantity(product.getQuantity() + qty);
        } else {
            if (product.getQuantity() < qty) {
                throw new RuntimeException(
                        "Insufficient stock! Available: " + product.getQuantity() + ", Requested: " + qty);
            }
            product.setQuantity(product.getQuantity() - qty);
        }
        productService.save(product);

        // FIX: Attach the fully-loaded product to the transaction before saving
        transaction.setProduct(product);
        return transactionRepository.save(transaction);
    }

    public Long totalStockIn() {
        Long result = transactionRepository.totalStockIn();
        return result != null ? result : 0L;
    }

    public Long totalStockOut() {
        Long result = transactionRepository.totalStockOut();
        return result != null ? result : 0L;
    }

    public long count() {
        return transactionRepository.count();
    }
}
