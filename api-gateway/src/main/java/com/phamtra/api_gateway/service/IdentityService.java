package com.phamtra.api_gateway.service;

import com.phamtra.api_gateway.dto.ApiResponse;
import com.phamtra.api_gateway.dto.request.IntrospectRequest;
import com.phamtra.api_gateway.dto.response.IntrospectResponse;
import com.phamtra.api_gateway.repository.client.IdentityClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface IdentityService {

    Mono<ApiResponse<IntrospectResponse>> introspect(String token);
}