package com.architecture.admin.models.daosub.access;

import com.architecture.admin.models.dto.member.MemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface AccessDaoSub {

    /*****************************************************
     * Select
     ****************************************************/
    /**
     * access 테이블에서 memberUuid 조회
     * @param memberDto memberUuid
     * @return MemberDto
     */
    MemberDto getAccessInfo(MemberDto memberDto);

    /**
     * app 테이블에서 동일 app_key 가 있는 지 조회
     * @param memberDto appKey
     * @return MemberDto
     */
    MemberDto getAppInfo(MemberDto memberDto);

    /**
     * app 테이블에서 동일 FCM KEY 가 있는 지 조회
     * @param memberDto appKey
     * @return MemberDto
     */
    Integer getAppSameFcmCount(MemberDto memberDto);

}
