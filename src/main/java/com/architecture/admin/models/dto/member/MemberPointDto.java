package com.architecture.admin.models.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberPointDto {

    private Long idx;
    private String memberUuid;
    private Integer point;          // 보유 포인트
    private Integer savePoint;      // 적립 포인트
    private Integer usePoint;       // 사용 포인트
    private Integer expirePoint;    // 만료 포인트
    private Integer state;
    private String regdate;
    private String regDateTz;
    private String expireDate;
    private String expireDateTz;

    /**
     * point_log
     */
    private Long pointSaveIdx;      // 적립 포인트 idx
    private String position;        // 지급/사용 위치(상품구매, 이벤트, 후기작성)
    private String title;           // 지급/사용 내역
    private String admin;           // 지급/차감 관리자 아이디
    private String partner;         // 지급/차감 파트너 아이디
    private String productOrderId;  // 상품주문번호
    private Integer type;           // 1: 적립, 2:사용, 3:소멸

    // 기타
    private Integer subResultPoint;      // 차감 결과 포인트
    private String typeText;             // 지급/사용/소멸 문자열 타입
    private String totalSavePointText;   // 총 포인트
    private String totalUsePointText;    // 총 사용 포인트
    private String totalExpirePointText; // 총 만료 포인트
    private String pointText;            // 포인트 문자열

    private List<MemberPointDto> startDateList;
    private List<MemberPointDto> middleDateList;
    private List<MemberPointDto> endDateList;


    // sql
    private Long insertedIdx;
}
