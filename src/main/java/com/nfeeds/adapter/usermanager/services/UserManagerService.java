package com.nfeeds.adapter.usermanager.services;

import org.springframework.stereotype.Service;

@Service
public class UserManagerService {

    private final AuthRemoteCallService authRemoteCallService;
    private final SubscriptionsRemoteCallService subscriptionsRemoteCallService;

    public UserManagerService(AuthRemoteCallService authRemoteCallService, SubscriptionsRemoteCallService subscriptionsRemoteCallService) {
        this.authRemoteCallService = authRemoteCallService;
        this.subscriptionsRemoteCallService = subscriptionsRemoteCallService;
    }
}
