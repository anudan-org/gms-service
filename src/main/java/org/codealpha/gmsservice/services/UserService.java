package org.codealpha.gmsservice.services;

import javax.xml.ws.ServiceMode;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.exceptions.UserNotFoundException;
import org.codealpha.gmsservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public User getUserByEmail(String email){
    User user = userRepository.findByEmailId(email);
    if(user==null){
      throw new UserNotFoundException("Username not found");
    }
    return user;
  }
}
