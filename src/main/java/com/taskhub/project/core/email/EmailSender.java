package com.taskhub.project.core.email;

public interface EmailSender {
    void send(String to, String text);
    void send(String to, String text, String subject);
}
