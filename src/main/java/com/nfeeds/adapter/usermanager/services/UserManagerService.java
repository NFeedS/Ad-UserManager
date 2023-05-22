package com.nfeeds.adapter.usermanager.services;

import com.nfeeds.adapter.usermanager.models.SubscriptionInfo;
import com.nfeeds.adapter.usermanager.models.SubscriptionModel;
import com.nfeeds.adapter.usermanager.models.UserModel;
import com.nfeeds.adapter.usermanager.utils.AuthUtils;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;


@Log4j2
@AllArgsConstructor
@Service
public class UserManagerService {

    private final AuthRemoteCallService authRemoteCallService;
    private final SubscriptionsRemoteCallService subscriptionsRemoteCallService;
    
    public UserModel getUser(String id) throws IOException, InterruptedException {
        log.debug(this.getClass().getSimpleName() + " - getUser");
        return authRemoteCallService.getUser(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
    
    public boolean createNewUser(String id, String psw) throws IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException {
        log.debug(this.getClass().getSimpleName() + " - createNewUser");
        
        // check that the user doesn't exist already
        if (authRemoteCallService.getUser(id).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }

        var salt = AuthUtils.generateSalt();
        var hash = AuthUtils.hashPassword(psw,salt);
        
        var user_instance = new UserModel(id,salt,hash);
        
        if (!authRemoteCallService.postNewUser(user_instance)){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed user creation");
        }

        // if the user has been successfully created add a subscription to the system default topic.
        subscriptionsRemoteCallService.postNewSubscription(new SubscriptionInfo(id,"system_default", ""));

        return true;
    }
    
    
    public boolean deleteUser(String user_id) throws IOException, InterruptedException {
        log.debug(this.getClass().getSimpleName() + " - deleteUser");
        
        var subs = subscriptionsRemoteCallService.getSubscriptionsOfUser(user_id);
        if(subs.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found while deleting subscriptions.");
        }
        
        for (var s : subs.get()){
            subscriptionsRemoteCallService.deleteSubscription(s.id());
        }
        
        return authRemoteCallService.deleteUser(user_id);
    }
    
    public List<SubscriptionModel> getSubscriptionsByUser(String user_id) throws IOException, InterruptedException {
        log.debug(this.getClass().getSimpleName() + " - getSubscriptionsByUser");
        return subscriptionsRemoteCallService.getSubscriptionsOfUser(user_id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscriptions of user not found"));
    }
    public boolean addSubscription(String user_id, String topic_id, String callback) throws IOException, InterruptedException {
        log.debug(this.getClass().getSimpleName() + " - addSubscription");
        return subscriptionsRemoteCallService.postNewSubscription(new SubscriptionInfo(user_id,topic_id, callback));
    }
    

    public void removeSubscription(String user_id, String topic_id) throws IOException, InterruptedException {
        log.debug(this.getClass().getSimpleName() + " - removeSubscription");
        subscriptionsRemoteCallService.deleteSubscription(user_id,topic_id);
    }
    
    
    public boolean checkAuthorization(String id, String psw) throws IOException, InterruptedException {

        var getResponse = authRemoteCallService.getUser(id);
        
        if (getResponse.isEmpty()) {
            return false;
        }
        
        var info = getResponse.get();
        return AuthUtils.validate(psw,info);
    }
}
