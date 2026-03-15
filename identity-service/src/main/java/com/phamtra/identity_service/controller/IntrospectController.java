package com.phamtra.identity_service.controller;

import com.phamtra.identity_service.dto.response.IntrospectResponse;
import com.phamtra.identity_service.service.IntrospectService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class IntrospectController {

    private final IntrospectService introspectService;

    public IntrospectController(IntrospectService introspectService) {
        this.introspectService = introspectService;
    }

    @GetMapping("/introspect")
    public ResponseEntity<IntrospectResponse> introspect(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        String token = extractToken(authorizationHeader);
        IntrospectResponse response = introspectService.introspect(token);
        return ResponseEntity.ok(response);
    }

    private String extractToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7);
    }
}