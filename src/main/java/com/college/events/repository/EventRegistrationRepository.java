package com.college.events.repository;

import com.college.events.model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    List<EventRegistration> findByStudentEmail(String studentEmail);

    EventRegistration findByVerificationToken(String verificationToken);

    EventRegistration findByStudentEmailAndVerificationToken(String studentEmail, String verificationToken);

    List<EventRegistration> findByEventId(Long eventId);

    @Query("SELECT r.event.name AS eventName, COUNT(r) AS registrationCount FROM EventRegistration r GROUP BY r.event.id ORDER BY COUNT(r) DESC")
    List<Map<String, Object>> getRegistrationStatistics();

    @Query("SELECT r FROM EventRegistration r JOIN FETCH r.event")
    List<EventRegistration> findAllWithEvents();
}
