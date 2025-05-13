package org.acme.service.word;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.acme.service.word.dto.BulkRequestWordDTO;
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

    @GET
    @Path("/test-authen")
    @RolesAllowed({ "USER"})
    public Response testAuthen(){
        System.out.println("here");
        return Response.ok().build();
    }

    @POST
    public Response addWord(@Valid RequestWordDTO requestWordDTO) {
        return Response.status(Response.Status.CREATED)
                .entity(wordService.add(requestWordDTO)).build();
    }

    @GET
    @Path("/{id}")
    public Response findWordById(@PathParam("id") String id) {
        ResponseWordDTO word = wordService.findWordById(id);
        if (word == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(word).build();
    }

    @GET
    public Response findAllWords(@QueryParam("isToday") Boolean isToday) {
        List<Word> result = new ArrayList<Word>();
        if (Objects.nonNull(isToday) && isToday) {
            result = wordService.findWordsRepeatTodayByAccountId();
        } else {
            result = wordService.findAllByAccountId();
        }
        return Response.ok(result.stream().map(WordMapper.INSTANCE::toResponseWordDTO).toList()).build();
    }

    @DELETE
    @Path("/{id}")
    public Response removeWord(@PathParam("id") String id) {
        wordService.removeWord(id);
        return Response.accepted().build();
    }

    @PUT
    @Path("/{id}")
    public Response updateWord(@PathParam("id") String id, RequestWordDTO requestWordDTO) {
        return Response.ok(wordService.updateWord(id, requestWordDTO)).build();
    }

    @POST
    @Path("/bulk")
    public Response createBulkWords(BulkRequestWordDTO bulkRequest) {
        return Response.status(Response.Status.CREATED).entity(wordService.createBulkWords(bulkRequest)).build();
    }
}
