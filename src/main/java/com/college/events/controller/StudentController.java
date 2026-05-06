package com.college.events.controller;

import com.college.events.model.Event;
import com.college.events.model.EventRegistration;
import com.college.events.service.EventRegistrationService;
import com.college.events.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class StudentController {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRegistrationService registrationService;

    @GetMapping("/")
    public String home(Model model) {
        List<Event> upcoming = eventService.getAllEvents().stream()
                .filter(e -> !e.getDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
        Map<String, List<Event>> groupedEvents = upcoming.stream()
                .collect(Collectors.groupingBy(Event::getType));
        model.addAttribute("groupedEvents", groupedEvents);
        return "home";
    }

    @GetMapping("/events")
    public String browseEvents(Model model,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String type) {
        List<Event> filteredEvents = eventService.searchEvents(date, department, type);
        Map<String, List<Event>> groupedEvents = filteredEvents.stream()
                .collect(Collectors.groupingBy(Event::getType));
        model.addAttribute("events", filteredEvents);
        model.addAttribute("groupedEvents", groupedEvents);
        return "events";
    }

    @Autowired
    private com.college.events.repository.UserRepository userRepository;

    @GetMapping("/events/{id}/register")
    public String showRegistrationForm(@PathVariable("id") Long id, Model model, org.springframework.security.core.Authentication auth) {
        Event event = eventService.getEventById(id);
        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        
        // Auto-fill logged in user info
        if (auth != null && auth.isAuthenticated()) {
            userRepository.findByUsername(auth.getName()).ifPresent(u -> {
                registration.setStudentName(u.getUsername());
                registration.setStudentEmail(u.getEmail());
            });
        }
        
        model.addAttribute("registration", registration);
        model.addAttribute("event", event);
        return "register";
    }

    @PostMapping("/events/{id}/register")
    public String processRegistration(@PathVariable("id") Long id,
            @Valid @ModelAttribute("registration") EventRegistration registration,
            BindingResult bindingResult, Model model,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        
        registration.setId(null); // CRITICAL FIX

        Event event = eventService.getEventById(id);
        registration.setEvent(event);

        if (bindingResult.hasErrors()) {
            model.addAttribute("event", event);
            return "register";
        }

        // Generate OTP but don't send email yet. We wait for payment.
        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        registration.setVerificationToken(otp);
        registration.setVerified(false);
        registration.setPaymentComplete(false); // Default to not paid
        
        EventRegistration saved = registrationService.registerStudent(registration);
        return "redirect:/payment?regId=" + saved.getId();
    }

    @GetMapping("/payment")
    public String showPaymentPage(@RequestParam("regId") Long regId, Model model) {
        EventRegistration registration = registrationService.getRegistrationById(regId);
        if (registration == null) {
            return "redirect:/events";
        }
        model.addAttribute("registration", registration);
        model.addAttribute("event", registration.getEvent());
        return "payment";
    }

    @PostMapping("/process-payment")
    public String processPayment(@RequestParam("registrationId") Long regId, 
                                 @RequestParam("otp") String otp, 
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        
        EventRegistration registration = registrationService.getRegistrationById(regId);
        if (registration == null) {
            return "redirect:/events";
        }

        // Verify OTP
        if (!otp.equals(registration.getVerificationToken())) {
            return "redirect:/payment?regId=" + regId + "&error=invalid-otp";
        }

        EventRegistration updatedRegistration = registrationService.updatePaymentStatus(regId, true);
        if (updatedRegistration != null) {
            updatedRegistration.setVerified(true);
            registrationService.registerStudent(updatedRegistration); // save it
            
            redirectAttrs.addFlashAttribute("registration", updatedRegistration);
            redirectAttrs.addFlashAttribute("toastMessage", "Registration Successful! Here is your ticket for " + updatedRegistration.getEvent().getName() + ".");
            return "redirect:/registration-success";
        }
        return "redirect:/events";
    }

    @GetMapping("/registration-success")
    public String registrationSuccess(Model model) {
        // Flash attributes like 'registration' are automatically added to the model
        if (!model.containsAttribute("registration")) {
            return "redirect:/events"; // Fallback if reloaded
        }
        return "registration-success";
    }

    @GetMapping("/verify-otp")
    public String showVerifyOtpPage(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtpSubmission(@RequestParam("email") String email, @RequestParam("otp") String otp) {
        boolean verified = registrationService.verifyStudent(email, otp);
        if (verified) {
            return "redirect:/?verified=true"; // Let home screen show success
        }
        return "redirect:/verify-otp?email=" + email + "&error=invalid-otp";
    }

    @GetMapping("/my-registrations")
    public String myRegistrationsView(Model model, org.springframework.security.core.Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            userRepository.findByUsername(auth.getName()).ifPresent(u -> {
                model.addAttribute("registrations", registrationService.getRegistrationsByEmail(u.getEmail()));
            });
        }
        return "my-registrations";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/portal")
    public String portal() {
        return "portal";
    }
}
