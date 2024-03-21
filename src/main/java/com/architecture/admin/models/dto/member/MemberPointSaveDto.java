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
public class MemberPointSaveDto {

    private Long idx;               // idx
    private Integer point;          // 지급 포인트
    private Integer restPoint;      // 잔여 포인트
    private Integer state;          // 상태
    private String expireDate;      // 만료일
    private String expireDateTz;    // 만료일 타임존
    private String regDate;         // 등록일
    private String regDateTz;       // 등록일 타임존
}
