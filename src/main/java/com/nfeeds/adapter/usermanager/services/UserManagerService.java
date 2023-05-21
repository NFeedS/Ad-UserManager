package com.nfeeds.adapter.usermanager.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfeeds.adapter.usermanager.models.UserInfo;
import com.nfeeds.adapter.usermanager.utils.AuthUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Service that provides functions directly related to the functionalities of the UserManager module.
 */
@Service
public class UserManagerService {

    private final AuthRemoteCallService authRemoteCallService;
    private final SubscriptionsRemoteCallService subscriptionsRemoteCallService;


    public UserManagerService(AuthRemoteCallService authRemoteCallService, SubscriptionsRemoteCallService subscriptionsRemoteCallService) {
        this.authRemoteCallService = authRemoteCallService;
        this.subscriptionsRemoteCallService = subscriptionsRemoteCallService;
    }

    /**
     * Given an id and a password will generate a salt and hash the password,
     * adding then a new entry in the users table and subscribe it to the system default topic.
     * @param id The unique identifier of the user to create.
     * @param psw The password that the user will use to authenticate.
     * @return True if the procedure goes well, False if the user already exists.
     * @throws IOException
     * @throws InterruptedException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public boolean createNewUser(String id, String psw) throws IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException {

        // check that the user doesn't exist already
        if (authRemoteCallService.getUser(id).left != HttpStatus.NOT_FOUND) {
            return false;
        }

        var salt = AuthUtils.generateSalt();
        var hash = AuthUtils.hashPassword(psw,salt);

        if (!authRemoteCallService.postNewUser(id,salt,hash)){
            return false;
        }

        // if the user has been successfully created add a subscription to the system default topic.
        subscriptionsRemoteCallService.postNewSubscription(id,"system_default", "");

        return true;
    }

    public boolean addSubscription(String user_id, String topic_id, String callback) throws IOException, InterruptedException {
        return subscriptionsRemoteCallService.postNewSubscription(user_id,topic_id, callback);
    }

    public boolean removeSubscription(String user_id, String topic_id) throws IOException, InterruptedException {
        return subscriptionsRemoteCallService.deleteSubscription(user_id,topic_id);
    }

    /**
     * Checks if the password provided correspond to the hash of the user with the id provided.
     * @param id The unique identifier of the user to authenticate.
     * @param psw The password to hash and validate.
     * @return True if there are no error and the check is positive, False otherwise or if the user was not found.
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean checkAuthorization(String id, String psw) throws IOException, InterruptedException {

        var getResponse = authRemoteCallService.getUser(id);

        if (getResponse.left != HttpStatus.OK) {
            return false;
        }

        if(getResponse.right.isPresent()){

            var info = getResponse.right.get();
            return AuthUtils.validate(psw,info);

        }

        return false;
    }
}
