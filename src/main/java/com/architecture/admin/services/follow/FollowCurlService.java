package com.architecture.admin.services.follow;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "followCurlService", url = "${sns.domain}/v1/follow")
public interface FollowCurlService {

    /*****************************************************
     *  SubFunction - select
     ****************************************************/
    /**
     * 팔로우 여부 조회
     *
     * @param memberUuid
     * @param followUuid
     * @return
     */
    @GetMapping("/check")
    String getFollowCheck(@RequestParam(name = "memberUuid") String memberUuid,
                          @RequestParam(name = "followUuid") String followUuid);
}
