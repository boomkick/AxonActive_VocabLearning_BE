package org.acme.base.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;
import java.util.HashMap;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        int status = exception.getResponse().getStatus();
        String message;

        switch (status) {
            case 404:
                message = "Resource not found";
                break;
            case 405:
                message = "Method not allowed";
                break;
            case 415:
                message = "Unsupported media type";
                break;
            default:
                message = exception.getMessage() != null ? exception.getMessage() : "Request failed";
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message)
                .status(status)
                .timestamp(LocalDateTime.now())
                .errors(new HashMap<>())
                .build();

        return Response.status(status)
                .entity(errorResponse)
                .build();
    }
}