package com.architecture.admin.services.member;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "snsMemberCurlService", url = "${sns.domain}/v1/member")
public interface SnsMemberCurlService {

    /**
     * 회원 badge 조회
     *
     * @param memberUuid 회원 UUID
     * @return
     */
    @GetMapping("/badge")
    String getMemberBadge(@RequestParam(name = "memberUuid") String memberUuid);
}
