package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.Notifications;
import org.codealpha.gmsservice.repositories.NotificationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class NotificationsService{

@Autowired
  private NotificationsRepository notificationsRepository;

  public List<Notifications> getUserNotifications(Long userId, boolean read){
  	return notificationsRepository.findByUserIdAndReadOrderByPostedOnDesc(userId, read);
  }
public List<Notifications> getAllUserNotifications(Long userId){
  	return notificationsRepository.findTop15ByUserIdOrderByPostedOnDesc(userId);
  }

  public Notifications saveNotification (String[] message, Long userId, Long id,String notificationFor){
      Notifications notification = new Notifications();
      notification.setMessage(message[1]);
      notification.setTitle(message[0]);
      notification.setPostedOn(new Date());
      notification.setRead(false);
      notification.setUserId(userId);
      notification.setNotificationFor(notificationFor.toUpperCase());
      if(notificationFor.equalsIgnoreCase("REPORT")){
          notification.setReportId(id);
      }else if(notificationFor.equalsIgnoreCase("GRANT")){
          notification.setGrantId(id);
      }else if(notificationFor.equalsIgnoreCase("DISBURSEMENT")){
        notification.setDisbursementId(id);
    }else if(notificationFor.equalsIgnoreCase("CLOSURE")){
      notification.setClosureId(id);
  }

      return notificationsRepository.save(notification);
  }

    public Notifications saveNotification (Notifications notif){
      return notificationsRepository.save(notif);
    }

  public Notifications getNotificationById(Long notificationId){
      return notificationsRepository.findById(notificationId).orElse(null);
  }
}