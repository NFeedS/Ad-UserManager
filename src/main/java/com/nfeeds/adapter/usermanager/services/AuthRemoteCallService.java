package com.nfeeds.adapter.usermanager.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lm.wrapper.http.JHWrapper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthRemoteCallService {

    @Value("${nfeeds.dl.auth.url}")
    private String baseUrl;

    public boolean postNewUser(String id, String salt, String psw) throws IOException, InterruptedException {

        var mapper = new ObjectMapper();
        var jsonBody = mapper.createObjectNode();
        jsonBody.put("id", id);
        jsonBody.put("salt", salt);
        jsonBody.put("hashpsw", psw);

        var response = JHWrapper.remotePostCall(baseUrl+"users", jsonBody.asText());

        return response.statusCode() == HttpStatus.CREATED.value();
    }

    public ImmutablePair<HttpStatus, Optional<String>> getUser(String id) throws IOException, InterruptedException {
        var response = JHWrapper.remoteGetCall(baseUrl,"users/{id}", Map.of("id", id));
        return new ImmutablePair<>(HttpStatus.resolve(response.statusCode()), Optional.ofNullable(response.body()));
    }
}
