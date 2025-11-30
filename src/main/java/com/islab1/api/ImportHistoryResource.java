package com.islab1.api;

import com.islab1.api.dto.ImportOperationDto;
import com.islab1.entities.User;
import com.islab1.entities.UserRole;
import com.islab1.repository.ImportOperationRepository;
import com.islab1.services.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/imports")
@Produces(MediaType.APPLICATION_JSON)
public class ImportHistoryResource {

    @Inject
    private ImportOperationRepository importOperationRepository;

    @Inject
    private UserService userService;

    @GET
    @Path("/history")
    public Response getHistory(
            @HeaderParam("X-Auth-Token") String tokenHeader,
            @QueryParam("token") String tokenQuery
    ) {
        User currentUser = requireUser(tokenHeader, tokenQuery);

        List<ImportOperationDto> dtos;

        if (currentUser.getRole() == UserRole.ADMIN) {
            // админ видит все операции
            dtos = importOperationRepository.findAllOrderByStartedDesc()
                    .stream()
                    .map(ImportOperationDto::fromEntity)
                    .collect(Collectors.toList());
        } else {
            // обычный пользователь видит только свои операции
            dtos = importOperationRepository.findByUserIdOrderByStartedDesc(currentUser.getId())
                    .stream()
                    .map(ImportOperationDto::fromEntity)
                    .collect(Collectors.toList());
        }

        return Response.ok(dtos).build();
    }

    private String resolveToken(String tokenHeader, String tokenQuery) {
        String token = (tokenHeader != null && !tokenHeader.isBlank())
                ? tokenHeader.trim()
                : (tokenQuery != null ? tokenQuery.trim() : null);

        if (token == null || token.isEmpty()) {
            throw new NotAuthorizedException("Требуется токен аутентификации (X-Auth-Token или ?token=).");
        }

        return token;
    }

    private User requireUser(String tokenHeader, String tokenQuery) {
        String tokenValue = resolveToken(tokenHeader, tokenQuery);
        return userService.findUserByToken(tokenValue)
                .orElseThrow(() -> new NotAuthorizedException("Недействительный токен."));
    }
}
