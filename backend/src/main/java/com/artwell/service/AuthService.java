package com.artwell.service;

import com.artwell.api.dto.LoginRequest;
import com.artwell.api.dto.LoginResponse;
import com.artwell.domain.entity.AppUser;
import com.artwell.domain.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        AppUser user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new com.artwell.web.exception.UnauthorizedException("Неверные учётные данные"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new com.artwell.web.exception.UnauthorizedException("Неверные учётные данные");
        }
        return new LoginResponse(
                "stub-" + user.getId(),
                "Bearer",
                86400,
                user.getRole(),
                user.getId(),
                user.getDisplayName()
        );
    }
}
