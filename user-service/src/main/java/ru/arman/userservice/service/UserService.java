package ru.arman.userservice.service;

import org.keycloak.representations.idm.UserRepresentation;
import ru.arman.userservice.domain.dto.UserDto;
import ru.arman.userservice.domain.dto.UserDtoResponse;
import ru.arman.userservice.domain.dto.UsersData;

public interface UserService {
    UserDtoResponse register(UserDto userDto);

    UserDtoResponse getUserById(String userId);

    UserDtoResponse updateUser(String userId, UserDto userDto);

    String assignRole(String userId, String roleName);

    String removeRole(String userId, String roleName);

    UsersData getUsersData(String userId);
}
