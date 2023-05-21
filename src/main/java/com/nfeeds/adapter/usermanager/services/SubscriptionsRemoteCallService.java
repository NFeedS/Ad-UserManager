package com.nfeeds.adapter.usermanager.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lm.wrapper.http.JHWrapper;
import com.nfeeds.adapter.usermanager.models.SubscriptionInfo;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Service that provides functions to perform specific remote http calls to the service DL-Subscriptions.
 */
@Service
public class SubscriptionsRemoteCallService {

    @Value("${nfeeds.dl.subscriptions.url}")
    private String baseUrl;


    public boolean postNewSubscription(String user_id, String topic_id, String callback) throws IOException, InterruptedException {
        var mapper = new ObjectMapper();
        var jsonBody = mapper.createObjectNode();
        jsonBody.put("userId", user_id);
        jsonBody.put("topicId", topic_id);
        jsonBody.put("callback", callback);

        var response = JHWrapper.remotePostCall(baseUrl+"subscriptions", jsonBody.asText());

        return response.statusCode() == HttpStatus.CREATED.value();
    }


    public ImmutablePair<HttpStatus, Optional<List<SubscriptionInfo>>> getSubscriptionsOfUser(String user_id) throws IOException, InterruptedException {
        var response = JHWrapper.remoteGetCall(baseUrl,"subscriptions/search/user", Map.of(), Map.of(), Map.of("id", user_id));

        if(response.body() == null) {
            return new ImmutablePair<>(HttpStatus.resolve(response.statusCode()), Optional.empty());
        }

        var sub_info = Arrays.asList(new ObjectMapper().readValue(response.body(), SubscriptionInfo[].class));
        return new ImmutablePair<>(HttpStatus.resolve(response.statusCode()), Optional.ofNullable(sub_info));
    }


    public ImmutablePair<HttpStatus, Optional<List<SubscriptionInfo>>> getSubscriptionsOnATopic(String topic_id) throws IOException, InterruptedException {
        var response = JHWrapper.remoteGetCall(baseUrl,"subscriptions/search/topic", Map.of(), Map.of(), Map.of("id", topic_id));

        if(response.body() == null) {
            return new ImmutablePair<>(HttpStatus.resolve(response.statusCode()), Optional.empty());
        }

        var sub_info_list = Arrays.asList(new ObjectMapper().readValue(response.body(), SubscriptionInfo[].class));
        return new ImmutablePair<>(HttpStatus.resolve(response.statusCode()), Optional.of(sub_info_list));
    }


    public boolean deleteSubscription(String id) throws IOException, InterruptedException {
        return HttpStatus.resolve(JHWrapper.remoteDeleteCall(baseUrl,"subscriptions/{id}",Map.of("id", id)).statusCode()) == HttpStatus.NO_CONTENT;
    }

    public boolean deleteSubscription(String user_id, String topic_id) throws IOException, InterruptedException {
        var subs_resp = getSubscriptionsOfUser(user_id);

        if(subs_resp.right.isEmpty()){ return false; }
        var subs = subs_resp.right.get();

        var sub = subs.stream().filter((s) -> Objects.equals(s.topicId(), topic_id));

        return HttpStatus.resolve(JHWrapper.remoteDeleteCall(baseUrl,"subscriptions/{id}",Map.of("id", user_id)).statusCode()) == HttpStatus.NO_CONTENT;
    }
}
