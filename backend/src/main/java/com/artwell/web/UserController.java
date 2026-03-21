package com.artwell.web;

import com.artwell.api.dto.CurrentUser;
import com.artwell.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    public CurrentUser me() {
        return userProfileService.getCurrentUser();
    }
}
