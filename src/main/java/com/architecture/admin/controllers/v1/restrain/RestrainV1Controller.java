package com.architecture.admin.controllers.v1.restrain;


import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dto.restrain.RestrainDto;
import com.architecture.admin.services.restrain.RestrainService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/restrain")
public class RestrainV1Controller extends BaseController {

    private final RestrainService restrainService;

    /**
     * 회원 제재 상세
     *
     * @param
     * @return 검색 성공
     */
    @GetMapping("")
    public ResponseEntity loginLists(@RequestHeader(value = "Authorization") String token,
                                     @RequestParam("type") Integer type) {
        // 토큰에서 회원 UUID 가져오기
        String memberUuid = super.getAccessMemberUuid(token);

        if (type == null || type < 0) {
            // 제재 타입이 비어있습니다.
            throw new CustomException(CustomError.RESTRAIN_TYPE_EMPTY);
        }

        RestrainDto restrainDto = new RestrainDto();
        // 회원 uuid 세팅
        restrainDto.setMemberUuid(memberUuid);
        // 제재 타입 세팅
        restrainDto.setType(type);

        // 제재 내역
        RestrainDto restrainInfo = restrainService.getInfoRestrain(restrainDto);

        Map<String, Object> map = new HashMap<>();
        map.put("restrain", restrainInfo);
        String sMessage;

        if (restrainInfo != null) {
            // 제재 내역 조회 성공
            sMessage = super.langMessage("lang.restrain.success");
        } else {
            // 제재 내역이 없습니다.
            sMessage = super.langMessage("lang.restrain.exception.empty");
        }

        JSONObject data = new JSONObject(map);
        return displayJson(true, "1000", sMessage, data);
    }

    @GetMapping("check")
    public ResponseEntity getRestrainCheck(@RequestHeader(value = "Authorization") String token,
                                           @RequestParam("restrainType") Integer restrainType) {

        // 토큰에서 회원 UUID 가져오기
        String memberUuid = super.getAccessMemberUuid(token);

        restrainService.getRestrainCheck(memberUuid, restrainType);

        JSONObject data = new JSONObject();

        // 제재 내역이 없습니다
        String message = super.langMessage("lang.member.exception.restrain.empty");
        return displayJson(true, "1000", message, data);
    }
}
