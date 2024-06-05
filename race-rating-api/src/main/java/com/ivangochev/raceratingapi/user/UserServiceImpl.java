package com.ivangochev.raceratingapi.user;

import com.ivangochev.raceratingapi.exception.UserNotFoundException;
import com.ivangochev.raceratingapi.user.dto.UserDto;
import com.ivangochev.raceratingapi.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean hasUserWithUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean hasUserWithEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User validateAndGetUserByUsername(String username) {
        return getUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with username %s not found", username)));
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = userRepository.findById(userDto.id())
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", userDto.id())));
        User updatedUser = userMapper.fromUserDto(userDto, user);
        return userMapper.toUserDto(userRepository.save(updatedUser));
    }


    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }
}
