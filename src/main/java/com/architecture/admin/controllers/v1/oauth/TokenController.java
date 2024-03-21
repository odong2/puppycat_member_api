package com.architecture.admin.controllers.v1.oauth;

import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.libraries.jwt.JwtLibrary;
import com.architecture.admin.models.dto.jwt.JwtDto;
import com.architecture.admin.services.auth.AppleAuthService;
import com.architecture.admin.services.auth.GoogleAuthService;
import com.architecture.admin.services.oauth.OauthService;
import com.architecture.admin.services.restrain.RestrainService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/oauth")
public class TokenController extends BaseController {
    private final JwtLibrary jwtLibrary;
    private final OauthService oauthService;
    private final RestrainService restrainService;
    private final AppleAuthService appleAuthService;
    private final GoogleAuthService googleAuthService;
    @Value("${jwt.token.refresh.secret.key}")
    private String refreshType;

    @PostMapping("/token")
    public ResponseEntity oauthRefreshToken(@RequestBody JwtDto jwtDto, HttpServletRequest httpRequest) {

        // refresh 검증
        Integer iToken = jwtLibrary.validateRefreshToken(jwtDto);

        // 토큰이 정상이 아니라면 에러 발생
        if (iToken != 1) {
            throw new CustomException(CustomError.TOKEN_ERROR);
        }

        JwtDto getTokenInfo = oauthService.setRefreshToken(jwtDto, httpRequest);

        // 제재 리스트 가져오기
        List<Integer> restrainList = restrainService.getRestrainList(getTokenInfo.getMemberUuid());
        restrainList = restrainList.stream().distinct().toList();

        JSONObject data = new JSONObject();
        data.put("accessToken", getTokenInfo.getAccessToken());
        data.put("refreshToken", getTokenInfo.getRefreshToken());
        data.put("restrainList", restrainList);

        String message = super.langMessage("lang.token.publish.success");

        return displayJson(true, "1000", message, data);
    }

    @PostMapping("/check/token")
    public ResponseEntity oauthRefreshTokenCheck(@RequestBody JwtDto jwtDto) {
        // refresh 검증
        Integer iToken = jwtLibrary.validateRefreshToken(jwtDto);

        // 토큰이 정상이 아니라면 에러 발생
        if (iToken != 1) {
            throw new CustomException(CustomError.TOKEN_ERROR);
        }

        JSONObject data = new JSONObject();

        String message = super.langMessage("lang.token.health.Ok");

        return displayJson(true, "1000", message, data);
    }

    /**
     * 애플 refresh_token 생성
     *
     * @param codeMap : authorization_code
     * @return
     */
    @PostMapping("/apple/refresh/token")
    public ResponseEntity<String> createAppleRefreshToken(@RequestBody Map<String, String> codeMap) {

        // 리프레시 토큰 생성
        String refreshToken = appleAuthService.createRefreshToken(codeMap);

        JSONObject data = new JSONObject();
        data.put("refreshToken", refreshToken);

        String message = super.langMessage("lang.token.publish.success");

        return displayJson(true, "1000", message, data);
    }

    /**
     * 구글 refresh_token 생성
     *
     * @param codeMap
     * @return
     */
    @PostMapping("/google/refresh/token")
    public ResponseEntity<String> createGoogleRefreshToken(@RequestBody Map<String, String> codeMap) {

        // 리프레시 토큰 생성
        String refreshToken = googleAuthService.createRefreshToken(codeMap);

        JSONObject data = new JSONObject();
        data.put("refreshToken", refreshToken);

        String message = super.langMessage("lang.token.publish.success");

        return displayJson(true, "1000", message, data);
    }
}
