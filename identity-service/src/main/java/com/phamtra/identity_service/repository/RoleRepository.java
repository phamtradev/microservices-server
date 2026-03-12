package com.phamtra.identity_service.repository;

import com.phamtra.identity_service.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Set<Role> findByNameIn(Set<String> names);

    Optional<Role> findByCode(String code);
}
