package com.architecture.admin.models.dao.member;

import com.architecture.admin.models.dto.member.MemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface LoginMemberDao {

    /*****************************************************
     * Select
     ****************************************************/
    void insertMemberLoginLog(MemberDto memberDto);
    /*****************************************************
     * Insert
     ****************************************************/
    /**
     * 회원 로그인 정보 등록
     *
     * @param memberDto memberUuid ip regDate
     * @return insertedIdx
     */
    int insertMemberLogin(MemberDto memberDto);
    /*****************************************************
     * Update
     ****************************************************/
     void updateMemberLogin(MemberDto memberDto);

}
