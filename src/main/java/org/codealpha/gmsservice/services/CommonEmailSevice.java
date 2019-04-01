package org.codealpha.gmsservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@ComponentScan
public class CommonEmailSevice {

  @Autowired
  private JavaMailSender mailSender;


  public void sendMail(String to, String subject, String messageText){

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(messageText);
    mailSender.send(message);
  }

}
