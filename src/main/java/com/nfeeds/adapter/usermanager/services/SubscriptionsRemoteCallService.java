package com.nfeeds.adapter.usermanager.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionsRemoteCallService {

    @Value("${nfeeds.dl.subscriptions.url}")
    private String baseUrl;
}
