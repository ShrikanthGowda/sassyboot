package com.cksutil.sassyboot.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String message;
    private Map<String, String> errors;
}

