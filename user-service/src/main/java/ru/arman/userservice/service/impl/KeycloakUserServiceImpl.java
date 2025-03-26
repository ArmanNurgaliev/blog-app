package ru.arman.userservice.service.impl;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.arman.userservice.domain.dto.UserDto;
import ru.arman.userservice.domain.model.User;
import ru.arman.userservice.exception.KeycloakUserCreationException;
import ru.arman.userservice.exception.RoleAssignException;
import ru.arman.userservice.service.KeycloakUserService;

import java.util.Collections;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserServiceImpl implements KeycloakUserService {
    @Value("${keycloak.realm}")
    private String realm;

    private final Keycloak keycloak;

    @Override
    public String createUser(User user) {
        log.info("Creating new UserRepresentation");
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(user.getLogin());
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.singleAttribute("mobile", user.getMobile());
        userRepresentation.setEmailVerified(true);

        log.info("Setting credentials");
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(user.getPassword());
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

        UsersResource usersResource = getUsersResource();

        log.info("Creating user in keycloak");
        Response response = usersResource.create(userRepresentation);

        if (Objects.equals(201,response.getStatus())){
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            log.info("UserRepresentation ID: {}", userId);
            try {
                assignRole(userId, "ROLE_USER");
            } catch (Exception e) {
                usersResource.delete(userId);
                log.error("Error assigning role in keycloak");
                throw new KeycloakUserCreationException(e.getMessage());
            }
            return userId;
        } else {
            log.error(response.readEntity(String.class));
            throw new KeycloakUserCreationException("Error creating user in Keycloak");
        }
    }

    @Override
    public void updateUser(String userId, UserDto userDto) {
        UserRepresentation userRepresentation = getUserById(userId);

        if (userDto.login() != null && !userDto.login().isBlank())
            userRepresentation.setUsername(userDto.login());
        if (userDto.firstName() != null && !userDto.firstName().isBlank())
            userRepresentation.setFirstName(userDto.firstName());
        if (userDto.lastName() != null && !userDto.lastName().isBlank())
            userRepresentation.setLastName(userDto.lastName());
        if (userDto.email() != null && !userDto.email().isBlank())
            userRepresentation.setEmail(userDto.email());
        if (userDto.mobile() != null && !userDto.mobile().isBlank())
            userRepresentation.singleAttribute("mobile", userDto.mobile());

        UsersResource usersResource = getUsersResource();

        log.info("Updating user in keycloak");
        usersResource.get(userId).update(userRepresentation);
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }

    @Override
    public UserRepresentation getUserById(String userId) {
        return  getUsersResource().get(userId).toRepresentation();
    }

    @Override
    public void deleteUserById(String userId) {
         getUserResource(userId).remove();
    }

    @Override
    public UserResource getUserResource(String userId) {
        return getUsersResource().get(userId);
    }

    @Override
    public void updatePassword(String password, String userId) {
        UserResource userResource = getUserResource(userId);
        CredentialRepresentation credentialRepresentation=new CredentialRepresentation();
        credentialRepresentation.setValue(password);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setTemporary(false);
        userResource.resetPassword(credentialRepresentation);
    }

    @Override
    public void assignRole(String userId, String roleName) {
        try {
            UserResource userResource = getUserResource(userId);
            RolesResource rolesResource = getRolesResource();
            RoleRepresentation representation = rolesResource.get(roleName).toRepresentation();
            userResource.roles().realmLevel().add(Collections.singletonList(representation));
        } catch (Exception e) {
            throw new RoleAssignException("Error assigning role in keycloak " + roleName + " to user with id: " + userId);
        }
    }

    @Override
    public void removeRole(String userId, String roleName) {
        UserResource userResource = getUserResource(userId);
        RolesResource rolesResource = getRolesResource();
        RoleRepresentation representation = rolesResource.get(roleName).toRepresentation();
        try {
            userResource.roles().realmLevel().remove(Collections.singletonList(representation));
        } catch (Exception e) {
            throw new RoleAssignException("Error removing role " + roleName + " from user with id: " + userId);
        }
    }

    private RolesResource getRolesResource(){
        return  keycloak.realm(realm).roles();
    }
}
