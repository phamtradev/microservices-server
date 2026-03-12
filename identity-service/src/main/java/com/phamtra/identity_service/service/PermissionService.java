package com.phamtra.identity_service.service;

import com.phamtra.identity_service.dto.request.CreatePermissionRequest;
import com.phamtra.identity_service.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionService {
    PermissionResponse createPermission(CreatePermissionRequest request);
    List<PermissionResponse> getAllPermissions();
    PermissionResponse getPermissionById(Long id);
    PermissionResponse updatePermission(Long id, CreatePermissionRequest request);
    void deletePermission(Long id);
}
