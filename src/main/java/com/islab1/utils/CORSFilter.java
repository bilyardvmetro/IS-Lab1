package com.islab1.utils;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class CORSFilter implements Filter {

    private static final String ALLOWED_ORIGINS = "http://localhost:4200";
    private static final String ALLOWED_METHODS = "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD";
    private static final String ALLOWED_HEADERS = "Content-Type, Authorization";
    private static final String ALLOWED_CREDENTIALS = "true";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        // обработка preflight метода, который проверяет наличие CORS фильтра
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
        }

        httpResponse.setHeader("Access-Control-Allow-Origin", ALLOWED_ORIGINS);
        httpResponse.setHeader("Access-Control-Allow-Methods", ALLOWED_METHODS);
        httpResponse.setHeader("Access-Control-Allow-Headers", ALLOWED_HEADERS);
        httpResponse.setHeader("Access-Control-Allow-Credentials", ALLOWED_CREDENTIALS);

        filterChain.doFilter(servletRequest, httpResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
