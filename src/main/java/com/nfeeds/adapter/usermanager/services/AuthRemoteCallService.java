package com.nfeeds.adapter.usermanager.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lm.wrapper.http.JHWrapper;
import com.nfeeds.adapter.usermanager.models.UserInfo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
public class AuthRemoteCallService {

    @Value("${nfeeds.dl.auth.url}")
    private String baseUrl;


    public boolean postNewUser(UserInfo user) throws IOException, InterruptedException {
        log.debug(this.getClass().getSimpleName() + " - postNewUser");
        
        var body = new ObjectMapper().writeValueAsString(user);
        
        var uri = new DefaultUriBuilderFactory(baseUrl).builder().build();
        
        var request = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .version(HttpClient.Version.HTTP_2)
                .header("Content-Type", "application/json")
                .build();
        
        var response = HttpClient.newHttpClient().send(request,HttpResponse.BodyHandlers.ofString());
        
        return response.statusCode() == HttpStatus.CREATED.value();
    }


    public Optional<UserInfo> getUser(String id) throws IOException, InterruptedException {
        log.debug(this.getClass().getSimpleName() + " - getUser");
        
        var uri = new DefaultUriBuilderFactory(baseUrl).builder().path("/{id}").build(id);
        
        var request = HttpRequest.newBuilder(uri)
                .GET()
                .version(HttpClient.Version.HTTP_2)
                .header("Content-Type", "application/json")
                .build();
        
        var response = HttpClient.newHttpClient().send(request,HttpResponse.BodyHandlers.ofString());
        
        try{
            return Optional.of(new ObjectMapper().readValue(response.body(),UserInfo.class));
        } catch (JsonProcessingException exception){
            return Optional.empty();
        }
    }


    public boolean deleteUser(String id) throws IOException, InterruptedException {
        log.debug(this.getClass().getSimpleName() + " - deleteUser");
        
        var uri = new DefaultUriBuilderFactory(baseUrl).builder().path("/{id}").build(id);
        
        var request = HttpRequest.newBuilder(uri)
                .DELETE()
                .version(HttpClient.Version.HTTP_2)
                .header("Content-Type", "application/json")
                .build();
        
        var response = HttpClient.newHttpClient().send(request,HttpResponse.BodyHandlers.ofString());
        
        return response.statusCode() == HttpStatus.NO_CONTENT.value();
    }
}
