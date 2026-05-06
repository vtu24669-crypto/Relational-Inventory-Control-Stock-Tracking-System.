package com.college.events.service;

import com.college.events.model.Event;
import com.college.events.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> searchEvents(LocalDate date, String department, String type) {
        return eventRepository.searchEvents(date, department, type);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid event id: " + id));
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
