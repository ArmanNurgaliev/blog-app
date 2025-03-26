package ru.arman.postservice.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.arman.postservice.controller.PostFeignController;
import ru.arman.postservice.domain.dto.UsersData;
import ru.arman.postservice.service.PostService;

@RestController
@RequiredArgsConstructor
public class PostFeignControllerImpl implements PostFeignController {
    private final PostService postService;
    @Override
    public UsersData getUsersData(Long postId) {
        return postService.getUsersData(postId);
    }
}
