package org.codealpha.gmsservice.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.codealpha.gmsservice.validators.NotificationValidator;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.codealpha.gmsservice.services.NotificationsService;
import org.codealpha.gmsservice.entities.Notifications;
import java.util.List;


@RestController
@RequestMapping("/user/{userId}/notifications")
@Api(value = "Notifications",description = "Notifications API endpoints")
public class NotificationsController{

	@Autowired
	private NotificationsService notificationsService;
	@Autowired NotificationValidator notificationValidator;

	@GetMapping("/")
	@ApiOperation("Get notifications for logged in user")
	public List<Notifications> getUserNotifications(@ApiParam(name = "userId",value = "Unique identifier of logger in user") @PathVariable("userId") Long userId){
		/*List<Notifications> notifications = notificationsService.getUserNotifications(userId, false);
		notifications.sort((a,b)->a.getPostedOn().compareTo(b.getPostedOn()));*/
		return notificationsService.getAllUserNotifications(userId);
	}

	@PutMapping("/markread/{notificationId}")
	@ApiOperation("Mark notification as read")
	public Notifications setNotificationAsRead(@ApiParam(name = "userId",value = "Unique identifier of logger in user") @PathVariable("userId") Long userId,
													 @ApiParam(name = "notificationId",value = "Unique identifier of notification to be marked as read") @PathVariable("notificationId") Long notificationId) {

		Notifications notif = notificationsService.getNotificationById(notificationId);
		notif.setRead(true);
		notif = notificationsService.saveNotification(notif);
		return notif;
	public List<Notifications> getUserNotifications(@RequestHeader("X-TENANT-CODE") String tenantCode, @ApiParam(name = "userId",value = "Unique identifier of logger in user") @PathVariable("userId") Long userId){
		notificationValidator.validate(userId,tenantCode);
		return notificationsService.getUserNotifications(userId, false);
	}

}