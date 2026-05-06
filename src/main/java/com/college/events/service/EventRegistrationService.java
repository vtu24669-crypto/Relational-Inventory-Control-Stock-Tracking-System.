package com.college.events.service;

import com.college.events.model.EventRegistration;
import com.college.events.repository.EventRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@Transactional
public class EventRegistrationService {

    private final EventRegistrationRepository registrationRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public EventRegistrationService(EventRegistrationRepository registrationRepository, JavaMailSender mailSender) {
        this.registrationRepository = registrationRepository;
        this.mailSender = mailSender;
    }

    public EventRegistration registerStudent(EventRegistration registration) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        registration.setVerificationToken(otp);
        registration.setVerified(false);
        EventRegistration saved = registrationRepository.save(registration);

        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(saved.getStudentEmail());
            email.setSubject("Verify Your Event Registration");
            email.setText("Dear " + saved.getStudentName()
                    + ",\n\nYour OTP for verifying your registration for the event is: " + otp);
            mailSender.send(email);
            System.out.println("Email sent successfully to " + saved.getStudentEmail());
        } catch (Exception e) {
            System.err.println("Failed to send OTP email. Console fallback. OTP=" + otp);
        }

        return saved;
    }

    public boolean verifyStudent(String email, String otp) {
        EventRegistration registration = registrationRepository.findByStudentEmailAndVerificationToken(email, otp);
        if (registration != null && !registration.isVerified()) {
            registration.setVerified(true);
            registrationRepository.save(registration);
            return true;
        }
        return false;
    }

    public List<EventRegistration> getRegistrationsByEmail(String email) {
        return registrationRepository.findByStudentEmail(email);
    }

    public List<EventRegistration> getAllRegistrations() {
        return registrationRepository.findAllWithEvents();
    }

    public EventRegistration getRegistrationById(Long id) {
        return registrationRepository.findById(id).orElse(null);
    }

    public EventRegistration updatePaymentStatus(Long id, boolean status) {
        EventRegistration reg = getRegistrationById(id);
        if (reg != null) {
            reg.setPaymentComplete(status);
            reg = registrationRepository.save(reg);
            // Initialize lazy event proxy to avoid LazyInitializationException in the view
            if (reg.getEvent() != null) {
                reg.getEvent().getName();
            }
        }
        return reg;
    }

    public List<Map<String, Object>> getRegistrationStatistics() {
        return registrationRepository.getRegistrationStatistics();
    }
}
