package com.nfeeds.adapter.usermanager.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfeeds.adapter.usermanager.models.SubscriptionInfo;
import com.nfeeds.adapter.usermanager.models.SubscriptionModel;
import com.nfeeds.adapter.usermanager.utils.HttpUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.util.*;

@Log4j2
@Service
public class SubscriptionsRemoteCallService {

    @Value("${nfeeds.dl.subscriptions.url}")
    private String baseUrl;


    public boolean postNewSubscription(SubscriptionInfo subscription) throws IOException, InterruptedException {
        log.debug(this.getClass().getSimpleName() + " - postNewSubscription");
        
        var body = new ObjectMapper().writeValueAsString(subscription);
        var uri = new DefaultUriBuilderFactory(baseUrl).builder().build();
        var response = HttpUtils.postRequest(uri,body);

        return response.statusCode() == HttpStatus.CREATED.value();
    }


    public Optional<List<SubscriptionModel>> getSubscriptionsOfUser(String user_id) throws IOException, InterruptedException {
        log.debug(this.getClass().getSimpleName() + " - getSubscriptionsOfUser");
        
        var uri = new DefaultUriBuilderFactory(baseUrl).builder().path("user/{id}").build(user_id);
        var response = HttpUtils.getRequest(uri);
        
        try{
            return Optional.of(new ObjectMapper().readerForListOf(SubscriptionInfo.class).readValue(response.body()));
        } catch (JsonProcessingException exception){
            return Optional.empty();
        }
    }


    public Optional<List<SubscriptionModel>> getSubscriptionsOnATopic(String topic_id) throws IOException, InterruptedException {
        log.debug(this.getClass().getSimpleName() + " - getSubscriptionsOnATopic");
        
        var uri = new DefaultUriBuilderFactory(baseUrl).builder().path("topic/{id}").build(topic_id);
        var response = HttpUtils.getRequest(uri);
        
        try{
            return Optional.of(new ObjectMapper().readerForListOf(SubscriptionInfo.class).readValue(response.body()));
        } catch (JsonProcessingException exception){
            return Optional.empty();
        }
    }


    public boolean deleteSubscription(String id) throws IOException, InterruptedException {
        log.debug(this.getClass().getSimpleName() + " - deleteSubscription");
        
        var uri = new DefaultUriBuilderFactory(baseUrl).builder().path("{id}").build(id);
        var response = HttpUtils.deleteRequest(uri);
        
        return response.statusCode() == HttpStatus.NO_CONTENT.value();
    }

    public void deleteSubscription(String user_id, String topic_id) throws IOException, InterruptedException {
        log.debug(this.getClass().getSimpleName() + " - deleteSubscription");
        var subs_resp = getSubscriptionsOfUser(user_id);

        if(subs_resp.isEmpty()){ return; }
        var subs = subs_resp.get();

        var sub = subs.stream().filter((s) -> Objects.equals(s.topicId(), topic_id)).toList();
        for(var s : sub){
            deleteSubscription(s.id());
        }
    }
}
