package com.nfeeds.adapter.usermanager.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfeeds.adapter.usermanager.models.UserModel;
import com.nfeeds.adapter.usermanager.utils.HttpUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.util.Optional;

@Log4j2
@Service
public class AuthRemoteCallService {

    @Value("${nfeeds.dl.auth.url}")
    private String baseUrl;


    public boolean postNewUser(UserModel user) throws IOException, InterruptedException {
        log.debug(this.getClass().getSimpleName() + " - postNewUser");
        
        var body = new ObjectMapper().writeValueAsString(user);
        var uri = new DefaultUriBuilderFactory(baseUrl).builder().build();
        var response = HttpUtils.postRequest(uri,body);
        
        return response.statusCode() == HttpStatus.CREATED.value();
    }


    public Optional<UserModel> getUser(String id) throws IOException, InterruptedException {
        log.debug(this.getClass().getSimpleName() + " - getUser");
        
        var uri = new DefaultUriBuilderFactory(baseUrl).builder().path("{id}").build(id);
        var response = HttpUtils.getRequest(uri);
        
        try{
            return Optional.of(new ObjectMapper().readValue(response.body(), UserModel.class));
        } catch (JsonProcessingException exception){
            return Optional.empty();
        }
    }


    public boolean deleteUser(String id) throws IOException, InterruptedException {
        log.debug(this.getClass().getSimpleName() + " - deleteUser");
        
        var uri = new DefaultUriBuilderFactory(baseUrl).builder().path("{id}").build(id);
        var response = HttpUtils.deleteRequest(uri);
        
        return response.statusCode() == HttpStatus.NO_CONTENT.value();
    }
}
