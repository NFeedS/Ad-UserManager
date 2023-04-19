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

/**
 * Service that provides functions to perform specific remote http calls to the service DL-Auth.
 */
@Service
public class AuthRemoteCallService {

    /** Base url used to make the http calls. */
    @Value("${nfeeds.dl.auth.url}")
    private String baseUrl;

    /**
     * <p> Make a POST call to add a new user to the database. </p>
     * <p>url : <code> POST <i> {baseUrl}/users </i></code></p>
     * <p>body : <code>
     *     {
     *         "id" : {id},
     *         "salt" : {salt},
     *         "hashpsw" : {psw}
     *     }
     * </code></p>
     * @param id The unique identified of the user to create.
     * @param salt The salt generated before the call.
     * @param psw The password hashed using the salt provided.
     * @return True if the request return CREATED.
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean postNewUser(String id, String salt, String psw) throws IOException, InterruptedException {

        var mapper = new ObjectMapper();
        var jsonBody = mapper.createObjectNode();
        jsonBody.put("id", id);
        jsonBody.put("salt", salt);
        jsonBody.put("hashpsw", psw);

        var response = JHWrapper.remotePostCall(baseUrl+"users", jsonBody.asText());

        return response.statusCode() == HttpStatus.CREATED.value();
    }

    /**
     * <p>Make a GET call to retrieve a single user information.</p>
     * <p>url : <code> GET <i> {baseUrl}/users/{id} </i></code></p>
     * @param id The identifier of the user searched.
     * @return An <code>ImmutablePair</code> containing the status of the response and an optional string containing the information of the user if found.
     * @throws IOException
     * @throws InterruptedException
     */
    public ImmutablePair<HttpStatus, Optional<String>> getUser(String id) throws IOException, InterruptedException {
        var response = JHWrapper.remoteGetCall(baseUrl,"users/{id}", Map.of("id", id));
        return new ImmutablePair<>(HttpStatus.resolve(response.statusCode()), Optional.ofNullable(response.body()));
    }
}
