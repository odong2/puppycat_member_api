package com.architecture.admin.controllers.v1.member;

import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.models.dto.member.MemberInfoDto;
import com.architecture.admin.services.member.MemberInfoService;
import com.architecture.admin.services.member.MemberService;
import com.architecture.admin.services.restrain.RestrainService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/member")
public class MemberV1Controller extends BaseController {

    private final MemberService memberService;
    private final MemberInfoService memberInfoService;
    private final RestrainService restrainService;

    /**
     * 탈퇴회원 7일이내 진입시 복구
     *
     * @param memberDto simpleId
     * @return 처리 결과
     * @throws ParseException 날짜 비교위해 String -> Date 타입으로 변환시
     */
    @PatchMapping("restore")
    public ResponseEntity restoreMember(@RequestBody MemberDto memberDto) throws ParseException {

        // 탈퇴회원 복구
        memberService.restore(memberDto);

        String message = super.langMessage("lang.member.success.restore");
        return displayJson(true, "1000", message);
    }

    /**
     * 회원 정보 ORDER BY NICK
     *
     * @param searchDto
     * @return
     */
    @PostMapping("/info/nick")
    public ResponseEntity getMemberInfoOrderByNick(@RequestBody SearchDto searchDto) {

        List<MemberInfoDto> memberInfo = memberInfoService.getMemberInfoOrderByNick(searchDto);

        String sMessage = super.langMessage("lang.common.success.search");

        JSONObject data = new JSONObject();
        data.put("memberInfo", memberInfo);

        return displayJson(true, "1000", sMessage, data);
    }


    /**
     * 회원 UUID 조회
     *
     * @param token
     * @return
     */
    @GetMapping("/uuid")
    public MemberDto getUuid(@RequestHeader(value = "Authorization") String token) {

        // 토큰에서 회원 UUID 가져오기
        String memberUuid = super.getAccessMemberUuid(token);

        if (ObjectUtils.isEmpty(memberUuid)) {
            throw new CustomException(CustomError.TOKEN_EXPIRE_TIME_ERROR);
        }

        // 회원이 실제 존재 하는지 체크
        MemberDto memberDto = new MemberDto();
        memberDto.setMemberUuid(memberUuid);

        // 회원 가입 하세요
        if (!memberService.getCountByUuid(memberDto)) {
            throw new CustomException(CustomError.MEMBER_OUT_COMPLETE_STATE);
        }

        // 제재 상태인지 체크
        restrainService.getRestrainCheck(memberUuid, 1);

        memberDto.setUuid(memberUuid);
        memberDto.setResult(true);
        memberDto.setCode("1000");

        return memberDto;
    }

    /**
     * 회원 uuid 유효한지 체크
     *
     * @param uuid
     * @return
     */
    @GetMapping("/uuid/check")
    public ResponseEntity<String> checkMemberUuid(String uuid) {
        MemberDto memberDto = MemberDto.builder()
                .memberUuid(uuid).build();

        Boolean isExist = memberService.getCountByUuid(memberDto);
        String sMessage = super.langMessage("lang.common.success.search");

        JSONObject data = new JSONObject();
        data.put("isExist", isExist);

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 회원 uuid 리스트 유효한지 체크
     *
     * @param uuidList
     * @return
     */
    @GetMapping("/uuid/list/check")
    public ResponseEntity<String> checkMemberUuidList(@RequestParam(name = "uuidList") List<String> uuidList) {

        boolean result = memberService.checkMemberUuidList(uuidList);

        String sMessage = super.langMessage("lang.common.success.search");

        JSONObject data = new JSONObject();
        data.put("isExist", result);

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 회원 검색
     *
     * @param searchWord 검색어
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity getMemberUuidBySearchDto(@RequestParam(name = "searchWord") String searchWord) {

        List<String> searchMemberUuidList = memberService.getMemberUuidBySearch(searchWord);

        String sMessage = super.langMessage("lang.common.success.search");

        JSONObject data = new JSONObject();
        data.put("searchMemberUuidList", searchMemberUuidList);

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 회원 정보 가져오기 by memberUuid
     *
     * @param uuidList
     * @return
     */
    @GetMapping("/info/list")
    public ResponseEntity getMemberInfoByMemberUuid(@RequestParam(name = "uuidList") List<String> uuidList) {

        List<MemberInfoDto> memberInfoList = memberService.getMemberInfoByUuidList(uuidList);

        String sMessage = super.langMessage("lang.common.success.search");

        JSONObject data = new JSONObject();
        data.put("memberInfoList", memberInfoList);

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 회원 닉네임 가져오기 by uuid
     *
     * @param uuid
     * @return
     */
    @GetMapping("/nick/{uuid}")
    public MemberDto getMemberNickByMemberUuid(@PathVariable(name = "uuid") String uuid) {

        String nick = memberService.getMemberNickByUuid(uuid);

        return MemberDto.builder()
                .nick(nick)
                .result(true)
                .code("1000")
                .build();
    }

    /**
     * 회원 정보 가져오기 by memberUuid
     *
     * @return
     */
    @GetMapping("/info/{memberUuid}")
    public MemberInfoDto getMemberInfoByUuid(@PathVariable("memberUuid") String memberUuid) {

        MemberDto memberDto = new MemberDto();
        memberDto.setUuid(memberUuid);

        return memberInfoService.getMemberInfoByUuid(memberDto);
    }

    /**
     * 회원 가입일 조회
     *
     * @param uuid
     * @return
     */
    @GetMapping("/regdate")
    public MemberDto getMemberRegdate(String uuid) {

        MemberDto memberDto = memberService.getMemberRegdate(uuid);
        String sMessage = super.langMessage("lang.common.success.search");

        memberDto.setResult(true);
        memberDto.setCode("1000");
        memberDto.setSMessage(sMessage);

        return memberDto;
    }
}
