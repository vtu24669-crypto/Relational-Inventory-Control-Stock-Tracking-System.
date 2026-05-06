package com.college.events.config;

import com.college.events.model.AppUser;
import com.college.events.model.Event;
import com.college.events.repository.EventRepository;
import com.college.events.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataSeeder {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Bean
    public CommandLineRunner loadData(EventRepository eventRepository) {
        return args -> {
            // Seed Users
            if (!userRepository.existsByUsername("Harshith reddy")) {
                AppUser admin = new AppUser();
                admin.setUsername("Harshith reddy");
                admin.setPassword(passwordEncoder.encode("07072005"));
                admin.setEmail("admin@college.edu");
                admin.setRole("ROLE_ADMIN");
                userRepository.save(admin);
            }

            if (eventRepository.count() == 0) {
                Event e1 = new Event();
                e1.setName("Treasure Hunting");
                e1.setDescription("A campus-wide technical treasure hunt solving puzzles and clues.");
                e1.setDate(LocalDate.now().plusDays(5));
                e1.setDepartment("Computer Science");
                e1.setType("Technical Event");
                e1.setTicketPrice(100.0);

                Event e2 = new Event();
                e2.setName("Coding Round");
                e2.setDescription("Competitive programming contest challenging algorithms and data structures.");
                e2.setDate(LocalDate.now().plusDays(7));
                e2.setDepartment("Computer Science");
                e2.setType("Technical Event");
                e2.setTicketPrice(150.0);

                Event e3 = new Event();
                e3.setName("Dance Competition");
                e3.setDescription("Annual inter-department dance off.");
                e3.setDate(LocalDate.now().plusDays(10));
                e3.setDepartment("Arts & Culture");
                e3.setType("Cultural Event");
                e3.setTicketPrice(75.0);

                Event e4 = new Event();
                e4.setName("Singing Idol");
                e4.setDescription("Solo and duet singing competition for aspiring vocalists.");
                e4.setDate(LocalDate.now().plusDays(12));
                e4.setDepartment("Arts & Culture");
                e4.setType("Cultural Event");
                e4.setTicketPrice(120.0);

                Event e5 = new Event();
                e5.setName("Cricket Tournament");
                e5.setDescription("Inter-college cricket championship.");
                e5.setDate(LocalDate.now().plusDays(15));
                e5.setDepartment("Physical Education");
                e5.setType("Sports Event");
                e5.setTicketPrice(200.0);

                Event e6 = new Event();
                e6.setName("Tug of War");
                e6.setDescription("Test of strength and teamwork in the classical Tug of War.");
                e6.setDate(LocalDate.now().plusDays(15));
                e6.setDepartment("Physical Education");
                e6.setType("Sports Event");
                e6.setTicketPrice(50.0);

                Event e7 = new Event();
                e7.setName("Debate Championship");
                e7.setDescription("Engage in intellectual discourse on current socio-economic issues.");
                e7.setDate(LocalDate.now().plusDays(20));
                e7.setDepartment("Humanities");
                e7.setType("Literary Event");
                e7.setTicketPrice(100.0);

                eventRepository.saveAll(List.of(e1, e2, e3, e4, e5, e6, e7));
            }

            if (eventRepository.searchEvents(null, "ece", null).isEmpty()) {
                Event e8 = new Event();
                e8.setName("ECE Circuit Design Challenge");
                e8.setDescription("A hardware competition focusing on logic gates and PCBs.");
                e8.setDate(LocalDate.now().plusDays(18));
                e8.setDepartment("ece");
                e8.setType("Technical Event");
                eventRepository.save(e8);
            }
        };
    }
}
