package com.phamtra.api_gateway.service.Impl;

import com.phamtra.api_gateway.dto.request.IntrospectRequest;
import com.phamtra.api_gateway.dto.response.IntrospectResponse;
import com.phamtra.api_gateway.repository.client.IdentityClient;
import com.phamtra.api_gateway.service.IdentityService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class IdentityServiceImpl implements IdentityService {

    private IdentityClient identityClient;

    public IdentityServiceImpl(IdentityClient identityClient) {
        this.identityClient = identityClient;
    }

    @Override
    public Mono<ResponseEntity<IntrospectResponse>> introspect(String token) {
        return identityClient.introspect(IntrospectRequest
                .builder()
                .token(token)
                .build());
    }
}
