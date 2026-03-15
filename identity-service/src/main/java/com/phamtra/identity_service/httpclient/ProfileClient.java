package com.phamtra.identity_service.httpclient;

import com.phamtra.identity_service.dto.request.CreateProfileRequest;
import com.phamtra.identity_service.dto.response.CreateProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "profile-service", url = "${app.services.profile}")
public interface ProfileClient {

    @PostMapping(value = "/profile/internal/users", produces = MediaType.APPLICATION_JSON_VALUE)
    CreateProfileResponse createUserProfile(@RequestBody CreateProfileRequest request);
}
