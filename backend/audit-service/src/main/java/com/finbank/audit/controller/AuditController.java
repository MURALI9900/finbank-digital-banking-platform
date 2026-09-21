package com.finbank.audit.controller;

import com.finbank.audit.dto.*;
import com.finbank.audit.entity.AuditResult;
import com.finbank.audit.service.AuditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audits")
@RequiredArgsConstructor
public class AuditController {
    private final AuditService auditService;

    @PostMapping
    public ResponseEntity<AuditResponse> create(@Valid @RequestBody CreateAuditRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auditService.create(request));
    }

    @GetMapping("/{reference}")
    public ResponseEntity<AuditResponse> get(@PathVariable String reference) {
        return ResponseEntity.ok(auditService.get(reference));
    }

    @GetMapping("/customer/{customerNumber}")
    public ResponseEntity<List<AuditResponse>> byCustomer(@PathVariable String customerNumber) {
        return ResponseEntity.ok(auditService.byCustomer(customerNumber));
    }

    @GetMapping("/actor/{actorId}")
    public ResponseEntity<List<AuditResponse>> byActor(@PathVariable String actorId) {
        return ResponseEntity.ok(auditService.byActor(actorId));
    }

    @GetMapping("/entity/{entityReference}")
    public ResponseEntity<List<AuditResponse>> byEntity(@PathVariable String entityReference) {
        return ResponseEntity.ok(auditService.byEntity(entityReference));
    }

    @GetMapping("/result/{result}")
    public ResponseEntity<List<AuditResponse>> byResult(@PathVariable AuditResult result) {
        return ResponseEntity.ok(auditService.byResult(result));
    }
}