package org.codealpha.gmsservice.controllers;

import java.time.LocalDateTime;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.codealpha.gmsservice.entities.Grantee;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.repositories.UserRepository;
import org.codealpha.gmsservice.services.CommonEmailSevice;
import org.codealpha.gmsservice.services.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

/**
 * @author Developer <developer@enstratify.com>
 **/
@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private UserRepository repository;
  @Autowired
  private OrganizationService organizationService;
  @Autowired
  private CommonEmailSevice commonEmailSevice;

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
    user = repository.save(user);

    UriComponents urlComponents = ServletUriComponentsBuilder.fromCurrentContextPath().build();

    String scheme = urlComponents.getScheme();
    String host = urlComponents.getHost();
    int port = urlComponents.getPort();

    String verificationLink = scheme+"://"+host+":"+port+"/grantee/verification?emailId="+user.getEmailId()+"&code="+RandomStringUtils.randomAlphanumeric(127);

    System.out.println(verificationLink);
    commonEmailSevice.sendMail(user.getEmailId(),"Anudan.org - Verification Link",verificationLink);
    return user;
  }


  @PostMapping("/activation")
  public HttpStatus verifyUser(@RequestParam("emailId") String email,
      @RequestParam("code") String code) {
    return HttpStatus.OK;
  }

}
