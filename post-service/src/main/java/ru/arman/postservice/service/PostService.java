package ru.arman.postservice.service;

import ru.arman.postservice.domain.dto.PostDto;
import ru.arman.postservice.domain.dto.PostDtoResponse;
import ru.arman.postservice.domain.dto.UsersData;

import java.security.Principal;
import java.util.List;

public interface PostService {
    PostDtoResponse addPost(Principal principal, PostDto postDto);

    PostDtoResponse updatePost(Long postId, PostDto postDto);

    String deletePost(Long postId);

    List<PostDtoResponse> getAllPosts();

    PostDtoResponse getPostById(Long postId);

    List<PostDtoResponse> getPostByTag(Long tagId);

    UsersData getUsersData(Long postId);
}
