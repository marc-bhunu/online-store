package com.marcusehocodes.cart_service.client;

import com.marcusehocodes.cart_service.domain.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Component
public class
UserClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public UserClient(@Value("${user.service.url}") String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
    }
    public UserDto getUserById(UUID userId){
        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/{userId}")
                .buildAndExpand(userId)
                .toUriString();
        ResponseEntity<UserDto> responseEntity = restTemplate.getForEntity(url, UserDto.class);
        return responseEntity.getBody();
    }

}
