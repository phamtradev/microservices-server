package com.phamtra.identity_service.service.Impl;

import com.phamtra.identity_service.dto.request.CreateUserRequest;
import com.phamtra.identity_service.dto.request.SetUserRoleCodesRequest;
import com.phamtra.identity_service.dto.request.UpdateUserRequest;
import com.phamtra.identity_service.dto.response.UserResponse;
import com.phamtra.identity_service.exception.IdInvalidException;
import com.phamtra.identity_service.mapper.UserMapper;
import com.phamtra.identity_service.model.Role;
import com.phamtra.identity_service.model.User;
import com.phamtra.identity_service.repository.RefreshTokenRepository;
import com.phamtra.identity_service.repository.RoleRepository;
import com.phamtra.identity_service.repository.UserRepository;
import com.phamtra.identity_service.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, 
                          RefreshTokenRepository refreshTokenRepository, UserMapper userMapper, 
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        validateCreateUserRequest(request);

        User user = buildUserFromRequest(request);

        User savedUser = userRepository.save(user);

        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = findUserByIdOrThrow(id);
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IdInvalidException("User not found with email: " + email));
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUserByIdOrThrow(id);

        updateUserFields(user, request);

        User updatedUser = userRepository.save(user);

        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse setActive(Long id, boolean active) {
        User user = findUserByIdOrThrow(id);
        user.setActive(active);
        User updatedUser = userRepository.save(user);
        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse setRolesByCodes(Long userId, SetUserRoleCodesRequest request) {
        User user = findUserByIdOrThrow(userId);

        Set<Role> roles = fetchRolesByCodes(request.getRoleCodes());

        user.setRoles(roles);

        User updatedUser = userRepository.save(user);

        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = findUserByIdOrThrow(id);
        
        // Delete refresh tokens first to avoid foreign key constraint
        refreshTokenRepository.deleteByUser(user);
        
        // Delete user roles association first
        user.setRoles(null);
        userRepository.save(user);
        
        // Now delete the user
        userRepository.delete(user);
    }

    private void validateCreateUserRequest(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IdInvalidException("Email already exists: " + request.getEmail());
        }
    }

    private User buildUserFromRequest(CreateUserRequest request) {
        User user = User.builder()
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .active(request.isActive())
                .roles(new HashSet<>())
                .build();

        if (request.getRoleCodes() != null && !request.getRoleCodes().isEmpty()) {
            Set<Role> roles = roleRepository.findByNameIn(request.getRoleCodes());
            user.setRoles(roles);
        }

        return user;
    }

    private User findUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("User not found with id: " + id));
    }

    private void updateUserFields(User user, UpdateUserRequest request) {
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (userRepository.existsByEmail(request.getEmail()) &&
                !request.getEmail().equals(user.getEmail())) {
                throw new IdInvalidException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user.setActive(request.isActive());

        // Update roles if roleCodes is provided
        if (request.getRoleCodes() != null && !request.getRoleCodes().isEmpty()) {
            Set<Role> roles = fetchRolesByCodes(request.getRoleCodes());
            user.setRoles(roles);
        }
    }

    private Set<Role> fetchRolesByCodes(Set<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return new HashSet<>();
        }

        Set<Role> roles = roleRepository.findByNameIn(roleCodes);

        if (roles.size() != roleCodes.size()) {
            Set<String> foundRoles = roles.stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());

            roleCodes.stream()
                    .filter(code -> !foundRoles.contains(code))
                    .findFirst()
                    .ifPresent(code -> {
                        throw new IdInvalidException("Role not found: " + code);
                    });
        }

        return roles;
    }
}
