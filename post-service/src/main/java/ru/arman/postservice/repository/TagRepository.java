package ru.arman.postservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.arman.postservice.domain.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
