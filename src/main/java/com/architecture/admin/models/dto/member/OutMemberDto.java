package com.architecture.admin.models.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutMemberDto {
    /**
     * puppycat_member_out
     */
    private Long idx;           // 일련번호
    private Long memberIdx;     // puppycat_member.idx
    private String id;          // 회원 아이디
    private String uuid;        // 고유아이디
    private String password;    // 비밀번호
    private String nick;        // 회원 닉네임
    private String partner;     // 파트너명
    private String lang;        // 사용언어
    private Integer isSimple;   // 간편가입
    private String loginIp;     // 로그인 아이피
    private String joinIp;      // 가입 아이피
    private String lastLogin;   // 마지막 로그인(UTC)
    private String lastLoginTz; // 마지막 로그인 타임존
    private String regDate;     // 가입일
    private String regDateTz;   // 가입일 타임존
    private Integer state;      // 탈퇴 상태값 [1:확정/2:대기/3:복구]
    private Integer code;       // 탈퇴사유코드
    private String outRegDate;      // 탈퇴신청일
    private String outRegDateTz;    // 탈퇴신청일 타임존
    private String outDate;         // 탈퇴확정일
    private String outDateTz;       // 탈퇴확정일 타임존

    /**
     * puppycat_member_info
     **/
    private String name;        // 이름
    private String phone;       // 전화번호
    private String gender;      // 성별(M: male, F: female)
    private String birth;       // 생년월일

    /**
     * puppycat_member_simple
     **/
    private String simpleId;    // 간편가입 넘어오는 아이디
    private String simpleType;  // 간편가입 타입 (ex kakao google)
    private String authToken;   // 토큰 값 (refresh)

    /**
     * puppycat_member_out_reason
     */
    private String reason; // 탈퇴 상세사유

    // sql
    private Long insertedIdx;
}
