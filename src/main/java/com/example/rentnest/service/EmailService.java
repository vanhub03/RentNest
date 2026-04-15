package com.example.rentnest.service;

import com.example.rentnest.model.dto.email.EmailDTO;

import java.io.IOException;

public interface EmailService {
    boolean sendEmail(EmailDTO email) throws IOException;
}
