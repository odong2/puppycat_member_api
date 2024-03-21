package com.architecture.admin.controllers.v1.login;

import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.models.dto.jwt.JwtDto;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.services.login.JoinService;
import com.architecture.admin.services.login.LoginService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/login")
public class LoginV1Controller extends BaseController {
    private final LoginService loginService;
    private final JoinService joinService;

    /**
     * 소셜 로그인
     *
     * @param memberDto
     * @param result
     * @param httpRequest
     * @return
     * @throws Exception
     */
    @PostMapping("/social")
    public ResponseEntity socialAuth(@RequestBody MemberDto memberDto,
                                     BindingResult result,
                                     HttpServletRequest httpRequest) throws Exception {

        if (result.hasErrors()) {
            return super.displayError(result);
        }

        // 회원 가입 된 아이디인지 체크
        Boolean bDupleId = joinService.checkDupleId(memberDto);

        // 가입되어있지 않으면
        if (Boolean.FALSE.equals(bDupleId)) {
            String sErrorMessage = "lang.login.exception.join.redirect";
            String message = super.langMessage(sErrorMessage);
            return super.redirectDisplayJson(false, "EJOI-7777", message);
        }

        // 로그인처리
        JwtDto oTokenInfo = loginService.login(memberDto, httpRequest);

        // set return data
        JSONObject data = new JSONObject();
        data.put("refreshToken", oTokenInfo.getRefreshToken());
        data.put("accessToken", oTokenInfo.getAccessToken());
        data.put("restrainList", oTokenInfo.getRestrainList());

        // return value
        String sErrorMessage = "lang.login.success.login";
        String message = super.langMessage(sErrorMessage);
        return displayJson(true, "1000", message, data);
    }
}