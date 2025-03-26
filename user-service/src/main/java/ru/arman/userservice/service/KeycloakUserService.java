package ru.arman.userservice.service;


import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import ru.arman.userservice.domain.dto.UserDto;
import ru.arman.userservice.domain.model.User;

public interface KeycloakUserService {
    String createUser(User user);
    UserRepresentation getUserById(String userId);
    void deleteUserById(String userId);
    UserResource getUserResource(String userId);
    void updatePassword(String password,String userId);
    void assignRole(String userId, String roleName);
    void removeRole(String userId, String roleName);
    void updateUser(String userId, UserDto userDto);
}
