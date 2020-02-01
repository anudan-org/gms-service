package org.codealpha.gmsservice.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.exceptions.InvalidCredentialsException;
import org.codealpha.gmsservice.exceptions.InvalidTenantException;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class AuthProvider implements AuthenticationProvider {

  @Autowired
  private UserService userService;
  @Autowired
  private OrganizationService organizationService;
    @Value("${spring.recaptcha-secret-key}")
    private String reCaptchaKey;
  @Value("${spring.use-captcha}")
  private Boolean useCaptcha;


  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String provider = authentication.getAuthorities().iterator().next().getAuthority();
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();
    String tenantCode = ((Map<String,String>) authentication.getDetails()).get("TOKEN");
    String captcha = ((Map<String,String>) authentication.getDetails()).get("CAPTCHA");

    if(provider.equalsIgnoreCase("ANUDAN") && useCaptcha) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://www.google.com/recaptcha/api/siteverify");
        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

        params.add("secret", reCaptchaKey);
        params.add("response", captcha);
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(params);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.POST,
                requestEntity,
                String.class);

        try {
            if (!((JsonNode) new ObjectMapper().readValue(responseEntity.getBody(), JsonNode.class)).get("success").asBoolean()) {
                throw new BadCredentialsException("Invalid Captcha credentials");
            }
        } catch (IOException e) {
            throw new BadCredentialsException("Captcha verification failed");
        }
    }

    if(tenantCode == null){
      throw new InvalidCredentialsException("Missing required header X-TENANT-CODE");
    }
    if ("ANUDAN".equalsIgnoreCase(provider.toUpperCase())) {
      Organization org = null;
      /*if (org == null) {
        throw new BadCredentialsException("Invalid login credentials");
      }*/

      User user = null;
      if(!"ANUDAN".equalsIgnoreCase(tenantCode)) {
        org = organizationService.findOrganizationByTenantCode(tenantCode);
        user = userService.getUserByEmailAndOrg(username, org);
      }else if ("ANUDAN".equalsIgnoreCase(tenantCode)){
        List<User> users = userService.getUsersByEmail(username);
        for (User eachUser : users) {
          if(eachUser.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE") || eachUser.getOrganization().getOrganizationType().equalsIgnoreCase("PLATFORM")){
            user = eachUser;
            org = user.getOrganization();
            break;
          }
        }
      }
      if (user != null) {

        if (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTER")
            && !((Granter) user.getOrganization()).getCode().equalsIgnoreCase(tenantCode)) {
          throw new BadCredentialsException("Could not deternmine user organization");
        }
        if (password.equalsIgnoreCase(user.getPassword())
            && username.equalsIgnoreCase(user.getEmailId())) {

          return new UsernamePasswordAuthenticationToken(username, password, new ArrayList());
        } else {
          //TODO - Read messages from a resource bundle
          throw new BadCredentialsException("Invalid login credentials");
        }
      }else{
        throw new BadCredentialsException("Authentication failed");
      }
    } else if ("GOOGLE".equalsIgnoreCase(provider.toUpperCase())) {
      Organization org = organizationService.findOrganizationByTenantCode(tenantCode);
      if (org == null) {
        throw new BadCredentialsException("Invalid login credentials");
      }
      User user = userService.getUserByEmailAndOrg(username,org);

      if (user != null) {

        if (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTER")
            && !((Granter) user.getOrganization()).getCode().equalsIgnoreCase(tenantCode)) {
          throw new BadCredentialsException("Invalid login credentials");
        }

        return new UsernamePasswordAuthenticationToken(username, password, new ArrayList());
      } else {
        throw new BadCredentialsException("Invalid login credentials");
      }

    }

    return null;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }

}
