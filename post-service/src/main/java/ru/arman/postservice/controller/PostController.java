package ru.arman.postservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.arman.postservice.domain.dto.PostDto;
import ru.arman.postservice.domain.dto.PostDtoResponse;

import java.security.Principal;
import java.util.List;

@RequestMapping("/api/v1/post-service/posts")
public interface PostController {
    @GetMapping
    ResponseEntity<List<PostDtoResponse>> getAllPosts();

    @GetMapping("/{postId}")
    ResponseEntity<PostDtoResponse> getPostById(Principal principal, @PathVariable Long postId);

    @GetMapping("/tag/{tagId}")
    ResponseEntity<List<PostDtoResponse>> getPostsByTag(@PathVariable Long tagId);

    @PostMapping
    ResponseEntity<PostDtoResponse> addPost(Principal principal, @RequestBody @Valid PostDto postDto);

    @PutMapping("/update/{postId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @postServiceImpl.isUserOwner(#principal, #postId)")
    ResponseEntity<PostDtoResponse> updatePost(Principal principal, @PathVariable Long postId, @RequestBody @Valid PostDto postDto);

    @DeleteMapping("/delete/{postId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @postServiceImpl.isUserOwner(#principal, #postId)")
    ResponseEntity<String> deletePost(Principal principal, @PathVariable Long postId);
}
