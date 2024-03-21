package com.architecture.admin.services.login;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "joinCurlService", url = "${sns.domain}/v1/join")
public interface JoinCurlService {

    /**
     * 소셜 회원가입 처리
     *
     * @param memberUuid
     * @return
     */
    @PostMapping("/social")
    String socialMemberJoin(@RequestBody String memberUuid);
}
