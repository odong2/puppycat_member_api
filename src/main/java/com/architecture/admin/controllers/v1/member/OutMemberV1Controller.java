package com.architecture.admin.controllers.v1.member;

import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.models.dto.member.OutMemberDto;
import com.architecture.admin.services.member.OutMemberService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/member/out")
public class OutMemberV1Controller extends BaseController {

    private final OutMemberService outMemberService;

    /**
     * 탈퇴 사유 가져오기
     *
     * @return 탈퇴사유 list
     */
    @GetMapping("code")
    public ResponseEntity getOutCodeList(){
        // list
        List<OutMemberDto> list = outMemberService.getOutCodeList();

        Map<String, Object> map = new HashMap<>();
        map.put("list", list);

        JSONObject data = new JSONObject(map);
        String sMessage = super.langMessage("lang.member.setting.success.list");

        // return value
        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 회원 탈퇴
     *
     * @param outMemberDto memberUuid, code, reason
     * @return 처리결과
     */
    @PutMapping("")
    public ResponseEntity outMember(@RequestHeader(value = "Authorization") String token, @RequestBody OutMemberDto outMemberDto) {

        // set MemberUuid
        String memberUuid = super.getAccessMemberUuid(token);
        outMemberDto.setUuid(memberUuid);

        // 회원 탈퇴
        outMemberService.outMember(outMemberDto);

        // set return data
        JSONObject data = new JSONObject();
        data.put("location", "/");

        // return value
        String message = super.langMessage("lang.member.success.out");
        return displayJson(true, "1000", message, data);
    }

}
