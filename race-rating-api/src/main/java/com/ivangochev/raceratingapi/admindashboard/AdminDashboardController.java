package com.ivangochev.raceratingapi.admindashboard;

import com.ivangochev.raceratingapi.user.UserService;
import com.ivangochev.raceratingapi.user.dto.UserDto;
import com.ivangochev.raceratingapi.user.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminDashboardController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> users = userService.getUsers().stream()
                .map(userMapper::toUserDto)
                .toList();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/users/update")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto) {
       return new ResponseEntity<>(userService.updateUser(userDto), HttpStatus.OK);
    }
}
