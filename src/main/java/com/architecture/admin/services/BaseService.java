package com.architecture.admin.services;

import com.architecture.admin.libraries.*;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.libraries.jwt.JwtLibrary;
import com.architecture.admin.models.daosub.member.MemberDaoSub;
import com.architecture.admin.models.dto.member.MemberDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*****************************************************
 * 코어 서비스
 ****************************************************/
@Service
public class BaseService {

    @Value("${jwt.token.access.secret.key}")
    protected String accessType;

    // 시간 라이브러리 참조
    @Autowired
    protected DateLibrary dateLibrary;

    // 숫자 변환 관련 라이브러리 참조
    @Autowired
    protected NumberFormatLibrary numberFormatLibrary;

    // 암호화 라이브러리
    @Autowired
    protected SecurityLibrary securityLibrary;

    // 세션
    @Autowired
    protected HttpSession session;

    // 텔레그램
    @Autowired
    protected TelegramLibrary telegramLibrary;

    // Redis 라이브러리
    @Autowired
    protected RedisLibrary redisLibrary;

    // Curl 라이브러리
    @Autowired
    protected CurlLibrary curlLibrary;

    // jwt 라이브러리
    @Autowired
    protected JwtLibrary jwtLibrary;

    /**
     * 메시지 가져오는 라이브러리
     */
    @Autowired
    protected MessageSource messageSource;

    @Autowired
    protected MemberDaoSub memberDaoSub;

    /*****************************************************
     * 세션 값 가져오기
     ****************************************************/
    public String getSession(String id) {
        return (String) session.getAttribute(id);
    }

    /*****************************************************
     * 레디스
     ****************************************************/
    // 레디스 값 생성
    public void setRedis(String key, String value, Integer expiredSeconds) {
        redisLibrary.setData(key, value, expiredSeconds);
    }

    // 레디스 값 불러오기
    public String getRedis(String key) {
        return redisLibrary.getData(key);
    }

    // 레디스 값 삭제하기
    public void removeRedis(String key) {
        redisLibrary.deleteData(key);
    }

    /*****************************************************
     * Curl
     ****************************************************/
    // get
    public String getCurl(String url, String header) {
        return curlLibrary.get(url, header);
    }

    // post
    public String postCurl(String url, Map dataset) {
        return curlLibrary.post(url, dataset);
    }

    /*****************************************************
     * 암호화 처리
     ****************************************************/
    // 양방향 암호화 암호화
    public String encrypt(String str) throws Exception {
        return securityLibrary.aesEncrypt(str);
    }

    // 양방향 암호화 복호화
    public String decrypt(String str) throws Exception {
        return securityLibrary.aesDecrypt(str);
    }

    // 단방향 암호화
    public String md5encrypt(String str) {
        return securityLibrary.md5Encrypt(str);
    }

    /*****************************************************
     * 디버깅
     ****************************************************/
    public void d() {
        int iSeq = 2;
        System.out.println("======================================================================");
        System.out.println("클래스명 : " + Thread.currentThread().getStackTrace()[iSeq].getClassName());
        System.out.println("메소드명 : " + Thread.currentThread().getStackTrace()[iSeq].getMethodName());
        System.out.println("줄번호 : " + Thread.currentThread().getStackTrace()[iSeq].getLineNumber());
        System.out.println("파일명 : " + Thread.currentThread().getStackTrace()[iSeq].getFileName());
    }

    public void pushAlarm(String sendMessage) {
        telegramLibrary.sendMessage(sendMessage);
    }

    public void pushAlarm(String sendMessage, String sChatId) {
        telegramLibrary.sendMessage(sendMessage, sChatId);
    }

    /*****************************************************
     * Language 값 가져오기
     ****************************************************/
    public String langMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    public String langMessage(String code, @Nullable Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }


    /*****************************************************
     * get locale Language 현재 언어 값
     ****************************************************/
    public String getLocaleLang() {
        String localLang = LocaleContextHolder.getLocale().toString().toLowerCase();

        switch (localLang) {
            case "ko_kr", "ko", "kr":
                return "ko";
            case "en":
                return "en";
            default:
                return "en";
        }
    }

    /*****************************************************
     * ip 값 가져오기
     * private => public 으로 변환
     ****************************************************/
    public String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    /*****************************************************
     * email값인지 체크하기
     ****************************************************/
    public boolean isEmail(String email) {
        boolean validation = false;

        if (Objects.equals(email, "") || email == null) {
            return false;
        }

        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            validation = true;
        }

        return validation;
    }

    /*****************************************************
     * 현재 도메인
     ****************************************************/
    public String getCurrentDomain() {
        // 도메인 받아오기
        String scheme = "";
        String currentDomain = "";
        HttpServletRequest request = ServerLibrary.getCurrReq();
        if (request.getServerName().equals("localhost")) {
            scheme = "http";
        } else {
            scheme = "https";
        } // http / https
        String serverName = request.getServerName();// 도메인만
        Integer serverPort = request.getServerPort();// 포트
        if (serverPort.equals(80) || serverPort.equals(443)) {
            currentDomain = scheme + "://" + serverName; // 전체 도메인
        } else {
            currentDomain = scheme + "://" + serverName + ":" + serverPort; // 전체 도메인
        }
        return currentDomain; // 전체 도메인
    }

    /*****************************************************
     * 컨텐츠 멘션 복호화
     ****************************************************/
    public String mentionDecrypt(String text) {

        Pattern mentionPattern = Pattern.compile("\\[@\\[[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝|_|.]*\\]]");
        Matcher mentionMatcher = mentionPattern.matcher(text);
        String extractMention = null;

        while (mentionMatcher.find()) {

            extractMention = mentionMatcher.group(); // 패턴에 일치하는 문자열 반환 ex) [@[ko07a1b7553]]

            // UUID에 해당하는 회원 닉네임 검색 ([@[, ]] 제거 후 검색)
            String memberNick = memberDaoSub.getMemberNickByUuid(extractMention.substring(3, extractMention.length() - 2));

            // UUID에 일치하는 닉네임 있다면 @닉네임 형식으로 변환
            if (memberNick != null) {
                // ex) [@[ko07a1b7553]] > @냐옹이
                text = text.replace(extractMention, "@" + memberNick);
            } else {
                // ex) [@[없는닉네임]] > @없는닉네임
                text = text.replace(extractMention, "@" + extractMention.substring(3, extractMention.length() - 2));
            }

        }

        return text;
    }

    /*****************************************************
     * 해시태그 복호화
     ****************************************************/
    public String tagDecrypt(String text) {

        Pattern tagPattern = Pattern.compile("\\[#\\[[\\p{L}\\p{M}\\p{N}_\\p{So}]*]*\\]]");
        Matcher tagMatcher = tagPattern.matcher(text);

        while (tagMatcher.find()) {
            // 패턴에 일치하는 문자열 반환 ex) [#[태그]]
            String extractTag = tagMatcher.group();
            // 문자만 추출
            String hashTag = extractTag.substring(3, extractTag.length() - 2);
            // 패턴 -> 해시태그로 변환
            text = text.replace(extractTag, "#" + hashTag);
        }
        return text;
    }

    /*****************************************************
     * access 토큰 회원 UUID 추출
     ****************************************************/
    public String getAccessMemberUuid(String token) {

        String memberUuid = jwtLibrary.getMemberUuidFromToken(accessType, token);

        if (memberUuid == null || memberUuid.equals("")) {
            // 토큰 값 에러 다시 로그인 해주세요.
            throw new CustomException(CustomError.TOKEN_EXPIRE_TIME_ERROR);
        }

        return memberUuid;
    }

    /*****************************************************
     * access 토큰 회원 AppKey 추출
     ****************************************************/
    public String getAccessAppKey(String token) {

        String appKey = jwtLibrary.getAppKeyFromToken(accessType, token);

        if (appKey == null || appKey.equals("")) {
            // 토큰 값 에러 다시 로그인 해주세요.
            throw new CustomException(CustomError.TOKEN_EXPIRE_TIME_ERROR);
        }

        return appKey;
    }

    /*****************************************************
     * access 토큰 회원 id 추출
     ****************************************************/
    public String getAccessId(String token) {

        String id = jwtLibrary.getIdFromToken(accessType, token);

        if (id  == null || id.equals("")) {
            // 토큰 값 에러 다시 로그인 해주세요.
            throw new CustomException(CustomError.TOKEN_EXPIRE_TIME_ERROR);
        }

        return id;
    }

    /*****************************************************
     * memberUuid 유효성 검사
     ****************************************************/
    public void chkMemberUuid(String uuid) {

        if (ObjectUtils.isEmpty(uuid)) {
            throw new CustomException(CustomError.MEMBER_UUID_EMPTY); // 회원 UUID가 비었습니다.
        }

        // 조회용 dto
        MemberDto memberDto = MemberDto.builder()
                .uuid(uuid).build();

        int count = memberDaoSub.getCountByUuid(memberDto);

        if (count < 1) {
            throw new CustomException(CustomError.MEMBER_UUID_ERROR); // 회원 UUID가 유효하지 않습니다.
        }
    }

}
