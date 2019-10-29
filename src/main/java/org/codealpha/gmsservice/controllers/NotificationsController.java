package org.codealpha.gmsservice.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.codealpha.gmsservice.services.NotificationsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.codealpha.gmsservice.entities.Notifications;
import java.util.List;


@RestController
@RequestMapping("/user/{userId}/notifications")
@Api(value = "Notifications",description = "Notifications API endpoints")
public class NotificationsController{

	@Autowired
	private NotificationsService notificationsService;

	@GetMapping("/")
	@ApiOperation("Get notifications for logged in user")
	public List<Notifications> getUserNotifications(@ApiParam(name = "userId",value = "Unique identifier of logger in user") @PathVariable("userId") Long userId){
		/*List<Notifications> notifications = notificationsService.getUserNotifications(userId, false);
		notifications.sort((a,b)->a.getPostedOn().compareTo(b.getPostedOn()));*/
		return notificationsService.getUserNotifications(userId, false);
	}

}