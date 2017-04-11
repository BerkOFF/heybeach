package de.daimler.heybeach.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenResponse {
    @JsonProperty
    private String token;

    public TokenResponse(String token) {
        this.token = token;
    }
}
