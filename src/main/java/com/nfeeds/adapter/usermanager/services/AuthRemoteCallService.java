package com.nfeeds.adapter.usermanager.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lm.wrapper.http.JHWrapper;
import com.nfeeds.adapter.usermanager.models.UserInfo;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

/**
 * Service that provides functions to perform specific remote http calls to the service DL-Auth.
 */
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


    public ImmutablePair<HttpStatus, Optional<UserInfo>> getUser(String id) throws IOException, InterruptedException {
        var response = JHWrapper.remoteGetCall(baseUrl,"users/{id}", Map.of("id", id));

        if(response.body() == null) {
            return new ImmutablePair<>(HttpStatus.resolve(response.statusCode()), Optional.empty());
        }

        var user_info = new ObjectMapper().readValue( response.body() , UserInfo.class);
        return new ImmutablePair<>(HttpStatus.resolve(response.statusCode()), Optional.ofNullable(user_info));
    }


    public boolean deleteUser(String id) throws IOException, InterruptedException {
        return HttpStatus.resolve(JHWrapper.remoteDeleteCall(baseUrl,"users/{id}",Map.of("id", id)).statusCode()) == HttpStatus.NO_CONTENT;
    }
}
