package com.architecture.admin.models.daosub.member;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.models.dto.member.MemberInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface MemberInfoDaoSub {

    /*****************************************************
     * Select
     *****************************************************
     /**
     * 고유아이디에 해당하는 카운트 가져오기: 아이디 중복 체크에 사용
     *
     * @param uuid (고유아이디)
     * @return count
     */
    Integer getCountByUuid(String uuid);

    /**
     * puppycat_member 정보 가져오기 by memberUuid
     *
     * @param memberInfoDto
     * @return
     */
    MemberInfoDto getMyInfo(MemberInfoDto memberInfoDto);

    /**
     * nick 정렬로 회원 정보 가져오기
     *
     * @param searchDto
     * @return
     */
    List<MemberInfoDto> getMemberInfoOrderByNick(SearchDto searchDto);

    /**
     * 회원 정보
     *
     * @param memberDto
     * @return
     */
    MemberInfoDto getMemberInfo(MemberDto memberDto);

    /**
     * 회원 정보 by Uuid
     *
     * @param memberDto
     * @return
     */
    MemberInfoDto getMemberInfoByUuid(MemberDto memberDto);

}
