package com.architecture.admin.models.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberNickDto {
    /**
     * puppycat_member
     **/
    private String nick;        // 닉네임

    /**
     * puppycat_member_nick_log
     **/
    private Long idx;        // 고유번호
    private String memberUuid;  // 회원번호
    private String modiDate;  // 수정일
    private String modiDateTz;// 수정일 타임존
    private String regDate;     // 등록일
    private String regDateTz;   // 등록일 타임존

    // sql
    private Integer insertedIdx;
    private Integer affectedRow;

}
