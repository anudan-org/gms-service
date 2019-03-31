package org.codealpha.gmsservice.security;

import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GmsUserDetailsService implements UserDetailsService {

  @Autowired
  private UserService userService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userService.getUserByEmail(username);
    if(user==null){
      throw new UsernameNotFoundException(username);
    }

    return new GmsUserPrincipal(user);
  }
}
