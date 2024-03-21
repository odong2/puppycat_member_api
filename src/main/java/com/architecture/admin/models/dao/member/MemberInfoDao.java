package com.architecture.admin.models.dao.member;

import com.architecture.admin.models.dto.member.MemberInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MemberInfoDao {

    /*****************************************************
     * Select
     ****************************************************/


    /*****************************************************
     * Insert
     ****************************************************/

    /*****************************************************
     * Update
     ****************************************************/

    /**
     * 본인 인증 정보 수정
     *
     * @param memberInfoDto phone memberUuid ci modiDate
     */
    Integer updateMemberCertificationInfo(MemberInfoDto memberInfoDto);
}
