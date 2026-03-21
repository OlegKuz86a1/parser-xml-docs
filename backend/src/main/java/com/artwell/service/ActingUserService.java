package com.artwell.service;

import com.artwell.domain.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActingUserService {

    private final AppUserRepository userRepository;

    /** Скелет: первый пользователь в БД считается текущим (до внедрения JWT). */
    public UUID requireUserId() {
        return userRepository.findAll().stream()
                .findFirst()
                .map(u -> u.getId())
                .orElseThrow(() -> new IllegalStateException("Нет пользователя в БД — проверьте DataInitializer"));
    }
}
