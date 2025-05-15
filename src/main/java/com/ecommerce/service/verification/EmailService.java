package com.ecommerce.service.verification;

import com.ecommerce.model.User;
import com.ecommerce.model.verification.EmailVerificationToken;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.verification.EmailTokenVerificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;
    private final String baseUrl;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private final EmailTokenVerificationRepository emailTokenVerificationRepository;

    public EmailService(JavaMailSender mailSender, @Value("${app.base-url}") String baseUrl, EmailTokenVerificationRepository emailTokenVerificationRepository){
        this.mailSender = mailSender;
        this.baseUrl = baseUrl;
        this.emailTokenVerificationRepository = emailTokenVerificationRepository;
    }

    public void sendConfirmationEmail(String to, String token, User user) {
        try {
            String confirmationUrl = baseUrl + "/confirm?token=" + token;
            String subject = "Confirm your email";
            String content = "<p>Hello,</p>"
                    + "<p>Thank you for registering. Please click the link below to confirm your email:</p>"
                    + "<p><a href=\"" + confirmationUrl + "\">Confirm Email</a></p>"
                    + "<br><p>If you did not request this, ignore this email.</p>";

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("noreply@yourdomain.com");
            helper.setText(content, true);

            EmailVerificationToken verificationToken = new EmailVerificationToken();
            verificationToken.setToken(token);
            verificationToken.setUser(user);
            verificationToken.setExpiryTime(LocalDateTime.now().plusMinutes(30));
            emailTokenVerificationRepository.save(verificationToken);


            mailSender.send(mimeMessage);
            log.info("Sent confirmation email to {}", to);

        } catch (MailException | MessagingException e) {
            log.error("Failed to send confirmation email to {}", to, e);
            throw new RuntimeException("Email sending failed", e);
        }
    }
}


