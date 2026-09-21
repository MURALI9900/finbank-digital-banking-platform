package com.finbank.support.controller;

import com.finbank.support.dto.*;
import com.finbank.support.entity.SupportStatus;
import com.finbank.support.service.SupportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/support")
public class SupportController {
 private final SupportService service;
 public SupportController(SupportService service){this.service=service;}

 @PostMapping("/tickets")
 @ResponseStatus(HttpStatus.CREATED)
 public TicketResponse create(@Valid @RequestBody CreateTicketRequest r, Authentication authentication){
  authorizeCustomer(authentication,r.customerNumber());
  return service.create(r);
 }

 @GetMapping("/tickets/{reference}")
 public TicketResponse get(@PathVariable String reference, Authentication authentication){
  TicketResponse response=service.get(reference);
  authorizeCustomer(authentication,response.customerNumber());
  return response;
 }

 @GetMapping("/customers/{customerNumber}/tickets")
 public List<TicketResponse> customer(@PathVariable String customerNumber, Authentication authentication){
  authorizeCustomer(authentication,customerNumber);
  return service.customerTickets(customerNumber);
 }

 @GetMapping("/tickets/status/{status}")
 public List<TicketResponse> status(@PathVariable SupportStatus status, Authentication authentication){
  requireOfficerOrAdmin(authentication);
  return service.byStatus(status);
 }

 @PutMapping("/tickets/{reference}")
 public TicketResponse update(@PathVariable String reference,@Valid @RequestBody UpdateTicketRequest r,Authentication authentication){
  requireOfficerOrAdmin(authentication);
  return service.update(reference,r);
 }

 private void authorizeCustomer(Authentication authentication,String resourceCustomerNumber){
  if(isCustomer(authentication)&&!customerNumber(authentication).equalsIgnoreCase(resourceCustomerNumber)){
   throw new AccessDeniedException("Customer can only access their own support tickets");
  }
 }

 private void requireOfficerOrAdmin(Authentication authentication){
  if(authentication==null||(!hasRole(authentication,"OFFICER")&&!hasRole(authentication,"ADMIN"))){
   throw new AccessDeniedException("Only officers and admins can manage support tickets");
  }
 }

 private boolean isCustomer(Authentication authentication){return hasRole(authentication,"CUSTOMER");}

 private boolean hasRole(Authentication authentication,String role){
  return authentication!=null&&authentication.getAuthorities().stream().anyMatch(a->("ROLE_"+role).equals(a.getAuthority()));
 }

 private String customerNumber(Authentication authentication){
  String principal=authentication.getName();
  int separator=principal.indexOf('|');
  return separator>=0?principal.substring(separator+1):"";
 }
}