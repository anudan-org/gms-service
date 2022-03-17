package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Notifications;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface NotificationsRepository extends CrudRepository<Notifications,Long> {
	public List<Notifications> findByUserIdAndReadOrderByPostedOnDesc(Long userId, boolean read);
	public List<Notifications> findByUserIdOrderByPostedOnDesc(Long userId);
}