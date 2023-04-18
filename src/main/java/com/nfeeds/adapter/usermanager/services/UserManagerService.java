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

@Service
public class UserManagerService {

    private final AuthRemoteCallService authRemoteCallService;
    private final SubscriptionsRemoteCallService subscriptionsRemoteCallService;

    public UserManagerService(AuthRemoteCallService authRemoteCallService, SubscriptionsRemoteCallService subscriptionsRemoteCallService) {
        this.authRemoteCallService = authRemoteCallService;
        this.subscriptionsRemoteCallService = subscriptionsRemoteCallService;
    }

    public boolean createNewUser(String id, String psw) throws IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException {

        // check that the user doesn't exist already
        if (authRemoteCallService.getUser(id).left != HttpStatus.NOT_FOUND) {
            return false;
        }

        var salt = AuthUtils.generateSalt();
        var hash = AuthUtils.hashPassword(psw,salt);

        return authRemoteCallService.postNewUser(id,salt,hash);
    }

    public boolean checkAuthorization(String id, String psw) throws IOException, InterruptedException {

        var getResponse = authRemoteCallService.getUser(id);

        if (getResponse.left != HttpStatus.OK) {
            return false;
        }

        if(getResponse.right.isPresent()){
            try {
                var info = new ObjectMapper().readValue(getResponse.right.get(), UserInfo.class);
                var hash = AuthUtils.hashPassword(psw,info.salt());

                return hash.equals(info.hashpsw());

            } catch (JsonProcessingException e) {
                return false;
            } catch (NoSuchAlgorithmException e) {
                return false;
            } catch (InvalidKeySpecException e) {
                return false;
            }
        }

        return false;
    }
}
