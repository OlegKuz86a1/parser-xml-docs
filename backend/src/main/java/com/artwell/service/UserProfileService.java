package com.artwell.service;

import com.artwell.api.dto.CurrentUser;
import com.artwell.domain.entity.AppUser;
import com.artwell.domain.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final ActingUserService actingUserService;
    private final AppUserRepository userRepository;

    public CurrentUser getCurrentUser() {
        UUID id = actingUserService.requireUserId();
        AppUser u = userRepository.findById(id).orElseThrow();
        return new CurrentUser(
                u.getId(),
                u.getDisplayName(),
                u.getRole(),
                new ArrayList<>(u.getAllowedDocumentTypes()),
                new ArrayList<>(u.getConstructionObjectIds())
        );
    }
}
