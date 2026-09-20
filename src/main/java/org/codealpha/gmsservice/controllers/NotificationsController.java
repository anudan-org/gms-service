package org.codealpha.gmsservice.controllers;


import org.codealpha.gmsservice.entities.Notifications;
import org.codealpha.gmsservice.services.NotificationsService;
import org.codealpha.gmsservice.validators.NotificationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;


@RestController
@RequestMapping("/user/{userId}/notifications")
public class NotificationsController{

	@Autowired
	private NotificationsService notificationsService;
	@Autowired NotificationValidator notificationValidator;

	@GetMapping("/")
	@Operation(description = "Get notifications for logged in user")
	public List<Notifications> getUserNotifications(@Parameter(name = "userId",description  = "Unique identifier of logger in user") 
	@PathVariable("userId") Long userId,@Parameter(name="X-TENANT-CODE",description = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode){
		return notificationsService.getAllUserNotifications(userId);
	}

	@PutMapping("/markread/{notificationId}")
	@Operation(description = "Mark notification as read")
	public Notifications setNotificationAsRead(@Parameter(name = "userId",description =  "Unique identifier of logger in user") @PathVariable("userId") Long userId,
													 @Parameter(name = "notificationId",description = "Unique identifier of notification to be marked as read") @PathVariable("notificationId") Long notificationId) {

		Notifications notif = notificationsService.getNotificationById(notificationId);
		if(notif!=null) {
			notif.setRead(true);
		}
		notif = notificationsService.saveNotification(notif);
		return notif;
	}
}