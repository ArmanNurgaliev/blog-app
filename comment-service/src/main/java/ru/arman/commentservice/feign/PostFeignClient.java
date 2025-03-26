package ru.arman.commentservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.arman.commentservice.domain.dto.UsersData;

@FeignClient("post-service")
public interface PostFeignClient {
    @GetMapping("/api/v1/post-service/users-data/{postId}")
    UsersData getUsersNameAndEmail(@PathVariable Long postId);
}
