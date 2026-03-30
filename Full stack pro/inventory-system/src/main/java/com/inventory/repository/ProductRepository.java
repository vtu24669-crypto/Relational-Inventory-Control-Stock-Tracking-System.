package com.inventory.repository;

import com.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    List<Product> findByNameContainingIgnoreCase(String name);

    // FIX: JPQL references field names (quantity, reorderLevel) not column names
    @Query("SELECT p FROM Product p WHERE p.quantity <= p.reorderLevel ORDER BY p.quantity ASC")
    List<Product> findLowStockProducts();
}
