package com.golfzonaca.officesharingplatform.web.auth;

import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.service.auth.AuthService;
import com.golfzonaca.officesharingplatform.service.auth.validation.AuthRequestValidation;
import com.golfzonaca.officesharingplatform.web.auth.form.SignUpSaveForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService customAuthService;
    private final AuthRequestValidation authRequestValidation;

    @ResponseBody
    @PostMapping("/signup")
    public void signup(@Validated @RequestBody SignUpSaveForm signUpSaveForm, BindingResult bindingResult) {
        User user = signUpSaveForm.toEntity();
        authRequestValidation.validation(user, bindingResult);
        customAuthService.join(user);
    }

    @GetMapping("/refresh")
    public void refreshToken() {}
}
