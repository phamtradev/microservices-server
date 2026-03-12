package com.phamtra.identity_service.controller;

import com.phamtra.identity_service.dto.request.CreateUserRequest;
import com.phamtra.identity_service.dto.request.SetUserRoleCodesRequest;
import com.phamtra.identity_service.dto.request.UpdateUserRequest;
import com.phamtra.identity_service.dto.response.UserResponse;
import com.phamtra.identity_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_USER_CREATE')")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERM_USER_VIEW')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_USER_VIEW')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/by-email")
    @PreAuthorize("hasAuthority('PERM_USER_VIEW')")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_USER_EDIT')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/active")
    @PreAuthorize("hasAuthority('PERM_USER_EDIT')")
    public ResponseEntity<UserResponse> setActive(
            @PathVariable Long id,
            @RequestParam boolean active) {
        UserResponse user = userService.setActive(id, active);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('PERM_USER_EDIT')")
    public ResponseEntity<UserResponse> setRolesByCodes(
            @PathVariable Long id,
            @Valid @RequestBody SetUserRoleCodesRequest request) {
        UserResponse user = userService.setRolesByCodes(id, request);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_USER_DELETE')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
