package com.golfzonaca.officesharingplatform.web.exception;

import com.golfzonaca.officesharingplatform.web.exception.form.ExceptionResponseForm;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExceptionHandlingController {

    @ExceptionHandler()
    ExceptionResponseForm placeException() {
        String status = "";
        String code = "";
        String message = "";
        return new ExceptionResponseForm(status, code, message);
    }
}
