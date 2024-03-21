package com.architecture.admin.models.dao.member.profile;

import com.architecture.admin.models.dto.member.profile.MemberIntroDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MemberIntroDao {

    /*****************************************************
     * Insert
     ****************************************************/
    /**
     * 프로필 소개 등록
     *
     * @param memberIntroDto memberUuid intro regDate
     * @return insertedIdx
     */
    int insertIntro(MemberIntroDto memberIntroDto);

    /**
     * 소개글 로그 인서트
     *
     * @param memberIntroDto memberUuid intro regDate
     * @return insertedId
     */
    int insertIntroLog(MemberIntroDto memberIntroDto);

    /*****************************************************
     * Update
     ****************************************************/
    /**
     * 프로필 소개글 업데이트
     *
     * @param memberIntroDto memberUuid intro regDate
     * @return affectedRow
     */
    int updateIntro(MemberIntroDto memberIntroDto);
}
