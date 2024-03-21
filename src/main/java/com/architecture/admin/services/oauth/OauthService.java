package com.architecture.admin.services.oauth;

import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.libraries.jwt.JwtLibrary;
import com.architecture.admin.models.dao.jwt.JwtDao;
import com.architecture.admin.models.daosub.jwt.JwtDaoSub;
import com.architecture.admin.models.dto.jwt.JwtDto;
import com.architecture.admin.services.BaseService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/*****************************************************
 * 이용약관 모델러
 ****************************************************/
@RequiredArgsConstructor
@Service
@Transactional
public class OauthService extends BaseService {
    private final JwtLibrary jwtLibrary;
    private final JwtDaoSub jwtDaoSub;
    private final JwtDao jwtDao;

    @Value("${jwt.token.refresh.secret.key}")
    private String refreshType;

    /*****************************************************
     *  SubFunction - Select
     ****************************************************/
    public JwtDto setRefreshToken(JwtDto jwtDto, HttpServletRequest httpRequest) {
        // map 생성
        HashMap<String, Object> tokenMap = new HashMap<>();

        // 토큰이 정상이라면 데이터 사용
        Claims refreshClaim = jwtLibrary.getAllClaims(refreshType, jwtDto.getRefreshToken());
        String newAccessToken = jwtLibrary.recreationAccessToken(refreshClaim.get("tokenMap"));
        // 토큰에서 회원 UUID 가져오기
        String memberUuid = super.getAccessMemberUuid(newAccessToken);
        String memberId = super.getAccessId(newAccessToken);
        String appKey = super.getAccessAppKey(newAccessToken);
        String newRefreshToken = jwtLibrary.recreationRefreshToken(refreshClaim.get("tokenMap"));

        // map put
        tokenMap.put("refreshToken", jwtDto.getRefreshToken());
        tokenMap.put("ip", getClientIP(httpRequest)); // ip 가져오기
        tokenMap.put("id", memberId);
        tokenMap.put("appKey", appKey);
        tokenMap.put("memberUuid", memberUuid);
        tokenMap.put("regDate", dateLibrary.getDatetime());

        // 해당 토큰이 존재 하는지 체크
        Integer check = jwtDaoSub.checkRefreshTokenByToken(tokenMap);

        // 해당 토큰이 없다면 에러
        if (check == 0) {
            throw new CustomException(CustomError.TOKEN_ERROR);
        }

        // 새로운 토큰 put 처리
        tokenMap.put("refreshToken", newRefreshToken);

        // refreshToken update
        Integer iResult = jwtDao.updateRefreshToken(tokenMap);

        if (iResult < 1) {
            throw new CustomException(CustomError.TOKEN_ERROR);
        }

        JwtDto returnJwtDto = new JwtDto();
        returnJwtDto.setMemberUuid(memberUuid);
        returnJwtDto.setAccessToken(newAccessToken);
        returnJwtDto.setRefreshToken(newRefreshToken);

        return returnJwtDto;
    }

    /*****************************************************
     *  SubFunction - Insert
     ****************************************************/

}