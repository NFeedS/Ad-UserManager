package com.nfeeds.adapter.usermanager.models;

import jakarta.annotation.Nullable;


public record SubscriptionInfo(String userId, String topicId, String callback) {}
