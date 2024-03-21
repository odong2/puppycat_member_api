package com.architecture.admin.controllers.v1.push;

import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.models.dto.push.PushDto;
import com.architecture.admin.services.push.PushService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/push")
public class PushV1Controller extends BaseController {

    private final PushService pushService;

    /**
     * 회원 FCM 토큰 리스트
     *
     * @param receiverUuid
     * @param typeIdx
     * @return
     */
    @GetMapping("token")
    public List<String> getPushTokenList(@RequestParam(value = "receiverUuid", required = false, defaultValue = "") String receiverUuid,
                                         @RequestParam(value = "typeIdx", required = false, defaultValue = "") Integer typeIdx) {
        PushDto pushDto = new PushDto();
        pushDto.setReceiverUuid(receiverUuid);
        pushDto.setTypeIdx(typeIdx);
        return pushService.getPushTokenList(pushDto);
    }

    /**
     * 회원 FCM 토큰 리스트 - jwt 토큰 없이 조회 [채팅 푸쉬용]
     *
     * @param receiverUuid
     * @param typeIdx
     * @return
     */
    @GetMapping("/none/token")
    public List<String> getNotJwtTokenPushTokenList(@RequestParam(value = "receiverUuid", required = false, defaultValue = "") String receiverUuid,
                                         @RequestParam(value = "typeIdx", required = false, defaultValue = "") Integer typeIdx) {
        PushDto pushDto = new PushDto();
        pushDto.setReceiverUuid(receiverUuid);
        pushDto.setTypeIdx(typeIdx);
        return pushService.getPushTokenList(pushDto);
    }
}
