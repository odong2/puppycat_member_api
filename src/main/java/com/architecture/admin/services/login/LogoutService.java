package com.architecture.admin.services.login;

import com.architecture.admin.models.dao.access.AccessDao;
import com.architecture.admin.models.dao.jwt.JwtDao;
import com.architecture.admin.models.daosub.access.AccessDaoSub;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.services.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
@RequiredArgsConstructor
@Service
public class LogoutService extends BaseService {

    private final AccessDao accessDao;
    private final AccessDaoSub accessDaoSub;
    private final JwtDao jwtDao;

    /**
     * 로그아웃
     *
     * @param memberDto appKey
     * @return true/false
     */
    @Transactional
    public Boolean logout(MemberDto memberDto) {

        // app_key 유효성 검사
        MemberDto appInfo = accessDaoSub.getAppInfo(memberDto);
        if (appInfo != null) {
            // fcm_token 빈 값 & state 0 업데이트
            memberDto.setModiDate(dateLibrary.getDatetime());
            accessDao.appStateUpdate(memberDto);
        }

        HashMap<String, Object> tokenMap = new HashMap<>(); // 토큰 업데이트용

        tokenMap.put("memberUuid", memberDto.getMemberUuid());
        tokenMap.put("appKey", memberDto.getAppKey());

        // jwt refresh 토큰 빈 값 업데이트
        Integer iResult = jwtDao.deleteRefreshToken(tokenMap);

        return iResult > 0;
    }
}
