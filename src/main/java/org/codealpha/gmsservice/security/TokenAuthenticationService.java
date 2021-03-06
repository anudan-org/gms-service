package org.codealpha.gmsservice.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.codealpha.gmsservice.exceptions.InvalidCredentialsException;
import org.codealpha.gmsservice.exceptions.TokenExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TokenAuthenticationService {

  static final int EXPIRATIONTIME = 2073600000; // 10 days
  static final String SECRET = "ThisIsASecret";
  static final String TOKEN_PREFIX = "Bearer";
  static final String HEADER_STRING = "Authorization";
  static final String HEADER_EXPIRES_IN = "X-expires_in";

  private TokenAuthenticationService() {
    /**
     * Private constructor to prevent initialization
     */
  }

  public static void addAuthentication(HttpServletResponse res, String auth, JsonNode userNode, String tenant)
      throws IOException {
    String jwt = Jwts.builder().setSubject(auth + "^" + tenant)
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME)).signWith(SignatureAlgorithm.HS512, SECRET)
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
    if (token != null) {
      // parse the token.
      String user = null;
      try {
        user = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody()
            .getSubject();
      } catch (ExpiredJwtException e) {
        throw new TokenExpiredException("Token Expired");
      }

      List<GrantedAuthority> list = new ArrayList<>();
      list.add(new SimpleGrantedAuthority("ADMIN"));
      return user != null ? new UsernamePasswordAuthenticationToken(user, null, list) : null;
    } else {
      throw new InvalidCredentialsException("You are not authorized to perform this action");
    }
  }
}
