package com.inventory.controller;

import com.inventory.service.CategoryService;
import com.inventory.service.ProductService;
import com.inventory.service.StockTransactionService;
import com.inventory.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;
    private final StockTransactionService transactionService;

    @GetMapping({ "/", "/dashboard" })
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productService.count());
        // FIX: Use count() from service instead of findAll().size() — avoids loading
        // entire list
        model.addAttribute("totalCategories", categoryService.count());
        model.addAttribute("totalSuppliers", supplierService.count());
        model.addAttribute("totalTransactions", transactionService.count());
        model.addAttribute("totalStockIn", transactionService.totalStockIn());
        model.addAttribute("totalStockOut", transactionService.totalStockOut());
        model.addAttribute("lowStockProducts", productService.findLowStockProducts());
        model.addAttribute("recentTransactions", transactionService.findRecent20());
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }
}
