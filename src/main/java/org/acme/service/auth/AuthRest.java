package org.acme.service.auth;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.service.account.Account;
import org.acme.service.account.AccountMapper;
import org.acme.service.account.dto.RequestLoginAccountDTO;
import org.acme.service.account.dto.RequestRegisterAccountDTO;

import javax.validation.Valid;
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
    public Response login(@Valid RequestLoginAccountDTO requestLoginAccountDTO) {
        Optional<String> tokenOpt = authService.authenticate(requestLoginAccountDTO.getEmail(), requestLoginAccountDTO.getPassword());
        if (tokenOpt.isPresent()) {
            return Response.ok(Map.of("token", tokenOpt.get())).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @POST
    @Path("/register")
    public Response register(@Valid RequestRegisterAccountDTO requestRegisterAccountDTO) {
        authService.register(AccountMapper.INSTANCE.toAccount(requestRegisterAccountDTO));
        return Response.status(Response.Status.CREATED).build();
    }
}
