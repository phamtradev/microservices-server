package com.phamtra.identity_service.service.Impl;

import com.phamtra.identity_service.dto.request.CreateRoleRequest;
import com.phamtra.identity_service.dto.response.RoleResponse;
import com.phamtra.identity_service.exception.IdInvalidException;
import com.phamtra.identity_service.model.Permission;
import com.phamtra.identity_service.model.Role;
import com.phamtra.identity_service.repository.PermissionRepository;
import com.phamtra.identity_service.repository.RoleRepository;
import com.phamtra.identity_service.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {
        if (roleRepository.findByCode(request.getCode()).isPresent()) {
            throw new IdInvalidException("Role already exists");
        }

        Role role = new Role();
        role.setName(request.getName());
        role.setCode(request.getCode());
        role.setDescription(request.getDescription());
        role.setPermissions(new HashSet<>()); // Khởi tạo empty set để tránh NullPointerException

        role = roleRepository.save(role);

        return toRoleResponse(role);
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::toRoleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponse getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Role not found"));
        return toRoleResponse(role);
    }

    @Override
    @Transactional
    public RoleResponse updateRole(Long id, CreateRoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Role not found"));

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        role = roleRepository.save(role);

        return toRoleResponse(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new IdInvalidException("Role not found");
        }
        roleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public RoleResponse setPermissions(Long id, Set<String> permissionCodes) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Role not found"));

        Set<Permission> permissions = new HashSet<>(permissionRepository.findByCodeIn(permissionCodes));

        role.setPermissions(permissions);
        role = roleRepository.save(role);

        return toRoleResponse(role);
    }

    private RoleResponse toRoleResponse(Role role) {
        Set<String> permissionCodes = role.getPermissions() == null 
                ? new HashSet<>() 
                : role.getPermissions().stream()
                        .map(Permission::getCode)
                        .collect(Collectors.toSet());

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .code(role.getCode())
                .description(role.getDescription())
                .permissions(permissionCodes)
                .build();
    }
}
