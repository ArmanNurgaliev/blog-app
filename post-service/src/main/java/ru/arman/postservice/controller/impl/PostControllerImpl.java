package ru.arman.postservice.controller.impl;

import lombok.RequiredArgsConstructor;
import org.ietf.jgss.Oid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;
import ru.arman.postservice.controller.PostController;
import ru.arman.postservice.domain.dto.PostDto;
import ru.arman.postservice.domain.dto.PostDtoResponse;
import ru.arman.postservice.service.PostService;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostControllerImpl implements PostController {
    private final PostService postService;

    @Override
    public ResponseEntity<List<PostDtoResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @Override
    public ResponseEntity<PostDtoResponse> getPostById(Principal principal, Long postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @Override
    public ResponseEntity<List<PostDtoResponse>> getPostsByTag(Long tagId) {
        return ResponseEntity.ok(postService.getPostByTag(tagId));
    }

    @Override
    public ResponseEntity<PostDtoResponse> addPost(Principal principal, PostDto postDto) {
        return new ResponseEntity<>(postService.addPost(principal, postDto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<PostDtoResponse> updatePost(Principal principal, Long postId, PostDto postDto) {
        return ResponseEntity.ok(postService.updatePost(postId, postDto));
    }

    @Override
    public ResponseEntity<String> deletePost(Principal principal, Long postId) {
        return ResponseEntity.ok(postService.deletePost(postId));
    }
}
