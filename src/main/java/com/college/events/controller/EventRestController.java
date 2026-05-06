package com.college.events.controller;

import com.college.events.model.Event;
import com.college.events.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventRestController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String type) {
        List<Event> events;
        if (date != null || department != null || type != null) {
            events = eventService.searchEvents(date, department, type);
        } else {
            events = eventService.getAllEvents();
        }
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        try {
            Event event = eventService.getEventById(id);
            return ResponseEntity.ok(event);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
