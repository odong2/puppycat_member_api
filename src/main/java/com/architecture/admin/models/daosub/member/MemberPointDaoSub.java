package com.architecture.admin.models.daosub.member;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.member.MemberPointDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface MemberPointDaoSub {
    /**
     * 사용 & 적립 포인트 카운트
     *
     * @param searchDto : loginMemberUuid
     * @return : totalCnt
     */
    int getMemberSaveAndUsePointCnt(SearchDto searchDto);

    /**
     * 사용 & 적립 포인트 리스트
     *
     * @param searchDto : page, limit, loginMemberUuid
     * @return : list
     */
    List<MemberPointDto> getMemberSaveAndUsePointList(SearchDto searchDto);

    /**
     * 소멸 예정 포인트 카운트
     *
     * @param searchDto : loginMemberUuid, startDate, endDate
     * @return : totalCnt
     */
    int getExpectedExpirePointCnt(SearchDto searchDto);

    /**
     * 소명 예정 포인트 리스트
     *
     * @param searchDto : loginMemberUuid, startDate, endDate
     * @return : list
     */
    List<MemberPointDto> getExpectedExpirePointList(SearchDto searchDto);

    /**
     * 이번달 소멸 예정 포인트 총합
     *
     * @param searchDto : loginMemberUuid, startDate : ex) 2024-01
     * @return : totalPoint
     */
    Integer getExpectedExpireThisMonthTotalPoint(SearchDto searchDto);
}
