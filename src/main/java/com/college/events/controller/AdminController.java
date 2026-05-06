package com.college.events.controller;

import com.college.events.model.Event;
import com.college.events.service.EventRegistrationService;
import com.college.events.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRegistrationService registrationService;

    @GetMapping({"", "/events"})
    public String dashboard(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        model.addAttribute("registrations", registrationService.getAllRegistrations());
        return "admin/dashboard";
    }

    @GetMapping("/events/create")
    public String createEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "admin/event-form";
    }

    @PostMapping("/events")
    public String saveEvent(@Valid @ModelAttribute("event") Event event, BindingResult bindingResult, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            return "admin/event-form";
        }
        boolean isNew = (event.getId() == null);
        eventService.saveEvent(event);
        redirectAttrs.addFlashAttribute("toastMessage", isNew ? "Event successfully created!" : "Event successfully updated!");
        return "redirect:/admin/events";
    }

    @GetMapping("/events/{id}/edit")
    public String editEventForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("event", eventService.getEventById(id));
        return "admin/event-form";
    }
    
    @PostMapping("/events/{id}/delete")
    public String deleteEvent(@PathVariable("id") Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        eventService.deleteEvent(id);
        redirectAttrs.addFlashAttribute("toastMessage", "Event deleted successfully.");
        return "redirect:/admin/events";
    }

    @GetMapping("/stats")
    public String viewStats(Model model) {
        model.addAttribute("stats", registrationService.getRegistrationStatistics());
        return "admin/stats";
    }
}
