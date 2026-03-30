package com.inventory.controller;

import com.inventory.entity.Supplier;
import com.inventory.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("activePage", "suppliers");
        return "suppliers/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        model.addAttribute("activePage", "suppliers");
        return "suppliers/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("supplier", supplierService.findById(id));
        model.addAttribute("activePage", "suppliers");
        return "suppliers/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Supplier supplier,
            BindingResult result,
            Model model,
            RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("activePage", "suppliers");
            return "suppliers/form";
        }
        supplierService.save(supplier);
        ra.addFlashAttribute("successMsg", "Supplier saved successfully!");
        return "redirect:/suppliers";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            supplierService.deleteById(id);
            ra.addFlashAttribute("successMsg", "Supplier deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Cannot delete — products are linked to this supplier.");
        }
        return "redirect:/suppliers";
    }
}
