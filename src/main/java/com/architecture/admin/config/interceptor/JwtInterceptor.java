package com.architecture.admin.config.interceptor;

import com.architecture.admin.libraries.DateLibrary;
import com.architecture.admin.libraries.TelegramLibrary;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.libraries.jwt.JwtLibrary;
import com.architecture.admin.models.dto.jwt.JwtDto;
import com.architecture.admin.services.member.LoginMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    // 해당 리스트에 포함된 경로 처리
    private static final String[] blacklist = {
            // api jwt 필터 적용 리스트
            "/v1/*"
    };
    private static final String[] whitelist = {
            "/v1/oauth/token"     // auth access token 에러
            , "/v1/oauth/check/token"     // auth refresh token 에러
            , "/v1/login/naver"      // 네이버 로그인
            , "/v1/login/kakao"      // 카카오 로그인
            , "/v1/login/google"     // 구글 로그인
            , "/v1/login"            // 로그인
            , "/v1/login/social"     // 소셜 로그인
            , "/v1/member/join"      // 회원가입
            , "/v1/join/social"      // 소셜 회원가입
            , "/v1/join/social/test" // 소셜 회원가입 테스트
            , "/v1/notice"           // 공지사항
            , "/v1/notice/menu"      // 공지사항 메뉴
            , "/v1/faq"              // FAQ
            , "/v1/faq/menu"         // FAQ 메뉴
            , "/v1/restrain/*"       // 제재 관련
            , "/v1/member/restore"   // 복구 관련
            , "/v1/member/nick/check"    // 닉네임 체크
            , "/v1/certification/popup"  // 본인인증
            , "/v1/certification/join"   // 본인인증 리턴 url
            , "/v1/certification/popup/my/info"  // 내 정보 본인인증
            , "/v1/certification/my/info"   // 내 정보 본인인증 리턴 url
            , "/v1/search"               // 검색 관련
            , "/v1/search/*"             // 검색 관련
            , "/v1/member/info/*"        // 회원 페이지 상세 관련
            , "/v1/contents/*/comment"   // 회원 페이지 상세 관련
            , "/v1/contents/*/comment/*/child"    // 대 댓글 페이지
            , "/v1/contents/*/detail"             // 컨텐츠 상세 리스트
            , "/v1/contents/*/popular/detail"     // 인기 컨텐츠 상세 리스트
            , "/v1/contents/*"                    // 컨텐츠 상세
            , "/v1/member/*/contents"             // 타인 컨텐츠 리스트
            , "/v1/member/*/tag/contents"         // 타인 태그 된 컨텐츠 리스트
            , "/v1/main/popular"            // 인기 유저 리스트
            , "/v1/member/*/pet"            // 회원 펫 정보
            , "/v1/member/uuid/check"       // 회원 uuid 체크
            , "/v1/member/uuid/list/check"  // 회원 uuid 리스트 체크
            , "/v1/check"                   // 헬스 체크
            , "/v1/follow/*"                // 팔로우 관련
            , "/v1/comment/like/many/member/info" // 좋아요 많은 댓글 회원 정보 조회
            , "/v1/member/nick/*"            // 회원 닉네임 정보
            , "/v1/noti/follow/list"              // 글 등록 팔로워 알림(cron)
            , "/v1/oauth/apple/refresh/token"     // 애플 리프레시 토큰 생성
            , "/v1/oauth/google/refresh/token"    // 구글 리프레시 토큰 생성
            , "/v1/push/none/token" //push 토큰 필요 없는 케수
    };
    @Autowired
    private final JwtLibrary jwtLibrary;
    @Autowired
    protected DateLibrary dateLibrary;
    @Autowired
    protected HttpSession session;
    @Autowired
    private TelegramLibrary telegramLibrary;
    @Autowired
    private LoginMemberService loginMemberService;

    @Override
    public boolean preHandle(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Object handler) throws IOException {
        String requestURI = httpRequest.getRequestURI();

        if (isJwtCheckPath(requestURI)) { // jwt토큰 검증 (블랙리스트)
            JwtDto jwtDto = new JwtDto();
            jwtDto.setAccessToken(httpRequest.getHeader(HttpHeaders.AUTHORIZATION));
            Integer iAccessTokenValue = jwtLibrary.validateAccessToken(jwtDto);

            // 토큰 검증
            if (iAccessTokenValue != 1) {
                throw new CustomException(CustomError.TOKEN_EXPIRE_TIME_ERROR);
            }

            // 토큰 생성 날짜 가져오기
            String tokenDate = jwtLibrary.getLoginDateFromToken("access", jwtDto.getAccessToken());
            String nowDate = dateLibrary.utcToLocalTime(dateLibrary.getDatetime());
            nowDate = nowDate.substring(0, 10);
            // 오늘 날짜랑 토큰 날짜가 다르면 LOGIN_LOG 인서트
            if (!Objects.equals(tokenDate, nowDate)) {
                String memberUUid = jwtLibrary.getMemberUuidFromToken("access", jwtDto.getAccessToken());
                loginMemberService.memberLoginInsert(memberUUid);
            }
        }
        return true;
    }

    /**
     * blackList 인증 체크
     * whiteList 목록 제외
     */
    private boolean isJwtCheckPath(String requestURI) {
        return PatternMatchUtils.simpleMatch(blacklist, requestURI) && !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }
}