package com.architecture.admin.models.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberDto {
    /**
     * puppycat_member
     **/
    private Long idx;           // 회원번호
    @Email
    private String id;          // 아이디
    private String uuid;        // 고유아이디
    private String memberUuid;  // 고유아이디
    private String uuidMain;    // 고유아이디 Main 값
    private String nick;        // 닉네임
    private String partner;     // 파트너명
    private String lang;        // 사용언어
    private Integer isSimple;   // 간편가입
    private String loginIp;     // 로그인 아이피
    private String joinIp;      // 가입 아이피
    private Integer state;      // 상태값 (1:정상/2:대기/3:제재)
    private String stateText;   // 상태값 문자변환
    private String stateBg;     // 상태 bg 색상
    private String lastLogin;   // 마지막 로그인(UTC)
    private String lastLoginTz; // 마지막 로그인 타임존
    private String regDate;     // 등록일
    private String regDateTz;   // 등록일 타임존
    private Integer isDel;      // 탈퇴상태 (0:정상/1:탈퇴)

    /**
     * puppycat_member_info
     **/
    private Long memberIdx;     // 회원번호
    private String name;        // 이름
    private String phone;       // 전화번호
    private String gender;      // 성별(M: male, F: female)
    private String genderText;  // 성별 문자변환
    private String birth;       // 생년월일

    /**
     * puppycat_member_password
     **/
    private String password;          // 패스워드
    private String passwordConfirm;   // 패스워드 확인
    private String modiDate;  // 수정일
    private String modiDateTz;// 수정일 타임존

    /**
     * puppycat_member_simple
     **/
    private String simpleId;  // 소셜에서 받아온 clientId
    private String simpleType;  // 간편가입 타입 (ex kakao, google)
    private String authToken;   // 토큰 값 (refresh)

    /**
     * puppycat_member_channel_talk
     */
    private String hashId;              // 채널톡 해시 값

    /**
     * puppycat_member_app
     */
    private String appKey;  // 진입 어플 키
    private String appVer;  // 앱버전
    private String fcmToken;    // Firebase Token 키

    /**
     * puppycat_member_access
     */
    private String domain;  // 진입 도메인
    private String accessIp;    // 로그인 아이피
    private String iso; // 국가 코드

    private String code;        // 코드값
    private String accessToken; // 소셜에서 받는 access 토큰 값
    private String refreshToken; // 소셜에서 받는 refresh 토큰 값

    // sql
    private Long insertedIdx;
    private Integer affectedRow;

    private String intro;           // 소개글
    private String imgUrl;          // img
    private String profileImgUrl;   // 프로필img
    private Integer isBadge;            // 뱃지여부

    /**
     * sns_policy_agree
     */
    private Integer selectPolicy_3;        // 이벤트 동의
    // 본인 인증 토큰
    private String credentialToken;
    private String ci;
    private String di;

    private Integer memberState;   // 회원 상태값 ( 0:탈퇴회원 1:정상회원 )

    // 기타
    private Integer favoriteState;         // 채팅 즐겨찾기 상태
    private String totalTagCnt;
    private String totalSaveCnt;
    private String totalContentsCnt;
    private String totalActivityTime;
    private String walkUuid;
    private Boolean result;
    private String sMessage;
    private List<String> uuidList;
    private String imgDomain;

}