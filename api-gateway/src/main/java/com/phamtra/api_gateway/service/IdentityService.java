package com.phamtra.api_gateway.service;

import com.phamtra.api_gateway.dto.response.IntrospectResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IdentityService {

    Mono<ResponseEntity<IntrospectResponse>> introspect(String token);
}