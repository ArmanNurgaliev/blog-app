package ru.arman.postservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.arman.postservice.domain.dto.UsersData;

@RequestMapping("/api/v1/post-service")
public interface PostFeignController {
    @GetMapping("/users-data/{postId}")
    UsersData getUsersData(@PathVariable Long postId);
}
