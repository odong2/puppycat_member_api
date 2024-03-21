package com.architecture.admin.models.dao.member;

import com.architecture.admin.models.dto.member.MemberNickDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MemberNickDao {

    /*****************************************************
     * Insert
     ****************************************************/
    /**
     * 회원 닉네임로그 등록
     *
     * @param memberNickDto 회원 닉네임dto
     */
    void insertLog(MemberNickDto memberNickDto);

    /*****************************************************
     * Update
     ****************************************************/
    /**
     * 회원 닉네임 등록/수정
     *
     * @param memberNickDto 회원 닉네임dto
     * @return
     */
    Integer nickUpdate(MemberNickDto memberNickDto);

    /*****************************************************
     * Delete
     ****************************************************/
}
