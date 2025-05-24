package org.acme.base.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Exception mapper for JSON processing errors (invalid JSON format)
@Provider
public class JsonProcessingExceptionMapper implements ExceptionMapper<JsonProcessingException> {

    @Override
    public Response toResponse(com.fasterxml.jackson.core.JsonProcessingException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put("json", "Invalid JSON format: " + exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Invalid request format")
                .status(400)
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }
}