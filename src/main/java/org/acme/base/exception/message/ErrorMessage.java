package org.acme.base.exception.message;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessage {
    public static final String INVALID_REQUEST = "Invalid request";
    public static final String INVALID_JSON_FORMAT = "Invalid JSON format";
}
