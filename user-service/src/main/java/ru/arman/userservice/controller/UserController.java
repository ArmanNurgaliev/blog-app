package ru.arman.userservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.arman.userservice.domain.dto.UserDto;
import ru.arman.userservice.domain.dto.UserDtoResponse;
import ru.arman.userservice.domain.dto.UsersData;

import java.security.Principal;

@RequestMapping("/api/v1/user-service/user")
public interface UserController {

    @PostMapping("/register")
    ResponseEntity<UserDtoResponse> register(@RequestBody @Valid UserDto userDto);

    @PutMapping("/assign-role/{userId}/role/{roleName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ROLE_MANAGER')")
    ResponseEntity<String> assignRole(@PathVariable String userId, @PathVariable String roleName);

    @PutMapping("/remove-role/{userId}/role/{roleName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    ResponseEntity<String> removeRole(@PathVariable String userId, @PathVariable String roleName);

    @GetMapping("/{userId}")
    ResponseEntity<UserDtoResponse> getUserById(@PathVariable String userId);

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #principal.name == #userId")
    ResponseEntity<UserDtoResponse> updateUser(Principal principal, @PathVariable String userId, @RequestBody UserDto userDto);

    @GetMapping("/users-data/{userId}")
    UsersData getUsersData(@PathVariable String userId);
}
