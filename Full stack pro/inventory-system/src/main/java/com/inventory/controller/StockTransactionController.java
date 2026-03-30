package com.inventory.controller;

import com.inventory.entity.StockTransaction;
import com.inventory.entity.StockTransaction.TransactionType;
import com.inventory.service.ProductService;
import com.inventory.service.StockTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockTransactionController {

    private final StockTransactionService transactionService;
    private final ProductService productService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("transactions", transactionService.findAll());
        model.addAttribute("activePage", "stock");
        return "stock/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("transaction", new StockTransaction());
        model.addAttribute("products", productService.findAll());
        model.addAttribute("transactionTypes", TransactionType.values());
        model.addAttribute("activePage", "stock");
        return "stock/form";
    }

    /**
     * FIX: Use @RequestParam for productId and transactionType (plain strings from
     * form),
     * then manually set on the entity. Previously Spring could not bind the enum
     * directly from the radio input name "transactionType".
     */
    @PostMapping("/save")
    public String save(@RequestParam Long productId,
            @RequestParam String transactionType,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String notes,
            RedirectAttributes ra) {
        try {
            StockTransaction tx = new StockTransaction();
            tx.setProduct(productService.findById(productId));
            tx.setTransactionType(TransactionType.valueOf(transactionType));
            tx.setQuantity(quantity);
            tx.setNotes(notes);
            transactionService.save(tx);
            ra.addFlashAttribute("successMsg", "Stock transaction recorded successfully!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/stock";
    }
}
