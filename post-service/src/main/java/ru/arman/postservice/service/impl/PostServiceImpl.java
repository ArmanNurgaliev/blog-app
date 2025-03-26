package ru.arman.postservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.arman.postservice.domain.dto.UsersData;
import ru.arman.postservice.domain.mapper.PostMapper;
import ru.arman.postservice.domain.dto.PostDto;
import ru.arman.postservice.domain.dto.PostDtoResponse;
import ru.arman.postservice.domain.model.Post;
import ru.arman.postservice.domain.model.Tag;
import ru.arman.postservice.exception.PostNotFoundException;
import ru.arman.postservice.feign.UserFeignClient;
import ru.arman.postservice.repository.PostRepository;
import ru.arman.postservice.repository.TagRepository;
import ru.arman.postservice.service.PostService;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostMapper postMapper;
    private final UserFeignClient userFeignClient;

    @Override
    @Transactional
    public PostDtoResponse addPost(Principal principal, PostDto postDto) {
        log.info("Starting adding new Post");
        log.info("Creating Post from PostDto");
        Post post = postMapper.postDtoToPost(postDto);
        post.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        post.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        addTagsToPost(postDto, post);

        log.info("Extract userId from token");
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        String userId = (String) token.getTokenAttributes().get("sub");
        post.setUserId(userId);

        log.info("Saving new Post in db");
        Post savedPost = postRepository.save(post);
        return postMapper.postToPostDtoResponse(savedPost);
    }

    @Override
    public PostDtoResponse updatePost(Long postId, PostDto postDto) {
        log.info("Starting updating Post with id: {}", postId);
        Post postFromDb = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));


        log.info("Updating Post properties");
        if (postDto.title() != null && !postDto.title().isBlank())
            postFromDb.setTitle(postDto.title());
        if (postDto.content() != null && !postDto.content().isBlank())
            postFromDb.setContent(postDto.content());
        addTagsToPost(postDto, postFromDb);
        postFromDb.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        log.info("Saving updated Post in db");
        Post savedPost = postRepository.save(postFromDb);
        return postMapper.postToPostDtoResponse(savedPost);
    }

    @Override
    public String deletePost(Long postId) {
        log.info("Deleting post with id: {}", postId);
        postRepository.deleteById(postId);
        return "Post was deleted";
    }

    @Override
    public List<PostDtoResponse> getAllPosts() {
        log.info("Searching for all posts");
        return postRepository.findAll().stream()
                .map(postMapper::postToPostDtoResponse).toList();
    }

    private void addTagsToPost(PostDto postDto, Post postFromDb) {
        log.info("Adding Tags to the Post");
        if (postDto.tagIds() != null && !postDto.tagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(postDto.tagIds());
            log.info("Found tags in db: {}", tags);
            for (Tag tag : tags)
                postFromDb.getTags().add(tag);
        }
    }

    @Override
    public PostDtoResponse getPostById(Long postId) {
        log.info("Searching for post with id: {}", postId);
        return postRepository.findById(postId)
                .map(postMapper::postToPostDtoResponse)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));
    }

    @Override
    public List<PostDtoResponse> getPostByTag(Long tagId) {
        log.info("Searching Posts by tag with id: {}", tagId);
        return postRepository.findByTags_id(tagId)
                .stream().map(postMapper::postToPostDtoResponse).toList();
    }

    @Override
    public UsersData getUsersData(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));
        return userFeignClient.getUsersData(post.getUserId());
    }

    public boolean isUserOwner(Principal principal, Long postId) {
        return postRepository.findById(postId)
                .map(post -> post.getUserId().equals(principal.getName()))
                .orElse(false);
    }
}
