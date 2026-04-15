package com.example.rentnest.service.email;

import com.example.rentnest.model.dto.email.EmailDTO;
import com.example.rentnest.service.EmailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Service
public class EmailServiceImpl implements EmailService {
    @Value(value = "${spring.sendgrid.sender-name}")
    private String SENDER_NAME;

    @Value(value = "${spring.sendgrid.api-key}")
    private String API_KEY;

    @Value(value = "${spring.sendgrid.sender-email}")
    private String SENDER_EMAIL;
    @Override
    public boolean sendEmail(EmailDTO email) throws IOException {
        Objects.requireNonNull(email.getEmailToList());

        Email sender = new Email(SENDER_EMAIL, SENDER_NAME);

        Personalization personalization = new Personalization();
        email.getEmailToList().forEach(emailTo -> personalization.addTo(new Email(emailTo)));
        if(Objects.nonNull(email.getEmailCCList())){
            email.getEmailCCList().forEach(emailCC -> personalization.addCc(new Email(emailCC)));
        }
        if(Objects.nonNull(email.getEmailBCCList())){
            email.getEmailBCCList().forEach(emailBCC -> personalization.addBcc(new Email(emailBCC)));
        }
        Content content = new Content("text/html", email.getBody());
        Mail mail = new Mail();
        mail.setFrom(sender);
        mail.setSubject(email.getSubject());
        mail.addPersonalization(personalization);
        mail.addContent(content);
        SendGrid sg = new SendGrid(API_KEY);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);
        return response.getStatusCode() == 202;
    }
}

