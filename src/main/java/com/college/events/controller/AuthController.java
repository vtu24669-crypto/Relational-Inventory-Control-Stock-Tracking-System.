package com.college.events.controller;

import com.college.events.model.AppUser;
import com.college.events.repository.UserRepository;
import com.college.events.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @GetMapping("/register-user")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new AppUser());
        return "register-user";
    }

    @PostMapping("/register-user")
    public String registerUserAccount(@ModelAttribute("user") AppUser user, Model model, RedirectAttributes redirectAttrs) {
        // Validate
        if (userRepository.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "Username already exists!");
            return "register-user";
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email already exists!");
            return "register-user";
        }

        // Save User
        String rawPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("ROLE_USER");
        userRepository.save(user);

        // Auto-login after successful registration
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        redirectAttrs.addFlashAttribute("toastMessage", "Account created successfully! Welcome, " + user.getUsername() + ".");
        return "redirect:/events";
    }
}
