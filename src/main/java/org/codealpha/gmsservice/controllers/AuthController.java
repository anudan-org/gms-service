package org.codealpha.gmsservice.controllers;

import org.codealpha.gmsservice.auth.dto.JwtResponse;
import org.codealpha.gmsservice.auth.dto.LoginRequest;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.repositories.OrganizationRepository;
import org.codealpha.gmsservice.repositories.UserRepository;
//import org.codealpha.gmsservice.security.jwt.JwtTokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/q")
public class AuthController {

    // private final UserRepository userRepository;
    // private final OrganizationRepository organizationRepository;
    // private final PasswordEncoder passwordEncoder;
    // private final JwtTokenService jwtTokenService;

    // public AuthController(UserRepository userRepository,
    //                       OrganizationRepository organizationRepository,
    //                       PasswordEncoder passwordEncoder,
    //                       JwtTokenService jwtTokenService) {
    //     this.userRepository = userRepository;
    //     this.organizationRepository = organizationRepository;
    //     this.passwordEncoder = passwordEncoder;
    //     this.jwtTokenService = jwtTokenService;
    // }

    // @PostMapping
    // public ResponseEntity<?> authenticate(@RequestBody LoginRequest request) {

    //     User user;
    //     String tenantCode = request.tenantCode();
    //     String email = request.email();
    //     System.out.println("Email: " + email + ", Tenant: " + tenantCode);
    //     if (!"ANUDAN".equalsIgnoreCase(tenantCode)) {

    //         // Case 1: Explicit tenant
    //         Organization org = organizationRepository
    //                 .findByCode(tenantCode)
    //                 .orElseThrow(() ->
    //                     new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid tenant"));
    //         System.out.println("Found organization: " + org.getName()+" org id: "+org.getId());
    //        user = userRepository
    //                  .findByEmailAndOrg(email, org.getId())
    //                  .orElseThrow(() ->
    //                  new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user"));
         
    //         // user = userRepository
    //         //         .findByEmailIdAndOrganization(email, org);
    //                 // .orElseThrow(() ->
    //                 //     new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user"));
         

    //     } else {

    //         // Case 2: ANUDAN mode
    //         List<User> users = userRepository.findByEmailId(email);
    //         System.out.println("Found users count: " + users.size());
    //         user = users.stream()
    //                 .filter(u ->
    //                     "GRANTEE".equalsIgnoreCase(u.getOrganization().getOrganizationType())
    //                  || "PLATFORM".equalsIgnoreCase(u.getOrganization().getOrganizationType())
    //                 )
    //                 .findFirst()
    //                 .orElseThrow(() ->
    //                     new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user"));
    //     }

    //     // Password validation (important)
    //     if (!passwordEncoder.matches(request.password(), user.getPassword())) {
    //         throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    //     }

    //     // Token generation (next step)
    //     JwtResponse response = jwtTokenService.createTokens(user);

    //     return ResponseEntity.ok()
    //             .header(HttpHeaders.AUTHORIZATION, "Bearer " + response.accessToken())
    //             .body(response.userPayload());
    // }
    @GetMapping
    public String test() {
        return "Hello World";
    }
    
}
