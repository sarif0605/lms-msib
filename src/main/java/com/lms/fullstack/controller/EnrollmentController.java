package com.lms.fullstack.controller;

import com.lms.fullstack.model.Enrollment;
import com.lms.fullstack.model.User;
import com.lms.fullstack.repository.EnrollmentRepository;
import com.lms.fullstack.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentRepository enrollmentRepository;
    private final CertificateService certificateService;

    @PostMapping("/enrollments/{id}/certificate")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow();

        // Security check: ensure student owns the enrollment and it's completed
        if (!enrollment.getStudent().getId().equals(user.getId()) || !enrollment.getIsCompleted()) {
            return ResponseEntity.status(403).build();
        }

        byte[] pdf = certificateService.generateCertificate(enrollment);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificate.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
