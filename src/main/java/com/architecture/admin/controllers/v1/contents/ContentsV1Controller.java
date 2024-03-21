package com.architecture.admin.controllers.v1.contents;

import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.models.dto.member.MemberInfoDto;
import com.architecture.admin.models.dto.tag.MentionTagDto;
import com.architecture.admin.services.contents.ContentsService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/contents")
public class ContentsV1Controller extends BaseController {

    private final ContentsService contentsService;

    /**
     * 멘션 시 uuid 조회 by nick
     *
     * @param nickList nick List
     * @return
     */
    @PostMapping("/mention/uuid")
    public ResponseEntity getUuidByNick(@RequestParam(name = "nickList") List<String> nickList) {

        List<MemberDto> uuidList = contentsService.getUuidByNick(nickList);

        String sMessage = super.langMessage("lang.common.success.search");

        JSONObject data = new JSONObject();
        data.put("list", uuidList);

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 멘션된 회원 정보 리스트
     * [텍스트 500자 이내로 멘션 가능하므로 get 요청 길이 제한으로 인해 post 요청]
     *
     * @param memberUuidList : 회원 uuid 리스트
     * @return : memberUuid, nick, outMemberUuid, outNick
     */
    @PostMapping("/mention/member/info")
    public ResponseEntity<String> getMentionInfoList(@RequestBody List<String> memberUuidList) {
        List<MentionTagDto> mentionInfoList = contentsService.getMentionInfoList(memberUuidList);

        String sMessage = super.langMessage("lang.common.success.search");

        JSONObject data = new JSONObject();
        data.put("list", mentionInfoList);

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 이미지 내 태그된 회원 정보 리스트
     *
     * @param memberUuidList
     * @return : uuid, nick, profileImgUrl, intro
     */
    @PostMapping("/img/tag/member/info")
    public ResponseEntity<String> getImgMemberTagInfoList(@RequestBody List<String> memberUuidList) {
        List<MemberDto> imgMemberTagInfoList = contentsService.getImgMemberTagInfoList(memberUuidList);

        String sMessage = super.langMessage("lang.common.success.search");

        JSONObject data = new JSONObject();
        data.put("list", imgMemberTagInfoList);

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 컨텐츠 작성자 정보
     *
     * @param memberUuid
     * @return
     */
    @GetMapping("/writer/info")
    public MemberInfoDto getWriterInfo(String memberUuid) {

        MemberInfoDto writerInfo = contentsService.getWriterInfoByUuid(memberUuid);

        String sMessage = super.langMessage("lang.common.success.search");

        writerInfo.setCode("1000");
        writerInfo.setResult(true);
        writerInfo.setSMessage(sMessage);

        return writerInfo;
    }

    /**
     * 컨텐츠 작성자 리스트 정보
     *
     * @param memberUuidList
     * @return
     */
    @PostMapping("/writer/info/list")
    public ResponseEntity<String> getWriterInfoList(@RequestBody List<String> memberUuidList) {


        List<MemberInfoDto> writerInfoList = contentsService.getWriterInfoList(memberUuidList);

        String sMessage = super.langMessage("lang.common.success.search");

        JSONObject data = new JSONObject();
        data.put("list", writerInfoList);

        return displayJson(true, "1000", sMessage, data);
    }
}
