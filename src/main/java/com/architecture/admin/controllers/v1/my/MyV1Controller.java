package com.architecture.admin.controllers.v1.my;

import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.models.dto.member.MemberInfoDto;
import com.architecture.admin.services.member.MemberInfoService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/my")
public class MyV1Controller extends BaseController {

    private final MemberInfoService memberInfoService;

    /**
     * 내 정보
     *
     * @return ResponseEntity
     */
    @GetMapping("/info")
    public ResponseEntity getMyInfoList(@RequestHeader(value = "Authorization") String token) {

        // 토큰에서 회원 UUID 가져오기
        String memberUuid = super.getAccessMemberUuid(token);

        // set MemberUuid
        MemberInfoDto memberInfoDto = new MemberInfoDto();
        memberInfoDto.setMemberUuid(memberUuid);
        String sMessage = "";

        // list
        MemberInfoDto memberInfo = memberInfoService.getMyInfo(memberInfoDto);

        // Dto
        if (memberInfo == null) {
            sMessage = super.langMessage("lang.member.info.list.empty"); // 정보 리스트가 없습니다.
        } else {
            sMessage = super.langMessage("lang.member.info.success.list"); // 리스트 가져오기 성공
        }

        // data set
        Map<String, Object> mMap = new HashMap<>();
        mMap.put("info", memberInfo);

        JSONObject oJsonData = new JSONObject(mMap);

        return displayJson(true, "1000", sMessage, oJsonData);
    }

    /**
     * 내 정보 update
     *
     * @param memberInfoDto memberUuid, uploadFile, intro, nick, name, phone, gender, birth, ci, di
     * @return ResponseEntity
     */
    @PutMapping("/info")
    public ResponseEntity modifyMyInfo(@RequestHeader(value = "Authorization") String token,
                                       @ModelAttribute MemberInfoDto memberInfoDto) throws Exception {

        // 토큰에서 회원 UUID 가져오기
        String memberUuid = super.getAccessMemberUuid(token);

        // set MemberUuid
        memberInfoDto.setMemberUuid(memberUuid);

        // 회원 정보 관련 업데이트
        memberInfoService.updateMyInfo(memberInfoDto);

        String sMessage = super.langMessage("lang.member.info.success.update");
        return displayJson(true, "1000", sMessage);
    }
}
