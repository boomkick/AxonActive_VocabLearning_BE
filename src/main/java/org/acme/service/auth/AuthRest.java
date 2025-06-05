package org.acme.service.auth;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.service.account.Account;
import org.acme.service.account.AccountMapper;
import org.acme.service.account.dto.RequestLoginAccountDTO;
import org.acme.service.account.dto.RequestRegisterAccountDTO;
import org.acme.service.account.dto.ResponseLoginAccountDTO;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthRest {

    @Inject
    JsonWebToken jwt;

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    public Response login(@Valid RequestLoginAccountDTO requestLoginAccountDTO) {
        Optional<ResponseLoginAccountDTO> responseLoginAccountDTO = authService.authenticate(requestLoginAccountDTO.getEmail(), requestLoginAccountDTO.getPassword());
        if (responseLoginAccountDTO.isPresent()) {
            return Response.ok(responseLoginAccountDTO).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @POST
    @Path("/register")
    public Response register(@Valid RequestRegisterAccountDTO requestRegisterAccountDTO) {
        authService.register(AccountMapper.INSTANCE.toAccount(requestRegisterAccountDTO));
        return Response.status(Response.Status.CREATED).build();
    }

    @POST
    @Path("/refresh-token")
    @RolesAllowed({"user", "admin"})
    public Response refreshToken() {
        return Response.ok(authService.refreshToken(jwt)).build();
    }
}
