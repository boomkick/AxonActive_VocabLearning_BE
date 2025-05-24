package org.acme.base.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Provider
public class InvalidFormatExceptionMapper implements ExceptionMapper<InvalidFormatException> {

    @Override
    public Response toResponse(InvalidFormatException exception) {
        Map<String, String> errors = new HashMap<>();

        // Extract field name from the path
        String fieldName = getFieldName(exception);
        String errorMessage = buildErrorMessage(exception);

        errors.put(fieldName, errorMessage);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Invalid field format")
                .status(400)
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }

    private String getFieldName(com.fasterxml.jackson.databind.exc.InvalidFormatException exception) {
        if (exception.getPath() != null && !exception.getPath().isEmpty()) {
            return exception.getPath().get(exception.getPath().size() - 1).getFieldName();
        }
        return "unknown";
    }

    private String buildErrorMessage(com.fasterxml.jackson.databind.exc.InvalidFormatException exception) {
        Object value = exception.getValue();
        Class<?> targetType = exception.getTargetType();

        // Handle enum conversion errors specifically
        if (targetType.isEnum()) {
            Object[] enumConstants = targetType.getEnumConstants();
            String validValues = java.util.Arrays.stream(enumConstants)
                    .map(Object::toString)
                    .collect(java.util.stream.Collectors.joining(", "));
            return String.format("Invalid value '%s'. Allowed values are: %s", value, validValues);
        }

        // Handle other type conversion errors
        String typeName = targetType.getSimpleName();
        return String.format("Invalid value '%s' for type %s", value, typeName);
    }
}
