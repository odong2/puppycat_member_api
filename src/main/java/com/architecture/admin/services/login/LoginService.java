package com.architecture.admin.services.login;

import com.architecture.admin.libraries.ServerLibrary;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.libraries.jwt.JwtLibrary;
import com.architecture.admin.models.dao.jwt.JwtDao;
import com.architecture.admin.models.dao.member.MemberDao;
import com.architecture.admin.models.daosub.jwt.JwtDaoSub;
import com.architecture.admin.models.dto.jwt.JwtDto;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.member.LoginMemberService;
import com.architecture.admin.services.restrain.RestrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/*****************************************************
 * 로그인 모델러
 ****************************************************/
@RequiredArgsConstructor
@Service
@Transactional
public class LoginService extends BaseService {
    private final MemberDao memberDao;
    private final AccessService accessService;              // access/app
    private final LoginMemberService loginMemberService;
    private final JwtLibrary jwtLibrary;
    private final JwtDao jwtDao;
    private final JwtDaoSub jwtDaoSub;
    private final RestrainService restrainService;


    /*****************************************************
     *  Modules
     ****************************************************/
    /**
     * 일반 로그인
     *
     * @param memberDto   id, password, appKey, appVer, domain, iso, fcmToken
     * @param httpRequest
     * @return
     * @throws Exception
     */
    public JwtDto login(MemberDto memberDto,
                        HttpServletRequest httpRequest) throws Exception {
        // 아이디/패스워드 검증
        String id = memberDto.getId();
        String password = memberDto.getSimpleId();

        if (id == null || id.equals("")) {
            throw new CustomException(CustomError.LOGIN_ID_ERROR);
        }

        if (password == null || password.equals("")) {
            throw new CustomException(CustomError.LOGIN_PW_ERROR);
        }

        if (memberDto.getIsSimple() == 1 && (memberDto.getIsSimple() == null || memberDto.getIsSimple().equals(""))) { // 간편가입 타입 확인
            throw new CustomException(CustomError.LOGIN_IS_SIMPLE_ERROR);
        }

        if (memberDto.getSimpleType() == null || memberDto.getSimpleType().equals("")) {
            throw new CustomException(CustomError.LOGIN_SIMPLE_TYPE_ERROR);
        }

        if (memberDto.getAppKey() == null || memberDto.getAppKey().equals("")) {
            throw new CustomException(CustomError.LOGIN_APP_KEY_ERROR);
        }

        if (memberDto.getAppVer() == null || memberDto.getAppVer().equals("")) {
            throw new CustomException(CustomError.LOGIN_APP_VER_ERROR);
        }

        if (memberDto.getDomain() == null || memberDto.getDomain().equals("")) {
            throw new CustomException(CustomError.LOGIN_DOMAIN_ERROR);
        }

        if (memberDto.getIso() == null || memberDto.getIso().equals("")) {
            throw new CustomException(CustomError.LOGIN_ISO_ERROR);
        }

        if (memberDto.getFcmToken() == null || memberDto.getFcmToken().equals("")) {
            throw new CustomException(CustomError.LOGIN_FCM_TOKEN_ERROR);
        }

        // 패스워드 암호화
        memberDto.setPassword(super.encrypt(password));

        // 회원가입 후 바로 로그인 MAIN DB에서 조회
        MemberDto memberInfo = getInfoForLogin(memberDto);

        // 회원 상태가 정상인지 체크
        if (memberInfo.getState() != null && memberInfo.getState() != 1) {
            throw new CustomException(CustomError.MEMBER_STATE_ERROR); // 계정상태를 확인해주세요.
        }

        // 탈퇴한 회원이 로그인 시도
        if (memberInfo.getIsDel() != null && memberInfo.getIsDel() == 1) {
            throw new CustomException(CustomError.MEMBER_OUT_STATE); // 탈퇴 대기 상태입니다.
        }

        if (memberInfo.getIdx() > 0) {

            // App_key 존재한다면
            if (memberDto.getAppKey() != null) {
                // App 정보 등록 or 수정
                memberDto.setMemberUuid(memberInfo.getUuid());
                accessService.accessAppLogInsert(memberDto);
            }

            // 마지막 로그인 시간 업데이트
            updateLastLogin(memberInfo);

            HashMap<String, Object> createTokenMap = new HashMap<>(); // 토큰에 담을 정보
            HashMap<String, Object> tokenMap = new HashMap<>(); // 토큰 업데이트용
            createTokenMap.put("memberUuid", memberInfo.getUuid());
            createTokenMap.put("id", memberInfo.getId());
            createTokenMap.put("appKey", memberDto.getAppKey());
            createTokenMap.put("regDate", dateLibrary.utcToLocalTime(dateLibrary.getDatetime())); // 로컬 현재시간

            // 토큰생성
            JwtDto oTokenInfo = jwtLibrary.createToken(createTokenMap);

            if (oTokenInfo != null) {
                if (oTokenInfo.getRefreshToken() == null || oTokenInfo.getRefreshToken().equals("")) {
                    throw new CustomException(CustomError.JOIN_REFRESH_TOKEN_ERROR); // Refresh Token 입력 해주세요
                }

                tokenMap.put("refreshToken", oTokenInfo.getRefreshToken());
                tokenMap.put("ip", getClientIP(httpRequest)); // ip 가져오기
                tokenMap.put("id", memberInfo.getId());
                tokenMap.put("appKey", memberDto.getAppKey());
                tokenMap.put("memberUuid", memberInfo.getUuid());
                tokenMap.put("regDate", dateLibrary.getDatetime());

                Integer check = jwtDaoSub.checkRefreshToken(tokenMap);

                if (check == 0) {
                    jwtDao.insertRefreshToken(tokenMap);
                } else {
                    jwtDao.updateRefreshToken(tokenMap);
                }
            }

            // 로그인 로그 저장
            loginMemberService.memberLoginInsert(memberInfo.getUuid());

            // 회원 제재 리스트
            List<Integer> restrainList = restrainService.getRestrainList(memberInfo.getUuid());
            restrainList = restrainList.stream().distinct().toList();
            if (!restrainList.isEmpty()) {
                Objects.requireNonNull(oTokenInfo).setRestrainList(restrainList);
            }

            return oTokenInfo;

        } else {
            throw new CustomException(CustomError.LOGIN_FAIL);
        }
    }

    /*****************************************************
     *  SubFunction - Select
     ****************************************************/
    /**
     * 로그인
     *
     * @param memberDto id, password
     * @return MemberDto 회원정보
     */
    public MemberDto getInfoForLogin(MemberDto memberDto) {
        return memberDao.getInfoForLogin(memberDto);
    }

    /*****************************************************
     *  SubFunction - Update
     ****************************************************/
    /**
     * 마지막 로그인 시간 & IP 업데이트
     *
     * @param memberDto member.idx
     */
    public void updateLastLogin(MemberDto memberDto) {
        HttpServletRequest request = ServerLibrary.getCurrReq();
        memberDto.setLoginIp(super.getClientIP(request));
        memberDto.setLastLogin(dateLibrary.getDatetime());
        memberDao.updateLastLogin(memberDto);
    }

}
