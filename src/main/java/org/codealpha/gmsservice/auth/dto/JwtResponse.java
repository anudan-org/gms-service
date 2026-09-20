package org.codealpha.gmsservice.auth.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record JwtResponse(
    String accessToken,
    JsonNode userPayload
) {}
