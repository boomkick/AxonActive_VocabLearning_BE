package org.acme.service.word;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.acme.service.word.dto.BulkRequestWordDTO;
import org.acme.service.word.dto.RequestPatchWordDTO;
import org.acme.service.word.dto.RequestWordDTO;
import org.acme.service.word.dto.ResponseWordDTO;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Path("/words")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WordRest {

    @Inject
    JsonWebToken jwt;

    @Inject
    private WordService wordService;

    @POST
    @RolesAllowed({"user", "admin"})
    public Response addWord(@Valid RequestWordDTO requestWordDTO) {
        return Response.status(Response.Status.CREATED).entity(wordService.add(requestWordDTO, jwt.getClaim("id"))).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"user", "admin"})
    public Response findWordById(@PathParam("id") String id) {
        ResponseWordDTO word = wordService.findWordById(id);
        if (word == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(word).build();
    }

    @GET
    @RolesAllowed({"user", "admin"})
    public Response findAllWords(@QueryParam("isToday") Boolean isToday) {
        List<Word> result = new ArrayList<Word>();
        if (Objects.nonNull(isToday) && isToday) {
            result = wordService.findWordsRepeatTodayByAccountId(jwt.getClaim("id"));
        } else {
            result = wordService.findAllByAccountId(jwt.getClaim("id"));
        }
        return Response.ok(result.stream().map(WordMapper.INSTANCE::toResponseWordDTO).toList()).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"user", "admin"})
    public Response removeWord(@PathParam("id") String id) {
        wordService.removeWord(id);
        return Response.accepted().build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"user", "admin"})
    public Response updateWord(@PathParam("id") String id, RequestWordDTO requestWordDTO) {
        wordService.updateWord(id, requestWordDTO);
        return Response.ok().build();
    }

    @POST
    @Path("/bulk")
    @RolesAllowed({"user", "admin"})
    public Response createBulkWords(BulkRequestWordDTO bulkRequest) {
        return Response.status(Response.Status.CREATED).entity(wordService.createBulkWords(bulkRequest, jwt.getClaim("id"))).build();
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({"user", "admin"})
    public Response updatePartialWord(@PathParam("id") String id, @Valid RequestPatchWordDTO requestPatchWordDTO) {
        wordService.updatePartialWord(id, requestPatchWordDTO);
        return Response.ok().build();
    }
}
