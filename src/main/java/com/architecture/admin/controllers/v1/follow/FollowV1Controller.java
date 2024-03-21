package com.architecture.admin.controllers.v1.follow;

import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.services.follow.FollowService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/follow")
public class FollowV1Controller extends BaseController {

    private final FollowService followService;


    /**
     * 검색시 팔로우 카운트
     *
     * @param searchDto searchType,searchWord
     * @return cnt
     */
    @PostMapping("/search/cnt")
    public ResponseEntity getFollowSearchCnt(@RequestBody SearchDto searchDto) {

        Long followSearchCnt = followService.getFollowSearchCnt(searchDto);

        String sMessage = super.langMessage("lang.common.success.search");

        JSONObject data = new JSONObject();
        data.put("followSearchCnt", followSearchCnt);
        return displayJson(true, "1000", sMessage, data);
    }

}
