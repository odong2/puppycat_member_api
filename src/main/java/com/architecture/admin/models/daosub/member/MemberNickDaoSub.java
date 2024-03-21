package com.architecture.admin.models.daosub.member;

import com.architecture.admin.models.dto.member.MemberNickDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MemberNickDaoSub {

    /*****************************************************
     * Select
     ****************************************************/

    /**
     * 닉네임에 해당하는 카운트 가져오기: 닉네임 중복 체크에 사용
     *
     * @param memberNickDto 회원 닉네임 Dto
     * @return 닉네임 카운트 값
     */
    Integer getCountByNick(MemberNickDto memberNickDto);

    String getNickByUuid(String memberUuid);
}
