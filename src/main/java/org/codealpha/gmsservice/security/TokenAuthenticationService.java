package org.codealpha.gmsservice.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.codealpha.gmsservice.exceptions.InvalidCredentialsException;
import org.codealpha.gmsservice.exceptions.TokenExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;

public class TokenAuthenticationService {
private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationService.class);

  static final int EXPIRATIONTIME = 2073600000; // 10 days
  static final String TOKEN_PREFIX = "Bearer";
  static final String HEADER_STRING = "Authorization";
  static final String HEADER_EXPIRES_IN = "X-expires_in";

  private TokenAuthenticationService() {
    // Private constructor to prevent initialization
  }


  private static SecretKey signingKey() {
    return JwtKeyUtil.hs512Key(JwtSecrets.login());
  }

  public static void addAuthentication(HttpServletResponse res, String auth, JsonNode userNode, String tenant)
      throws IOException {

String jwt = Jwts.builder()
    .subject(auth + "^" + tenant)
    .expiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
    .signWith(signingKey())
    .compact();

    res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + jwt);
    res.addIntHeader(HEADER_EXPIRES_IN, EXPIRATIONTIME);
    res.setHeader("X-TENANT-CODE", tenant);

    res.setHeader("Access-Control-Allow-Headers",
        "Authorization, x-xsrf-token, Access-Control-Allow-Headers, Origin, Accept, X-Requested-With, "
            + "Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, X-TENANT-CODE, ACCESS_TOKEN, X-USER-ID");

    ObjectMapper mapper = new ObjectMapper();
    res.getWriter().write(mapper.writeValueAsString(userNode));
  }

  static Authentication getAuthentication(HttpServletRequest request) {
    String token = request.getHeader(HEADER_STRING);
 
    logger.debug("[TokenAuthenticationService] Checking for Authorization header. Token present: {}", (token != null));

    if (token != null) {
      String user;
      try {
            user = Jwts.parser()
           .verifyWith(signingKey()) // Replaces setSigningKey()
           .build()
           .parseSignedClaims(token.replace(TOKEN_PREFIX, "").trim()) // Replaces parseClaimsJws()
           .getPayload() // Replaces getBody()
           .getSubject();
      } catch (ExpiredJwtException e) {
        logger.error("[TokenAuthenticationService] Token Expired");
        throw new TokenExpiredException("Token Expired");
      }
      
      List<GrantedAuthority> list = new ArrayList<>();
      list.add(new SimpleGrantedAuthority("ADMIN"));
      logger.debug("[TokenAuthenticationService] Token validated successfully for user: {}", user);
      return user != null ? new UsernamePasswordAuthenticationToken(user, null, list) : null;
    } else {
      logger.error("[TokenAuthenticationService] Authorization header missing - throwing InvalidCredentialsException");
      throw new InvalidCredentialsException("You are not authorized to perform this action");
    }
  }
  
}
