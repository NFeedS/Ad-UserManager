package com.nfeeds.adapter.usermanager.models;

/**
 * <p> Record representing an entry of a Subscription in the database. </p>
 * <p> To marshall a String to this record use: </p>
 * <pre>
 * {@code
 * var sub_info = new ObjectMapper().readValue( jsonString , SubsctiptionInfo.class);
 * }
 * </pre>
 * @param userId The unique id of the user that has this subscription.
 * @param topicId The topic to which the user is subscribed to.
 * @param callback The endpoint to which the system will send messages to.
 */
public record SubscriptionInfo(String userId, String topicId, String callback) {}
