package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.MailLog;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Component
@ComponentScan
public class CommonEmailSevice {

  @Autowired
  private JavaMailSender mailSender;

  @Value("${spring.send-mail}")
  private boolean sendMail;

  @Autowired
  private MailLogService mailLogService;

  @Async("threadPoolTaskExecutor")
  public void sendMail(String[] to, String[] ccList, String subject, String messageText, String footer[]) {
    if (!sendMail) {
      mailLogService.saveMailLog(new MailLog(DateTime.now().toDate(), StringUtils.arrayToCommaDelimitedString(ccList),
          StringUtils.arrayToCommaDelimitedString(to), messageText, subject, true));
      return;
    }

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
      // SimpleMailMessage message = new SimpleMailMessage();
      if (to != null) {
        mimeMessageHelper.setTo(to);
      }
      if (ccList != null) {
        mimeMessageHelper.setCc(ccList);
      }
      mimeMessageHelper.setFrom("admin@anudan.org", "donotreply");
      mimeMessageHelper.setSubject(subject);
      for (String footerBlock : footer) {
        messageText = messageText.concat(footerBlock);
      }
      mimeMessageHelper.setText(messageText, true);
      mailSender.send(message);
      mailLogService.saveMailLog(new MailLog(DateTime.now().toDate(), StringUtils.arrayToCommaDelimitedString(ccList),
          StringUtils.arrayToCommaDelimitedString(to), messageText, subject, true));

    } catch (MessagingException | UnsupportedEncodingException | MailSendException | MailAuthenticationException mse) {
      mailLogService.saveMailLog(new MailLog(DateTime.now().toDate(), StringUtils.arrayToCommaDelimitedString(ccList),
          StringUtils.arrayToCommaDelimitedString(to), mse.getMessage(), null, true));
    }
  }

}
