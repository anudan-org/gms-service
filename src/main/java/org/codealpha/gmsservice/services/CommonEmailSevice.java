package org.codealpha.gmsservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@ComponentScan
public class CommonEmailSevice {

  @Autowired
  private JavaMailSender mailSender;

  @Value("${spring.send-mail}")
  private boolean sendMail;

  @Async("threadPoolTaskExecutor")
  public void sendMail(String to, String subject, String messageText){
    if(!sendMail){
      return;
    }

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
      //SimpleMailMessage message = new SimpleMailMessage();
      mimeMessageHelper.setTo(to);
      mimeMessageHelper.setSubject(subject);
      mimeMessageHelper.setText(messageText,true);
      mailSender.send(message);
    }catch (MessagingException mse){
      mse.printStackTrace();
    }
  }

}
