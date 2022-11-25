package com.golfzonaca.officesharingplatform.web.auth;

import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.service.auth.AuthService;
import com.golfzonaca.officesharingplatform.service.auth.CustomAuthService;
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

    @ResponseBody
    @PostMapping("/signup")
    public ConcurrentHashMap<String, Object> signup(@Validated @RequestBody SignUpSaveForm signUpSaveForm, BindingResult bindingResult) {
        ConcurrentHashMap<String, Object> errorMap = new ConcurrentHashMap<>();
        if (bindingResult.hasErrors()) {
            for (ObjectError objectError : bindingResult.getAllErrors()) {
                errorMap.put(objectError.getCode() + "ValidationError", Objects.requireNonNull(objectError.getDefaultMessage()));
            }
            return errorMap;
        }

        User user = signUpSaveForm.toEntity();
        if (!customAuthService.isAvailableEmail(user.getEmail())) {
            errorMap.put("EmailError", "중복된 이메일 입니다.");
        } else if (!customAuthService.isAvailableTelNum(user.getPhoneNumber())) {
            errorMap.put("PhoneNumberError", "이미 사용하고 있는 전화번호 입니다.");
        } else {
            customAuthService.join(user);
        }

        return errorMap;
    }

    @GetMapping("/refresh")
    public void refreshToken() {

    }
}
