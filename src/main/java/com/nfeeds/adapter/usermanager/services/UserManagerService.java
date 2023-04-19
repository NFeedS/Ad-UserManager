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

    /** Instance of <code>AuthRemoteCallService</code> to abstract the remote calls done to DL-Auth */
    private final AuthRemoteCallService authRemoteCallService;

    /** Instance of <code>SubscriptionsRemoteCallService</code> to abstract the remote calls done to DL-Subscriptions */
    private final SubscriptionsRemoteCallService subscriptionsRemoteCallService;

    public UserManagerService(AuthRemoteCallService authRemoteCallService, SubscriptionsRemoteCallService subscriptionsRemoteCallService) {
        this.authRemoteCallService = authRemoteCallService;
        this.subscriptionsRemoteCallService = subscriptionsRemoteCallService;
    }

    /**
     * Given an id and a password will create a new user.
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

        return authRemoteCallService.postNewUser(id,salt,hash);
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
            try {
                var info = new ObjectMapper().readValue(getResponse.right.get(), UserInfo.class);
                return AuthUtils.validate(psw,info);
            } catch (JsonProcessingException e) {
                return false;
            }
        }

        return false;
    }
}
