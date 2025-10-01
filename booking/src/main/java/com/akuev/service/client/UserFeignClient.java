package com.akuev.service.client;

import com.akuev.config.FeignConfig;
import com.akuev.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;

@FeignClient(value = "userservice", configuration = FeignConfig.class)
public interface UserFeignClient {
    @GetMapping("/api/v1/users/{id}")
    Optional<UserDTO> findUserById(@PathVariable("id") UUID id);
}
