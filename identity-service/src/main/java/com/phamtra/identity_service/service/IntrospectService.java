package com.phamtra.identity_service.service;

import com.phamtra.identity_service.dto.response.IntrospectResponse;

public interface IntrospectService {

    IntrospectResponse introspect(String token);
}