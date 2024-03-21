package com.architecture.admin.controllers.v1.member;

import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.models.dto.member.MemberNickDto;
import com.architecture.admin.services.member.MemberNickService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/member/nick")
public class MemberNickV1Controller extends BaseController {

    private final MemberNickService memberNickService;

    /**
     * 닉네임 사용여부 체크
     *
     * @param memberNickDto
     * @return
     */
    @PostMapping("/check")
    public ResponseEntity checkNick(@RequestBody MemberNickDto memberNickDto) {

        // 닉네임 공백 제거 및 소문자 변환
        memberNickDto.setNick(memberNickDto.getNick().trim());

        // 닉네임 사용 가능 여부 체크
        Boolean bIsCheckNick = memberNickService.checkNick(memberNickDto);

        // 체크 실패시
        if (Boolean.FALSE.equals(bIsCheckNick)) {
            throw new CustomException(CustomError.NICK_CHECK_FAIL);
        }

        // set return data
        JSONObject data = new JSONObject();

        // return value
        String sErrorMessage = "lang.member.success.nick";
        String message = super.langMessage(sErrorMessage);
        return displayJson(true, "1000", message, data);
    }

    @GetMapping("")
    public MemberDto getNickByUuid(String uuid) {

        String memberNick = memberNickService.getNickByUuid(uuid);

        MemberDto memberDto = new MemberDto();
        memberDto.setNick(memberNick);
        memberDto.setResult(true);
        memberDto.setCode("1000");

        return memberDto;
    }
}
