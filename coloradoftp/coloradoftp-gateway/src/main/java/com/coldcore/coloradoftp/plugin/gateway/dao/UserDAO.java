package com.coldcore.coloradoftp.plugin.gateway.dao;

import com.coldcore.coloradoftp.plugin.gateway.User;

/**
 * User data access object.
 */
public interface UserDAO {

  /** Get user by username
   * @param username User name
   * @return User or NULL if does not exist
   */
  public User getUser(String username) throws Exception;


  /** Get user by username and password
   * @param username User name
   * @param passowrd Password
   * @return User or NULL if does not exist or the password is incorrect
   */
  public User getUser(String username, String passowrd) throws Exception;


  /** Save a new user
   * @param user User
   */
  public void insertUser(User user) throws Exception;


  /** Update an existing user
   * @param user User
   */
  public void updateUser(User user) throws Exception;
}
