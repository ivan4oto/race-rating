package com.ivangochev.raceratingapi.user;

import com.ivangochev.raceratingapi.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getUsers();

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);

    boolean hasUserWithUsername(String username);

    boolean hasUserWithEmail(String email);

    User validateAndGetUserByUsername(String username);

    User saveUser(User user);

    UserDto updateUser(UserDto user);
    void deleteUser(User user);
}
