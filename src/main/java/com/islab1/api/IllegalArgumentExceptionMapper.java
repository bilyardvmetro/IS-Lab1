package com.islab1.api;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        String msg = exception.getMessage();
        Map<String, Object> body = new HashMap<>();

        // Специально обрабатываем этот кринж "Invalid separator..."
        if (msg != null && msg.contains("Invalid separator")) {
            body.put("error", "UNAUTHORIZED");
            body.put("message", "Некорректный формат токена.");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(body)
                    .build();
        }

        // Остальные IllegalArgument → 400 Bad Request с аккуратным JSON
        body.put("error", "BAD_REQUEST");
        body.put("message", msg != null ? msg : "Некорректный запрос.");

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}
