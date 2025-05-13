package com.sharks.email_service.services;

import com.sharks.email_service.models.EmailDetails;
import com.sharks.email_service.models.EmailDetailsWithAttachment;
import com.sharks.email_service.services.impl.EmailServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> simpleMailMessageCaptor;

    @Captor
    private ArgumentCaptor<MimeMessage> mimeMessageCaptor;

    @BeforeEach
    void setUp() {
        // Set sender field via reflection since it's injected by @Value
        ReflectionTestUtils.setField(emailService, "sender", "sender@example.com");
    }

    @Test
    void testSendMail_Success() {
        EmailDetails details = new EmailDetails("to@example.com", "Subject", "Body");

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        emailService.sendMail(details);

        verify(javaMailSender, times(1)).send(simpleMailMessageCaptor.capture());
        SimpleMailMessage sent = simpleMailMessageCaptor.getValue();
        assertEquals("sender@example.com", sent.getFrom());
        assertEquals("to@example.com", sent.getTo()[0]);
        assertEquals("Subject", sent.getSubject());
        assertEquals("Body", sent.getText());
    }

    @Test
    void testSendMail_MailException_Handled() {
        EmailDetails details = new EmailDetails("to@example.com", "Subject", "Body");

        doThrow(new MailException("fail") {
        }).when(javaMailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> emailService.sendMail(details));
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendMailWithAttachment_Success() throws Exception {
        byte[] bytes = "file-content".getBytes();
        EmailDetailsWithAttachment details = new EmailDetailsWithAttachment(
                "to@example.com", "Subject", "Body", bytes, "file.txt", "text/plain");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        emailService.sendMailWithAttachment(details);

        verify(javaMailSender, times(1)).send(mimeMessage);
        verify(javaMailSender, times(1)).createMimeMessage();
    }

    @Test
    void testSendMailWithAttachment_MessagingException_Handled() throws Exception {
        byte[] bytes = "file-content".getBytes();
        EmailDetailsWithAttachment details = new EmailDetailsWithAttachment(
                "to@example.com", "Subject", "Body", bytes, "file.txt", "text/plain");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Simulate MessagingException by spying on MimeMessageHelper constructor
        try (MockedConstruction<MimeMessageHelper> ignored = Mockito.mockConstruction(MimeMessageHelper.class,
                (mock, context) -> {
                    doThrow(new MessagingException("fail")).when(mock).setFrom(anyString());
                })) {
            assertDoesNotThrow(() -> emailService.sendMailWithAttachment(details));
        }
        verify(javaMailSender, times(1)).createMimeMessage();
        // send should still be called, but with an empty message
        verify(javaMailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendMailWithAttachment_MailException_Handled() throws Exception {
        byte[] bytes = "file-content".getBytes();
        EmailDetailsWithAttachment details = new EmailDetailsWithAttachment(
                "to@example.com", "Subject", "Body", bytes, "file.txt", "text/plain");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new MailException("fail") {
        }).when(javaMailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() -> emailService.sendMailWithAttachment(details));
        verify(javaMailSender, times(1)).send(mimeMessage);
    }
}
