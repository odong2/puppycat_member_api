package com.architecture.admin.models.dto.walk;

import com.architecture.admin.models.dto.member.MemberInfoDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalkResultDto {


    private Long step;           // 걸음수
    private Double distance;     // 거리
    private String startDate;    // 산책 시작 시간
    private String endDate;      // 산책 종료 시간
    private Integer state;       // 상태

    /**
     * walk
     */
    private String walkUuid;     // 산책 uuid
    private Integer together;    // 함께하기 여부 ( 1:함께 0:혼자 )

    /**
     * walk_member
     */
    private Long walkIdx;        // 산책 idx
    private String memberUuid;   // 회원 uuid

    /**
     * walk_member_pet
     */
    private Double calorie;      // 칼로리

    /**
     * etc
     */
    private List<String> memberUuidList; // 산책 참가자 uuid 리스트
    private String walkTime;             // 산책 시간
    private List<MemberInfoDto> walkMemberList;  // 산책 참여 회원 리스트
}
