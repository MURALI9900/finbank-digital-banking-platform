package com.finbank.support.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
 private final SecretKey key;
 public JwtAuthenticationFilter(@Value("${app.jwt.secret}") String secret){key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));}
 protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws ServletException,IOException{
  String h=request.getHeader("Authorization");
  if(h!=null&&h.startsWith("Bearer ")){try{Claims c=Jwts.parser().verifyWith(key).build().parseSignedClaims(h.substring(7)).getPayload();String r=c.get("role",String.class);String customerNumber=c.get("customerNumber",String.class);if(c.getSubject()!=null&&r!=null)SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(c.getSubject()+"|"+(customerNumber==null?"":customerNumber),null,List.of(new SimpleGrantedAuthority("ROLE_"+r))));}catch(Exception e){SecurityContextHolder.clearContext();}}
  chain.doFilter(request,response);
 }
}