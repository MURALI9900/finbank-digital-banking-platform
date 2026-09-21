package com.finbank.support.controller;
import com.finbank.support.dto.*;import com.finbank.support.entity.SupportStatus;import com.finbank.support.service.SupportService;import jakarta.validation.Valid;import org.springframework.http.HttpStatus;import org.springframework.web.bind.annotation.*;import java.util.List;
@RestController @RequestMapping("/api/v1/support") public class SupportController{
 private final SupportService service;public SupportController(SupportService service){this.service=service;}
 @PostMapping("/tickets") @ResponseStatus(HttpStatus.CREATED) public TicketResponse create(@Valid @RequestBody CreateTicketRequest r){return service.create(r);}
 @GetMapping("/tickets/{reference}") public TicketResponse get(@PathVariable String reference){return service.get(reference);}
 @GetMapping("/customers/{customerNumber}/tickets") public List<TicketResponse> customer(@PathVariable String customerNumber){return service.customerTickets(customerNumber);}
 @GetMapping("/tickets/status/{status}") public List<TicketResponse> status(@PathVariable SupportStatus status){return service.byStatus(status);}
 @PutMapping("/tickets/{reference}") public TicketResponse update(@PathVariable String reference,@Valid @RequestBody UpdateTicketRequest r){return service.update(reference,r);}
}