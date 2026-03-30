package com.inventory.controller;

import com.inventory.entity.Category;
import com.inventory.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("activePage", "categories");
        return "categories/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("activePage", "categories");
        return "categories/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.findById(id));
        model.addAttribute("activePage", "categories");
        return "categories/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Category category,
            BindingResult result,
            Model model,
            RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("activePage", "categories");
            return "categories/form";
        }
        categoryService.save(category);
        ra.addFlashAttribute("successMsg", "Category saved successfully!");
        return "redirect:/categories";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            categoryService.deleteById(id);
            ra.addFlashAttribute("successMsg", "Category deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Cannot delete — products are linked to this category.");
        }
        return "redirect:/categories";
    }
}
