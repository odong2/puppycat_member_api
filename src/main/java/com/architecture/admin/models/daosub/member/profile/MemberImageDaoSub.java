package com.architecture.admin.models.daosub.member.profile;

import com.architecture.admin.models.dto.member.profile.MemberImageDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Repository
@Mapper
public interface MemberImageDaoSub {

    /*****************************************************
     * Select
     ****************************************************/
    /**
     * 회원 프로필 이미지 유무 체크
     *
     * @param memberImageDto memberUuid
     * @return count
     */
    Integer getCountByImage(MemberImageDto memberImageDto);

    /**
     * 프로필 이미지 고유아이디 중복체크
     * [puppycat_member_profile_img]
     *
     * @param uuid 고유아이디
     * @return count
     */
    Integer getCountByUuid(String uuid);

}
