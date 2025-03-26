package ru.arman.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.arman.userservice.domain.dto.UserDto;
import ru.arman.userservice.domain.dto.UserDtoResponse;
import ru.arman.userservice.domain.dto.UsersData;
import ru.arman.userservice.domain.mapper.UserMapper;
import ru.arman.userservice.domain.model.Role;
import ru.arman.userservice.domain.model.User;
import ru.arman.userservice.exception.UserAlreadyExistsException;
import ru.arman.userservice.exception.UserNotFoundException;
import ru.arman.userservice.repository.RoleRepository;
import ru.arman.userservice.repository.UserRepository;
import ru.arman.userservice.service.KeycloakUserService;
import ru.arman.userservice.service.UserService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final KeycloakUserService keycloakUserService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDtoResponse register(UserDto userDto) {
        log.info("Starting register new user");
        checkUser(userDto);
        log.info("Creating User from UserDto");

        User user = userMapper.userDtoToUser(userDto);
        user.setRegisteredAt(Timestamp.valueOf(LocalDateTime.now()));
        Role role = getRoleByName("ROLE_USER");
        user.getRoles().add(role);

        log.info("Saving new user in keycloak");
        String userId = keycloakUserService.createUser(user);

        user.setId(UUID.fromString(userId));
        User savedUser = new User();

        try {
            log.info("Adding new User to db");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            savedUser = userRepository.save(user);
        } catch (Exception e) {
            keycloakUserService.deleteUserById(userId);
            log.error(e.getMessage());
        }
        return userMapper.userToUserResponseDto(savedUser);
    }

    private User getUser(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found with id:" + userId));
    }

    @Override
    public UserDtoResponse getUserById(String userId) {
        log.info("Searching for user with id {} in db", userId);
        return userRepository.findById(UUID.fromString(userId))
                .map(userMapper::userToUserResponseDto)
                .orElseThrow(() -> new UserNotFoundException("User not found with id:" + userId));
    }

    @Override
    public UserDtoResponse updateUser(String userId, UserDto userDto) {
        log.info("Starting updating user with id: {}", userId);
        User userFromDB = getUser(userId);

        log.info("Updating user login");
        if (userDto.login() != null && !userDto.login().isBlank()) {
            userRepository.findByLogin(userDto.login()).ifPresentOrElse(
                    user -> {throw new UserAlreadyExistsException("User already exists with login: " + userDto.login());},
                    () -> userFromDB.setLogin(userDto.login())
            );
        }

        log.info("Updating user email");
        if (userDto.email() != null && !userDto.email().isBlank()) {
            userRepository.findByEmail(userDto.email()).ifPresentOrElse(
                    user -> {throw new UserAlreadyExistsException("User already exists with email: " + userDto.login());},
                    () -> userFromDB.setEmail(userDto.email())
            );
        }

        log.info("Updating user properties");
        if (userDto.firstName() != null && !userDto.firstName().isBlank())
            userFromDB.setFirstName(userDto.firstName());
        if (userDto.middleName() != null && !userDto.middleName().isBlank())
            userFromDB.setMiddleName(userDto.middleName());
        if (userDto.lastName() != null && !userDto.lastName().isBlank())
            userFromDB.setLastName(userDto.lastName());
        if (userDto.intro() != null && !userDto.intro().isBlank())
            userFromDB.setIntro(userDto.intro());
        if (userDto.profile() != null && !userDto.profile().isBlank())
            userFromDB.setProfile(userDto.profile());

        log.info("Saving updated user in db");
        User savedUser = userRepository.save(userFromDB);

        log.info("Updating user in keycloak");
        keycloakUserService.updateUser(userId, userDto);

        return userMapper.userToUserResponseDto(savedUser);
    }

    @Override
    public String assignRole(String userId, String roleName) {
        log.info("Assign role to user");
        User user = getUser(userId);
        Role role = getRoleByName(roleName);

        user.getRoles().add(role);

        log.info("Assign role to user in keycloak");
        keycloakUserService.assignRole(userId, role.getName());

        try {
            log.info("Saving user changes in db");
            userRepository.save(user);
        } catch (Exception e) {
            keycloakUserService.removeRole(userId, role.getName());
            log.error(e.getMessage());
        }

        return String.format("User %s has role %s now", user.getLogin(), roleName);
    }

    @Override
    public String removeRole(String userId, String roleName) {
        log.info("Remove role from user");
        User user = getUser(userId);
        Role role = getRoleByName(roleName);

        user.getRoles().remove(role);

        log.info("Remove role from user in keycloak");
        keycloakUserService.removeRole(userId, role.getName());

        try {
            log.info("Saving user changes in db");
            userRepository.save(user);
        } catch (Exception e) {
            keycloakUserService.assignRole(userId, role.getName());
            log.error(e.getMessage());
        }

        return String.format("User %s doesn't have role %s now", user.getLogin(), roleName);
    }

    @Override
    public UsersData getUsersData(String userId) {
        UserDtoResponse user = getUserById(userId);
        return new UsersData(user.email(), user.login());
    }

    private Role getRoleByName(String roleName) {
        log.info("Searching for role with name {} in db", roleName);
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with name:" + roleName));
    }

    private void checkUser(UserDto userDto) {
        log.info("Validating user by login, email and mobile");
        if (userRepository.findByLogin(userDto.login()).isPresent())
            throw new UserAlreadyExistsException("User already exists with login: " + userDto.login());
        else if (userRepository.findByEmail(userDto.email()).isPresent())
            throw new UserAlreadyExistsException("User already exists with email: " + userDto.email());
        else if (userRepository.findByMobile(userDto.mobile()).isPresent())
            throw new UserAlreadyExistsException("User already exists with mobile: " + userDto.mobile());
    }
}
