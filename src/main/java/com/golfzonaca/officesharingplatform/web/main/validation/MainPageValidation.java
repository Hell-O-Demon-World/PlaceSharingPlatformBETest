package com.golfzonaca.officesharingplatform.web.main.validation;

import com.golfzonaca.officesharingplatform.exception.MisMatchingPasswordException;
import com.golfzonaca.officesharingplatform.exception.NonExistedUserException;
import com.golfzonaca.officesharingplatform.exception.NonExistedUserTelException;
import com.golfzonaca.officesharingplatform.repository.rating.RatingRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.room.RoomRepository;
import com.golfzonaca.officesharingplatform.repository.roomkind.RoomKindRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.web.validation.RequestValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
@RequiredArgsConstructor
public class MainPageValidation{
    private static final String NON_EMAIL_MESSAGE = "존재하지 않는 회원 입니다.";
    private static final String NON_TEL_MESSAGE = "핸드폰 번호를 잘못 입력하셨습니다.";
    private final RequestValidation requestValidation;
    private final UserRepository userRepository;

    public void validationBindingResult(BindingResult bindingResult) {
        requestValidation.bindingResultCheck(bindingResult);
    }

    public void validationEmailTelBindingResult(String email, String tel, BindingResult bindingResult) {
        requestValidation.bindingResultCheck(bindingResult);
        validEmail(email);
        validTel(tel);
    }

    public void validationEmailTelBindingResult(String email, String tel, String pw, String pw2, BindingResult bindingResult) {
        requestValidation.bindingResultCheck(bindingResult);
        validEmail(email);
        validTel(tel);
        validPwAndPw2(pw, pw2);
    }

    private void validPwAndPw2(String pw, String pw2) {
        if (!pw.equals(pw2)) {
            throw new MisMatchingPasswordException("첫번째 비밀번호 값과 두번째 비밀번호 값이 다릅니다.");
        }
    }

    private void validTel(String tel) {
        if (userRepository.isUniqueTel(tel)) {
            throw new NonExistedUserTelException(NON_TEL_MESSAGE);
        }
    }

    private void validEmail(String email) {
        if (userRepository.isUniqueEmail(email)) {
            throw new NonExistedUserException(NON_EMAIL_MESSAGE);
        }
    }

}
