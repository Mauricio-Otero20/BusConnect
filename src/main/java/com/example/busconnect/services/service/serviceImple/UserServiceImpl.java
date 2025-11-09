package com.example.busconnect.services.service.serviceImple;

import com.example.busconnect.services.service.UserService;
import com.example.busconnect.api.dto.UserDtos.*;
import com.example.busconnect.domine.entities.User;
import com.example.busconnect.domine.entities.enums.UserRole;
import com.example.busconnect.domine.entities.enums.UserStatus;
import com.example.busconnect.domine.repositories.UserRepository;
import com.example.busconnect.services.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService { 
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }
        if (userRepository.existsByPhone(request.phone())) {
            throw new IllegalArgumentException("Phone already exists: " + request.phone());
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }
    @Override
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        if (!user.getPhone().equals(request.phone()) && userRepository.existsByPhone(request.phone())) {
            throw new IllegalArgumentException("Phone already exists: " + request.phone());
        }

        userMapper.updateEntity(request, user);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }
    @Override
    @Transactional
    public UserResponse updateSelf(Long id, UserSelfUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        // Validar unicidad del teléfono (si cambió)
        if (!user.getPhone().equals(request.phone()) && userRepository.existsByPhone(request.phone())) {
            throw new IllegalArgumentException("Phone already exists: " + request.phone());
        }

        // Validar unicidad del email (si cambió)
        if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }

        // Actualizar campos permitidos para auto-actualización
        user.setUsername(request.username());
        user.setPhone(request.phone());
        user.setEmail(request.email()); // Permitir cambio de email con validación

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }


    @Override
    @Transactional
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        return userMapper.toResponse(user);
    }
    @Override
    @Transactional
    public UserResponse getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }
    @Override
    @Transactional
    public UserResponse getByPhone(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("User not found with phone: " + phone));
        return userMapper.toResponse(user);
    }
    @Override
    @Transactional
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<UserResponse> getByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<UserResponse> getByRoleAndStatus(UserRole role, UserStatus status) {
        return userRepository.findByRoleAndStatus(role, status).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    public UserResponse changeStatus(Long id, UserStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        user.setStatus(status);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }
}
