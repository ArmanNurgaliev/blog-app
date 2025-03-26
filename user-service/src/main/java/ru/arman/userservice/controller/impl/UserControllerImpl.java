package ru.arman.userservice.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.arman.userservice.controller.UserController;
import ru.arman.userservice.domain.dto.UserDto;
import ru.arman.userservice.domain.dto.UserDtoResponse;
import ru.arman.userservice.domain.dto.UsersData;
import ru.arman.userservice.service.UserService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {
    private final UserService userService;
    @Override
    public ResponseEntity<UserDtoResponse> register(UserDto userDto) {
        return new ResponseEntity<>(userService.register(userDto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> assignRole(String userId, String roleName) {
        return ResponseEntity.ok(userService.assignRole(userId, roleName));
    }

    @Override
    public ResponseEntity<String> removeRole(String userId, String roleName) {
        return ResponseEntity.ok(userService.removeRole(userId, roleName));
    }

    @Override
    public ResponseEntity<UserDtoResponse> getUserById(String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Override
    public ResponseEntity<UserDtoResponse> updateUser(Principal principal, String userId, UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(userId, userDto));
    }

    @Override
    public UsersData getUsersData(String userId) {
        return userService.getUsersData(userId);
    }
}
