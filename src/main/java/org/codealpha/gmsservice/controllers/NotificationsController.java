package org.codealpha.gmsservice.controllers;

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
public class NotificationsController{

	@Autowired
	private NotificationsService notificationsService;

	@GetMapping("/")
	public List<Notifications> getUserNotifications(@PathVariable("userId") Long userId){
		return notificationsService.getUserNotifications(userId, false);
	}

}