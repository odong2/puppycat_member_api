package com.architecture.admin.models.daosub.member;

import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.models.dto.member.OutMemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface OutMemberDaoSub {

    /*****************************************************
     * Select
     ****************************************************/
    /**
     * 회원 정보 조회하기
     * @param outMemberDto member.idx
     * @return OutMemberDto 탈퇴 회원 정보
     */
    OutMemberDto getInfoForOut(OutMemberDto outMemberDto);

    /**
     * puppycat_member_out 정보 가져오기 by uuid
     *
     * @param uuid
     * @return
     */
    List<MemberDto> getMemberOutInfoByUuid(String uuid);

    /**
     * 탈퇴사유 리스트 조회하기
     *
     * @return 탈퇴사유 리스트
     */
    List<OutMemberDto> getOutCodeList();
}
