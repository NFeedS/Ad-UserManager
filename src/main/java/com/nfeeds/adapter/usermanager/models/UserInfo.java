package com.nfeeds.adapter.usermanager.models;

/**
 * <p> Record representing an entry of a User in the database. </p>
 * <p> To marshall a String to this record use: </p>
 * <pre>
 * {@code
 *  var user_info = new ObjectMapper().readValue( jsonString , UserInfo.class);
 * }
 * </pre>
 * @param salt Random string used to generate the hash of the password to authenticate the user.
 * @param hashpsw The hash of the password used to validate the password of the user.
 */
public record UserInfo (String salt, String hashpsw) {}
