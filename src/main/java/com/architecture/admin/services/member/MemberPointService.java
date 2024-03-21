package com.architecture.admin.services.member;

import com.architecture.admin.libraries.NumberFormatLibrary;
import com.architecture.admin.libraries.PaginationLibray;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.member.MemberPointDao;
import com.architecture.admin.models.daosub.member.MemberPointDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.member.MemberPointDto;
import com.architecture.admin.models.dto.member.MemberPointSaveDto;
import com.architecture.admin.services.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;


@RequiredArgsConstructor
@Service
@Transactional
public class MemberPointService extends BaseService {

    private final MemberPointDao memberPointDao;
    private final MemberPointDaoSub memberPointDaoSub;
    private final NumberFormatLibrary numberFormatLibrary;

    @Value("${point.type.save}")
    private int POINT_TYPE_SAVE;
    @Value("${point.type.used}")
    private int POINT_TYPE_USED;
    @Value("${point.type.expire}")
    private int POINT_TYPE_EXPIRE;

    /*****************************************************
     *  Modules
     ****************************************************/

    /**
     * 사용 & 적립 포인트 목록
     *
     * @param searchDto : page, limit, loginMemberUuid, period
     * @return : list
     */
    public List<MemberPointDto> getMemberSaveAndUsedPoint(SearchDto searchDto) {
        // 시작 날짜, 종료 날짜 구하기
        Map<String, String> periodMap = dateLibrary.getStartDateAndEndDateByPeriod(searchDto.getPeriod());

        searchDto.setStartDate(periodMap.get("startDate"));
        searchDto.setEndDate(periodMap.get("endDate"));

        // 사용 & 적립 포인트 카운트
        int totalCnt = memberPointDaoSub.getMemberSaveAndUsePointCnt(searchDto);

        List<MemberPointDto> saveAndUsePointList = new ArrayList<>();

        if (totalCnt > 0) {
            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);
            // 사용 & 적립 포인트 리스트
            saveAndUsePointList = memberPointDaoSub.getMemberSaveAndUsePointList(searchDto);

            // 포인트 포맷팅
            for (MemberPointDto memberPointDto : saveAndUsePointList) {
                memberPointDto.setPointText(numberFormatLibrary.formatNumber(memberPointDto.getPoint()) + "P");
            }
            // 텍스트 변환
            stateText(saveAndUsePointList);
        }

        return saveAndUsePointList;
    }

    /**
     * 소멸 예정 포인트 목록
     *
     * @param searchDto : page, limit, loginMemberUuid
     * @return : list
     */
    public List<MemberPointDto> getExpectedExpirePoint(SearchDto searchDto) {

        String nowDateTime = dateLibrary.getDatetime();

        // 현재 년월 구하기
        String startDate = nowDateTime.substring(0, 10);                                 // 시작 날 ex) 2024-01-24
        String middleDate = getDateByCaculatingMonth(nowDateTime, "+", 1);  // 1개월 후 ex) 2024-02-24
        String endDate = getDateByCaculatingMonth(nowDateTime, "+", 2);     // 2개월 후 ex) 2024-03-24


        searchDto.setStartDate(startDate);
        searchDto.setEndDate(endDate);

        // 소멸 예정 포인트 카운트
        int totalCnt = memberPointDaoSub.getExpectedExpirePointCnt(searchDto);
        // return value
        List<MemberPointDto> list = new ArrayList<>();
        MemberPointDto expectedExpirePointDto = new MemberPointDto();

        if (totalCnt > 0) {
            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);
            // 사용 & 적립 포인트 리스트
            List<MemberPointDto> expectedExpirePointList = memberPointDaoSub.getExpectedExpirePointList(searchDto);

            // 월 별로 묶기
            List<MemberPointDto> startDateList = new ArrayList<>();
            List<MemberPointDto> middleDateList = new ArrayList<>();
            List<MemberPointDto> endDateList = new ArrayList<>();

            startDate = startDate.substring(0, 7);   // ex) 2024-01
            middleDate = middleDate.substring(0, 7); // ex) 2024-02
            endDate = endDate.substring(0, 7);       // ex) 2024-03

            for (MemberPointDto memberPointDto : expectedExpirePointList) {
                // 포인트 포맷팅
                memberPointDto.setPointText("-" + numberFormatLibrary.formatNumber(memberPointDto.getPoint()) + "P");

                // 월별로 묶기
                if (memberPointDto.getExpireDate().substring(0, 7).equals(startDate)) {
                    startDateList.add(memberPointDto);

                } else if (memberPointDto.getExpireDate().substring(0, 7).equals(middleDate)) {
                    middleDateList.add(memberPointDto);
                } else if (memberPointDto.getExpireDate().substring(0, 7).equals(endDate)) {
                    endDateList.add(memberPointDto);
                }
            }
            // 월별 소멸 예정 포인트 리스트 추가
            expectedExpirePointDto.setStartDateList(startDateList);
            expectedExpirePointDto.setMiddleDateList(middleDateList);
            expectedExpirePointDto.setEndDateList(endDateList);

            list.add(expectedExpirePointDto); // 리스트 추가
        }

        // 소멸 예정 포인트
        return list;
    }

    /**
     * 타입별 포인트 총 합계
     * [외부에서 호출 시 주의사항] : memberPointDtoList가 null이 아니어야 함
     *
     * @param memberPointDtoList : list(type, point)
     * @return : totalSavePoint, totalUsedPoint, totalExpirePoint
     */
    public MemberPointDto getPointSumByType(List<MemberPointDto> memberPointDtoList) {

        int totalSavePoint = 0;
        int totalUsedPoint = 0;

        for (MemberPointDto memberPointDto : memberPointDtoList) {
            if (memberPointDto.getType() == POINT_TYPE_SAVE) { // 적립
                totalSavePoint += memberPointDto.getPoint();
            } else if (memberPointDto.getType() == POINT_TYPE_USED) { // 사용
                totalUsedPoint += memberPointDto.getPoint();
            }
        }

        return MemberPointDto.builder()
                .totalSavePointText(numberFormatLibrary.formatNumber(totalSavePoint) + "P")
                .totalUsePointText(numberFormatLibrary.formatNumber(totalUsedPoint) + "P")
                .build();
    }

    /**
     * 당월 소멸 포인트
     *
     * @param searchDto : loginMemberUuid, startDate
     * @return : totalPoint
     */
    public String getThisMonthExpectedExpireTotalPoint(SearchDto searchDto) {
        // 당월 소멸 예정 포인트 총합
        String endDate = searchDto.getStartDate();          // 종료일
        String startDate = endDate.substring(0, 8) + "01";  // 시작일(당월 1일)

        SearchDto newSearchDto = new SearchDto();
        newSearchDto.setStartDate(startDate);
        newSearchDto.setEndDate(endDate);

        Integer thisMonthTotalPoint = memberPointDaoSub.getExpectedExpireThisMonthTotalPoint(searchDto);
        thisMonthTotalPoint = thisMonthTotalPoint != null ? thisMonthTotalPoint : 0;

        return numberFormatLibrary.formatNumber(thisMonthTotalPoint) + "P";
    }

    /**
     * 소멸 예정 포인트 총합
     *
     * @param expectedExpirePointDto : startDateList, middleDateList, endDateList
     * @return : startDateTotalPoint, middleDateTotalPoint, endDateTotalPoint
     */
    public Map<String, String> getTotalExpirePoint(MemberPointDto expectedExpirePointDto) {
        List<MemberPointDto> starartDateList = expectedExpirePointDto.getStartDateList();
        List<MemberPointDto> middleDateList = expectedExpirePointDto.getMiddleDateList();
        List<MemberPointDto> endDateList = expectedExpirePointDto.getEndDateList();

        int startDateTotalPoint = 0;
        int middleDateTotalPoint = 0;
        int endDateTotalPoint = 0;

        if (!ObjectUtils.isEmpty(starartDateList)) {
            for (MemberPointDto memberPointDto : starartDateList) {
                startDateTotalPoint += memberPointDto.getPoint();
            }
        }

        if (!ObjectUtils.isEmpty(middleDateList)) {
            for (MemberPointDto memberPointDto : middleDateList) {
                middleDateTotalPoint += memberPointDto.getPoint();
            }
        }

        if (!ObjectUtils.isEmpty(endDateList)) {
            for (MemberPointDto memberPointDto : endDateList) {
                endDateTotalPoint += memberPointDto.getPoint();
            }
        }

        String startSign = startDateTotalPoint < 1 ? "" : "-";
        String middleSign = middleDateTotalPoint < 1 ? "" : "-";
        String endSign = endDateTotalPoint < 1 ? "" : "-";

        return Map.of(
                "startDateTotalPoint", startSign + numberFormatLibrary.formatNumber(startDateTotalPoint) + "P",
                "middleDateTotalPoint", middleSign + numberFormatLibrary.formatNumber(middleDateTotalPoint) + "P",
                "endDateTotalPoint", endSign + numberFormatLibrary.formatNumber(endDateTotalPoint) + "P"
        );
    }

    /**
     * 포인트 적립
     *
     * @param pointDto : memberUuid, title, position, point, expiredate(UTC)
     */
    public void savePoint(MemberPointDto pointDto) {

        if (pointDto.getPoint() == null || pointDto.getPoint() < 1) {
            throw new CustomException(CustomError.POINT_EMPTY); // 포인트가 비었습니다
        }
        if (ObjectUtils.isEmpty(pointDto.getPosition())) {
            throw new CustomException(CustomError.POINT_POSITION_EMPTY); // position이 비었습니다
        }
        if (ObjectUtils.isEmpty(pointDto.getExpireDate())) {
            throw new CustomException(CustomError.POINT_EXPIRE_DATE_EMPTY); // 포인트 만료일이 비었습니다
        }
        if (ObjectUtils.isEmpty(pointDto.getTitle())) {
            throw new CustomException(CustomError.POINT_TITLE_EMPTY); // title이 비었습니다
        }
        // expiredate 날짜 형식 검사
        if (!dateLibrary.isDate(pointDto.getExpireDate())) {
            throw new CustomException(CustomError.POINT_EXPIRE_DATE_ERROR); // 포인트 만료일 형식이 올바르지 않습니다
        }

        pointDto.setRegdate(dateLibrary.getDatetime()); // 등록일

        // [1] member_point_save 등록
        insertMemberPointSave(pointDto);

        // [2] member_point_log 등록
        insertMemberPointLog(pointDto, POINT_TYPE_SAVE);

        int point = pointDto.getPoint(); // 지급할 포인트
        MemberPointDto memberPointDto = getMemberPoint(pointDto.getMemberUuid()); // 회원 포인트 정보 조회

        int restPoint = memberPointDto.getPoint();          // 기존 보유 포인트
        int totalSavePoint = memberPointDto.getSavePoint(); // 기존 적립 포인트

        memberPointDto.setPoint(restPoint + point);               // 기존 잔여 포인트 + 적립 포인트 = 회원 총 보유 포인트
        memberPointDto.setSavePoint(totalSavePoint + point);      // 기존 적립 포인트 + 적립 포인트 = 회원 총 적립 포인트
        memberPointDto.setMemberUuid(pointDto.getMemberUuid());   // 회원 uuid

        // [3] member_point 업데이트
        updateMemberPoint(memberPointDto);
    }

    /**
     * 포인트 사용
     *
     * @param usePointDto : memberUuid, point, title, position, productOrderId
     */
    public void usePoint(MemberPointDto usePointDto) {

        if (usePointDto.getUsePoint() == null || usePointDto.getUsePoint() < 1) {
            throw new CustomException(CustomError.POINT_EMPTY); // 포인트가 비었습니다
        }
        if (ObjectUtils.isEmpty(usePointDto.getTitle())) {
            throw new CustomException(CustomError.POINT_TITLE_EMPTY); // title이 비었습니다
        }
        if (ObjectUtils.isEmpty(usePointDto.getPosition())) {
            throw new CustomException(CustomError.POINT_POSITION_EMPTY); // position이 비었습니다
        }
        if (ObjectUtils.isEmpty(usePointDto.getProductOrderId())) {
            throw new CustomException(CustomError.POINT_PRODUCT_ORDER_ID_EMPTY); // 상품 주문번호가 비었습니다
        }

        String memberUuid = usePointDto.getMemberUuid();
        int usePoint = usePointDto.getUsePoint();  // 사용할 포인트

        // 보유 포인트 조회 [point, save_point, use_point, expire_point]
        MemberPointDto restMemberPointDto = getMemberPoint(memberUuid); // 보유 포인트 조회

        int restPoint = restMemberPointDto.getPoint(); // 보유 포인트
        usePointDto.setPoint(restPoint);               // 보유 포인트 set

        if (usePoint > restPoint) {
            throw new CustomException(CustomError.POINT_USE_OVER); // 보유 포인트가 부족합니다.
        }

        // [1] member_point_save에서 사용할 포인트 만큼 차감
        subTractMemberPointSave(usePointDto);

        int totalUsePoint = restMemberPointDto.getUsePoint() + usePoint; // 총 사용 포인트
        restPoint = restPoint - usePoint;                                // 보유 포인트 - 사용할 포인트

        restMemberPointDto.setUsePoint(totalUsePoint); // 총 사용 포인트 set
        restMemberPointDto.setPoint(restPoint);        // 보유 포인트 - 사용 포인트 set
        restMemberPointDto.setMemberUuid(memberUuid);  // 회원 uuid

        // [2] member_point 업데이트
        updateMemberPoint(restMemberPointDto);

    }

    /**
     * 포인트 차감
     *
     * @param memberPointDto : memberPointDto : memberUuid, usePoint, title, position, productOrderId
     */
    private void subTractMemberPointSave(MemberPointDto memberPointDto) {

        int usePoint = memberPointDto.getUsePoint(); // 사용할 포인트
        long beforeIdx = 0;

        MemberPointSaveDto restPointDto; // restPoint 조회용 dto

        // member_point_log 등록용 dto
        MemberPointDto pointDto = MemberPointDto.builder()
                .memberUuid(memberPointDto.getMemberUuid())
                .position(memberPointDto.getPosition())
                .title(memberPointDto.getTitle())
                .productOrderId(memberPointDto.getProductOrderId())
                .type(POINT_TYPE_USED)
                .regdate(dateLibrary.getDatetime())
                .state(1).build();

        // 사용할 포인트가 0이 될 때까지 차감
        while (usePoint > 0) {

            /** [1] member_point_save 한개 로우에서 남은 포인트 조회 **/
            restPointDto = memberPointDao.getMemberRestPointFromSave(memberPointDto);

            // 무한 루프 방지 : 이전 idx 값 가져오면 exception
            if (ObjectUtils.isEmpty(restPointDto) || beforeIdx == restPointDto.getIdx()) {
                throw new CustomException(CustomError.POINT_USE_ERROR); // 포인트 사용에 실패하였습니다.
            }

            beforeIdx = restPointDto.getIdx(); // 이전 idx 값 set
            pointDto.setPointSaveIdx(restPointDto.getIdx()); // save_idx set

            /** [2] restPoint 와 사용할 포인트 차감하여 양수,음수 체크 **/
            int restPoint = restPointDto.getRestPoint(); // 남은 포인트
            int subResultPoint = restPoint - usePoint;   // 차감 결과 포인트

            // 차감 결과 양수 - [조회한 save row 로 만 포인트 차감 가능]
            if (subResultPoint > 0) {

                pointDto.setSubResultPoint(subResultPoint); // 해당 로우 남은 포인트 차감된 결과값으로 set
                /** [3] subRestPoint(차감 결과 포인트)를 rest_point 로 업데이트 **/
                updateMemberRestPointToSave(pointDto);

                pointDto.setPoint(usePoint); // member_point_log 테이블에 사용한 포인트 set, [4]에서 활용
                usePoint = 0;   // break condition

                // 차감 결과 음수 - [다음 save row 로 조회 필요]
            } else if (subResultPoint < 0) {

                pointDto.setSubResultPoint(0); // 차감 결과 0으로 set
                /** [3] subRestPoint(차감 결과 포인트)를 rest_point 로 업데이트 **/
                updateMemberRestPointToSave(pointDto);

                pointDto.setPoint(restPoint); // member_point_log 테이블에 사용한 포인트 set, [4]에서 활용
                usePoint = usePoint - restPoint; // break condition

                // 차감 결과 0 - [조회한 save row 로 만 포인트 차감 가능]
            } else {

                pointDto.setSubResultPoint(subResultPoint);
                /** [3] subRestPoint(차감 결과 포인트)를 rest_point 로 업데이트 **/
                updateMemberRestPointToSave(pointDto);

                pointDto.setPoint(restPoint); // member_point_log 테이블에 사용한 포인트 set, [4]에서 활용
                usePoint = 0; // break condition
            }

            /** [4] member_point_log 테이블 등록 **/
            memberPointDao.insertMemberPointLog(pointDto);
        } // end of while

    }

    /*****************************************************
     *  SubFunction - Select
     ****************************************************/

    /**
     * 회원 보유 포인트 조회
     *
     * @param memberUuid : 회원 uuid
     * @return : point, save_point, use_point, expire_point
     */
    private MemberPointDto getMemberPoint(String memberUuid) {
        return memberPointDao.getMemberPoint(memberUuid);
    }

    /*****************************************************
     *  SubFunction - Insert
     ****************************************************/

    /**
     * member_point_log 등록
     *
     * @param memberPointDto : memberUuid, point, expiredate, regdate,
     *                       insertedIdx(member_point_save idx), productOrderId
     * @param type           : 1: 적립, 2: 사용, 3: 소멸
     */
    private void insertMemberPointLog(MemberPointDto memberPointDto, int type) {

        MemberPointDto insertDto = MemberPointDto.builder()
                .pointSaveIdx(memberPointDto.getInsertedIdx())      // 적립 idx
                .memberUuid(memberPointDto.getMemberUuid())         // 회원 uuid
                .point(memberPointDto.getPoint())                   // 지급할 포인트
                .position(memberPointDto.getPosition())             // 지급 위치(이벤트, 후기 작성 등)
                .title(memberPointDto.getTitle())                   // 지급 내용
                .productOrderId(memberPointDto.getProductOrderId()) // 상품 주문 번호
                .expireDate(memberPointDto.getExpireDate())         // 만료일
                .type(type)                                         // 로그 유형
                .regdate(memberPointDto.getRegdate())               // 등록일
                .build();

        memberPointDao.insertMemberPointLog(insertDto);
    }

    /**
     * member_point_save 등록
     * insertedIdx : member_point_save idx 반환
     *
     * @param memberPointDto : memberUuid, point, expiredate, regdate
     */
    private void insertMemberPointSave(MemberPointDto memberPointDto) {
        memberPointDao.insertMemberPointSave(memberPointDto);
    }

    /*****************************************************
     *  SubFunction - Update
     ****************************************************/

    /**
     * 회원 보유 포인트 업데이트
     *
     * @param memberPointDto : memberUuid, point, savePoint, usePoint, expirePoint
     */
    private void updateMemberPoint(MemberPointDto memberPointDto) {

        MemberPointDto updateDto = MemberPointDto.builder()
                .memberUuid(memberPointDto.getMemberUuid())   // 회원 uuid
                .point(memberPointDto.getPoint())             // 보유 포인트
                .savePoint(memberPointDto.getSavePoint())     // 적립 포인트
                .usePoint(memberPointDto.getUsePoint())       // 사용 포인트
                .expirePoint(memberPointDto.getExpirePoint()) // 만료 포인트
                .build();

        int result = memberPointDao.updateMemberPoint(updateDto);

        if (result < 1) {
            throw new CustomException(CustomError.POINT_SAVE_ERROR);  // 포인트 적립에 실패하였습니다.
        }
    }

    /**
     * subRestPoint(차감 결과 포인트)를 rest_point 로 업데이트
     *
     * @param pointDto : idx, subRestPoint
     */
    private void updateMemberRestPointToSave(MemberPointDto pointDto) {
        int result = memberPointDao.updateMemberRestPointToSave(pointDto);

        if (result < 1) {
            throw new CustomException(CustomError.POINT_USE_ERROR); // 포인트 사용에 실패하였습니다.
        }
    }

    /*****************************************************
     *  SubFunction - ETC
     ****************************************************/

    /**
     * 입력된 날짜로 부터 계산된 날짜 구하기
     *
     * @param inputDate : 날짜 텍스트
     * @param sign      : + , -
     * @param amount    : 계산할 값
     * @return : 계산된 날짜
     */
    private String getDateByCaculatingMonth(String inputDate, String sign, int amount) {

        int value = Integer.parseInt(sign + amount);

        Calendar cal = Calendar.getInstance();
        cal.setTime(Timestamp.valueOf(inputDate));
        cal.add(Calendar.MONTH, value);
        SimpleDateFormat formatDatetime = new SimpleDateFormat("yyyy-MM-dd");

        return formatDatetime.format(cal.getTime());
    }

    /**
     * 포인트 텍스트 변환
     *
     * @param memberPointList : list
     */
    private void stateText(List<MemberPointDto> memberPointList) {
        for (MemberPointDto memberPointDto : memberPointList) {
            stateText(memberPointDto);
        }
    }

    /**
     * 포인트  텍스트 변환
     *
     * @param memberPointDto : type
     */
    private void stateText(MemberPointDto memberPointDto) {

        // 포인트 타입
        if (!ObjectUtils.isEmpty(memberPointDto.getType())) {
            if (memberPointDto.getType() == POINT_TYPE_SAVE) {
                memberPointDto.setTypeText(super.langMessage("lang.point.type.save"));
            } else if (memberPointDto.getType() == POINT_TYPE_USED) {
                memberPointDto.setTypeText(super.langMessage("lang.point.type.use"));
            } else if (memberPointDto.getType() == POINT_TYPE_EXPIRE) {
                memberPointDto.setTypeText(super.langMessage("lang.point.type.expire"));
            }
        }
    }

}
