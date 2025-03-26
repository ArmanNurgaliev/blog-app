package ru.arman.postservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.arman.postservice.domain.dto.UsersData;

@FeignClient("user-service")
public interface UserFeignClient {
    @GetMapping("/api/v1/user-service/user/users-data/{userId}")
    UsersData getUsersData(@PathVariable String userId);
}
