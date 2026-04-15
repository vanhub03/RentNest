package com.example.rentnest.Utils;

import com.example.rentnest.model.dto.email.EmailDTO;
import com.example.rentnest.service.EmailService;

import java.io.IOException;
import java.util.List;

public class EmailUtils {
    public static boolean sendEmail(EmailService emailService, String subject, List<String> toEmails, List<String> cc, List<String> bcc, String body) throws IOException {
        EmailDTO emailDTO = EmailDTO.builder()
                .subject(subject)
                .body(body)
                .emailToList(toEmails)
                .emailCCList(cc)
                .emailBCCList(bcc)
                .build();
        return emailService.sendEmail(emailDTO);
    }
}
