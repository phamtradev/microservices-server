package com.phamtra.identity_service.repository;

import com.phamtra.identity_service.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByCode(String code);

    Set<Permission> findByCodeIn(Set<String> codes);

    boolean existsByCode(String code);
}
