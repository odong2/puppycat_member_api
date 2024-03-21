package com.architecture.admin.services.comment;

import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.daosub.comment.CommentDaoSub;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.models.dto.wordcheck.MemberWordCheckDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.wordcheck.MemberWordCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService extends BaseService {

    private final CommentDaoSub commentDaoSub;
    private final MemberWordCheckService wordCheckService;
    @Value("${cloud.aws.s3.img.url}")
    private String imgDomain;

    /*****************************************************
     *  Modules
     ****************************************************/

    /**
     * 좋아요 많은 댓글 회원 정보
     *
     * @param uuid
     * @return
     */
    public MemberDto getLikeManyCommentMemberInfo(String uuid) {

        if (ObjectUtils.isEmpty(uuid)) {
            throw new CustomException(CustomError.MEMBER_UUID_EMPTY); // 회원 uuid가 비었습니다.
        }

        MemberDto memberDto = new MemberDto();
        memberDto.setImgDomain(imgDomain);
        memberDto.setUuid(uuid);

        // 댓글 작성자 정보 조회
        MemberDto writerInfo = commentDaoSub.getLikeManyCommentMemberInfo(memberDto);

        if (writerInfo != null) {
            // intro 금칙어 치환
            List<MemberWordCheckDto> introBadWordList = wordCheckService.getIntroBadWordList();
            String intro = wordCheckService.memberWordCheck(writerInfo.getIntro(), introBadWordList); // 금칙어 치환
            writerInfo.setIntro(intro); // 치환된 금칙어 set
        }

        return writerInfo;
    }

    /*****************************************************
     *  SubFunction - select
     ****************************************************/

    /*****************************************************
     *  SubFunction - update
     ****************************************************/

    /*****************************************************
     *  SubFunction - insert
     ****************************************************/

    /*****************************************************
     *  SubFunction - delete
     ****************************************************/

    /*****************************************************
     *  ETC
     ****************************************************/
}
