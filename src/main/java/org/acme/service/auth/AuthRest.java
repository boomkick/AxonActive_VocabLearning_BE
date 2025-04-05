package org.acme.service.auth;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.service.account.Account;

import java.util.Map;
import java.util.Optional;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthRest {

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    public Response login(Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Optional<String> tokenOpt = authService.authenticate(username, password);
        if (tokenOpt.isPresent()) {
            return Response.ok(Map.of("token", tokenOpt.get())).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @POST
    @Path("/register")
    public Response register(Account account) {
        authService.register(account);
        return Response.status(Response.Status.CREATED).build();
    }
}
