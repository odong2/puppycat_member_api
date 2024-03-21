package com.architecture.admin.services.auth;

import com.amazonaws.util.IOUtils;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.services.BaseService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

/*****************************************************
 * 애플
 ****************************************************/

@RequiredArgsConstructor
@Service
@Transactional
public class AppleAuthService extends BaseService {


    // 구글 연동해제 api url
    @Value("${auth.apple.tokenUrl}")
    private String tokenUrl;
    @Value("${auth.apple.keyId}")
    private String keyId;
    @Value("${auth.apple.teamId}")
    private String teamId;
    private String expire_revoke_msg = "The code has expired or has been revoked.";
    private String already_used_msg = "The code has already been used.";


    /**
     * authorization_code 이용한 refresh_token 요청
     *
     * @param codeMap
     * @return refresh_token
     */
    public String createRefreshToken(Map<String, String> codeMap) {

        String authorizationCode = codeMap.get("authorizationCode");
        String clientId = codeMap.get("clientId");

        if (ObjectUtils.isEmpty(authorizationCode)) {
            throw new CustomException(CustomError.AUTHORIZATION_CODE_EMPTY); // Authorization 코드가 비었습니다.
        }

        if (ObjectUtils.isEmpty(clientId)) {
            throw new CustomException(CustomError.CLIENTID_CODE_EMPTY); // client Id가비었습니다
        }

        String refreshToken = "";
        // 시크릿 키 생성
        String clientKey = createClientKey(clientId);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientKey);
        formData.add("code", authorizationCode);
        formData.add("grant_type", "authorization_code");

        // 애플에게 리프레시 토큰 요청
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
        String errorDescription = "";

        // 리프레시 토큰 존재
        if (isExistToken == true) {
            refreshToken = data.get("refresh_token").toString();
        }
        // 토큰 없는 경우 (에러)
        else {
            boolean isExistDescription = data.has("error_description");
            // 설명이 있는 경우
            if (isExistDescription == true) {
                errorDescription = data.get("error_description").toString().trim();

                if (errorDescription.equalsIgnoreCase(expire_revoke_msg)) {
                    throw new CustomException(CustomError.AUTHORIZATION_CODE_EXPIRE_REVOKE); // 만료 또는 해지된 Authorization 코드입니다.

                } else if (errorDescription.equalsIgnoreCase(already_used_msg)) {
                    throw new CustomException(CustomError.AUTHORIZATION_CODE_ALREADY_USED); // 이미 사용된 Authorization 코드입니다.

                } else {
                    throw new CustomException(CustomError.REFRESH_TOKEN_CREATE_ERROR); // 리프레시 토큰 생성에 실패하였습니다.
                }
            }
            // 그 외
            else {
                throw new CustomException(CustomError.REFRESH_TOKEN_CREATE_ERROR); // 리프레시 토큰 생성에 실패하였습니다.
            }
        }

        return refreshToken;
    }

    private String createClientKey(String clientId) {

        if (clientId == null || clientId.equals("")) {
            throw new CustomException(CustomError.CLIENTID_CODE_EMPTY); // client Id가비었습니다
        }

        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        // 클라이언트 시크릿 키 생성
        return Jwts.builder()
                .setHeaderParam("kid", keyId)
                .setHeaderParam("alg", "ES256")
                .setIssuer(teamId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate)
                .setAudience("https://appleid.apple.com")
                .setSubject(clientId)
                .signWith(SignatureAlgorithm.ES256, getPrivateKey())
                .compact();
    }

    /**
     * private key 생성
     *
     * @return
     */
    @SneakyThrows
    private PrivateKey getPrivateKey() {

        Resource resource = new ClassPathResource("authkey/AppleAuthKey_5962SWP5LJ.p8"); // resources/authkey에 apple key
        InputStream resourceInputStream = resource.getInputStream();
        String privateKey = new String(IOUtils.toByteArray(resourceInputStream));
        Reader pemReader = new StringReader(privateKey);
        PEMParser pemParser = new PEMParser(pemReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();

        return converter.getPrivateKey(object);
    }

}
