package com.architecture.admin.models.dto.member.profile;

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
public class MemberIntroDto {
    /**
     * puppycat_member_profile_intro
     */
    private Long idx;           // 고유번호
    private String memberUuid;    // 회원번호
    private String intro;       // 회원 소개글
    private String regDate;     // 등록일
    private String regDateTz;    // 등록일 타임존

    // sql
    private Integer insertedId;
    private Integer affectedRow;

}
