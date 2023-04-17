package com.nfeeds.adapter.usermanager.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthRemoteCallService {

    @Value("${nfeeds.dl.auth.url}")
    private String baseUrl;

    // remote calls implementation ...
}
