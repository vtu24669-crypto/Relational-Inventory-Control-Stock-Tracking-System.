package com.college.events.repository;

import com.college.events.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE " +
           "(:date IS NULL OR e.date = :date) AND " +
           "(:department IS NULL OR :department = '' OR e.department = :department) AND " +
           "(:type IS NULL OR :type = '' OR e.type = :type) " +
           "ORDER BY e.date ASC")
    List<Event> searchEvents(@Param("date") LocalDate date,
                             @Param("department") String department,
                             @Param("type") String type);
}
