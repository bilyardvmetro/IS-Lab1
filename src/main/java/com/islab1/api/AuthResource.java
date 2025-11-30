package com.islab1.api;

import com.islab1.api.dto.*;
import com.islab1.entities.AuthToken;
import com.islab1.entities.User;
import com.islab1.services.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private UserService userService;

    @POST
    @Path("/register")
    public Response register(@Valid RegisterRequest request) {
        User user = userService.register(request.getUsername(), request.getPassword());
        return Response.status(Response.Status.CREATED)
                .entity(UserDto.fromEntity(user))
                .build();
    }

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        AuthToken token = userService.login(request.getUsername(), request.getPassword());
        AuthResponse response = new AuthResponse(token.getToken(), UserDto.fromEntity(token.getUser()));
        return Response.ok(response).build();
    }

    /**
     * Пример endpoint'а, чтобы фронт мог проверить токен и получить текущего юзера.
     * Ожидает заголовок Authorization: Bearer &lt;token&gt;
     */
    @GET
    @Path("/me")
    public Response me(
            @HeaderParam("X-Auth-Token") String tokenHeader,
            @QueryParam("token") String tokenQuery
    ) {
        String tokenValue = resolveToken(tokenHeader, tokenQuery);

        return userService.findUserByToken(tokenValue)
                .map(user -> Response.ok(UserDto.fromEntity(user)).build())
                .orElseThrow(() -> new NotAuthorizedException("Недействительный токен."));
    }

    private String resolveToken(String tokenHeader, String tokenQuery) {
        // Приоритет: заголовок X-Auth-Token, затем query ?token=
        String token = (tokenHeader != null && !tokenHeader.isBlank())
                ? tokenHeader.trim()
                : (tokenQuery != null ? tokenQuery.trim() : null);

        if (token == null || token.isEmpty()) {
            throw new NotAuthorizedException("Требуется заголовок X-Auth-Token или параметр ?token=.");
        }

        return token;
    }


}
