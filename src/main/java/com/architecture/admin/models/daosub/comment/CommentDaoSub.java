package com.architecture.admin.models.daosub.comment;

import com.architecture.admin.models.dto.member.MemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Repository
@Mapper
public interface CommentDaoSub {

    /*****************************************************
     * Select
     ****************************************************/

    /**
     * 좋아요 많은 댓글 작성자 정보
     *
     * @param memberDto uuid imgDomain
     * @return
     */
    MemberDto getLikeManyCommentMemberInfo(MemberDto memberDto);

}
