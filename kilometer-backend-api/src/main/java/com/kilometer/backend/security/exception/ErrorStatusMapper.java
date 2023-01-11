package com.kilometer.backend.security.exception;

import com.kilometer.exception.KilometerErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.kilometer.exception.KilometerErrorCode.*;

@Component
public class ErrorStatusMapper {

    private static final Map<KilometerErrorCode, HttpStatus> MAPPER = new HashMap<>();


    private ErrorStatusMapper() {
        MAPPER.put(ARCHIVE_NOT_FOUND,HttpStatus.NOT_FOUND);
    }

    public HttpStatus getStatus(KilometerErrorCode code) {
        return MAPPER.get(code);
    }
}