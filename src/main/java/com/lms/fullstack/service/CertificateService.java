package com.lms.fullstack.service;

import com.lms.fullstack.model.Enrollment;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final TemplateEngine templateEngine;

    public byte[] generateCertificate(Enrollment enrollment) {
        Context context = new Context();
        context.setVariable("studentName", enrollment.getStudent().getFullName());
        context.setVariable("courseTitle", enrollment.getCourse().getTitle());
        context.setVariable("completionDate", enrollment.getEnrolledAt().toLocalDate());

        String html = templateEngine.process("certificate_template", context);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}
