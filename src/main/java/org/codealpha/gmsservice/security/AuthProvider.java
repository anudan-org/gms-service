package org.codealpha.gmsservice.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.exceptions.InvalidCredentialsException;
import org.codealpha.gmsservice.services.OrganizationService;
import org.codealpha.gmsservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AuthProvider implements AuthenticationProvider {

  public static final String INVALID_LOGIN_CREDENTIALS = "Invalid login credentials";
  public static final String ANUDAN = "ANUDAN";
  @Autowired
  private UserService userService;
  @Autowired
  private OrganizationService organizationService;
  @Value("${spring.recaptcha-secret-key}")
  private String reCaptchaKey;
  @Value("${spring.use-captcha}")
  private Boolean useCaptcha;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String provider = authentication.getAuthorities().iterator().next().getAuthority();
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();
    String tenantCode = ((Map<String, String>) authentication.getDetails()).get("TOKEN");
    String captcha = ((Map<String, String>) authentication.getDetails()).get("CAPTCHA");

    if (provider.equalsIgnoreCase(ANUDAN) && Boolean.TRUE.equals(useCaptcha)) {
      UriComponentsBuilder builder = UriComponentsBuilder
          .fromHttpUrl("https://www.google.com/recaptcha/api/siteverify");
      LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

      params.add("secret", reCaptchaKey);
      params.add("response", captcha);
      HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params);

      RestTemplate restTemplate = new RestTemplate();
      ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST,
          requestEntity, String.class);

      try {
        if (!(new ObjectMapper().readValue(responseEntity.getBody(), JsonNode.class)).get("success")
            .asBoolean()) {
          throw new BadCredentialsException("Invalid Captcha credentials");
        }
      } catch (IOException e) {
        throw new BadCredentialsException("Captcha verification failed");
      }
    }

    if (tenantCode == null) {
      throw new InvalidCredentialsException("Missing required header X-TENANT-CODE");
    }
    if (ANUDAN.equalsIgnoreCase(provider.toUpperCase())) {
      Organization org = null;

      User user = null;
      if (!ANUDAN.equalsIgnoreCase(tenantCode)) {
        org = organizationService.findOrganizationByTenantCode(tenantCode);
        user = userService.getUserByEmailAndOrg(username, org);
      } else if (ANUDAN.equalsIgnoreCase(tenantCode)) {
        List<User> users = userService.getUsersByEmail(username);
        for (User eachUser : users) {
          if (eachUser.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE")
              || eachUser.getOrganization().getOrganizationType().equalsIgnoreCase("PLATFORM")) {
            user = eachUser;
            user.getOrganization();
            break;
          }
        }
      }
      if (user != null) {
        if (!user.isActive() || user.isDeleted()) {
          throw new BadCredentialsException("Inactive user");
        }

        if (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTER")
            && !((Granter) user.getOrganization()).getCode().equalsIgnoreCase(tenantCode)) {
          throw new BadCredentialsException("Could not deternmine user organization");
        }
        if (user.isPlain()) {
          if (password.equalsIgnoreCase(user.getPassword()) && username.equalsIgnoreCase(user.getEmailId())) {

            return new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
          } else {
            throw new BadCredentialsException(INVALID_LOGIN_CREDENTIALS);
          }
        } else {
          if (passwordEncoder.matches(password, user.getPassword()) && username.equalsIgnoreCase(user.getEmailId())) {

            return new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
          } else {
            throw new BadCredentialsException(INVALID_LOGIN_CREDENTIALS);
          }
        }
      } else {
        throw new BadCredentialsException("Authentication failed");
      }
    } else if ("GOOGLE".equalsIgnoreCase(provider.toUpperCase())) {
      Organization org = organizationService.findOrganizationByTenantCode(tenantCode);
      if (org == null) {
        throw new BadCredentialsException(INVALID_LOGIN_CREDENTIALS);
      }
      User user = userService.getUserByEmailAndOrg(username, org);

      if (user != null) {

        if (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTER")
            && !((Granter) user.getOrganization()).getCode().equalsIgnoreCase(tenantCode)) {
          throw new BadCredentialsException(INVALID_LOGIN_CREDENTIALS);
        }

        return new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
      } else {
        throw new BadCredentialsException(INVALID_LOGIN_CREDENTIALS);
      }

    }

    return null;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }

}
