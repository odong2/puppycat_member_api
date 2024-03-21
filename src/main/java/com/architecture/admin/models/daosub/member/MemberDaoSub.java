package com.architecture.admin.models.daosub.member;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.member.MemberDto;

import com.architecture.admin.models.dto.member.MemberInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface MemberDaoSub {

    /*****************************************************
     * Select
     ****************************************************/
    /**
     * 아이디에 해당하는 카운트 가져오기: 아이디 중복 체크에 사용
     *
     * @param memberDto 회원 Dto
     * @return 아이디 카운트 값
     */
    Integer getCountById(MemberDto memberDto);

    /**
     * CI && SimpleType 해당하는 카운트 가져오기: 중복 체크에 사용
     *
     * @param memberDto 회원 Dto
     * @return 아이디 카운트 값
     */
    Integer getCountByCi(MemberDto memberDto);

    /**
     * 고유아이디에 해당하는 카운트 가져오기: 아이디 중복 체크에 사용
     *
     * @param uuid (고유아이디)
     * @return count
     */
    Integer getCountByUuid(String uuid);

    /**
     * 회원 카운트 값 by uuid
     *
     * @param memberDto uuid
     * @return count
     */
    int getCountByUuid(MemberDto memberDto);

    /**
     * 회원 닉네임 가져오기 by uuid
     *
     * @param uuid 회원 uuid
     * @return 닉네임
     */
    String getMemberNickByUuid(String uuid);


    /**
     * 회원 가입일 가져오기 by uuid
     *
     * @param uuid 회원 uuid
     * @return
     */
    String getMemberJoinDate(String uuid);

    /**
     * 회원가입일 가져오기
     *
     * @param memeberUuid
     * @return
     */
    MemberDto getMemberRegdate(String memeberUuid);

    /**
     * memberUuid 가져오기 by simpleId
     *
     * @param memberDto simpleId
     * @return memberUuid
     */
    String getMemberUuidBySimpleId(MemberDto memberDto);

    /**
     * 회원 검색
     *
     * @param searchWord 검색어
     * @return memberUuid list
     */
    List<String> getMemberUuidBySearch(String searchWord);

    /**
     * 회원 정보 가져오기 by memberUuidList
     *
     * @param memberDto uuidList, imgDomain
     * @return MemberDto list
     */
    List<MemberInfoDto> getMemberInfoByUuidList(MemberDto memberDto);

    /**
     * sns_member 정보 가져오기 by uuid
     *
     * @param uuid
     * @return
     */
    List<MemberDto> getMemberInfoByUuid(MemberDto memberDto);

    /**
     * 회원 아이디 조회
     *
     * @param memberDto
     * @return
     */
    String getMemberIdByCi(MemberDto memberDto);
}
