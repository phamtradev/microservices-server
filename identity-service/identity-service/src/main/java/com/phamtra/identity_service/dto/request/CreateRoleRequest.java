package com.phamtra.identity_service.dto.request;

import lombok.Data;

@Data
public class CreateRoleRequest {

    private String name;
    private String code;
    private String description;
}
