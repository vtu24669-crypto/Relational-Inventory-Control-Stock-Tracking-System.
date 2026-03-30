package com.inventory.controller;

import com.inventory.entity.Category;
import com.inventory.entity.Product;
import com.inventory.entity.Supplier;
import com.inventory.service.CategoryService;
import com.inventory.service.ProductService;
import com.inventory.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        if (search != null && !search.isBlank()) {
            model.addAttribute("products", productService.searchByName(search));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("products", productService.findAll());
        }
        model.addAttribute("activePage", "products");
        return "products/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("activePage", "products");
        return "products/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("activePage", "products");
        return "products/form";
    }

    /**
     * FIX: Manually resolve category/supplier from submitted IDs.
     * Thymeleaf sends category.id and supplier.id as form params, but Spring MVC
     * cannot
     * auto-bind nested entity objects from an ID alone — we do it manually.
     */
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Product product,
            BindingResult result,
            @RequestParam(name = "category.id", required = false) Long categoryId,
            @RequestParam(name = "supplier.id", required = false) Long supplierId,
            Model model,
            RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("suppliers", supplierService.findAll());
            model.addAttribute("activePage", "products");
            return "products/form";
        }
        // Resolve FK references
        if (categoryId != null) {
            Category cat = new Category();
            cat.setId(categoryId);
            product.setCategory(cat);
        } else {
            product.setCategory(null);
        }
        if (supplierId != null) {
            Supplier sup = new Supplier();
            sup.setId(supplierId);
            product.setSupplier(sup);
        } else {
            product.setSupplier(null);
        }

        productService.save(product);
        ra.addFlashAttribute("successMsg", "Product saved successfully!");
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            productService.deleteById(id);
            ra.addFlashAttribute("successMsg", "Product deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Cannot delete — stock transactions may be linked.");
        }
        return "redirect:/products";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        model.addAttribute("activePage", "products");
        return "products/view";
    }
}
