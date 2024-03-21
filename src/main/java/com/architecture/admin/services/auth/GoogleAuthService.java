package com.architecture.admin.services.auth;

import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.services.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;


/*****************************************************
 * 구글 로그인
 ****************************************************/
@RequiredArgsConstructor
@Service
@Transactional
public class GoogleAuthService extends BaseService {

    @Value("${auth.google.clientId}")
    private String googleClientId;
    @Value("${auth.google.callbackUrl}")
    private String googleCallbackUrl;
    @Value("${auth.google.tokenUrl}")
    private String tokenUrl;
    @Value("${auth.google.clientId}")
    private String clientId;
    @Value("${auth.google.clientSecret}")
    private String clientSecret;
    @Value("${auth.google.callbackUrl}")
    private String redirectUrl;


    /*****************************************************
     *  Function
     ***************************************************/

    /**
     * 구글 refresh_token 생성
     *
     * @param codeMap
     * @return
     */
    @SneakyThrows
    public String createRefreshToken(Map<String, String> codeMap) {

        String refreshToken = "";

        String authorizationCode = codeMap.get("authorizationCode");

        if (ObjectUtils.isEmpty(authorizationCode)) {
            throw new CustomException(CustomError.AUTHORIZATION_CODE_EMPTY);
        }

        authorizationCode = URLDecoder.decode(authorizationCode, StandardCharsets.UTF_8);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("code", authorizationCode);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", redirectUrl); // redirect_uri

        super.pushAlarm(String.valueOf(formData), "KTH");

        // 구글에게 리프레시 토큰 요청
        String responseData = WebClient.create()
                .post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .exchange()
                .block()
                .bodyToMono(String.class)
                .block();

        JSONObject data = new JSONObject(responseData);

        boolean isExistToken = data.has("refresh_token");

        super.pushAlarm(String.valueOf(data), "KTH");

        // 리프레시 토큰 존재
        if (isExistToken == true) {
            refreshToken = data.get("refresh_token").toString();
        } else {
            throw new CustomException(CustomError.REFRESH_TOKEN_CREATE_ERROR);
        }

        return refreshToken;
    }

}
