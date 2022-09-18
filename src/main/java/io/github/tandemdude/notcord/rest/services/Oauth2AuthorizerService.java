package io.github.tandemdude.notcord.rest.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tandemdude.notcord.utils.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class Oauth2AuthorizerService {
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public Oauth2AuthorizerService(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    public void canAccessResource(String authorizationHeader, String resource) {

    }
}
