package com.phamtra.identity_service.service.Impl;

import com.phamtra.identity_service.dto.request.CreatePermissionRequest;
import com.phamtra.identity_service.dto.response.PermissionResponse;
import com.phamtra.identity_service.exception.IdInvalidException;
import com.phamtra.identity_service.model.Permission;
import com.phamtra.identity_service.repository.PermissionRepository;
import com.phamtra.identity_service.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        if (permissionRepository.existsByCode(request.getCode())) {
            throw new IdInvalidException("Permission already exists");
        }

        Permission permission = Permission.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .build();

        permission = permissionRepository.save(permission);
        return toPermissionResponse(permission);
    }

    @Override
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::toPermissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PermissionResponse getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Permission not found"));
        return toPermissionResponse(permission);
    }

    @Override
    @Transactional
    public PermissionResponse updatePermission(Long id, CreatePermissionRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Permission not found"));

        permission.setName(request.getName());
        permission.setDescription(request.getDescription());

        permission = permissionRepository.save(permission);
        return toPermissionResponse(permission);
    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new IdInvalidException("Permission not found");
        }
        permissionRepository.deleteById(id);
    }

    private PermissionResponse toPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .code(permission.getCode())
                .description(permission.getDescription())
                .build();
    }
}
