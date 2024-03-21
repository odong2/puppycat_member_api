package com.architecture.admin.models.daosub.member;

import com.architecture.admin.models.dto.member.MemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface LoginMemberDaoSub {

    /*****************************************************
     * Select
     ****************************************************/
    Integer getMemberCntCheck(MemberDto memberDto);

}
