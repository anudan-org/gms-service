package org.codealpha.gmsservice.controllers;

import java.time.LocalDateTime;
import java.util.Optional;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Developer <developer@enstratify.com>
 **/
@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserRepository repository;

	@GetMapping(value = "/{id}")
	public User get(@PathVariable(name = "id") Long id) {
		Optional<User> user = repository.findById(id);
		if (user.isPresent()) {
			return user.get();
		}
		return null;
	}

	@PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User create(@RequestBody User user) {
		//BCryptPasswordEncoder a  = new BCryptPasswordEncoder
		user.setCreatedAt(LocalDateTime.now());
		user.setCreatedBy("Api");
		user.setPassword(user.getPassword());
		return repository.save(user);
	}

}
