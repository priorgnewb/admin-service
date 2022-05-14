package org.faze.adminservice.controllers;

import java.util.List;
import javax.validation.Valid;
import org.faze.adminservice.exceptions.BadResponseException;
import org.faze.adminservice.models.User;
import org.faze.adminservice.payload.request.LoginRequest;
import org.faze.adminservice.payload.response.JwtResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
public class AdministratorController {

  private final static Logger log = LoggerFactory.getLogger(AdministratorController.class);

  private static final String REGISTRATION_URL = "http://localhost:8080/api/auth/signin";
  private static final String HELLO_URL = "http://localhost:8080/api/auth";
  private static final String ALL_USERS_URL = "http://localhost:8080/api/admin/allusers";

  private final RestTemplate restTemplate;

  @Autowired
  public AdministratorController(RestTemplateBuilder builder) {
    this.restTemplate = builder.build();
  }

  @GetMapping
  public String hello() {
    ResponseEntity<String> response
        = restTemplate.getForEntity(HELLO_URL, String.class);
    System.out.println(response.getStatusCode());
    return response.getBody();
  }

  // обратиться к стороннему сервису за токеном и получить список всех пользователей
  // TODO: реализовать хранение токена для запросов (сейчас при каждом запросе генерируется новый токен)
  @GetMapping("/users")
  public ResponseEntity<List<User>> showAllUsers(@Valid @RequestBody LoginRequest loginRequest) {
    JwtResponse jwtResponse
        = restTemplate.postForObject(REGISTRATION_URL, loginRequest, JwtResponse.class);

    if (jwtResponse != null) {
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.set("Authorization", "Bearer " + jwtResponse.getAccessToken());
      HttpEntity<List<User>> entity = new HttpEntity<>(httpHeaders);

      return restTemplate.exchange(ALL_USERS_URL, HttpMethod.GET, entity,
          new ParameterizedTypeReference<List<User>>() {
          });

    } else {
      throw new BadResponseException("Bad response");
    }
  }
}