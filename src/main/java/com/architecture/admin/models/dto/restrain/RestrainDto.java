package com.architecture.admin.models.dto.restrain;

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
public class RestrainDto {

    private Long idx;               // 고유번호
    private String memberUuid;        // 회원 고유아이디
    private String id;              // 회원 아이디
    private String nick;            // 회원 닉네임
    private String title;           // 회원 닉네임
    private String restrainName;    // 제재 사유
    private String restrainType;    // 제재 타입
    private Integer type;           // 제재상태
    private Integer date;           // 제재 기간
    private String startDate;       // 제재 시작일
    private String endDate;         // 제재 종료일
    private Integer state;          // 상태값
    
    private String nowDate;         // 현재 시간
}