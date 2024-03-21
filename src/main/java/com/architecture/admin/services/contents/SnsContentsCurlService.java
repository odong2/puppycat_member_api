package com.architecture.admin.services.contents;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "snsContentsCurlService", url = "${sns.domain}/v1/contents")
public interface SnsContentsCurlService {

    /**
     * 컨텐츠 좋아요 조회
     *
     * @param memberUuid 회원 UUID
     * @return
     */
    @GetMapping("/like/check")
    String getContentsLike(@RequestParam(name = "memberUuid") String memberUuid,
                           @RequestParam(name = "contentsIdx") Long contentsIdx);
}
