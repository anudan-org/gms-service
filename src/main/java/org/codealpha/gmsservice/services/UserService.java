package org.codealpha.gmsservice.services;

import java.util.List;

import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.exceptions.UserNotFoundException;
import org.codealpha.gmsservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;



  public User getUserByEmailAndOrg(String email,Organization org ){
    User user = userRepository.findByEmailIdAndOrganization(email,org);
    return user;
  }

  public User getUserById(Long userId){
    return userRepository.findById(userId).get();
  }

  public User getUserByEmailAndTenant(String email,String tenant){
    User user = userRepository.findByEmailId(email);
    if(user==null){
      throw new UserNotFoundException();
    }
    return user;
  }

  public User save(User user) {
    return userRepository.save(user);
  }

  public List<User> usersToNotifyOnWorkflowSateChangeTo(Long toStateId){
    return userRepository.usersToNotifyOnWorkflowSateChangeTo(toStateId);
  }

  public List<User> getAllTenantUsers(Organization org){
    return userRepository.findByOrganizationAndActive(org,true);
  }

}
