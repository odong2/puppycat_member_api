package com.architecture.admin.controllers.v1.login;

import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.services.login.LogoutService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/logout")
public class LogoutV1Controller extends BaseController {
    private final LogoutService logoutService;

    /**
     * 로그아웃
     *
     * @param token memberUuid, appKey
     * @return 로그아웃 되었습니다.
     */
    @PostMapping("")
    public ResponseEntity logout(@RequestHeader(value = "Authorization") String token) {

        // 토큰에서 회원 AppKey 가져오기
        String appKey = super.getAccessAppKey(token);
        // 토큰에서 회원 UUID 가져오기
        String memberUuid = super.getAccessMemberUuid(token);

        // set MemberIdx
        MemberDto memberDto = new MemberDto();
        memberDto.setAppKey(appKey);
        memberDto.setMemberUuid(memberUuid);

        // 회원 로그아웃 처리
        Boolean bIsLogout = logoutService.logout(memberDto);

        if (Boolean.FALSE.equals(bIsLogout)) {
            throw new CustomException(CustomError.LOGOUT_FAIL);
        }

        // set return data
        JSONObject data = new JSONObject();
        data.put("location", "/");

        // return value
        String sErrorMessage = "lang.login.success.logout";
        String message = super.langMessage(sErrorMessage);
        return displayJson(true, "1000", message, data);
    }
}
