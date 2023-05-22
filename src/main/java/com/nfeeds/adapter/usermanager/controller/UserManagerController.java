package com.nfeeds.adapter.usermanager.controller;

import com.nfeeds.adapter.usermanager.models.UserInfo;
import com.nfeeds.adapter.usermanager.models.UserModel;
import com.nfeeds.adapter.usermanager.services.UserManagerService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("api/v1/usermanager")
public class UserManagerController {

    private final UserManagerService userManagerService;
    
    @GetMapping("/user/{id}")
    public UserModel getUserById(@PathVariable("id") String user_id) throws IOException, InterruptedException {
        log.debug(this.getClass().getSimpleName() + " - getUserById");
        return userManagerService.getUser(user_id);
    }
    
    @PostMapping("/user")
    public boolean createUser(@RequestBody UserInfo body) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException {
        log.debug(this.getClass().getSimpleName() + " - createUser");
        return userManagerService.createNewUser(body.id(), body.psw());
    }
    
    
}
