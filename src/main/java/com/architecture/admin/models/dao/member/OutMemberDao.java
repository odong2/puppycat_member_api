package com.architecture.admin.models.dao.member;

import com.architecture.admin.models.dto.member.OutMemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface OutMemberDao {

    /*****************************************************
     * Insert
     ****************************************************/
    /**
     * 회원 탈퇴
     * [puppycat_member_out]
     *
     * @param outMemberDto 회원정보, code, outRegDate
     * @return 실행결과
     */
    Integer insertOutMember(OutMemberDto outMemberDto);

    /**
     * 회원 탈퇴 (간편가입)
     * [puppycat_member_simple_out]
     *
     * @param outMemberDto insertedIdx, simpleId, simpleType, authToken
     */
    void insertOutSimpleMember(OutMemberDto outMemberDto);

    /**
     * 직접입력시 탈퇴 상세사유
     * [puppycat_member_out_reason]
     *
     * @param outMemberDto insertedIdx, reason
     */
    void insertOutReason(OutMemberDto outMemberDto);

    /*****************************************************
     * Update
     ****************************************************/
    /**
     * 회원 상태 업데이트
     * [puppycat_member]
     *
     * @param outMemberDto uuid
     * @return 실행결과
     */
    Integer updateIsDel(OutMemberDto outMemberDto);

    /*****************************************************
     * Delete
     ****************************************************/
    /**
     * jwt 토큰 제거
     * [puppycat_jwt_refresh_token]
     *
     * @param outMemberDto uuid
     */
    void deleteJwtToken(OutMemberDto outMemberDto);

    /**
     * fcm 토큰 제거
     * [puppycat_member_app]
     * @param outMemberDto uuid
     */
    void deleteFcmToken(OutMemberDto outMemberDto);
}
