package ru.arman.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.arman.userservice.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByLogin(String login);
    Optional<User> findByEmail(String email);
    Optional<User> findByMobile(String mobile);
}
