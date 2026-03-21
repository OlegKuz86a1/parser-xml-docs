package com.artwell.config;

import com.artwell.api.enums.DocumentTypeCode;
import com.artwell.api.enums.UserRole;
import com.artwell.domain.entity.AppUser;
import com.artwell.domain.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedUsers() {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }
            AppUser u = new AppUser();
            u.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
            u.setUsername("demo");
            u.setPasswordHash(passwordEncoder.encode("demo"));
            u.setRole(UserRole.CONTRACTOR);
            u.setDisplayName("Демо пользователь");
            u.setAllowedDocumentTypes(Set.copyOf(java.util.EnumSet.allOf(DocumentTypeCode.class)));
            u.setConstructionObjectIds(Set.of());
            userRepository.save(u);
            log.info("Создан пользователь demo / demo");
        };
    }
}
