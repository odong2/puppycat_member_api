package com.architecture.admin.controllers.v1.member;

import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.member.MemberPointDto;
import com.architecture.admin.services.member.MemberPointService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/member")
public class MemberPointV1Controller extends BaseController {

    private final MemberPointService pointService;

    /**
     * 사용 & 적립 포인트 목록
     *
     * @param token     : jwt token
     * @param searchDto : page, limit, period
     * @return : list
     */
    @GetMapping("/point/used-save")
    public ResponseEntity<String> getMemberSaveAndUsedPoint(@RequestHeader(value = "Authorization", defaultValue = "") String token,
                                                            SearchDto searchDto) {

        // 토큰에서 로그인 회원 UUID set
        searchDto.setLoginMemberUuid(super.getAccessMemberUuid(token));

        // 적립 사용 리스트
        List<MemberPointDto> memberPointList = pointService.getMemberSaveAndUsedPoint(searchDto);

        // 타입별 총 합계
        MemberPointDto memberPointDto = MemberPointDto.builder()
                .totalSavePointText("0P")
                .totalUsePointText("0P").build();

        JSONObject data = new JSONObject();

        if (!ObjectUtils.isEmpty(memberPointList)) {
            memberPointDto = pointService.getPointSumByType(memberPointList); // 타입별 총 합계
            data.put("params", new JSONObject(searchDto));          // 페이징
        }

        String sMessage = super.langMessage("lang.common.success.search"); // 조회 완료하였습니다.

        data.put("list", memberPointList);                      // 리스트
        data.put("pointTotal", new JSONObject(memberPointDto)); // 타입 별 총 합계

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 소멸 예정 포인트 목록
     *
     * @param token     : jwt token
     * @param searchDto : page, limit, period, 2(소멸)
     * @return : list
     */
    @GetMapping("/point/expected-expire")
    public ResponseEntity<String> getMemberExpectedExpirePoint(@RequestHeader(value = "Authorization", defaultValue = "") String token,
                                                               SearchDto searchDto) {

        // 토큰에서 로그인 회원 UUID set
        searchDto.setLoginMemberUuid(super.getAccessMemberUuid(token));

        // 소멸 리스트
        List<MemberPointDto> expectedExpirePointList = pointService.getExpectedExpirePoint(searchDto);
        JSONObject data = new JSONObject();

        if (!ObjectUtils.isEmpty(expectedExpirePointList)) {
            // 당월 소멸 예정 총합
            String totalPoint = pointService.getThisMonthExpectedExpireTotalPoint(searchDto);
            Map<String, String> map = pointService.getTotalExpirePoint(expectedExpirePointList.get(0));

            data.put("expireTotalPoint", totalPoint);
            data.put("params", new JSONObject(searchDto)); // 페이징
            data.put("startDateTotalPoint", map.get("startDateTotalPoint"));   // 당월 소멸 예정 포인트 총합
            data.put("middleDateTotalPoint", map.get("middleDateTotalPoint")); // 당월 다음달 소멸 예정 포인트 총합
            data.put("endDateTotalPoint", map.get("endDateTotalPoint"));       // 당월 다다음달 소멸 예정 포인트 총합
        }

        String sMessage = super.langMessage("lang.common.success.search"); // 조회 완료하였습니다.

        data.put("list", expectedExpirePointList); // 리스트

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 포인트 적립 (컬 통신용)
     *
     * @param token          : jwt token
     * @param memberPointDto : title, point, position, expiredate(UTC)
     * @return : success
     */
    @PostMapping("/point/save")
    public ResponseEntity<String> savePoint(@RequestHeader(value = "Authorization", defaultValue = "") String token,
                                            @RequestBody MemberPointDto memberPointDto) {

        // 토큰에서 로그인 회원 UUID set
        memberPointDto.setMemberUuid(super.getAccessMemberUuid(token));

        // 포인트 적립
        pointService.savePoint(memberPointDto);

        String sMessage = super.langMessage("lang.point.success.save"); // 포인트가 적립되었습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 포인트 사용 (컬 통신용)
     *
     * @param token          : jwt token
     * @param memberPointDto : title, usePoint, position, productOrderId
     * @return : success
     */
    @PostMapping("/point/use")
    public ResponseEntity<String> usePoint(@RequestHeader(value = "Authorization", defaultValue = "") String token,
                                            @RequestBody MemberPointDto memberPointDto) {

        // 토큰에서 로그인 회원 UUID set
        memberPointDto.setMemberUuid(super.getAccessMemberUuid(token));

        // 포인트 사용
        pointService.usePoint(memberPointDto);

        Object[] args = {memberPointDto.getUsePoint()};
        String sMessage = super.langMessage("lang.point.success.use", args); // {0} 포인트를 사용하였습니다.

        return displayJson(true, "1000", sMessage);
    }
}
