package org.codealpha.gmsservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.codealpha.gmsservice.repositories.NotificationsRepository;
import org.codealpha.gmsservice.entities.Notifications;

import java.util.Date;
import java.util.List;


@Service
public class NotificationsService{

@Autowired
  private NotificationsRepository notificationsRepository;

  public List<Notifications> getUserNotifications(Long userId, boolean read){
  	return notificationsRepository.findByUserIdAndReadOrderByPostedOnDesc(userId, read);
  }

  public Notifications saveNotification (String message, Long userId, Long grantId){
      Notifications notification = new Notifications();
      notification.setMessage(message);
      notification.setPostedOn(new Date());
      notification.setRead(false);
      notification.setUserId(userId);
      notification.setGrantId(grantId);
      return notificationsRepository.save(notification);
  }
}