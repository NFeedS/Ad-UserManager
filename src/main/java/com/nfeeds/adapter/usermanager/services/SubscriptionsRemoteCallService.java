package com.nfeeds.adapter.usermanager.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lm.wrapper.http.JHWrapper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Service that provides functions to perform specific remote http calls to the service DL-Subscriptions.
 */
@Service
public class SubscriptionsRemoteCallService {

    /** Base url used to make the http calls. */
    @Value("${nfeeds.dl.subscriptions.url}")
    private String baseUrl;

    /**
     * <p> Make a POST call to add a new subscription to the database. </p>
     * <p>url : <code> POST <i> {baseUrl}/subscriptions </i></code></p>
     * <p>body : <code>
     *     {
     *         "userId" : {user_id},
     *         "topicId" : {topic_id},
     *         "callback" : {callback}
     *     }
     * </code></p>
     * @param user_id The unique id of the user to subscribe to a topic.
     * @param topic_id The unique id of the topic to which the user has to be subscribed to.
     * @param callback The endpoint to call to send message to the user on the topic to which is subscribed.
     * @return True if the request return CREATED.
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean postNewSubscription(String user_id, String topic_id, String callback) throws IOException, InterruptedException {

        var mapper = new ObjectMapper();
        var jsonBody = mapper.createObjectNode();
        jsonBody.put("userId", user_id);
        jsonBody.put("topicId", topic_id);
        jsonBody.put("callback", callback);

        var response = JHWrapper.remotePostCall(baseUrl+"subscriptions", jsonBody.asText());

        return response.statusCode() == HttpStatus.CREATED.value();
    }

    /**
     * <p> Make a GET call to retrieve all the subscriptions of a given user. </p>
     * <p>url : <code> GET <i> {baseUrl}/subscriptions/search/user?id={user_id} </i></code></p>
     * @param user_id The unique id of the user searched.
     * @return An <code>ImmutablePair</code> containing the status of the response and an optional string containing the list of subscriptions of the user if any.
     * @throws IOException
     * @throws InterruptedException
     */
    public ImmutablePair<HttpStatus, Optional<String>> getSubscriptionsOfUser(String user_id) throws IOException, InterruptedException {
        var response = JHWrapper.remoteGetCall(baseUrl,"subscriptions/search/user?id={id}", Map.of("id", user_id));
        return new ImmutablePair<>(HttpStatus.resolve(response.statusCode()), Optional.ofNullable(response.body()));
    }

    /**
     * <p> Make a GET call to retrieve all the subscriptions done to a given topic. </p>
     * <p>url : <code> GET <i> {baseUrl}/subscriptions/search/topic?id={user_id} </i></code></p>
     * @param topic_id The unique id of the topic searched.
     * @return An <code>ImmutablePair</code> containing the status of the response and an optional string containing the list of subscriptions relative to the topic if any.
     * @throws IOException
     * @throws InterruptedException
     */
    public ImmutablePair<HttpStatus, Optional<String>> getSubscriptionsOnATopic(String topic_id) throws IOException, InterruptedException {
        var response = JHWrapper.remoteGetCall(baseUrl,"subscriptions/search/topic?id={id}", Map.of("id", topic_id));
        return new ImmutablePair<>(HttpStatus.resolve(response.statusCode()), Optional.ofNullable(response.body()));
    }
}
