package com.phamtra.identity_service.service;

import com.phamtra.identity_service.dto.request.CreateRoleRequest;
import com.phamtra.identity_service.dto.response.RoleResponse;

import java.util.List;
import java.util.Set;

public interface RoleService {

    RoleResponse createRole(CreateRoleRequest request);

    List<RoleResponse> getAllRoles();

    RoleResponse getRoleById(Long id);

    RoleResponse updateRole(Long id, CreateRoleRequest request);

    void deleteRole(Long id);

    RoleResponse setPermissions(Long id, Set<String> permissionCodes);
}
