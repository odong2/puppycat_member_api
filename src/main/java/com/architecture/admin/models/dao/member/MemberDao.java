package com.architecture.admin.models.dao.member;

import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.models.dto.member.MemberPointDto;
import com.architecture.admin.models.dto.member.OutMemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MemberDao {

    /*****************************************************
     * Select
     ****************************************************/
    /**
     * 로그인 성공시 회원 정보 가져오기
     *
     * @param memberDto 회원dto
     * @return MemberDto
     */
    MemberDto getInfoForLogin(MemberDto memberDto);

    /**
     * 탈퇴신청일 가져오기
     *
     * @param memberDto member.idx
     * @return OutMemberDto
     */
    OutMemberDto getOutDate(MemberDto memberDto);

    /*****************************************************
     * Insert
     ****************************************************/
    /**
     * 회원 등록
     *
     * @param memberDto 회원dto
     * @return insertedIdx
     */
    Integer insert(MemberDto memberDto);

    /**
     * 회원 정보 등록
     *
     * @param memberDto 회원dto
     */
    int insertInfo(MemberDto memberDto);

    /**
     * 회원 비밀번호 등록
     *
     * @param memberDto 회원dto
     */

    int insertPassword(MemberDto memberDto);

    /**
     * 간편로그인 등록
     *
     * @param memberDto 회원dto
     */
    void insertSimple(MemberDto memberDto);

    /**
     * 채널톡 회원 해시값 등록
     *
     * @param memberDto memberUuid , hashId
     */
    void insertChannelTalk(MemberDto memberDto);

    int insertPoint(MemberPointDto pointDto);

    /*****************************************************
     * Update
     ****************************************************/
    /**
     * 마지막 로그인 날짜 및 아이피 update
     *
     * @param memberDto member.idx, loginIp, lastLogin
     */
    void updateLastLogin(MemberDto memberDto);

    /**
     * 탈퇴 회원 복구
     * 회원 정보 업데이트
     *
     * @param memberDto member.idx
     */
     void updateState(MemberDto memberDto);

    /**
     * 탈퇴 테이블 상태값 변경 (3: 복구)
     *
     * @param memberDto member.idx
     */
    void updateOutState(MemberDto memberDto);
}
