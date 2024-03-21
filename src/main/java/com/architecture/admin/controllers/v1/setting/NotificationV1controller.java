package com.architecture.admin.controllers.v1.setting;

import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.models.dto.setting.NotificationSettingDto;
import com.architecture.admin.services.setting.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/notification")
public class NotificationV1controller extends BaseController {

    private final NotificationSettingService notificationSettingService;

    /**
     * 설정 알림 목록
     *
     * @return 설정 알림 리스트
     */
    @GetMapping("")
    public ResponseEntity lists(@RequestHeader(value = "Authorization") String token) {
        
        // 토큰에서 회원 UUID 가져오기
        String memberUuid = super.getAccessMemberUuid(token);

        MemberDto memberDto = new MemberDto();
        memberDto.setMemberUuid(memberUuid);

        List<NotificationSettingDto> mainList = notificationSettingService.getMainList(memberDto);
        List<NotificationSettingDto> subList = notificationSettingService.getSubList(memberDto);

        String sMessage = "";
        Map<String, Object> map = new HashMap<>();
        map.put("mainList", mainList);
        map.put("subList", subList);

        sMessage = super.langMessage("lang.member.setting.success.list"); // 리스트 가져오기 성공

        JSONObject data = new JSONObject(map);
        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 설정 알림 Update
     *
     * @return 설정 알림 업데이트
     */
    @PutMapping("")
    public ResponseEntity updateSetting(@RequestHeader(value = "Authorization") String token,
                                        @RequestBody Map<String, Object> params) {

        // 토큰에서 회원 UUID 가져오기
        String memberUuid = super.getAccessMemberUuid(token);

        // data set
        params.put("memberUuid", memberUuid);

        // 상태 값 변경
        notificationSettingService.updateSetting(params);

        String sMessage = "";
        sMessage = super.langMessage("lang.noti.success.update"); // 업데이트 성공

        return displayJson(true, "1000", sMessage);
    }
}
