package com.architecture.admin.models.daosub.member.profile;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Repository
@Mapper
public interface MemberIntroDaoSub {

    /*****************************************************
     * Select
     ****************************************************/

    /**
     * 회원 인트로 조회
     *
     * @param memberUuid
     * @return
     */
    String getIntroByMemberUuid(String memberUuid);
}
